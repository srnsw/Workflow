package au.gov.nsw.records.digitalarchive.action.wrapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration related to Droid's setting
 * @author wisanup
 * @see <a href="http://sourceforge.net/projects/droid/">DROID website</a>
 */
@XStreamAlias("droid")
public class DroidConfig {

	private String signature_file;

	/**
	 * Create an instance of this class
	 * @param signature
	 */
	public DroidConfig(String signature) {
		super();
		this.signature_file = signature;
	}

	/**
	 * Get the location of Drois's signature file
	 * @return the location of Drois's signature file
	 */
	public String getSignatureFile() {
		return signature_file;
	}

	/**
	 * Set the location of Drois's signature file
	 * @param signature_file the location of Drois's signature file
	 */
	public void setSignatureFile(String signature_file) {
		this.signature_file = signature_file;
	}	
	
}
