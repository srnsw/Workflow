package au.gov.nsw.records.digitalarchive.action.test;

import java.io.IOException;

import au.gov.nsw.records.digitalarchive.action.metadata.TikaMetadataAction;

public class TikaMetadataMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName = "sample.pdf";

		TikaMetadataAction ta = new TikaMetadataAction();
		ta.createMeataDataFile(fileName, fileName + ".tika.xml");
	}

}
