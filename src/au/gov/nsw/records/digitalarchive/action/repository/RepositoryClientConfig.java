package au.gov.nsw.records.digitalarchive.action.repository;

import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("repository")
public class RepositoryClientConfig {

	private String host;
	private int port;
	
	private String repositoryURL;
	private String repositoryUser;
	private String repositoryPassword;
	private String repositoryName;
	private String workspaceName;
	private String removePathPrefix;
	
	public RepositoryClientConfig(String host, int port, String repositoryURL,
			String repositoryUser, String repositoryPassword,
			String repositoryName, String workspaceName,
			String removePathPrefix) {
		super();
		this.host = host;
		this.port = port;
		this.repositoryURL = repositoryURL;
		this.repositoryUser = repositoryUser;
		this.repositoryPassword = repositoryPassword;
		this.repositoryName = repositoryName;
		this.workspaceName = workspaceName;
		this.removePathPrefix = removePathPrefix;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
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
	public List<String> getRemovePathPrefix() {
		return Arrays.asList(removePathPrefix.split(",")) ;
	}
	public void setRemovePathPrefix(String removePathPrefix) {
		this.removePathPrefix = removePathPrefix;
	}
}
