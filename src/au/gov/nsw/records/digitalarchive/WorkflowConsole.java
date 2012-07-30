package au.gov.nsw.records.digitalarchive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.gov.nsw.records.digitalarchive.kernel.WorkflowController;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationListener;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.Workflow;

/**
 * The interactive command line for sending and receiving event to DAW(Digital Archives Workflow) 
 * @author wisanup
 *
 */
public class WorkflowConsole implements WorkflowNotificationListener{

	private static final Log log = LogFactory.getLog(WorkflowConsole.class);
	/**
	 * Start processing an interactive command line console
	 */
	public void start(){
		
		System.out.println();
		
		printBanner();
		printHelp();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		while(!command.equalsIgnoreCase("exit")){
			try {
				System.out.print("DA workfow>");
				command = br.readLine();
				processCommand(command);
			} catch (Exception e){
				log.error("error", e);
			}
		}
	}
	
	/**
	 * Process the given command
	 * @param command
	 */
	private void processCommand(String command){
		List<String> args = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(command, " ");
		// discard command
		if (st.countTokens() > 0){
			st.nextToken();
		}
		while (st.hasMoreTokens()){
			args.add(st.nextToken());
		}
		
		try{
			if (command.startsWith("create")){
				create(args);
			} else if (command.startsWith("start")){
				start(args);
			} else if (command.startsWith("cancel")){
				cancel(args);
			} else if (command.startsWith("info")){
				info(args);
			} else if (command.startsWith("list")){
				list();
			} else if (command.startsWith("help")){
				printHelp();
			} else if (command.startsWith("@")){
				create("C:\\Nott\\workflowdemo\\inbox\\StateRobotNSW\\TheTransferOfJedi", "workflow.xml", "nottRef1");
			} else if (command.startsWith("#")){
				create("/ifs/devdata/dadata/inbox/SRNSW/Test1", "workflow.xml", "nottRef1");
			}
		}catch (IndexOutOfBoundsException e){
			System.out.println("Invalid arguments, expect more argument");
		}catch (NumberFormatException e){
			System.out.println("Invalid arguments, id must be a number");
		}
	}
	
	/**
	 * Show JSON info of the given workflow id
	 * @param args
	 */
	private void info(List<String> args) {
		WorkflowCache wfc = ActiveRecordFactory.getWorkflowCache(Long.parseLong(args.get(0)));
		if (wfc != null){
			System.out.println( wfc.toJson(true, "id", "name", "state", "error", "reference"));
		}else{
			System.out.println(String.format("The workflow [%s] does not exist", args.get(0)));
		}
	}

	/**
	 * Cancel a workflow by the given id
	 * @param args
	 */
	private void cancel(List<String> args) {
		
		Workflow wf = WorkflowController.getInstance().cancelWorkflow(Long.parseLong(args.get(0)));
		if (wf!=null){
			System.out.println(wf.getCache().toJson(true));
		}else{
			System.out.println(String.format("The workflow [%s] does not exist", args.get(0)));
		}
		
	}

	/**
	 * Start processing the workflow
	 * @param args
	 */
	private void start(List<String> args) {
		Workflow wf = WorkflowController.getInstance().startWorkflow(Long.parseLong(args.get(0)));
		if (wf!=null){
			System.out.println(wf.getCache().toJson(true));
		}else{
			System.out.println(String.format("The workflow [%s] does not exist", args.get(0)));
		}
	}
	
	private void list(){
		Collection<Workflow> wfs = WorkflowController.getInstance().getActiveWorkflows();
		for (Workflow wf:wfs){
			System.out.println("found: " + wf.getCache().getString("id"));
			System.out.println(wf.getCache().toJson(true, "id", "name", "state", "reference", "lasterror", "error"));
		}
		System.out.println("No more active workflows");
	}

	private void create(List<String> args) {
		create(args.get(0), args.get(1), args.get(2));
	}

	private void create(String path, String xmlname, String ref){
		Workflow wf = WorkflowController.getInstance().createWorkflow(path, xmlname, ref);
		if (wf!=null){
			ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println(mapper.writeValueAsString(wf));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Unable to create a workflow with the supplied xml");
		}
	}
	
	private void printHelp(){
		
		System.out.println("\n[To list all incompleted workflow]");
		System.out.println(">list");
		
		System.out.println("\n[To create a new workflow]");
		System.out.println(">create {records path} {workflow xml name} {reference}");
		System.out.println(">create c:\\project\\records workflow.xml myrefernce1");
		
		System.out.println("\n[To start the created workflow]");
		System.out.println(">start {workflow id} ");
		System.out.println(">start 1 ");
		
		System.out.println("\n[To cancel the workflow]");
		System.out.println(">cancel {workflow id} ");
		System.out.println(">cancel 1 ");
		
		System.out.println("\n[To show information of the created workflow]");
		System.out.println(">info {workflow id} ");
		System.out.println(">info 1 ");
		
		System.out.println("\n[To quit]");
		System.out.println(">exit");
		
		System.out.println("############################################################");
	}
	private void printBanner(){
		System.out.println("############################################################");
		System.out.println("   ___  ___ _      __  _____                   __   ");
		System.out.println("  / _ \\/ _ | | /| / / / ___/__  ___  ___ ___  / /__ ");
		System.out.println(" / // / __ | |/ |/ / / /__/ _ \\/ _ \\(_-</ _ \\/ / -_)");
		System.out.println("/____/_/ |_|__/|__/  \\___/\\___/_//_/___/\\___/_/\\__/");
		System.out.println("############################################################");
		System.out.println("");
	}

	@Override
	public void onRenditionFileCreated(long id, String reference,
			String fileLocation, String fileName, String info) {
		System.out.println(String.format("onRenditionFileCreated:[%d][%s][%s][%s][%s]", id, reference, fileLocation, fileName, info));
	}

	@Override
	public void onActionCompleted(long id, String reference, String actionName) {
		System.out.println(String.format("onActionCompleted:[%d][%s][%s]", id, reference, actionName));
		
	}

	@Override
	public void onActionSetCompleted(long id, String reference,
			String actionSetName) {
		System.out.println(String.format("onActionSetCompleted:[%d][%s][%s]", id, reference, actionSetName));
	}

	@Override
	public void onMetadataFileCreated(long id, String reference,
			String fileLocation, String fileName, String info) {
		System.out.println(String.format("onMetadataFileCreated:[%d][%s][%s][%s][%s]", id, reference, fileLocation, fileName, info));	
	}

	@Override
	public void onWorkflowCompleted(long id, String reference) {
		System.out.println(String.format("onWorkflowCompleted:[%d][%s]", id, reference));
	}

	@Override
	public void onWorkflowError(long id, String reference, String error) {
		System.out.println(String.format("onWorkflowError:[%d][%s][%s]", id, reference, error));
	}

	@Override
	public void onMetadataDetected(long id, String reference,
			String sourceFile, Map<String, String> metadata) {
		System.out.println(String.format("onMetadataDetected:[%d][%s][%s][%s]", id, reference, sourceFile, metadata));
	}
}
