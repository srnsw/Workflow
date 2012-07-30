package au.gov.nsw.records.digitalarchive.action.repository;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("repository")
public class RepositoryAction extends AbstractAction {

	private static RepositoryActionConfig config;
	private static String clientURL;
	
	private String source;
	@XStreamOmitField
	private long workflowId;
	@XStreamOmitField
	private int actionSetId;
	private WorkflowCache cache;
	
	private static final Log log = LogFactory.getLog(RepositoryAction.class);
	
	public RepositoryAction() {

		if (config == null){
			ConfigDeserializer<RepositoryActionConfig> configLoader = new ConfigDeserializer<RepositoryActionConfig>();
			RepositoryActionConfig templateConf = new RepositoryActionConfig();
			config = configLoader.load(templateConf, ConfigHelper.getRepositoryConfig());
			clientURL = String.format("http://%s:%s/repository", config.getHost(), config.getPort());
		}
	}
	
	@Override
	public void processAction() {
		
		if (source==null || source.isEmpty()){
			source = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		log.info(String.format("Using [%s]", clientURL));
		Client client = Client.create();
		client.setReadTimeout(30000);
		WebResource webResource = client.resource(clientURL);
				
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("filepath", source);
		queryParams.add("callback", config.getCallbackURL());
		queryParams.add("actionid", String.valueOf(getID()));
		queryParams.add("workflowid", String.valueOf(workflowId));
		queryParams.add("actionsetid", String.valueOf(actionSetId));
	
		try{
			ClientResponse response = webResource.queryParams(queryParams).post(ClientResponse.class);
			
			if (response.getClientResponseStatus() != ClientResponse.Status.OK){
				final String error = String.format("Action [%s] executed for [%s] but got HTTP status: %s ", getName(), workflowName, response.getClientResponseStatus().toString());
				log.error(error);
				log.error(String.format("HTTP response: %s ", response.getEntity(String.class)));
				log.error(String.format("HTTP headers: %s ", response.getHeaders()));
				
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						listener.onActionError(RepositoryAction.this, error);
					}
				});
			}else{
				log.info(String.format("Action [%s] executed for [%s] with HTTP status: %s ", getName(), workflowName, response.getClientResponseStatus().toString() ));
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						listener.onActionFinished(RepositoryAction.this);
					}
				});
			}
		}catch  (final Exception e){
			log.error(String.format("Action [%s] executed for [%s] but got error", getName(), workflowName ), e);
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionError(RepositoryAction.this, e.getMessage());
				}
			});
		}
	}

	@Override
	public boolean processResponse(final String response) {
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				if (response.equals("OK")){
					log.info(String.format("Action [%s] callback executed for [%s] ", getName(), workflowName));
					listener.onActionFinished(RepositoryAction.this);
				}else{
					log.error(String.format("Action [%s] executed for [%s] but got error response: %s ", getName(), workflowName, response));
					listener.onActionError(RepositoryAction.this, response);
				}
			}
		});

		return false;
	}

	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		this.cache = cache;
		workflowId = cache.getLongId();
		this.actionSetId = actionSetId;
	}

}
