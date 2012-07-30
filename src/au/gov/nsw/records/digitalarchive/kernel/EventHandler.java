package au.gov.nsw.records.digitalarchive.kernel;

import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;

/**
 * Implement this interface to handle event from remote {@link Action}
 * @author wisanup
 *
 */
public interface EventHandler {

	/**
	 * Notification indicating the result from remote {@link Action}.  
	 * @param workflowId the identification number of the Workflow
	 * @param actionSetId the identification number of the ActionSet
	 * @param actionId the identification number of the Action
	 * @param response the response data
	 */
	public void onEvent(long workflowId, int actionSetId, int actionId, String response);
}
