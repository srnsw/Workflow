package au.gov.nsw.records.digitalarchive.action.preservation;

import java.util.List;

public class BinaryPreservationPath implements PreservationPath {

	@Override
	public boolean createPreservedFiles(List<String> inputFiles) {
		// this is basically do nothing
		return true;
	}

	@Override
	public String createPreservedFile(String inputFile) {
		// this is basically do nothing
		return null;
	}

}
