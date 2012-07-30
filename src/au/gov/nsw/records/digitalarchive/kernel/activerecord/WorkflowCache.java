package au.gov.nsw.records.digitalarchive.kernel.activerecord;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("workflowcaches") 
public class WorkflowCache extends Model{

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String STATE = "state";
	public static final String ACTIONSET = "actionset";
	public static final String LOCATION = "location";
	public static final String RECENTLOCATION = "recentlocation";
	public static final String XML = "xml";
	public static final String REFERENCE = "reference";
	public static final String ERROR = "error";
	public static final String LASTERROR = "lasterror";
	
}
