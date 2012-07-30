package au.gov.nsw.records.digitalarchive.utils.thread;

import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NameableThreadFactory implements ThreadFactory {
	
	private String name;
	private int count = 0;
	private static final Log log = LogFactory.getLog(NameableThreadFactory.class);
	/**
	 * Create an instance of this class with will produce thread with the specified name
	 * @param name
	 */
	public NameableThreadFactory(String name){
		this.name = name;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		log.debug( String.format("Created a new thread %s-%d", name, count++));
		return new Thread(r, String.format("%s-%d", name, count++));
	}

}
