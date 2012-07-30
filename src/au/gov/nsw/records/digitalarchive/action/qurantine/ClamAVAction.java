package au.gov.nsw.records.digitalarchive.action.qurantine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.action.wrapper.ClamAVSocket;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The interface of ClamAV daemon. This class send command to ClamAV daemon to perform virus scanning on the specified location. 
 * @author wisanup
 * @see <a href="http://www.clamav.net/lang/en/">ClamAV website</a>
 */
@XStreamAlias("clamScan")
public class ClamAVAction extends AbstractAction {

	private ClamAVActionConfig config;
	private String path;
	private WorkflowCache cache;

	private static final Log log = LogFactory.getLog(ClamAVAction.class);
	
	@Override
	public void processAction() {
		
		if (path==null || path.isEmpty()){
			path = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		log.info(String.format("ClamAV processing  [%s] for [%s] at [%s]", getName(), workflowName, path));
		
		final boolean isOK = scan(path);
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				// do virus scanning the path recursively..
				if (isOK){
					log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
					listener.onActionFinished(ClamAVAction.this);
				}else{
					log.error(String.format("Action [%s] executed for [%s] but got error response", getName(), workflowName));
					listener.onActionError(ClamAVAction.this, "Virus detected at " + path);	
				}
			}
		});	
	}
	
	/**
	 * Delegate ClamAV daemon to scan recursively on the specified path 
	 * @param pathToScan the path to be scanned
	 * @return true if no virus detected, false otherwise
	 */
	public boolean scan(String pathToScan){
		final ClamAVSocket avSocket = new ClamAVSocket(config.getClamdhost(), config.getClamdport());
		return avSocket.scan(pathToScan);
	}

	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		this.cache = cache;
		ConfigDeserializer<ClamAVActionConfig> configLoader = new ConfigDeserializer<ClamAVActionConfig>();
		ClamAVActionConfig templateConf = new ClamAVActionConfig("10.0.0.22", 3310);
		config = configLoader.load(templateConf, ConfigHelper.getClamAVConfig());
	}

}
