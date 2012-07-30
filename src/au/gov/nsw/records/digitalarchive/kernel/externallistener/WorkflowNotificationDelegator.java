package au.gov.nsw.records.digitalarchive.kernel.externallistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

public class WorkflowNotificationDelegator {

	private static List<WorkflowNotificationListener> listeners = new ArrayList<WorkflowNotificationListener>();
	
	private WorkflowNotificationDelegator(){
		
	}
	
	public static void addListenerClass(List<WorkflowNotificationListener> listeners){
		WorkflowNotificationDelegator.listeners.addAll(listeners);
	}
	
	public static void addListenerClassName(String clazzName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> clazz = Class.forName(clazzName);
		listeners.add((WorkflowNotificationListener) clazz.newInstance());
	}

	public static void onRenditionFileCreated(final long id, final String reference,
			final String fileLocation, final String fileName, final String info) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onRenditionFileCreated(id, reference, fileLocation, fileName, info);
				}
			}
		});
	}

	public static void onActionCompleted(final long id, final String reference, final String actionName) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onActionCompleted(id, reference, actionName);
				}
			}
		});
	}

	public static void onActionSetCompleted(final long id, final String reference,
			final String actionSetName) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onActionSetCompleted(id, reference, actionSetName);
				}
			}
		});

	}

	public static void onMetadataFileCreated(final long id, final String reference,
			final String fileLocation, final String fileName, final String info) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onMetadataFileCreated(id, reference, fileLocation, fileName, info);
				}
			}
		});

	}

	public static void onWorkflowCompleted(final long id, final String reference) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onWorkflowCompleted(id, reference);
				}
			}
		});

	}

	public static void onWorkflowError(final long id, final String reference, final String error) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onWorkflowError(id, reference, error);
				}
			}
		});

	}

	public static void onMetadataDetected(final long id, final String reference,
			final String sourceFile, final Map<String, String> metadata) {
		CoreThreadFactory.getNotificationExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				for (final WorkflowNotificationListener listener:listeners){
					listener.onMetadataDetected(id, reference, sourceFile, metadata);
				}
			}
		});
	}
	

}
