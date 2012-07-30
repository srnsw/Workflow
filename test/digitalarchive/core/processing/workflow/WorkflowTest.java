package digitalarchive.core.processing.workflow;

import junit.framework.TestCase;

public class WorkflowTest extends TestCase {
//
//	private ActionSet startActionSet;
//	private ActionSet endActionSet;
//	private ActionSet startEndActionSet;
//	private ActionSet errorActionSet;
//	private ActionSet startErrorActionSet;
//	private WorkflowCache mockWorkflowCache;
//	
//	private static final int ACTION_ID = 1;
//    /**
//     * Sets up the test fixture.
//     * Called before every test case method.
//     */
//	@Before
//	protected void setUp() {
//		startActionSet = EasyMock.createMock(ActionSet.class);
//		endActionSet = EasyMock.createMock(ActionSet.class);
//		startEndActionSet = EasyMock.createMock(ActionSet.class);
//		errorActionSet = EasyMock.createMock(ActionSet.class);
//		startErrorActionSet = EasyMock.createMock(ActionSet.class);
//		mockWorkflowCache = EasyMock.createMock(WorkflowCache.class);
//		
//		EasyMock.expect(startActionSet.getState()).andReturn(ProcessingState.FINISHED).anyTimes();
//		EasyMock.expect(startActionSet.start()).andReturn(true).once();
//		
//		EasyMock.expect(startEndActionSet.getState()).andReturn(ProcessingState.FINISHED).anyTimes();
//		EasyMock.expect(startEndActionSet.start()).andReturn(true).once();
//		
//		EasyMock.expect(endActionSet.getState()).andReturn(ProcessingState.FINISHED).anyTimes();
//		EasyMock.expect(endActionSet.start()).andReturn(true).once();
//		
//		
//		//this actionset will return fail
//		EasyMock.expect(errorActionSet.getState()).andReturn(ProcessingState.ERROR).anyTimes();
//		errorActionSet.processAction(ACTION_ID, null);
//		EasyMock.expectLastCall().anyTimes();
//		EasyMock.expect(errorActionSet.start()).andReturn(false).once();
//		
//		//this actionset will return fail
//		EasyMock.expect(startErrorActionSet.getState()).andReturn(ProcessingState.ERROR).anyTimes();
//		startErrorActionSet.processAction(ACTION_ID, null);
//		EasyMock.expectLastCall().anyTimes();
//		EasyMock.expect(startErrorActionSet.start()).andReturn(false).once();
//	}
//	
//	/**
//     * Tears down the test fixture.
//     * Called after every test case method.
//     */
//	@After
//	protected void tearDown() {
//	}
//	
//	@Test
//	public void testMultiActionSet(){
//
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startActionSet);
//		wf.addActionSet(endActionSet);
//
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startActionSet.getNextActionSetName()).andReturn("next-action-set").anyTimes();
//		EasyMock.expect(startActionSet.getName()).andReturn("start").anyTimes();
//		
//		EasyMock.expect(endActionSet.getNextActionSetName()).andReturn("end").anyTimes();
//		EasyMock.expect(endActionSet.getErrorActionSetName()).andReturn("true").anyTimes();
//		EasyMock.expect(endActionSet.getName()).andReturn("next-action-set").anyTimes();
//		
//		mockListener.onActionSetFinished(startActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onActionSetFinished(endActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onWorkflowFinished(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startActionSet, endActionSet, mockListener);
//		
//		assertTrue(wf.start());
//		assertEquals(ProcessingState.PROCESSING, wf.getState());
//		wf.onActionSetFinished(startActionSet);
//		wf.onActionSetFinished(endActionSet);
//		assertEquals(ProcessingState.FINISHED, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(startActionSet, endActionSet, mockListener);
//	}
//	
//	@Test
//	public void testSingleActionSet(){
//		
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startEndActionSet);
//		
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startEndActionSet.getNextActionSetName()).andReturn("end").anyTimes();
//		EasyMock.expect(startEndActionSet.getName()).andReturn("start").anyTimes();
//		
//		mockListener.onActionSetFinished(startEndActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onWorkflowFinished(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startEndActionSet, mockListener);
//		
//		assertTrue(wf.start());
//		assertEquals(ProcessingState.PROCESSING, wf.getState());
//		wf.onActionSetFinished(startEndActionSet);
//		assertEquals(ProcessingState.FINISHED, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(startEndActionSet, mockListener);
//	}
//	
//	@Test
//	public void testNoStartActionSet(){
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startActionSet);
//		wf.addActionSet(endActionSet);
//
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startActionSet.getErrorActionSetName()).andReturn("error-handle-set").anyTimes();
//		EasyMock.expect(startActionSet.getName()).andReturn("invalid-start").anyTimes();
//		
//		EasyMock.expect(endActionSet.getNextActionSetName()).andReturn("end").anyTimes();
//		EasyMock.expect(endActionSet.getName()).andReturn("error-handle-set").anyTimes();
//		
//		mockListener.onWorkflowError(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startActionSet, endActionSet, mockListener);
//		
//		assertFalse(wf.start());
//		assertEquals(ProcessingState.ERROR, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(mockListener);
//	}
//	
//	@Test
//	public void testNoEndActionSet(){
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startActionSet);
//		wf.addActionSet(endActionSet);
//
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startActionSet.getNextActionSetName()).andReturn("next-set").anyTimes();
//		EasyMock.expect(startActionSet.getName()).andReturn("start").anyTimes();
//		
//		EasyMock.expect(endActionSet.getNextActionSetName()).andReturn("unkonow").anyTimes();
//		EasyMock.expect(endActionSet.getName()).andReturn("next-set").anyTimes();
//		
//		mockListener.onActionSetFinished(startActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onActionSetFinished(endActionSet);
//		EasyMock.expectLastCall().once();
//		
//		mockListener.onWorkflowError(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startActionSet, endActionSet, mockListener);
//		
//		assertTrue(wf.start());
//		wf.onActionSetFinished(startActionSet);
//		wf.onActionSetFinished(endActionSet);
//		assertEquals(ProcessingState.ERROR, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(mockListener);
//	}
//	
//	@Test
//	public void testMultiActionSetFail(){
//
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startActionSet);
//		wf.addActionSet(endActionSet);
//
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startActionSet.getErrorActionSetName()).andReturn("error-handle-set").anyTimes();
//		EasyMock.expect(startActionSet.getName()).andReturn("start").anyTimes();
//		
//		EasyMock.expect(endActionSet.getNextActionSetName()).andReturn("end").anyTimes();
//		EasyMock.expect(endActionSet.getName()).andReturn("error-handle-set").anyTimes();
//		
//		mockListener.onActionSetError(startActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onActionSetFinished(endActionSet);
//		EasyMock.expectLastCall().once();
//		
//		mockListener.onWorkflowFinished(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startActionSet, endActionSet, mockListener);
//		
//		assertTrue(wf.start());
//		assertEquals(ProcessingState.PROCESSING, wf.getState());
//		wf.onActionSetError(startActionSet);
//		assertEquals(ProcessingState.PROCESSING, wf.getState());
//		wf.onActionSetFinished(endActionSet);
//		assertEquals(ProcessingState.FINISHED, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(startActionSet, endActionSet, mockListener);
//	}
//	
//	@Test
//	public void testSingleActionSetFail(){
//		Workflow wf = new Workflow("test");
//		wf.prepare(mockWorkflowCache);
//		wf.addActionSet(startEndActionSet);
//		
//		WorkflowListener mockListener = EasyMock.createMock(WorkflowListener.class);
//		wf.setListener(mockListener);
//		
//		EasyMock.expect(startEndActionSet.getErrorActionSetName()).andReturn("end").anyTimes();
//		EasyMock.expect(startEndActionSet.getName()).andReturn("start").anyTimes();
//		
//		mockListener.onActionSetError(startEndActionSet);
//		EasyMock.expectLastCall().once();
//		mockListener.onWorkflowFinished(wf);
//		EasyMock.expectLastCall().once();
//		
//		EasyMock.replay(startEndActionSet, mockListener);
//		
//		assertTrue(wf.start());
//		assertEquals(ProcessingState.PROCESSING, wf.getState());
//		wf.onActionSetError(startEndActionSet);
//		assertEquals(ProcessingState.FINISHED, wf.getState());
//		
//		sleep(100);
//		EasyMock.verify(startEndActionSet, mockListener);
//	}
//
//	private void sleep(int time){
//		try {
//			Thread.sleep(time);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
