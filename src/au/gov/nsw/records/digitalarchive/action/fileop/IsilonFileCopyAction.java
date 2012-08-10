package au.gov.nsw.records.digitalarchive.action.fileop;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.Entry;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.StringHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The Isilon system file operation. This class copy files from the source path to the destination path in Isilon system. 
 * @author wisanup
 *
 */
@XStreamAlias("isilonCopy")
public class IsilonFileCopyAction extends AbstractAction {

	private WorkflowCache cache;
	
	public String from = "";
	public String to = "";

	private static final Log log = LogFactory.getLog(IsilonFileCopyAction.class);

	
	@Override
	public void processAction() {

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));

		IsilonFileOperations fileOp = new IsilonFileOperations();
		for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
			String file = StringHelper.formatUnixPath(ent.getString(Entry.NAME));
			final String sourcePath = StringHelper.joinDirectoryString(from , StringHelper.trimAfterLastSlash(file));
			final String destPath = StringHelper.joinDirectoryString(to , StringHelper.trimAfterLastSlash(file));

			if (!fileOp.copy(sourcePath, destPath)){
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
						 listener.onActionError(IsilonFileCopyAction.this, String.format("failed to copy files from [%s] to [%s]", sourcePath, destPath));
					}
				});
				// no more processing
				return;
			}
			// all good
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					cache.set(WorkflowCache.RECENTLOCATION, to);
					listener.onActionFinished(IsilonFileCopyAction.this);
				}
			});
		}
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId){ 
		super.prepare(cache, actionSetId);
		this.cache = cache;
	}
}