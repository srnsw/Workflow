package au.gov.nsw.records.digitalarchive.action.notification;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration related to SMTP mail server setting
 * @author wisanup
 * @see EmailNotificationAction
 *
 */
@XStreamAlias("emailnotification")
public class EmailNotificationActionConfig {

	public EmailNotificationActionConfig(String mailHost,
			String mailUser, String mailPassword, int mailPort) {
		super();
		this.mailHost = mailHost;
		this.mailUser = mailUser;
		this.mailPassword = mailPassword;
		this.mailPort = mailPort;
	}
	
	private String mailHost;
	private String mailUser;
	private String mailPassword;
	private int mailPort;

	/**
	 * Get the host name of the SMTP mail server 
	 * @return the host name of the SMTP mail server
	 */
	public String getMailHost() {
		return mailHost;
	}
	
	/**
	 * Set the host name of the SMTP mail server 
	 * @param mailHost the host name of the SMTP mail server
	 */
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}
	
	/**
	 * Get the name of the user to send an email
	 * @return the name of the user to send an email
	 */
	public String getMailUser() {
		return mailUser;
	}
	
	/**
	 * Set the name of the user to send an email
	 * @param mailUser the name of the user to send an email
	 */
	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}
	
	/**
	 * Get the password of the user to authenticate at SMTP server
	 * @return the password of the user to authenticate at SMTP server 
	 */
	public String getMailPassword() {
		return mailPassword;
	}
	
	/**
	 * Set the password of the user to authenticate at SMTP server
	 * @param mailPassword the password of the user to authenticate at SMTP server
	 */
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	
	/**
	 * Get the port number of the SMTP mail server
	 * @return the port number of the SMTP mail server
	 */
	public int getMailPort() {
		return mailPort;
	}
	
	/**
	 * Set the port number of the SMTP mail server
	 * @param mailPort the port number of the SMTP mail server
	 */ 
	public void setMailPort(int mailPort) {
		this.mailPort = mailPort;
	}
	
}
