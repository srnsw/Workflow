package au.gov.nsw.records.digitalarchive.kernel.externallistener;

import java.util.Map;

public interface WorkflowNotificationListener {

	public void onRenditionFileCreated(long id, String reference, String fileLocation, String fileName, String info);
	public void onActionCompleted(long id, String reference, String actionName);
	public void onActionSetCompleted(long id, String reference, String actionSetName);
	public void onMetadataFileCreated(long id, String reference, String fileLocation, String fileName, String info);
	public void onWorkflowCompleted(long id, String reference);
	public void onWorkflowError(long id, String reference, String error);
	public void onMetadataDetected(long id, String reference, String sourceFile, Map<String, String> metadata);
	
}
