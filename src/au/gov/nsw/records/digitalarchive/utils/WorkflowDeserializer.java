package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;
import au.gov.nsw.records.digitalarchive.kernel.processing.actionset.ActionSet;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.Workflow;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class WorkflowDeserializer {

	private static XStream xstream = new XStream(new DomDriver());
	private static final Log log = LogFactory.getLog(WorkflowDeserializer.class);
	
	private WorkflowDeserializer(){}
	
	
	public static Workflow loadFromFile(String location){		
		return(Workflow)xstream.fromXML(new File(location));
	}
	
	public static Workflow load(String xml){		
		return(Workflow)xstream.fromXML(xml);
	}
		
	public static void registerActions(List<Action> actions){
		
		// XStream need to know what classes are exist before loading
		// explicitly required only when we use annotation
		xstream.autodetectAnnotations(true);
		Workflow wf = new Workflow("generic");
		ActionSet as1 = new ActionSet("quarantine");
		for(Action action: actions){
			as1.addAction(action);
		}
		wf.addActionSet(as1);
	
		log.info("Template workflow : \n" + xstream.toXML(wf));
	}
}
