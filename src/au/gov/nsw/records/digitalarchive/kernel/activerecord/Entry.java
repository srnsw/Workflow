package au.gov.nsw.records.digitalarchive.kernel.activerecord;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

@Table("entries")
@BelongsTo(parent = WorkflowCache.class, foreignKeyName = "workflowcache_id")
public class Entry extends Model{

	public static final String NAME = "name";
	public static final String MD5 = "md5";
	public static final String PUID = "puid";
}
