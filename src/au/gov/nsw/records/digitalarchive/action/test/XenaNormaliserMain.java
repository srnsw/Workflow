package au.gov.nsw.records.digitalarchive.action.test;

import au.gov.nsw.records.digitalarchive.action.wrapper.XenaWrapper;

public class XenaNormaliserMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String file = "Test a hahahaha.docx";
		String file = "smartgit-win32.zip";
		//String file = "test.jpg";
		//String file = "test.bmp";
		
		XenaWrapper xw  = new XenaWrapper();
		xw.cratePerservedFile(file);
	}
}
