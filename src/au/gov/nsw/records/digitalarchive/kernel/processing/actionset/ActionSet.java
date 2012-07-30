package au.gov.nsw.records.digitalarchive.kernel.processing.actionset;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationDelegator;
import au.gov.nsw.records.digitalarchive.kernel.processing.ProcessingState;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.ActionListener;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Collection of {@code Action} objects, all Actions will be executed sequentially
 * @author wisanup
 *
 */
@XStreamAlias("actionSet")
public class ActionSet implements ActionListener {
	
	private static final Log log = LogFactory.getLog(ActionSet.class);
	
	@XStreamOmitField
	private static AtomicInteger globalID = new AtomicInteger(0);
	@XStreamOmitField
	private int myID;
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String actionSetName;
	@XStreamImplicit
	private List<Action> actions;
	@XStreamOmitField
	private List<Action> completedActions;
	@XStreamAsAttribute
	@XStreamAlias("nextActionSet")
	private String nextActionSetName;
	@XStreamAsAttribute
	@XStreamAlias("errorActionSet")
	private String errorActionSetName;
	@XStreamOmitField
	private ActionSetListener listener;
	@XStreamOmitField
	private boolean isError;
	@XStreamOmitField
	private Action currentAction;
	
	private String workflowName;
	
	private boolean isStopped = false;
	private WorkflowCache cache;
	@XStreamOmitField
	private boolean isStarted = false;
	/**
	 * Creates an instance of ActionSet object with the given name
	 * @param name
	 */
	public ActionSet(String name) {
		myID = globalID.getAndIncrement();
		actionSetName = name;
		//a map for sequential access
		actions = new LinkedList<Action>();
		completedActions = new LinkedList<Action>();
		isError = false;
	}
	
	/**
	 * Adds a ActionSetListener to this object
	 * @param listener
	 */
	public void setListener(ActionSetListener listener){
		this.listener = listener;
	}
	
	/**
	 * Assigns WorkflowCache to this object and makes it aware to process further action 
	 * @param workflowCache
	 */
	public void prepare(WorkflowCache workflowCache){

		// XStream deserialization ignoring constructor, cause null in these two fields
		
		myID = globalID.getAndIncrement();

		if (actions==null){
			actions = new LinkedList<Action>();	
		}
		if (completedActions==null){
			completedActions = new LinkedList<Action>();
		}
		
		for (Action action:actions){
			action.setListener(this);
			action.prepare(workflowCache, getID());
		}
		cache = workflowCache;
		workflowName = (String)workflowCache.get(WorkflowCache.NAME);
	}
	/**
	 * Get identification number of this object
	 * @return identification number of this object
	 */
	public int getID() {
		return myID;
	}

	/**
	 * Get name of this object
	 * @return name of this object
	 */
	public String getName() {
		return actionSetName;
	}
	
	/**
	 * Get {@code Action} of this object by Action ID
	 * @param id
	 * @return the {@code Action} if available, {@code null} otherwise
	 */
	public Action getAction(int id){
		for (Action ac:actions){
			if (ac.getID() == id){
				return ac;
			}
		}
		return null;
	}
	
	/**
	 * Get unfinished actions belong to this object
	 * @return collection of action
	 */
	public Collection<Action> getActions(){
		return actions;
	}
	
	/**
	 * Add action to this object
	 * @param action
	 */
	public void addAction(Action action){
		actions.add(action);
	}
	
	/**
	 * Set the name of the next {@code ActionSet} to be processed after this 
	 * action set completely executed
	 * @param name
	 */
	public void setNextActionSetName(String name){
		nextActionSetName = name;
	}
	/**
	* Get the name of the next {@code ActionSet} to be processed after this 
	* action set completely executed
	*/
	public String getNextActionSetName(){
		return nextActionSetName;
	}
	/**
	 * Set the name of the {@code ActionSet} to be processed when this 
	 * action unable to process underlying action
	 * @param name
	 */
	public void setErrorActionSetName(String name){
		errorActionSetName = name;
	}
	
	public void stop(){
		isStopped = true;
	}
	/**
	 * Get the name of the {@code ActionSet} to be processed when this 
	 * action unable to process underlying action
	 * @return
	 */
	public String getErrorActionSetName(){
		return errorActionSetName;
	}

	/**
	 * Start process underlying action sequentially
	 * @return
	 */
	public boolean start(){
		isStarted = true;
		isStopped = false;
		return processNextAction();
	}
	
	/**
	 * Process all actions until found asynchronous action  
	 * @return true if the action was either successfully executed  
	 */
	private boolean processNextAction(){
		if (isStopped){
			return false;
		}
		List <Action> iterateActions = new LinkedList<Action>(actions);
		for (Action action: iterateActions){
			currentAction = action;
			log.info(String.format("Processing next action [%s][%s] for [%s] ", getName(), action.getName(), workflowName));
			action.processAction();
			return true;
		}
		
		if (listener!=null){
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionSetFinished(ActionSet.this);
				}
			});
		}
		
		log.info(String.format("No more action to process in set [%s] for [%s] ", getName(), workflowName));
		// all actions are processed 
		return true;
	}
	
	/**
	 * Process {@code Action} contained in this {@code ActionSet} 
	 * @param actionID
	 * @param payload
	 * @return {@code true} if the contained {@code Action} finished, {@code false} otherwise
	 */
	public void processAction(int actionID, String payload){
		Action action = getAction(actionID);
		if (action!=null){
			log.info(String.format("Process action [%s] in set [%s] for [%s] ", action.getName(), getName(), workflowName));
			action.processResponse(payload);
		} 
		log.warn(String.format("Unable to get Action - [%d] in set [%s] for [%s]", actionID, getName(), workflowName));
	}
	

	public String getState(){
		// return FINISHED only if *all* actions are FINISHED
		// return ERROR if any action is ERROR
		// return PROCESSING if one of action is PROCESSING
		if (isError){
			return ProcessingState.ERROR.toString();
		}else if (actions.isEmpty()){
			return ProcessingState.FINISHED.toString();
		} 
		
		if (isStopped){
			return ProcessingState.ERROR.toString();
		}
		if (isStarted){
			return ProcessingState.PROCESSING.toString();
		}else{
			return ProcessingState.WAITING.toString();
		}
	}

	@Override
	public void onActionFinished(Action action) {
		ActiveRecordFactory.initDBConnection();
		actions.remove(action);
		completedActions.add(action);
		
		WorkflowNotificationDelegator.onActionCompleted(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), action.getName());
		
		if (actions.isEmpty()){
			if (listener!=null){
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						listener.onActionSetFinished(ActionSet.this);
					}
				});
			}
		}else{
			processNextAction();
		}
		log.warn(String.format("Finished action [%s] in set [%s] for [%s]", action.getName(), getName(), workflowName));
	}

	@Override
	public void onActionError(Action action, String error) {
		ActiveRecordFactory.initDBConnection();
		isError = true;
		cache.setBoolean(WorkflowCache.ERROR, Boolean.TRUE);
		cache.setString(WorkflowCache.LASTERROR, error);
		cache.save();
		// notify listener
		if (listener!=null){
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionSetError(ActionSet.this);
				}
			});
		}
		log.warn(String.format("Error: action [%s] in set [%s] for [%s]", action.getName(), getName(), workflowName));
	}
	
	public Action getCurrentAction(){
		return currentAction;
	}
}
