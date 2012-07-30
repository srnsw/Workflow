package au.gov.nsw.records.digitalarchive.action.preservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.action.wrapper.DroidConfig;
import au.gov.nsw.records.digitalarchive.action.wrapper.DroidWrapper;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.Entry;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationDelegator;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("preservation")
public class PreservationAction extends AbstractAction {

	
	private static final Log log = LogFactory.getLog(PreservationAction.class);

	@XStreamOmitField
	private Map<String, List<String>> preservationStrategy;
	
	@XStreamOmitField
	private static Map<String, PreservationPath> preservationClass = new HashMap<String, PreservationPath>();
	
	private WorkflowCache cache;
	
	private String path;
	
	private String purpose;
	
	private static DroidWrapper droid;
	@Override
	public void processAction() {

		if (path==null || path.isEmpty()){
			path = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		log.info(String.format("PreservationAction started "));
		
		for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
			String puid = ent.getString(Entry.PUID);
			if (!puid.isEmpty()){
				for (String pathway:preservationStrategy.get(puid)){
					PreservationPath preservator = getPreservationPath(pathway);
					if (preservator!=null){
						String inputFile = StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME));
						log.info(String.format("Preserving [%s]", inputFile));
						String newFileName = preservator.createPreservedFile(inputFile);
						if (newFileName!=null){
							log.info(String.format("created", newFileName));
							WorkflowNotificationDelegator.onRenditionFileCreated(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), path, newFileName, "");
						}else{
							log.warn(String.format("No file created for [%s]", inputFile));
						}
					}else{
						log.error(String.format("Unable to load preservation class [%s]", pathway));
					}
				}
			}else{
				log.warn(String.format("No PUID definition for [%s]", ent.getString(Entry.NAME)));
			}
		}
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				listener.onActionFinished(PreservationAction.this);
			}
		});
	}

	private PreservationPath getPreservationPath(String classname){
		PreservationPath path = preservationClass.get(classname);
		if (path==null){
			try{
				Class<?> clazz = Class.forName(classname);
				path = (PreservationPath) clazz.newInstance();
				preservationClass.put(classname, path);
			} catch (Exception e){
				log.error("Error during class deserilization", e);
				return null;
			}
		}
		return path;
	}
	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		
		if (purpose == null || purpose.isEmpty()){
			log.error("No purpose configured, ignoring preservation action");
			return;
		}
		
		this.cache = cache;
		preservationStrategy = new HashMap<String, List<String>>();
		
		ConfigDeserializer<PreservationConfig> configLoader = new ConfigDeserializer<PreservationConfig>();
		PreservationConfig templateConf = new PreservationConfig();
		PreservationConfig config = configLoader.load(templateConf, ConfigHelper.getPreservationConfig());
		
		try {

			if (droid==null){
				ConfigDeserializer<DroidConfig> droidConfigLoader = new ConfigDeserializer<DroidConfig>();
				DroidConfig droidTemplateConf = new DroidConfig("./data/DROID_SignatureFile_V45.xml");
				DroidConfig droidConf = droidConfigLoader.load(droidTemplateConf, ConfigHelper.getDroidConfig());
				droid = new DroidWrapper(droidConf.getSignatureFile());
			}

			for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
				if (ent.getString(Entry.PUID) == null || ent.getString(Entry.PUID).isEmpty()){
					IdentificationResult res;
					
					log.debug(String.format("Using [%s]/[%s] to produce [%s]", cache.getString(WorkflowCache.LOCATION), ent.getString(Entry.NAME), StringHelper.joinDirectoryString(cache.getString(WorkflowCache.LOCATION), ent.getString(Entry.NAME))));
					
					res = droid.getIdentification(StringHelper.joinDirectoryString(cache.getString(WorkflowCache.LOCATION), ent.getString(Entry.NAME)));
					
					if (res!=null && !res.getPuid().isEmpty()){
						ent.set(Entry.PUID, res.getPuid());
						ActiveRecordFactory.save(ent);
						log.info(String.format("Action [%s] identified format for [%s] as [%s] ", getName(), ent.getString(Entry.NAME), res.getPuid()));
					}else{
						log.warn(String.format("Action [%s] could not identify PUID for [%s]", getName(), ent.getString(Entry.NAME)));
					}
				}
				
				if (ent.getString(Entry.PUID) != null && !ent.getString(Entry.PUID).isEmpty()){
					if (!preservationStrategy.containsKey(ent.getString(Entry.PUID))){
						preservationStrategy.put(ent.getString(Entry.PUID), new ArrayList<String>());
					}
				}
			}
			
			for (String key:preservationStrategy.keySet()){
				List<String> preservationClasses = preservationStrategy.get(key);
				Client client = Client.create();
				
				String url = String.format("%s/lookup/%s/%s.json", config.getPreservationPath(), key, purpose);
				WebResource webResource = client.resource(url);

				ClientResponse response = webResource.get(ClientResponse.class);
				String resp = response.getEntity(String.class);

				log.info(String.format("Action [%s] processing [%s] of [%s]", getName(), resp, url));
				
				ObjectMapper mapper = new ObjectMapper();
				try{
					JsonNode actualObj = mapper.readTree(resp);
					log.debug("Got Node:" + actualObj.toString());
					JsonNode node = actualObj.get("pathways");
					if (node!=null){
						log.debug(String.format("Got [%d] Node: %s", node.size(), node.toString()));
						for (int i=0;i<node.size();i++){
							log.debug(String.format("Got no [%d] Node: %s", i, node.get(i).toString()));
							String pathWayClass = node.get(i).get("preservation_class").asText();
							log.info(String.format("Using PathwayClass [%s] for [%s]", pathWayClass, key));
							preservationClasses.add(pathWayClass);
						}
					}
				}catch (JsonParseException e){
					log.error(String.format("No pathway configured for [%s]", url));
				}
			}
		} catch (Exception e){
			log.error("Error during prepare", e);
		} 

	}
}
