package au.gov.nsw.records.digitalarchive.action.repository;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

public abstract class AbstractWebService {
	public static URI BASE_URI;
	private static HttpServer createServer(String packages) throws IOException {
				
		System.out.println("Starting webserver");
		ResourceConfig rc = new PackagesResourceConfig(packages);
		return HttpServerFactory.create( BASE_URI, rc);
	}
	
	protected void startService() throws IOException {
	
		// start a web service
		BASE_URI = UriBuilder.fromUri("http://" + getHost() + "/").port(getPort()).build();
		HttpServer httpServer = createServer(getWebResourcePackageName());
		httpServer.start();
		//httpServer.getServerConfiguration().setJmxEnabled(true);
		System.out.println(String.format("App started with WADL available at "
				+ "%s/application.wadl\n Hit enter to stop it...",
				BASE_URI, BASE_URI));
		
		// stopping application
		System.in.read();
		httpServer.stop(2);
	}
	
	
	protected abstract String getHost();
	protected abstract int getPort();
	protected abstract String getWebResourcePackageName();
}
