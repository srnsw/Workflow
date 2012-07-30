package au.gov.nsw.records.digitalarchive.action.test;

import java.io.IOException;

import au.gov.nsw.records.digitalarchive.action.identification.TikaFileIdentificationAction;

public class TikaFileIdentificationMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String file = "Series_001.jpg";
		TikaFileIdentificationAction ta = new TikaFileIdentificationAction();
		ta.createFileIdentificationFile(file, file + ".id.tika.xml");
	}

}
