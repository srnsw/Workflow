package au.gov.nsw.records.digitalarchive.action.preservation;

import java.util.List;

public interface PreservationPath {

	/**
	 * Create the preserved files of the given files 
	 * @param files the files to be preserved
	 * @return true if successfully created the preserved format, false otherwise
	 */
	public boolean createPreservedFiles(List<String> inputFiles);
	
	/**
	 * Create the preserved file of the given file 
	 * @param files the files to be preserved
	 * @return the created file if successful, null if failed to create the preserved format file
	 */
	public String createPreservedFile(String inputFile);
	
}
