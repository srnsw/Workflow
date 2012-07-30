package au.gov.nsw.records.digitalarchive.action.fileop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.JerseyClientHelper;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class IsilonFileOperations {
	
	private static final Log log = LogFactory.getLog(IsilonFileOperations.class);
	private static IsilonActionConfig config;
	
	private WebResource webResource;

	public IsilonFileOperations(){
		if (config==null){
			ConfigDeserializer<IsilonActionConfig> configLoader = new ConfigDeserializer<IsilonActionConfig>();
			IsilonActionConfig templateConf = new IsilonActionConfig("isilonHost", 9443, "user", "passwd");
			config = configLoader.load(templateConf, ConfigHelper.getIsilonConfig());
		}
	}
	
	private Client createClient(){
		Client client =  JerseyClientHelper.createClient();
		client.setConnectTimeout(0);
		client.addFilter(new HTTPBasicAuthFilter(config.getUser(), config.getPassword()));
		return client;
	}
	/**
	 * Tell Isilon to move the contents in source directory into the destination directory
	 * @param sourceDir the source directory
	 * @param destDir the destination directory
	 * @return true if the operation was success, false otherwise
	 */
	public boolean move(String sourceDir, String destDir){
		
		// sine we are unable to create remote directory recursively with 'COPY' command..
		createDirectory(destDir);
		
		// both source dir and dest dir must end with '/'
		sourceDir = StringHelper.formatIsilonDirectoryString(sourceDir);
		destDir = StringHelper.formatIsilonDirectoryString(destDir);
		
		log.info(String.format("Moving [%s] to [%s]", sourceDir, destDir));
		
		String url = String.format("https://%s:%d/1%s", config.getHost(), config.getPort(), sourceDir);
		webResource = createClient().resource(url);
		log.info(String.format("Executing POST [%s] with [%s]", url, destDir));
		// issue POST for directory moving
		try {
			ClientResponse response = webResource.header("x-isi-set-location", destDir).post(ClientResponse.class);
			
			if (response.getClientResponseStatus() == ClientResponse.Status.CREATED){
				log.info(String.format("Directory moved to [%s]", destDir));
				return true;
			}else{
				log.error(String.format("Failed to move, got HTTP status: %s ", response.getClientResponseStatus().toString() ));
				log.error(String.format("Isilon Response: [%s]", response.getEntity(String.class)));
			}
	
		} catch (UniformInterfaceException e) {
			log.error("Error while trying to move file", e);
		} 
		
		return false;
	}
	

	/**
	 * Delete the path and containing files in Isilon system 
	 * @param path the desired path
	 * @return true if the file was deleted, false otherwise
	 */
	public boolean delete(String path){
		
		path = StringHelper.formatIsilonDirectoryString(path);
		
		log.info(String.format("Deleting [%s]", path));
		
		String url = String.format("https://%s:%d/1%s?recursive=true", config.getHost(), config.getPort(), path);
		// specify "recursive=true" to recursively delete sub-directory
		webResource = createClient().resource(url);

		log.info(String.format("Executing DELETE [%s]", url));
		// issue DELETE for directory deleting
		final ClientResponse response = webResource.delete(ClientResponse.class);

		if (response.getClientResponseStatus() == ClientResponse.Status.NO_CONTENT){
			log.info(String.format("Deleted [%s] ", path));
			return true;
		}else{
			log.error(String.format("Failed to delete, got HTTP status: %s ", response.getClientResponseStatus().toString() ));
			log.error(String.format("Isilon Response: [%s]", response.getEntity(String.class)));
		}
		return false;
	}

	/**
	 * Tell Isilon to copy the contents in source directory into the destination directory
	 * @param sourceDir the source directory
	 * @param destDir the destination directory
	 * @return true if the copy operation was successful, false otherwise
	 */
	public boolean copy(String sourceDir, String destDir){
		
		// sine we are unable to create remote directory recursively with 'COPY' command..
		createDirectory(destDir);
		
		sourceDir = StringHelper.formatIsilonDirectoryString(sourceDir);
		destDir = StringHelper.formatIsilonDirectoryString(destDir);
		
		log.info(String.format("Coping [%s] to [%s]", sourceDir, destDir));
		
		String url = String.format("https://%s:%d/1%s", config.getHost(), config.getPort(), destDir);
		webResource = createClient().resource(url);
		log.info(String.format("Executing PUT [%s]", url));
		// issue PUT for directory copying
		final ClientResponse response = webResource.header("x-isi-copy-source", sourceDir).put(ClientResponse.class);
		
		if (response.getClientResponseStatus() == ClientResponse.Status.CREATED){
			log.info(String.format("Copied to [%s] ", destDir));
			return true;
		}else{
			log.error(String.format("Failed to copy, got HTTP status: %s ", response.getClientResponseStatus().toString() ));
			log.error(String.format("Isilon Response: [%s]", response.getEntity(String.class)));
		}
		
		return false;
	}
	

	public void createDirectory(String path){
		path = StringHelper.formatIsilonDirectoryString(path);
		
		log.info(String.format("Creating [%s] ", path));
		
		String url = String.format("https://%s:%d/1%s?recursive=true", config.getHost(), config.getPort(), path);
		webResource = createClient().resource(url);
		log.info(String.format("Executing PUT [%s]", url));
		// issue PUT for directory copying
		final ClientResponse response = webResource.put(ClientResponse.class);
		
		if (response.getClientResponseStatus() == ClientResponse.Status.CREATED || response.getClientResponseStatus() == ClientResponse.Status.OK ){
			log.info(String.format("Created or already exist [%s] - HTTP Status [%s]", path, response.getClientResponseStatus().toString()));
		}else{
			log.warn(String.format("Failed to create, got HTTP status: %s ", response.getClientResponseStatus().toString() ));
			log.warn(String.format("Isilon Response: [%s]", response.getEntity(String.class)));
		}
	}
}
