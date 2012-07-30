package au.gov.nsw.records.digitalarchive.action.fileop;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration related to Isilon's setting
 * @author wisanup
 *
 */
@XStreamAlias("isilon")
public class IsilonActionConfig {

	private String host;
	private int port;
	private String user;
	private String password;
	
	/**
	 * Constructs the new instance of {@code IsilonActionConfig} class
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 */
	public IsilonActionConfig(String host, int port, String user,
			String password) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Get the host name or IP address of Isilon machine
	 * @return the host name or IP address of Isilon machine
	 */
	public String getHost() {
		return host;
	}
	/**
	 * Set the host name or IP address of Isilon machine
	 * @param host the host name or IP address of Isilon machine
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Set the port number of Isilon machine
	 * @return the port number of Isilon machine
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set the port number of Isilon machine
	 * @param port the port number of Isilon machine
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Get the user name that assigned to the wheel group in Isilon
	 * @return the user name that assigned to the wheel group in Isilon
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Set the user name that assigned to the wheel group in Isilon
	 * @param user the user name that assigned to the wheel group in Isilon
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * Get the password associated with the user
	 * @return the password associated with the user
	 * @see #setUser(String)
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set the password associated with the user
	 * @param password the password associated with the user
	 * @see #setUser(String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
