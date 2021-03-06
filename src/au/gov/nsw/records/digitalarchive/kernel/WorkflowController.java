package au.gov.nsw.records.digitalarchive.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.Workflow;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.WorkflowState;
import au.gov.nsw.records.digitalarchive.utils.WorkflowDeserializer;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;
import au.gov.nsw.records.digitalarchive.utils.thread.NameableThreadFactory;

/**
 * The WorkflowController responsible for managing {@link Workflow} objects. It is an entry point of workflow creation.  
 * @author wisanup
 *
 */

public class WorkflowController implements EventHandler{

	private static final Log log = LogFactory.getLog(WorkflowController.class);

	private Map<Long, Workflow> workflows;

	private static final ScheduledExecutorService worker =  Executors.newSingleThreadScheduledExecutor(new NameableThreadFactory("daw-workflow-cleanup"));

	private static final WorkflowController instance = new WorkflowController();

	public static WorkflowController getInstance(){
		return instance;
	}

	/**
	 * Create an instance of Workflow object 
	 */
	private WorkflowController(){
		workflows = new HashMap<Long, Workflow>();

		// Internal cache cleanup thread, triggered every 5 mins
		worker.scheduleAtFixedRate( new Runnable() {
			@Override
			public void run() {
				log.debug(String.format("Checking internal cache"));
				List<Long> ids = new ArrayList<Long>(workflows.keySet());
				for (Long id:ids){
					String state = workflows.get(id).getCache().getString(WorkflowCache.STATE.toString());
					if ( !(state.equals(WorkflowState.WAITING.toString()) || state.equals(WorkflowState.PROCESSING.toString()))){
						workflows.remove(id);
						log.info(String.format("Cleanedup internal cache [%d] state [%s]", id, state));
					}else{
						log.debug(String.format("Ignore internal cache [%d] state [%s]", id, state));
					}
				}
			}
		}, 5, 5, TimeUnit.MINUTES);
	}

	@Override
	public void onEvent(long workflowId, int actionSetId, int actionId, String response) {
		Workflow workflow = workflows.get(workflowId);
		if (workflow!=null){
			workflow.processEvent(actionSetId, actionId, response);
		} else {
			// unknown request
			log.warn("Workflow [" + workflowId + "] does not exist");
		}
	}

	public Workflow createWorkflow(String workflowXml, String reference){

		Workflow wf = WorkflowDeserializer.load(workflowXml);
		if (wf!=null){

			// invalid workflow definition
			if (!wf.getActionSetSequence().get(wf.getActionSetSequence().size() - 1).equals("end")){
				log.fatal(String.format("Invalid workflow - no path to the end action set specified"));
				return null;
			}

			WorkflowCache cache = ActiveRecordFactory.addWorkflowCache(wf.getName(), WorkflowState.WAITING.toString(), "", workflowXml, wf.getContextPath() , reference);

			workflows.put(cache.getLongId(), wf);
			wf.prepare(cache);
			return wf;
		}else{
			log.error("Error while parsing workflow XML");
		}

		return null;
	}

	/**
	 * Load and continue execute all workflows that are not in completed or cancelled state.
	 * Ideally, this method should be called immediately after the application has started.
	 */
	public void startRecovery(){
		for (final WorkflowCache cache:ActiveRecordFactory.getActiveWorkflowCache()){
			String xml = (String)cache.get(WorkflowCache.XML);
			if (xml!=null && !xml.isEmpty()){
				final Workflow wf = WorkflowDeserializer.load(xml);
				if (wf!=null){
					workflows.put(cache.getLongId(), wf);
					wf.prepare(cache);
					String state = (String)wf.getCache().get(WorkflowCache.STATE);
					// WAITING and ERROR should be started manually through User's action
					if (!state.equals(WorkflowState.WAITING.toString()) && !state.equals(WorkflowState.ERROR.toString())){
						CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
							@Override
							public void run() {
								wf.start((String)cache.get(WorkflowCache.ACTIONSET));
							}
						});
						log.info("recovered:[" + (String)cache.get(WorkflowCache.NAME) + "]");
					}else{
						log.info(String.format("ignored recovery: [%d - %s] - state [%s]", cache.getLongId(), (String)cache.get(WorkflowCache.NAME), state));
					}
				}else{
					log.error("Unable to load workflow [" + (String)cache.get(WorkflowCache.NAME) + "] during recovery");
				}
			}
		}
		// iterate through all unfinished workflow's data

	}

	public Collection<Workflow> getActiveWorkflows(){
		return workflows.values();
	}

	public Workflow cancelWorkflow(long id){
		log.info(String.format("Attemtping to cancel [%d]", id));
		Workflow workflow = workflows.get(id);
		if (workflow!=null && workflow.cancel()){
			log.info(String.format("Workflow [%d] cancelled", id));
		} else {
			// unknown request
			log.warn("Workflow [" + id + "] does not exist");
		}
		return null;
	}
	public Workflow startWorkflow(long id) {
		log.info(String.format("Attemtping to start [%d]", id));
		final Workflow workflow = workflows.get(id);
		if (workflow!=null){
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					workflow.start();
				}
			});
			log.info(String.format("Workflow [%d] started", id));
			return workflow;
		} else {
			// unknown request
			log.warn("Workflow [" + id + "] does not exist");
		}
		return null;
	}

	public void shutdown(){
		worker.shutdownNow();
	}
}
