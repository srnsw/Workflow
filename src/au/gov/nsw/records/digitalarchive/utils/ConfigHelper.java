package au.gov.nsw.records.digitalarchive.utils;

public class ConfigHelper {

	public static String getApplicationConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.CONF_FILE;
	}
	
	public static String getClamAVConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.CLAMAV_CONF_FILE;
	}
	
	public static String getDroidConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.DROID_CONF_FILE;
	}
	
	public static String getPreservationConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.PRESERVATION_CONF_FILE;
	}
	
	public static String getIsilonConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.ISILON_CONF_FILE;
	}
	
	public static String getMailNotificationConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.MAIL_NOTIFICATION_CONF_FILE;
	}
	
	public static String getRepositoryConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.REPO_CONF_FILE;
	}
	
	public static String getXenaConfig(){
		return DAConstants.CONFIG_PATH + "/" + DAConstants.XENA_CONF_FILE;
	}

}
