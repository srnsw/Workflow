package au.gov.nsw.records.digitalarchive.action.helper;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class simply hold on the task and execute it in the later time. The time unit is configurable in minute, hour and day.
 * @author wisanup
 *
 */
@XStreamAlias("sleep")
public class SleepAction extends AbstractAction {

	public int minute;
	public int hour;
	public int day;
		
	private static final Log log = LogFactory.getLog(SleepAction.class);
	
	@Override
	public void processAction() {
		
		int minutes = minute + hour*60 + day*24*60;
		
		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		
		log.info(String.format("Scheduling a task to be executed at the next [%d days %d hours %d minutes ~ (%d minutes)]", day, hour, minute, minutes));

		Timer timer = new Timer();
		timer.schedule(new CallbackTask(), minutes * 60 * 1000);
	}
	
	class CallbackTask extends TimerTask {
		public void run() {
			log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
			listener.onActionFinished(SleepAction.this);
		}
	}

}
