package au.gov.nsw.records.digitalarchive.kernel.processing.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationDelegator;
import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSet;
import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSetListener;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("workflow")
public class Workflow implements ActionSetListener {

	@XStreamAsAttribute
	private String name;
	@XStreamImplicit
	private List<ActionSet> actionsets;
	@XStreamOmitField
	private List<ActionSet> completedActionsets;
	@XStreamOmitField
	private ActionSet currentActionSet;
	@XStreamOmitField
	private WorkflowListener listener;
	@XStreamOmitField
	boolean isError;
	@XStreamOmitField
	private static final String START_ACTIONSET_NAME = "start";
	@XStreamOmitField
	private static final String END_ACTIONSET_NAME = "end";
	
	@XStreamOmitField
	private WorkflowCache cache;

	private static final Log log = LogFactory.getLog(Workflow.class);
	/**
	 * Creates an instance of Workflow object with the given name
	 * @param name
	 */
	public Workflow(String name){
		actionsets = new LinkedList<ActionSet>();
		completedActionsets = new LinkedList<ActionSet>();
		this.name = name;
		isError = false;
	}
	
	/**
	 * Adds a WorkflowListener to this object
	 * @param listener
	 */
	public void setListener(WorkflowListener listener){
		this.listener = listener;
	}
	
	/**
	 * Assigns WorkflowCache to this object and makes it aware to process further action 
	 * @param workflowCache
	 */
	public void prepare(WorkflowCache workflowCache){
		this.cache = workflowCache;
		
		// XStream deserialization ignoring constructor, cause null in these two fields
		if (actionsets == null){
			actionsets = new LinkedList<ActionSet>();
		}
		if (completedActionsets == null){
			completedActionsets = new LinkedList<ActionSet>();
		}
		
		for (ActionSet actionset:actionsets){
			actionset.setListener(this);
			actionset.prepare(workflowCache);
		}
		this.name = (String)workflowCache.get(WorkflowCache.NAME);
	}
	
	/**
	 * Start processing the first contained ActionSet
	 * @return true if no error occurs, false otherwise
	 */
	public boolean start(){
		String state = cache.getString(WorkflowCache.STATE);
		if (state.equalsIgnoreCase(WorkflowState.WAITING.toString())){
			cache.set(WorkflowCache.STATE, WorkflowState.PROCESSING.toString());
			ActiveRecordFactory.addWorkflowHistory("Started processing: " + getName(), cache);
			return processActionSet(START_ACTIONSET_NAME);	
		}else{
			log.warn(String.format("Unable to start due to in state [%s]", state));
			return false;
		}
	}
	
	public boolean cancel(){
		if (cache.getString(WorkflowCache.STATE).equals(WorkflowState.PROCESSING.toString()) ||
			cache.getString(WorkflowCache.STATE).equals(WorkflowState.WAITING.toString())){

			if (currentActionSet != null){
				currentActionSet.stop();
			}
			
			cache.set(WorkflowCache.STATE, WorkflowState.CANCELLED.toString());
			ActiveRecordFactory.save(cache);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Start processing at the specified ActionSet name  
	 * @return true if no error occurs, false otherwise
	 */
	public boolean start(String actionSet){
		cache.set(WorkflowCache.STATE, WorkflowState.PROCESSING.toString());
		ActiveRecordFactory.save(cache);
		return processActionSet(actionSet);
	}
	
	/**
	 * Get the name of this Workflow
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Retrieves all ActionSet in this Workflow object
	 * @return a collection of ActionSet
	 */
	public Collection<ActionSet> getActionSets(){
		return actionsets;
	}
	
	/**
	 * Retrieves the ActionSet at the specified ID 
	 * @return the ActionSet instance, null if the given ID does not exist
	 */
	public ActionSet getActionSet(int id){
		for (ActionSet as:actionsets){
			if (as.getID() == id){
				return as;
			}
		}
		return null;
	}
	
	/**
	 * Registers the ActionSet to this object
	 * 
	 */
	public void addActionSet(ActionSet actionset){
		actionsets.add(actionset);
	}
	
	private boolean processActionSet(String setName){
		List<ActionSet> copyActionSet = new LinkedList<ActionSet>(actionsets);
		for (ActionSet as: copyActionSet){
			if (as.getName().equalsIgnoreCase(setName)){
				currentActionSet = as;
				log.info(String.format("Starting actionset [%s] for [%s]", setName, getName()));
				if(!as.start()){
					return false;
				}
				// it's asynchronous
				return true;
			}
		}
		
		// unable to find the desired actionset
		log.warn(String.format("Unable to process actionset [%s] for [%s]", setName, getName()));
		isError = true;
		listener.onWorkflowError(this);
		WorkflowNotificationDelegator.onWorkflowError(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), "Unable to find actionset:" + setName);
		return false;
	}
	
	/**
	 * Retrieves the current status of workflow 
	 * @return  return FINISHED only if all actions set are processed regardless it's status
     * 			return ERROR if it can't find the next ActionSet or error ActionSet of current ActionSet to process
	 * 			return PROCESSING if one of action set is in PROCESSING state
	 */
	public String getState() {
		// return FINISHED only if *all* actions set are processed regardless it's status
		// return ERROR if it can't find nextActionSet or errorActionSet of current actionset
		// return PROCESSING if one of action set is PROCESSING
//		if (isError){
//			return ProcessingState.ERROR;
//		}else if (completedActionsets.equals(actionsets)){
//			return ProcessingState.FINISHED;
//		} 
//		return ProcessingState.PROCESSING;
		
		return cache.getString(WorkflowCache.STATE);
	}
	
	public WorkflowCache getCache(){
		return cache;
	}

	@Override
	public void onActionSetFinished(final ActionSet actionSet) {
		ActiveRecordFactory.initDBConnection();
		if (isError){
			return;
		}
		//verify current actionset before you go go go
		if (currentActionSet == actionSet){ // yes, checking the address of two objects
			completedActionsets.add(actionSet);
			if (listener!=null){
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						listener.onActionSetFinished(actionSet);
					}
				});
			}
			

			if (currentActionSet.getNextActionSetName().equalsIgnoreCase(END_ACTIONSET_NAME)){
				if (listener!=null){
					CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
						@Override
						public void run() {
							listener.onWorkflowFinished(Workflow.this);
						}
					});
				}
				
				
				ActiveRecordFactory.addWorkflowHistory("ActionSet finished: " + actionSet.getName(), cache);
				ActiveRecordFactory.addWorkflowHistory("Workflow finished: " + getName(), cache);
				
				// persist all data after ActionSet finished
				cache.set(WorkflowCache.STATE, WorkflowState.FINISHED.toString());
				cache.set(WorkflowCache.ACTIONSET, "end");
				ActiveRecordFactory.save(cache);
				
				log.info(String.format("Workflow completed [%s] ", getName()));
				
				WorkflowNotificationDelegator.onActionSetCompleted(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), actionSet.getName());
				WorkflowNotificationDelegator.onWorkflowCompleted(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE));
			}else{
				
				ActiveRecordFactory.addWorkflowHistory("ActionSet finished: " + actionSet.getName(), cache);
				cache.set(WorkflowCache.ACTIONSET, currentActionSet.getNextActionSetName());
				// persist all data after ActionSet finished
				ActiveRecordFactory.save(cache);
				
				WorkflowNotificationDelegator.onActionSetCompleted(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), actionSet.getName());
				
				log.info(String.format("Processing set [%s] for [%s] ", currentActionSet.getNextActionSetName(), getName()));
				isError = !processActionSet(currentActionSet.getNextActionSetName());
			}
		
		}
		log.info(String.format("Processing finished at set [%s] for [%s] ", actionSet.getName(), getName()));
	}

	@Override
	public void onActionSetError(ActionSet actionSet) {
		ActiveRecordFactory.initDBConnection();
		if (isError){
			cache.setBoolean(WorkflowCache.ERROR, Boolean.TRUE);
			cache.save();
			return;
		}
		if (listener!=null){
			listener.onActionSetError(actionSet);
		}
		if (currentActionSet == actionSet){ // yes, checking the memory's address of two objects
			completedActionsets.add(actionSet);
			if (currentActionSet.getErrorActionSetName().equalsIgnoreCase(END_ACTIONSET_NAME) || completedActionsets.equals(actionsets)){
				if (listener!=null){
					CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
						@Override
						public void run() {
							listener.onWorkflowFinished(Workflow.this);
						}
					});
				}
				
				WorkflowNotificationDelegator.onWorkflowCompleted(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE));
			}else{
				isError = !processActionSet(currentActionSet.getErrorActionSetName());
			}
		}
		log.warn(String.format("Processing error at set [%s] for [%s] ", actionSet.getName(), getName()));
		// rollback all data after ActionSet fail
		//workflowCache.rollbackTransaction();
		ActiveRecordFactory.addWorkflowHistory("ActionSet error rollback transaction: " + actionSet.getName(), cache);
	}

	/**
	 * Process the event related to the contains Action and ActionSet 
	 * @param actionSetId
	 * @param actionId
	 * @param payload
	 */
	public void processEvent(int actionSetId, int actionId, String payload) {
		ActionSet as = getActionSet(actionSetId);
		if (as!=null){
			as.processAction(actionId, payload);
			log.info("event processed:" + actionSetId + ", " + actionId );
		}
	}
	
	public boolean validate(){
		List<String> list = getActionSetSequence();
		return list.get(list.size() -1).equals(END_ACTIONSET_NAME);
	}
	
	public List<String> getActionSetSequence(){
		List<String> actionSetList = new ArrayList<String>();
		
		List<ActionSet> copyActionSet = new LinkedList<ActionSet>(actionsets);
		String lookupSetName = START_ACTIONSET_NAME;
		boolean error = false;
		boolean completed = false;
		while (!error && !completed){
			actionSetList.add(lookupSetName);
			boolean found = false;
			for (ActionSet as: copyActionSet){
				if (as.getName().equalsIgnoreCase(lookupSetName)){
					found = true;
					lookupSetName = as.getNextActionSetName();
					if (lookupSetName.equals(END_ACTIONSET_NAME)){
						actionSetList.add(END_ACTIONSET_NAME);
						completed = true;
					}
					break;
				}
			}
			error = !found;
			if (error){
				log.error(String.format("Unable to find the action set [%s]", lookupSetName));
			}
		}
		
		return actionSetList;
	}
	
	public boolean validateXML(){
		// validate duplicate entries
		// validate linkage to the end of the set
		// validate unreachable action set
		return false;
	}
}
