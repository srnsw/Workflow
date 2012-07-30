package au.gov.nsw.records.digitalarchive.action.metadata;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.action.wrapper.TikaWrapper;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.Entry;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationDelegator;
import au.gov.nsw.records.digitalarchive.output.OutputWriter;
import au.gov.nsw.records.digitalarchive.output.XMLFileWriter;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The metadata extraction action implementing on top of Apache Tika library
 * @author wisanup
 * @see <a href="http://tika.apache.org/">Apache Tika website</a>
 */
@XStreamAlias("tikaMetadata")
public class TikaMetadataAction extends AbstractAction {

	private WorkflowCache cache;
	private static final Log log = LogFactory.getLog(TikaMetadataAction.class);
	private String path;
	
	@XStreamOmitField
	private TikaWrapper tika;
	
	/**
	 * Constructs the new instance of this class
	 * This should be used for testing purpose only
	 */
	public TikaMetadataAction(){
		tika = new TikaWrapper();
	}
	
	@Override
	public void processAction() {
	
		if (path==null || path.isEmpty()){
			path = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		try {
			List<Entry> ents = cache.getAll(Entry.class);
			for (Entry ent: ents){
				createMeataDataFile(StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME)), StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME)) + ".metadata.tika.xml");
				log.info(String.format("MetaData file created for [%s]", path + ent.getString(Entry.NAME)));
			}
			log.info(String.format("All MetaData files created for [%s]", workflowName));
		} catch (final Exception e) {
			log.error("Encountered an error while creating metadata files",e);
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionError(TikaMetadataAction.this, e.getMessage());
				}
			});
			return;
		}
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
				listener.onActionFinished(TikaMetadataAction.this);
			}
		});
	}

	/**
	 * Extract metadata attributes and values of the given file
	 * @param file the file to perform extraction
	 * @return the map containing metadata key and metadata value of the given file
	 * @throws IOException
	 */
	public Map<String, String> getMetadata(String file) throws IOException{
		return tika.getMetaData(file);
	}
	
	/**
	 * Produce the metadata file of the given input file.
	 * @param sourceFile the file to be processed for metadata extraction
	 * @param outputFile the file to be created 
	 * @throws IOException
	 */
	public void createMeataDataFile(String sourceFile, String outputFile) throws IOException{
		
		Map<String, String> metadata = getMetadata(sourceFile);
		WorkflowNotificationDelegator.onMetadataDetected(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), sourceFile, metadata);
		OutputWriter ow = new XMLFileWriter();
		ow.writeMetadata(metadata, "tika", "", outputFile);
		WorkflowNotificationDelegator.onMetadataFileCreated(cache.getLongId(), cache.getString(WorkflowCache.REFERENCE), outputFile, sourceFile, "tika");
	}

	/**
	 * Produce the metadata files of the given input files.
	 * The output file name will be automatically generated by {originalname}.metadata.exif.xml 
	 * @param files the files to be processed for metadata extraction
	 * @throws IOException
	 */
	public void createMetaDataFiles(List<String> files) throws IOException{
		for (String file: files){
			createMeataDataFile(file, file + ".metadata.tika.xml");
			log.info(String.format("MetaData file created for [%s]", file));
		}
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		
		tika = new TikaWrapper();
		this.cache = cache;
	}
}
