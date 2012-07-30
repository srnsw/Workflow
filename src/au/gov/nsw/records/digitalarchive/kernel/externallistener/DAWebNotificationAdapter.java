package au.gov.nsw.records.digitalarchive.kernel.externallistener;

import java.util.Map;


/**
 * The notification adapter to transform the Action's notifications to the Digital Archive Platform web interface
 * @author wisanup
 *
 */
public class DAWebNotificationAdapter implements WorkflowNotificationListener {

	@Override
	public void onRenditionFileCreated( long id, String reference, String fileLocation,
			String fileName, String info) {
		System.out.println(String.format("DAWebNotificationAdapter:onRenditionFileCreated %d %s %s" , id, reference, fileLocation));
	}

	@Override
	public void onMetadataFileCreated( long id, String reference, String fileLocation,
			String fileName, String info) {
		System.out.println(String.format("DAWebNotificationAdapter:onMetadataFileCreated %d %s %s" , id,  reference, fileLocation));
	}


	@Override
	public void onWorkflowCompleted(long id, String reference) {
		System.out.println(String.format("DAWebNotificationAdapter:onWorkflowCompleted %d %s" , id, reference));
	}

	@Override
	public void onWorkflowError( long id, String reference, String error) {
		System.out.println(String.format("DAWebNotificationAdapter:onWorkflowError %d %s %s" , id, reference, error));
	}

	@Override
	public void onActionCompleted(long id, String reference, String actionName) {
		System.out.println(String.format("DAWebNotificationAdapter:onActionCompleted %d %s %s" , id, reference, actionName));
	}

	@Override
	public void onActionSetCompleted(long id, String reference,
			String actionSetName) {
		System.out.println(String.format("DAWebNotificationAdapter:onActionSetCompleted %d %s %s" , id, reference, actionSetName));
	}

	@Override
	public void onMetadataDetected(long id, String reference,
			String sourceFile, Map<String, String> metadata) {
		// TODO Auto-generated method stub
		
	}

}
