package au.gov.nsw.records.digitalarchive.action.qurantine;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration related to ClamAV setting
 * @author wisanup
 * @see <a href="http://www.clamav.net/lang/en/">ClamAV website</a>
 */
@XStreamAlias("clamav")
public class ClamAVActionConfig {

	private String clamdhost;
	private int clamdport;
	
	/**
	 * Construct an instance of this class
	 * @param clamdhost
	 * @param clamdport
	 */
	public ClamAVActionConfig(String clamdhost, int clamdport) {
		this.clamdhost = clamdhost;
		this.clamdport = clamdport;
	}
	
	/**
	 * Get the host name or IP address of ClamAV daemon
	 * @return the host name or IP address of ClamAV daemon
	 */
	public String getClamdhost() {
		return clamdhost;
	}
	
	/**
	 * Set the host name or IP address of ClamAV daemon
	 * @param clamdhost the host name or IP address of ClamAV daemon
	 */
	public void setClamdhost(String clamdhost) {
		this.clamdhost = clamdhost;
	}
	
	/**
	 * Get the port number of ClamAV daemon
	 * @return the port number of ClamAV daemon
	 */
	public int getClamdport() {
		return clamdport;
	}
	
	/**
	 * Set the port number of ClamAV daemon
	 * @param clamdport the port number of ClamAV daemon
	 */
	public void setClamdport(int clamdport) {
		this.clamdport = clamdport;
	}

}
