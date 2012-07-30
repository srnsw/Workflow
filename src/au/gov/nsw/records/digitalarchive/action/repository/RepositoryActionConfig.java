package au.gov.nsw.records.digitalarchive.action.repository;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("repository")
public class RepositoryActionConfig {

	private String callbackURL;
	
	private String host;
	private String port;
	private String repositoryURL;
	private String repositoryUser;
	private String repositoryPassword;
	private String repositoryName;
	private String workspaceName;
	private String removePathPrefix;
	  
	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getRepositoryURL() {
		return repositoryURL;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public String getRepositoryUser() {
		return repositoryUser;
	}

	public void setRepositoryUser(String repositoryUser) {
		this.repositoryUser = repositoryUser;
	}

	public String getRepositoryPassword() {
		return repositoryPassword;
	}

	public void setRepositoryPassword(String repositoryPassword) {
		this.repositoryPassword = repositoryPassword;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	public String getRemovePathPrefix() {
		return removePathPrefix;
	}

	public void setRemovePathPrefix(String removePathPrefix) {
		this.removePathPrefix = removePathPrefix;
	}
	
}
