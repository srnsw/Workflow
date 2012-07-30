package au.gov.nsw.records.digitalarchive.kernel.processing.workflow;

import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSet;


/**
 * Notification indicating the result from @link {@link Workflow} execution
 * 
 * @author wisanup
 *
 */
public interface WorkflowListener {

	/**
	 * Notifies that the specific Workflow has finished execution. 
	 * @param workflow
	 */
	public void onWorkflowFinished(Workflow workflow);
	
	/**
	 * Notifies that the specific Workflow encountered error during execution.
	 * @param workflow
	 */
	public void onWorkflowError(Workflow workflow);

	/**
	 * Notifies that the specific ActionSet has finished execution.
	 * @param actionSet
	 */
	public void onActionSetFinished(ActionSet actionSet);

	/**
	 * Notifies that the specific ActionSet encountered error during execution.
	 * @param actionSet
	 */
	public void onActionSetError(ActionSet actionSet);
}
