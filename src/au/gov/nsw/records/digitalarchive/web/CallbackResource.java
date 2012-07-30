package au.gov.nsw.records.digitalarchive.web;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import au.gov.nsw.records.digitalarchive.kernel.WorkflowController;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/callback")
public class CallbackResource {

	@POST 
	@Produces(MediaType.APPLICATION_JSON)
	public String updateWorkflow(@PathParam("workflow") int workflowId, @PathParam("actionset") int actionsetId, @PathParam("action") int actionId, @PathParam("payload") String payload) {

		WorkflowController.getInstance().onEvent(workflowId, actionsetId, actionId, payload);

		return "{status: \"OK\" }";
	}
}
