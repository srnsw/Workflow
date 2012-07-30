package au.gov.nsw.records.digitalarchive.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreThreadFactory {

	public static final int CONCURRENT_THREAD = 10;
	private static ExecutorService callbackService = Executors.newFixedThreadPool(CONCURRENT_THREAD, new NameableThreadFactory("daw-callback"));
	private static ExecutorService notificationService = Executors.newFixedThreadPool(1, new NameableThreadFactory("daw-notification"));

	// do not allow to create an instance of this class
	private CoreThreadFactory(){
	}

	/**
	 * Get the {@link ExecutorService} for firing internal callbacks.
	 * @return ExecutorService
	 */
	public static ExecutorService getCallBackExecutorService(){
		return callbackService;
	}

	/**
	 * Get the {@link ExecutorService} for firing notifications.
	 * @return ExecutorService
	 */
	public static ExecutorService getNotificationExecutorService(){
		return notificationService;
	}

	public static void shutdownServices(){
		callbackService.shutdownNow();
		notificationService.shutdownNow();
	}
}

