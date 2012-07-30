package au.gov.nsw.records.digitalarchive.kernel.activerecord;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.javalite.activejdbc.Base;

import au.gov.nsw.records.digitalarchive.DigitalArchiveConfig;
import au.gov.nsw.records.digitalarchive.kernel.processing.workflow.WorkflowState;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.DigitalArchiveConfigDeserializer;

public class ActiveRecordFactory {
	
	private static DigitalArchiveConfig conf = DigitalArchiveConfigDeserializer.load(ConfigHelper.getApplicationConfig());
	private static final Log log = LogFactory.getLog(ActiveRecordFactory.class);
	
	public static WorkflowCache addWorkflowCache(String name, String state, String actionset, String xml, String location, String reference){
		initDBConnection();
		WorkflowCache wc = new WorkflowCache();
		wc.set("name", name);
		wc.set("state", state);
		wc.set("actionset", actionset);
		wc.set("xml", xml);
		wc.set("error", Boolean.FALSE);
		wc.set("reference", reference);
		wc.set("location", location);
		wc.set("recentlocation", location);
		wc.set("lasterror", "");
		
		if (wc.save()){
			return wc;
		}
		return null;
	}
	
	public static Entry addWorkflowEntry(String name, String md5, String puid, WorkflowCache cache){
		initDBConnection();
		Entry ent = new Entry();
		ent.set("name", name);
		ent.set("md5", md5);
		ent.set("puid", puid);
		
		cache.add(ent);
		if (cache.save()){
			return ent;
		}
		return null;
	}
	
	public static History addWorkflowHistory(String event, WorkflowCache cache){
		initDBConnection();
		History hist = new History();
		hist.set("event", event);
		
		cache.add(hist);
		if (cache.save()){
			return hist;
		}
		return null;
	}
	
	
	public static List<WorkflowCache> getActiveWorkflowCache(){
		initDBConnection();
		String queryStr = String.format("select * from workflowcaches where state not in (\'%s\', \'%s\')", WorkflowState.FINISHED, WorkflowState.CANCELLED);
		return WorkflowCache.findBySQL(queryStr);
	}
	
	public static WorkflowCache getWorkflowCache(long id){
		initDBConnection();
		return (WorkflowCache)WorkflowCache.findById(id);
	}
	
	public static List<WorkflowCache> getAllWorkflowCache(int page, int size){
		initDBConnection();
		return WorkflowCache.findAll();
	}
	
	// this is bound to the caller's thread
	// expect to be called multiple times depending on the number of running thread
	public static void initDBConnection(){
		try {	
			if (Base.hasConnection() && !Base.connection().isValid(0)){
				Base.close();
				log.info(String.format("Recovered from db connection timeout of the thread [%s]", Thread.currentThread().getName()));
			}
		} catch (SQLException e) {
			log.error("Error while trying to close the DB connection", e);
		}

		// establish the new connection
		if (!Base.hasConnection()){
			String conn = String.format("jdbc:mysql://%s:%d/%s", conf.getDbhost(), conf.getDbport(), conf.getDbname());
			Base.open("com.mysql.jdbc.Driver", conn, conf.getDbuser(), conf.getDbpassword());
			//log.debug(String.format("New db connection [%s] openned for [%s]", conn, Thread.currentThread().getName()));
		}
	}

	public static boolean save(WorkflowCache cache) {
		initDBConnection();
		return cache.save();
	}
	
	public static boolean save(Entry entry) {
		initDBConnection();
		return entry.save();
	}
}
