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
 * The Isilon system file operation. This class delete files from the specified path in Isilon system. 
 * @author wisanup
 *
 */
@XStreamAlias("isilonDelete")
public class IsilonFileDeleteAction extends AbstractAction {
	
	private WorkflowCache cache;
	
	public String path = "";

	private static final Log log = LogFactory.getLog(IsilonFileDeleteAction.class);
	
	@Override
	public void processAction() {

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		IsilonFileOperations fileOp = new IsilonFileOperations();
		
		for (Entry ent: (List<Entry>)cache.getAll(Entry.class)){
			String file = StringHelper.formatUnixPath(ent.getString(Entry.NAME));
			String dir = StringHelper.joinDirectoryString(path , StringHelper.trimAfterLastSlash(file));;
			if (!fileOp.delete(dir)){
				CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
					@Override
					public void run() {
							listener.onActionFinished(IsilonFileDeleteAction.this);
							listener.onActionError(IsilonFileDeleteAction.this, String.format("failed to delte files from [%s]", path));
					}
				});
				
				return;
			}
		}
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
					listener.onActionFinished(IsilonFileDeleteAction.this);
			}
		});
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId){ 
		super.prepare(cache, actionSetId);
		this.cache = cache;
	}
}
