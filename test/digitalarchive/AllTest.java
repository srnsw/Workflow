package digitalarchive;

import junit.framework.Test;
import junit.framework.TestSuite;
import digitalarchive.core.processing.actionset.ActionSetTest;
import digitalarchive.core.processing.workflow.WorkflowTest;

public class AllTest {
	
	    public static Test suite() {

	        TestSuite suite = new TestSuite();
	  
	        suite.addTestSuite(ActionSetTest.class);
	        suite.addTestSuite(WorkflowTest.class);
	        
	        // Another example test suite of tests.
	        //suite.addTest(CreditCardTestSuite.suite());

	        return suite;
	    }

	    /**
	     * Runs the test suite using the textual runner.
	     */
	    public static void main(String[] args) {
	        junit.textui.TestRunner.run(suite());
	    }
	
}
