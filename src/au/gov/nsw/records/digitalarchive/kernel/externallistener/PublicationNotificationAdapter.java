package au.gov.nsw.records.digitalarchive.kernel.externallistener;

import java.util.Map;


/**
 * The notification adapter to transform the Action's notifications to the Publication NSW website.
 * @author wisanup
 *
 */
public class PublicationNotificationAdapter implements
		WorkflowNotificationListener {

	@Override
	public void onRenditionFileCreated(long id, String reference,
			String fileLocation, String fileName, String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionCompleted(long id, String reference, String actionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionSetCompleted(long id, String reference,
			String actionSetName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMetadataFileCreated(long id, String reference,
			String fileLocation, String fileName, String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWorkflowCompleted(long id, String reference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWorkflowError(long id, String reference, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMetadataDetected(long id, String reference,
			String sourceFile, Map<String, String> metadata) {
		// TODO Auto-generated method stub
		
	}

	

}
