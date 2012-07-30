package au.gov.nsw.records.digitalarchive.output;

import java.util.Map;

public interface OutputWriter {

	public void writeMetadata(Map<String, String> values, String writerName, String writerVersion, String outputFile);
}
