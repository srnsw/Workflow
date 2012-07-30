package au.gov.nsw.records.digitalarchive.action.fileop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
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

	public String from = "";
	public String to = "";

	private static final Log log = LogFactory.getLog(IsilonFileCopyAction.class);

	
	@Override
	public void processAction() {
		// moving all resources under specified path would make more sense than coping each file individually
		final String sourceDir = StringHelper.formatIsilonDirectoryString(StringHelper.trimLastSlash(from));
		final String destDir = StringHelper.formatIsilonDirectoryString(StringHelper.trimLastSlash(to));

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));
		
		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				IsilonFileOperations fileOp = new IsilonFileOperations();
				if (fileOp.copy(sourceDir, destDir)){
					cache.set(WorkflowCache.RECENTLOCATION, destDir);
					listener.onActionFinished(IsilonFileCopyAction.this);
				}else{
					listener.onActionError(IsilonFileCopyAction.this, String.format("failed to copy files from [%s] to [%s]", sourceDir, destDir));
				}
			}
		});
		
	}
}