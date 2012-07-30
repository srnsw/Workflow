package au.gov.nsw.records.digitalarchive.utils;


public class DAConstants {

	/**
	 * Configuration file name of Digital Archive application
	 */
	public static final String CONF_FILE = "config.xml";
	
	/**
	 * ClamAVAction configuration file
	 */
	public static final String CLAMAV_CONF_FILE = "clamav_config.xml";
	
	/**
	 * Droid's Action configuration file
	 */
	public static final String DROID_CONF_FILE = "droid_config.xml";
	
	/**
	 * Preseration Action configuration file
	 */
	public static final String PRESERVATION_CONF_FILE = "preservation_config.xml";
	/**
	 * Isilon Action configuration file
	 */
	public static final String ISILON_CONF_FILE = "isilon_config.xml";
	
	/**
	 * Mail Notification Action configuration file
	 */
	public static final String MAIL_NOTIFICATION_CONF_FILE = "emailnotification_config.xml";
	
	/**
	 * Repository Action configuration file
	 */
	public static final String REPO_CONF_FILE = "repository_config.xml";
	
	/**
	 * XENA Action configuration file
	 */
	public static final String XENA_CONF_FILE = "xena_config.xml";
	
	/**
	 * Configuration file path 
	 */
	public static final String CONFIG_PATH = "config";
	
	public static final String XML_FILE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<%s>\n" +
			"%s" +
			"</%s>";
	
	public static final String XML_ELEMENT = " <%s>%s</%s>\n";
	
}
