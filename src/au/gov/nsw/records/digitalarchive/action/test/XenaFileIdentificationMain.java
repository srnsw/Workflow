package au.gov.nsw.records.digitalarchive.action.test;

import java.io.IOException;

import au.gov.nsw.records.digitalarchive.action.identification.XenaFileIdentificationAction;

public class XenaFileIdentificationMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		XenaFileIdentificationAction xa = new XenaFileIdentificationAction();
		//String file = "Test Log Scenario 2 Network drive.xls";
		String file = "Series_001.jpg";
		
		xa.createFileIdentificationFile(file, file + ".id.xena.xml");
	}

}
