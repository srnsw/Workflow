package au.gov.nsw.records.digitalarchive.action.repository.message;

import java.util.Map;

public class RepositoryResponseMessage {

	private Map<String, String> repositoryResult;
	private boolean isError;
	
	public Map<String, String> getRepositoryResult() {
		return repositoryResult;
	}
	
	public void setRepositoryResult(Map<String, String> repositoryResult) {
		this.repositoryResult = repositoryResult;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public void setError(boolean isError) {
		this.isError = isError;
	}
	
}
