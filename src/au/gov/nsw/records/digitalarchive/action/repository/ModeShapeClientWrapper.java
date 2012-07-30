package au.gov.nsw.records.digitalarchive.action.repository;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modeshape.web.jcr.rest.client.IRestClient;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.domain.Repository;
import org.modeshape.web.jcr.rest.client.domain.Server;
import org.modeshape.web.jcr.rest.client.domain.Workspace;
import org.modeshape.web.jcr.rest.client.json.JsonRestClient;

public class ModeShapeClientWrapper {
	
	private static final Log log = LogFactory.getLog(ModeShapeClientWrapper.class);
	
	private Server server;
	private Repository repository;
	private Workspace workspace;
	
	public ModeShapeClientWrapper(RepositoryClientConfig config){
	
		server = new Server(config.getRepositoryURL(), config.getRepositoryUser(), config.getRepositoryPassword());
		repository = new Repository(config.getRepositoryName(), server);
		workspace = new Workspace(config.getWorkspaceName(), repository);
	}

	public boolean addDirectory(String directory, String destinationDir){

		File dir = new File(directory);
		IRestClient restClient = new JsonRestClient();
		
		Status status = restClient.publish(workspace, destinationDir, dir);
		if (status.isError()) {
			log.error(String.format("Error while adding [%s] - [%s]", directory, status.getMessage()));
			return false;
		}
		log.info(String.format("Added dir [%s] to [%s]", dir, destinationDir));
		return true;
	}
	
	public boolean addFile(File file, String storePath){
		IRestClient restClient = new JsonRestClient();
		// Publish
		Status status = restClient.publish(workspace, storePath, file);
		if (status.isError()) {
			log.error("Error while adding files:" + status.getMessage());
			return false;
		}
		log.info(String.format("Added file [%s] to [%s]", file.getName(), storePath));
		return true;
	}
}
