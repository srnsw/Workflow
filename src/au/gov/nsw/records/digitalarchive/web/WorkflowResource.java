package au.gov.nsw.records.digitalarchive.web;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import au.gov.nsw.records.digitalarchive.kernel.WorkflowController;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.History;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.Workflow;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/workflows")
public class WorkflowResource {
	
	private ObjectMapper mapper = new ObjectMapper();
	private static final Log log = LogFactory.getLog(WorkflowResource.class);
//	@GET 
//	@Produces(MediaType.APPLICATION_JSON)
//	public String showMainPage() {
//
//		//WorkflowController.getInstance().startRecovery();
//		return getAllWorkflows();
//	}
	
/*
  	private String allActiveWorkflowInfo(){
		try {
			List<WorkflowInfo> infoList = new ArrayList<WorkflowInfo>();
			for (Workflow wf: WorkflowController.getInstance().getActiveWorkflows()){
				infoList.add(wf.getData().getWorkflowInfo());
			}
			return mapper.writeValueAsString(infoList);
			//return mapper.writeValueAsString(wc.getAllWorkflows());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "{}";
	}
*/	
	@POST 
	@Produces(MediaType.APPLICATION_JSON)
	public String createWorkflow(@QueryParam("reference") String reference, @QueryParam("xml") String xml){
		Workflow wf = WorkflowController.getInstance().createWorkflow(xml, reference);
		try {
			return mapper.writeValueAsString(wf);
		} catch (Exception e) {
			log.error("", e);
		}
		// Shuouldn't fall through this point
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	}
//	private String getAllWorkflows(){
//		try {
//			List<WorkflowCache> wfi =  sort(ActiveRecordFactory.getAllWorkflowCache(0, 0), on(WorkflowCache.class).getId());
//			return mapper.writeValueAsString(wfi);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
//	}
	
	@GET @Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String showWorkflow(@PathParam("id") int id) {
		
		WorkflowCache wfc = ActiveRecordFactory.getWorkflowCache(id);
		if (wfc != null){
			return wfc.toJson(false);
		}else{
			throw new NotFoundException("Workflow, " + id + ", is not found");
		}
	}
	
	@POST @Path("/{id}/start")
	@Produces(MediaType.APPLICATION_JSON)
	public String startWorkflow(@PathParam("id") long id) {
		
		Workflow wf = WorkflowController.getInstance().startWorkflow(id);
		if (wf!=null){
			return wf.getCache().toJson(false);
		}else{
			throw new NotFoundException("Workflow, " + id + ", is not found");
		}
	}

	@POST @Path("/{id}/cancel")
	@Produces(MediaType.APPLICATION_JSON)
	public String cancelWorkflow(@PathParam("id") long id) {
		
		Workflow wf = WorkflowController.getInstance().cancelWorkflow(id);
		if (wf!=null){
			return wf.getCache().toJson(false);
		}
		// Shuouldn't fall through this point
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	}
	
	@GET @Path("/{id}/history")
	@Produces(MediaType.APPLICATION_JSON)
	public String showWorkflowHistory(@PathParam("id") int id) {
		
		WorkflowCache wfc = ActiveRecordFactory.getWorkflowCache(id);
		if (wfc != null){
			try{
				List<History> hist = sort(wfc.getAll(History.class), on(History.class).get("created_at"));
				return mapper.writeValueAsString(hist);
			} catch (Exception e) {
				log.error("", e);
			}
		}else{
			throw new NotFoundException("Workflow, " + id + ", is not found");
		}
		// Shuouldn't fall through this point
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
	}
}
