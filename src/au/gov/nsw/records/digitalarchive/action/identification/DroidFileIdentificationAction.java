package au.gov.nsw.records.digitalarchive.action.identification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.action.wrapper.DroidConfig;
import au.gov.nsw.records.digitalarchive.action.wrapper.DroidWrapper;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.Entry;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.output.OutputWriter;
import au.gov.nsw.records.digitalarchive.output.XMLFileWriter;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The file format identificator action using DROID library
 * @author wisanup
 * @see <a href="http://sourceforge.net/projects/droid/">DROID website</a>
 *
 */
@XStreamAlias("droidIdentification")
public class DroidFileIdentificationAction extends AbstractAction{

	private static final Log log = LogFactory.getLog(DroidFileIdentificationAction.class);
	private WorkflowCache cache;
	@XStreamOmitField
	private DroidWrapper droid;
	private String path;
	
	public DroidFileIdentificationAction(String signatureFile){
		droid = new DroidWrapper(signatureFile);
	}
	
	@Override
	public void processAction() {
		
		if (path==null || path.isEmpty()){
			path = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		try {
			for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
				createFileIdentificationFile(StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME)), StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME))  + ".format.droid.xml");
				log.info(String.format("Identification file created for [%s]", path + ent.getString(Entry.NAME)));
			}
			log.info(String.format("All identification files created for [%s]", workflowName));
		} catch (final Exception e) {
			log.error("Encountered an error while creating identification files",e);
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionError(DroidFileIdentificationAction.this, e.getMessage());
				}
			});
			return;
		}
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
				listener.onActionFinished(DroidFileIdentificationAction.this);
			}
		});
	}
	
	/**
	 * Produce the format identification files of the given input files.
	 * The output file name will be automatically generated by {originalname}.format.droid.xml 
	 * @param files the files to be processed for file format identification
	 * @throws IOException
	 */
	public void createFileIdentificationFiles(List<String> files) throws IOException{
		for (String file: files){
			createFileIdentificationFile(file, file + ".format.droid.xml");
			log.info(String.format("Identification file created for [%s]", file));
		}
	}
	
	/**
	 * Produce the format identification file of the given input file.
	 * @param sourceFile the file to be processed for file format identification
	 * @param outputFile the file to be created 
	 * @throws IOException
	 */
	public void createFileIdentificationFile(String sourceFile, String outputFile) throws IOException{

		IdentificationResult res = getFileIdentification(sourceFile);
		if (res!=null){
			Map<String, String> valueMap = new HashMap<String, String>();
			valueMap.put("Format-name", res.getName());
			valueMap.put("Content-Type", res.getMimeType());
			valueMap.put("PUID", res.getPuid());
			valueMap.put("Identification-Method", res.getMethod().toString());
			
			OutputWriter ow = new XMLFileWriter();
			ow.writeMetadata(valueMap, "droid", "", outputFile);
		}else{
			log.warn(String.format("Unable to identify [%s]", sourceFile));
		}
	}
	
	/**
	 * Get the file identification result of the given file
	 * @param filePath the file to be processed for file format identification 
	 * @return the identification result
	 * @throws IOException
	 */
	public IdentificationResult getFileIdentification(String filePath) throws IOException{
		return droid.getIdentification(filePath);
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		this.cache = cache;
	
		ConfigDeserializer<DroidConfig> configLoader = new ConfigDeserializer<DroidConfig>();
		DroidConfig templateConf = new DroidConfig("./data/DROID_SignatureFile_V45.xml");
		DroidConfig config = configLoader.load(templateConf, ConfigHelper.getDroidConfig());
		
		droid = new DroidWrapper(config.getSignatureFile());
	}
}
