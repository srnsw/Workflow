package au.gov.nsw.records.digitalarchive.action.test;

import java.io.IOException;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;

import au.gov.nsw.records.digitalarchive.action.identification.DroidFileIdentificationAction;

public class DroidIdentificationMain {

	public static void main(String[] args) throws IOException{
		
		DroidFileIdentificationAction da = new DroidFileIdentificationAction("./assets/DROID_SignatureFile_V59.xml");
		
		//IdentificationResult ir = da.getFileIdentification("Test Log Scenario 2 Network drive.xls");
		IdentificationResult ir = da.getFileIdentification("test.psd");
		String file = "Test Log Scenario 2 Network drive.xls";
		System.out.println(ir.getMimeType());
		System.out.println(ir.getName());
		System.out.println(ir.getPuid());
		System.out.println(ir.getVersion());
		System.out.println(ir.getMethod());
	
		da.createFileIdentificationFile(file, file + ".id.droid.xml");
	}
	
	
}
