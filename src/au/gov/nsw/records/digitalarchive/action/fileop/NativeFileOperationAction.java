package au.gov.nsw.records.digitalarchive.action.fileop;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The native system file operation. This class copy or delete or move files from the source path to the destination path in the native file system. 
 * @author wisanup
 *
 */
@XStreamAlias("fileOps")
public class NativeFileOperationAction extends AbstractAction {
	
	private static final Log log = LogFactory.getLog(NativeFileOperationAction.class);
	
	private String actionOption = "COPY";
	private String source;
	private String target;
	private WorkflowCache cache;
	
	@Override
	public void processAction() {
		
		if (source==null || source.isEmpty()){
			source = cache.getString(WorkflowCache.RECENTLOCATION);
		}
		try {
			if (actionOption.equalsIgnoreCase("COPY")){
				copy(source, target);
			}else if (actionOption.equalsIgnoreCase("MOVE")){
				move(source, target);
			}else if (actionOption.equalsIgnoreCase("DELETE")){
				delete(source);
			}else{
				log.error(String.format("Unknow option [%s]", actionOption));
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						listener.onActionError(NativeFileOperationAction.this, String.format("failed to perform file operation, unknown command [%s]", actionOption));
					}
				});
			}
		} catch (final IOException e) {
			log.error(String.format("Exception caught at option [%s]", actionOption), e);
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionError(NativeFileOperationAction.this, String.format("failed to perform file operation, error [%s]", e.getMessage()));
				}
			});
		}
	}

	/**
	 * Copy the whole directory to another location 
	 * @param sourceDir the source directory to be copied
	 * @param destDir the destination directory to store the files
	 * @throws IOException
	 */
	public void copy(String sourceDir, String destDir) throws IOException{
		FileUtils.copyDirectory(new File(sourceDir), new File(destDir));
		log.info(String.format("Directory [%s] copied to [%s] ", sourceDir, destDir));
		cache.set(WorkflowCache.RECENTLOCATION, destDir);
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				listener.onActionFinished(NativeFileOperationAction.this);
			}
		});
	}
	
	/**
	 * Move the whole directory to another location 
	 * @param sourceDir the source directory to be moved
	 * @param destDir the destination directory to store the files
	 * @throws IOException
	 */
	public void move(String sourceDir, String destDir) throws IOException{
		FileUtils.moveDirectory(new File(sourceDir), new File(destDir));
		log.info(String.format("Directory [%s] moved to [%s] ", sourceDir, destDir));
		cache.set(WorkflowCache.RECENTLOCATION, destDir);
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				listener.onActionFinished(NativeFileOperationAction.this);
			}
		});
	}
	
	/**
	 * Delete the whole directory from the file system
	 * @param dir the directory to be deleted
	 * @throws IOException
	 */
	public void delete(String dir) throws IOException{
		FileUtils.deleteDirectory(new File(dir));
		log.info(String.format("Directory [%s] deleted", dir));
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				listener.onActionFinished(NativeFileOperationAction.this);
			}
		});
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		this.cache = cache;
	}
}
