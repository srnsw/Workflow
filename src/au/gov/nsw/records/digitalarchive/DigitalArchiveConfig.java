package au.gov.nsw.records.digitalarchive;

import java.util.ArrayList;
import java.util.List;

import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationListener;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("da")
public class DigitalArchiveConfig {
	
	private int listenPort;
	private String listenHost;
	private String dbuser;
	private String dbpassword;
	private String dbhost;
	private int dbport;
	private String dbname;
	private List<WorkflowNotificationListener> notificationListeners;
	private List<Action> actions = new ArrayList<Action>();
	
	public DigitalArchiveConfig(int port, String host, String dbuser,
			String dbpassword, String dbhost, int dbport, String dbname,
			List<WorkflowNotificationListener> notificationAdapter) {
		super();
		this.listenPort = port;
		this.listenHost = host;
		this.dbuser = dbuser;
		this.dbpassword = dbpassword;
		this.dbhost = dbhost;
		this.dbport = dbport;
		this.dbname = dbname;
		this.notificationListeners = notificationAdapter;
	}

	public String getDbuser() {
		return dbuser;
	}

	public String getDbpassword() {
		return dbpassword;
	}

	public String getDbhost() {
		return dbhost;
	}

	public int getDbport() {
		return dbport;
	}

	public String getDbname() {
		return dbname;
	}

	public List<WorkflowNotificationListener> getNotificationListeners() {
		return notificationListeners;
	}
	
	public int getListenPort() {
		return listenPort;
	}
	
	public String getListenHost() {
		return listenHost;
	}
	
	public String getUser() {
		return dbuser;
	}
	
	public String getPassword() {
		return dbpassword;
	}
	
	public List<Action> getActions() {
		return actions;
	}
}
