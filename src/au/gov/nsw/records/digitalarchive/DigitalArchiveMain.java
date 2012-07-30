package au.gov.nsw.records.digitalarchive;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.kernel.WorkflowController;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.ActiveRecordFactory;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationDelegator;
import au.gov.nsw.records.digitalarchive.kernel.externallistener.WorkflowNotificationListener;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.DigitalArchiveConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.WorkflowDeserializer;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

public class DigitalArchiveMain {

	public static URI BASE_URI;
	public static String VERSION = DigitalArchiveMain.class.getPackage().getImplementationVersion();
	
	private static final Log log = LogFactory.getLog(DigitalArchiveMain.class);

	protected static HttpServer createHttpServer() throws IOException {

		log.info("Starting light weight HTTP");
		ResourceConfig rc = new PackagesResourceConfig("au.gov.nsw.records.digitalarchive.web");
		return HttpServerFactory.create( BASE_URI, rc);
	}

	public static void main(String[] args) {

		try{
			log.info("SRNSW Digital Archive Workflow v." + VERSION);
	
			// ensure that std out and std err are recorded to our log.
			//StandardOutputRedirector.tieSystemOutAndErrToLog();
			
			// load a config file
			DigitalArchiveConfig conf = DigitalArchiveConfigDeserializer.load(ConfigHelper.getApplicationConfig());
	
			// load & register notification listeners class
			WorkflowNotificationDelegator.addListenerClass(conf.getNotificationListeners());
	
			// register actions
			WorkflowDeserializer.registerActions(conf.getActions());
	
			ActiveRecordFactory.initDBConnection();
			
			// recover all unfinished workflows
			log.info("Recovering workflows");
			WorkflowController.getInstance().startRecovery();
			log.info("Recovered all workflows");
	
			// start a web service
			BASE_URI = UriBuilder.fromUri("http://" + conf.getListenHost()+ "/").port(conf.getListenPort()).build();
			HttpServer httpServer = createHttpServer();
			httpServer.start();
			System.out.println(String.format("DAW started with WADL available at %sapplication.wadl\n",BASE_URI));
	
			boolean waitedForExit = false;
			for (WorkflowNotificationListener wnl : conf.getNotificationListeners()){
				// process command line request
				if (wnl instanceof WorkflowConsole){
					((WorkflowConsole)wnl).start();
					waitedForExit = true;
				}
			}
			
			if (!waitedForExit){
				System.out.println("Hit enter to exit");
				System.in.read();
			}
			
			// reach here when the console has triggered with exit command
			WorkflowController.getInstance().shutdown();
			CoreThreadFactory.shutdownServices();
			// stop with 2 seconds delay
			httpServer.stop(2);
		}catch(Exception e){
			log.error("Error starting DAW", e);
		}
	}    
}
