package au.gov.nsw.records.digitalarchive.kernel.processing.actionset;

public interface ActionSetListener {

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
