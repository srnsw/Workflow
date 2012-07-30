package au.gov.nsw.records.digitalarchive.action.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.Entry;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The integrity check operation by using md5 sum checking 
 * @author wisanup
 *
 */
@XStreamAlias("sumCheck")
public class SumcheckAction extends AbstractAction {

	private WorkflowCache cache;
	private String path;
	private static final Log log = LogFactory.getLog(SumcheckAction.class);

	private String lastError = "";
	@Override
	public void processAction() {

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		
		if (path==null || path.isEmpty()){
			path = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		
		boolean isError = false;

		// get all files? in the project and checking sum of it. 
		List<Entry> ents = cache.getAll(Entry.class);
		for (Entry ent:ents){

			if (!isMD5Valid(StringHelper.joinDirectoryString(path , ent.getString(Entry.NAME)), (String)ent.get(Entry.MD5))){
				
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						log.error(String.format("Action [%s] failed for [%s] ", getName(), workflowName));
						listener.onActionError(SumcheckAction.this, lastError);
					}

				});
				isError = true;
				break;
			}
		}		

		if (!isError){
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
					listener.onActionFinished(SumcheckAction.this);
				}
			});
		}
	}

	/**
	 * Perform integrity check on the given file against given md5 has
	 * @param pathToFile the location of the file to perform integrity check
	 * @param md5 the expected md5 hash value 
	 * @return true if the calculated md5 hash of the given file is the same as the given md5, false otherwise 
	 */
	public boolean isMD5Valid(String pathToFile, String md5){
		FileInputStream fis = null;
		try {
			File f = new File(pathToFile);
			if (!f.exists()){
				lastError = f.getAbsoluteFile() + " not exist";
				log.error(f.getAbsoluteFile() + " not exist");
				
				return false;
			}else{
				log.debug("Checking MD5 - " + f.getAbsoluteFile());
			}
			fis = new FileInputStream( new File(pathToFile) );
			final String calculatedMd5 = DigestUtils.md5Hex( fis );
			if (!calculatedMd5.equals(md5)){
				lastError = String.format("Expect md5 of [%s] value [%s] but got [%s] ", pathToFile, md5, calculatedMd5);
				log.error(lastError);
				
				return false;
			}else{
				log.info(String.format("[%s] md5 ok", pathToFile));
				return true;
			}
		} catch (Exception e) {
			log.error(String.format("Action [%s] error for [%s] ", getName(), workflowName), e);
		}finally{
			if (fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					log.error(String.format("Action [%s] error for [%s] ", getName(), workflowName), e);
				}
			}
		}
		return false;
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId){ 
		super.prepare(cache, actionSetId);
		this.cache = cache;
		try{
			for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
				if (ent.get(Entry.MD5) == null || ((String)ent.get(Entry.MD5)).isEmpty()){
					//String pathToFile = cache.getString(WorkflowCache.LOCATION) + File.separatorChar + (String)ent.get(Entry.NAME);
					String pathToFile = StringHelper.joinDirectoryString(cache.getString(WorkflowCache.LOCATION) , ent.getString(Entry.NAME));
					String calculatedMd5 = DigestUtils.md5Hex(new FileInputStream(new File(pathToFile)));
					ent.set(Entry.MD5, calculatedMd5);
					ActiveRecordFactory.save(ent);
					log.info(String.format("Action [%s] for [%s] - [%s] calculated MD5 is [%s]", getName(), workflowName, (String)ent.get(Entry.NAME), calculatedMd5));
				}
			}

		}catch(IOException e){
			log.error(String.format("Action [%s] error for [%s] ", getName(), workflowName), e);
		}
	}
}
