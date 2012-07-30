package au.gov.nsw.records.digitalarchive.action.repository;

import java.io.File;
import java.util.regex.Pattern;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import au.gov.nsw.records.digitalarchive.action.repository.message.RepositoryResponseMessage;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/repository")
public class RepositoryClientResource {

	private static ObjectMapper mapper = new ObjectMapper();
	private static RepositoryClientConfig config;
	private static ModeShapeClientWrapper repoClient;
	private static final Log log = LogFactory.getLog(RepositoryClientResource.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addFile(@QueryParam("filepath") String filepath, @QueryParam("callback") String callbackURL,
			@QueryParam("actionid") int actionId, @QueryParam("workflowid") int workflowId, @QueryParam("actionsetid") int actionsetId){
		
		if (config==null){
			ConfigDeserializer<RepositoryClientConfig> configLoader = new ConfigDeserializer<RepositoryClientConfig>();
			RepositoryClientConfig templateConf = new RepositoryClientConfig("localhost", 8080, "http://repohost:8080", "user", "pwd", "DigitalArchives", "DAWorkspace", "\\staging");
			config = configLoader.load(templateConf, ConfigHelper.getRepositoryConfig());
		}
		
		// use ModeShape REST client API
		if (repoClient == null){
			repoClient = new ModeShapeClientWrapper(config);
		}
		try {
			RepositoryResponseMessage response = new RepositoryResponseMessage();
			
			// the destination location should not include the specific path i.e. /staging/AgencyAbc, the /staging/ should be removed.
			String destPath = filepath;
			for (String prefix : config.getRemovePathPrefix()){
				destPath = destPath.replaceFirst(Pattern.quote(prefix.trim()), "");
			}
			destPath = destPath.replaceAll(Pattern.quote("\\"), "/");
			log.info(String.format("Depositing [%s] to [%s]", filepath, destPath));
			File inputDir = new File(filepath);
			for (File f:inputDir.listFiles()){
				if (!repoClient.addFile(f, destPath)){
					response.setError(true);
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
				}
			}
			
			if (callbackURL!=null && !callbackURL.isEmpty()){
				// fire callback to the workflow
				Client client = Client.create();
				WebResource webResource = client.resource(callbackURL);
				
				String responseString = mapper.writeValueAsString(response);
				MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
				queryParams.add("payload", responseString);
				queryParams.add("actionid", String.valueOf(actionId));
				queryParams.add("workflowid", String.valueOf(workflowId));
				queryParams.add("actionsetid", String.valueOf(actionsetId));
			
				webResource.queryParams(queryParams).get(String.class);
			}
			return "{}";
		} catch (Exception e) {
			log.error("Exception caught", e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}
