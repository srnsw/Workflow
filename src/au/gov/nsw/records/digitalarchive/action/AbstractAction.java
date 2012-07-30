package au.gov.nsw.records.digitalarchive.action;

import java.util.concurrent.atomic.AtomicInteger;

import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.processing.ProcessingState;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.ActionListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Common implementation of action
 * @author wisanup
 *
 */
public abstract class AbstractAction implements Action {

	@XStreamOmitField
	private static AtomicInteger globalID = new AtomicInteger(0);
	
	@XStreamOmitField
	protected int myID;

	protected WorkflowCache cache;
	
	@XStreamAsAttribute
	@XStreamAlias("name")
	protected String actionName;

	@XStreamOmitField
	protected ActionListener listener;
	
	@XStreamOmitField
	protected String workflowName;
	
	ProcessingState state;
	
	public AbstractAction() {
		//myID = globalID.getAndIncrement();
	}
	
	@Override
	public void setListener(ActionListener listener){
		this.listener = listener;
	}
	
	@Override
	public int getID() {
		return myID;
	}

	@Override
	public String getName() {
		return actionName;
	}
	
	@Override
	public ProcessingState getState() {
		return state;
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId){
		myID = globalID.getAndIncrement();
		state = ProcessingState.WAITING;
		workflowName = (String)cache.get(WorkflowCache.NAME);
		this.cache = cache;
	}
	
	@Override
	public boolean processResponse(String response) {
		return false;
	}
}
