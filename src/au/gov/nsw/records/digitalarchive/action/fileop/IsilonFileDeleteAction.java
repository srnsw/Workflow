package au.gov.nsw.records.digitalarchive.action.fileop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The Isilon system file operation. This class delete files from the specified path in Isilon system. 
 * @author wisanup
 *
 */
@XStreamAlias("isilonDelete")
public class IsilonFileDeleteAction extends AbstractAction {
	
	public String path = "";

	private static final Log log = LogFactory.getLog(IsilonFileDeleteAction.class);
	
	@Override
	public void processAction() {

		log.info(String.format("Action [%s] is being executed for [%s] ", getName(), workflowName));

		CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				IsilonFileOperations fileOp = new IsilonFileOperations();
				if (fileOp.delete(path)){
					listener.onActionFinished(IsilonFileDeleteAction.this);
				}else{
					listener.onActionError(IsilonFileDeleteAction.this, String.format("failed to delte files from [%s]", path));
				}
			}
		});
	}
}
