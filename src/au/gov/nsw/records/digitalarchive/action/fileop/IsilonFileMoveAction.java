package au.gov.nsw.records.digitalarchive.action.fileop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.JerseyClientHelper;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The Isilon system file operation. This class move files from the source path to the destination path in Isilon system. 
 * @author wisanup
 *
 */
@XStreamAlias("isilonMove")
public class IsilonFileMoveAction extends AbstractAction {

	public String from = "";
	public String to = "";

	
	private IsilonActionConfig config;
	private WebResource webResource;
	private Client client;
	
	private static final Log log = LogFactory.getLog(IsilonFileMoveAction.class);
	
	@Override
	public void processAction() {
		// moving all resources under specified dir would make more sense than coping each file individually
		final String sourceDir = from;
		final String destDir = to;

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));

		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				IsilonFileOperations fileOp = new IsilonFileOperations();
				if (fileOp.move(sourceDir, destDir)){
					cache.set(WorkflowCache.RECENTLOCATION, destDir);
					listener.onActionFinished(IsilonFileMoveAction.this);
				}else{
					listener.onActionError(IsilonFileMoveAction.this, String.format("failed to move files from [%s] to [%s]", sourceDir, destDir));
				}
			}
		});
	}

	/**
	 * Tell Isilon to move the contents in source directory into the destination directory
	 * @param sourceDir the source directory
	 * @param destDir the destination directory
	 * @return true if the operation was success, false otherwise
	 */
	public boolean move(String sourceDir, String destDir){
		
		// both source dir and dest dir must end with '/'
		sourceDir = StringHelper.formatIsilonDirectoryString(sourceDir);
		destDir = StringHelper.formatIsilonDirectoryString(destDir);
		
		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		String url = String.format("https://%s:%d/1%s", config.getHost(), config.getPort(), sourceDir);
		webResource = client.resource(url);
		log.info(String.format("Executing POST [%s] with [%s]", url, destDir));
		// issue POST for directory moving
		try {
			ClientResponse response = webResource.header("x-isi-set-location", destDir).post(ClientResponse.class);
			
			if (response.getClientResponseStatus() == ClientResponse.Status.CREATED){
				log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
				cache.set(WorkflowCache.RECENTLOCATION, destDir);
				return true;
			}else{
				log.error(String.format("Action [%s] executed for [%s] and got HTTP status: %s ", getName(), workflowName, response.getClientResponseStatus().toString() ));
				log.error(String.format("Isilon Response: [%s]", response.getEntity(String.class)));
			}
	
		} catch (UniformInterfaceException e) {
			log.error("Error while trying to move file", e);
		} 
		
		return false;
	}

	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {

		super.prepare(cache, actionSetId);
		
		ConfigDeserializer<IsilonActionConfig> configLoader = new ConfigDeserializer<IsilonActionConfig>();
		IsilonActionConfig templateConf = new IsilonActionConfig("isilonHost", 9443, "user", "passwd");
		config = configLoader.load(templateConf, ConfigHelper.getIsilonConfig());

		client = JerseyClientHelper.createClient();
		client.setConnectTimeout(0);
		client.addFilter(new HTTPBasicAuthFilter(config.getUser(), config.getPassword()));
		
		log.info("Using::" + config.getUser() + ":" + config.getPassword());
		
	}
}
