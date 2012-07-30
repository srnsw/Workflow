package digitalarchive.core.processing.actionset;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import au.gov.nsw.records.digitalarchive.kernel.processing.ProcessingState;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;
import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSet;
import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSetListener;

public class ActionSetTest extends TestCase {

	Action firstAction;
	Action secondAction;
	ActionSetListener listener;
    /**
     * Sets up the test fixture.
     * Called before every test case method.
     */
	protected void setUp() {
		firstAction = EasyMock.createMock(Action.class);
		secondAction = EasyMock.createMock(Action.class);
		listener = EasyMock.createMock(ActionSetListener.class);
		
		EasyMock.expect(firstAction.getName()).andReturn("firstAction").anyTimes();
		EasyMock.expect(firstAction.getID()).andReturn(1).anyTimes();
		firstAction.processAction();
		EasyMock.expectLastCall().once();
		
		EasyMock.expect(secondAction.getName()).andReturn("secondAction").anyTimes();
		EasyMock.expect(secondAction.getID()).andReturn(1).anyTimes();
		secondAction.processAction();
		EasyMock.expectLastCall().once();
	}
	
	/**
     * Tears down the test fixture.
     * Called after every test case method.
     */
	protected void tearDown() {
	}
	
	@Test
	public void testMultiAction(){
		
		ActionSet as = new ActionSet("testActionSet");
		as.addAction(firstAction);
		as.addAction(secondAction);
		as.setListener(listener);
		
		listener.onActionSetFinished(as);
		EasyMock.expectLastCall();
		
		// start collecting results
		EasyMock.replay(firstAction);
		EasyMock.replay(secondAction);
		EasyMock.replay(listener);
		
		assertEquals( ProcessingState.PROCESSING , as.getState());
		// start workflow
		assertTrue(as.start());
		as.onActionFinished(firstAction);
		as.onActionFinished(secondAction);
		// all actions finished
		assertEquals( ProcessingState.FINISHED , as.getState());
		
		EasyMock.verify(firstAction);
		EasyMock.verify(secondAction);
		sleep(100);
		EasyMock.verify(listener);
		
	}
	
	@Test
	public void testMultiActionImmediateError(){

		
		ActionSet as = new ActionSet("testActionSet");
		as.addAction(firstAction);
		as.addAction(secondAction);
		as.setListener(listener);
		
		listener.onActionSetError(as);
		EasyMock.expectLastCall();
		// start collecting results
		EasyMock.replay(firstAction);
		EasyMock.replay(secondAction);
		EasyMock.replay(listener);
		
		assertEquals( ProcessingState.PROCESSING , as.getState());
		// start workflow
		assertTrue(as.start());
		as.onActionError(firstAction, "error");
		assertEquals( ProcessingState.ERROR , as.getState());
		
		EasyMock.verify(firstAction);
		sleep(100);
		EasyMock.verify(listener);
	}
	
	@Test
	public void testMultiActionSubsequentError(){
		ActionSet as = new ActionSet("testActionSet");
		as.addAction(firstAction);
		as.addAction(secondAction);
		as.setListener(listener);
		
		listener.onActionSetError(as);
		EasyMock.expectLastCall();
		// start collecting results
		EasyMock.replay(firstAction);
		EasyMock.replay(secondAction);
		EasyMock.replay(listener);
		
		assertEquals( ProcessingState.PROCESSING , as.getState());
		// start workflow
		assertTrue(as.start());
		as.onActionFinished(firstAction);
		assertEquals( ProcessingState.PROCESSING , as.getState());
		as.onActionError(secondAction, "error");
		assertEquals( ProcessingState.ERROR , as.getState());
		
		EasyMock.verify(firstAction);
		EasyMock.verify(secondAction);
		sleep(100);
		EasyMock.verify(listener);
	}
	
	private void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
