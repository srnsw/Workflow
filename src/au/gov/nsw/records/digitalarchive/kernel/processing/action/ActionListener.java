package au.gov.nsw.records.digitalarchive.kernel.processing.action;

public interface ActionListener {

	/**
	 * Notifies that the specific Action encountered error during execution.
	 * @param action
	 */
	public void onActionError(Action action, String error);

	/**
	 * Notifies that the specific Action has finished execution.
	 * @param action
	 */
	public void onActionFinished(Action action);
}
