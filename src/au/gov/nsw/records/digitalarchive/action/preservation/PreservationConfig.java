package au.gov.nsw.records.digitalarchive.action.preservation;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("preservation")
public class PreservationConfig {

	private String preservationPath;

	public String getPreservationPath() {
		return preservationPath;
	}
}
