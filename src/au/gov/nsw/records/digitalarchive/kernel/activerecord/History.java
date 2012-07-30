package au.gov.nsw.records.digitalarchive.kernel.activerecord;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

@Table("histories")
@BelongsTo(parent = WorkflowCache.class, foreignKeyName = "workflowcache_id")
public class History extends Model{

}
