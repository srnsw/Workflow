package au.gov.nsw.records.digitalarchive.kernel.processing.action;

import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.processing.ProcessingState;

/**
 * The smallest processing unit of the DA system 
 * @author wisanup
 *
 */
public interface Action {
	
	/**
	 * This will be called to register the ActionListener to this Action
	 * @param listener
	 */
	public void setListener(ActionListener listener);
	
	/**
	 * This will be called to register the WorkflowCache to this Action
	 * @param workflowCache
	 * @param id 
	 */
	public void prepare(WorkflowCache workflowCache, int id);
	

	/**
	 * Get unique identification number of this object
	 * @return unique identification number of this object
	 */
	public int getID();
	
	/**
	 * Get the name of this action instance
	 * @return action name
	 */
	public String getName();
	
	/**
	 * Perform action
	 */
	public void processAction();
	
	/**
	 * Process response asynchronously
	 * @return {@code true} when the action is finished, {@code false} otherwise
	 */
	public boolean processResponse(String response);
	
	/**
	 * Check the state of this action
	 * @return {@code ProcessingState}
	 */
	public ProcessingState getState();

}
