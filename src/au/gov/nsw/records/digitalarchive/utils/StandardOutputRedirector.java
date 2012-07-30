package au.gov.nsw.records.digitalarchive.utils;

import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StandardOutputRedirector {

	private static final Log log = LogFactory.getLog(StandardOutputRedirector.class);
	
    public static void tieSystemOutAndErrToLog() {
       // System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                log.info(string);
            }
        };
    }
}
