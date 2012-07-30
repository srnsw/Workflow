package au.gov.nsw.records.digitalarchive.utils;

import org.javalite.instrumentation.Instrumentation;

public class ActiveJDBCInstrumentationHelper {

	public static void main(String[] args) {
		try {
			System.out.println("Using outputdir:" + ClassLoader.getSystemResource("").getPath());
			Instrumentation instrumentation = new Instrumentation(); 
			instrumentation.setOutputDirectory(ClassLoader.getSystemResource("").getPath());
			instrumentation.instrument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
