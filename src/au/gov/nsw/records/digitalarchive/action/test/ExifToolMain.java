package au.gov.nsw.records.digitalarchive.action.test;

import java.io.IOException;

import au.gov.nsw.records.digitalarchive.action.metadata.ExifMetadataAction;
import au.gov.nsw.records.digitalarchive.action.metadata.ExifOutputConverter;
import au.gov.nsw.records.digitalarchive.utils.CommandExecutor;

public class ExifToolMain {

	public static void main(String[] args){

		String file = "Series_001.jpg";
		//String file = "workflow.xml";
		
		try {
			ExifOutputConverter.convert(CommandExecutor.exec(new String[]{"exiftool", file}));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExifMetadataAction ea = new ExifMetadataAction();
		
		try {
			ea.createMeataDataFile(file, file + ".exif.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
