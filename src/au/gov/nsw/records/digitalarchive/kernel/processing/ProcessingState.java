package au.gov.nsw.records.digitalarchive.kernel.processing;

public enum ProcessingState {
	
	/**
	 * waiting and not yet started
	 */
	WAITING,
	
	/**
	 * working and not yet finished
	 */
	PROCESSING,
	
	/**
	 * finished processing
	 */
	FINISHED,
	
	/**
	 * processed input but got error
	 */
	ERROR;
}
