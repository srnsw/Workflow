package au.gov.nsw.records.digitalarchive.action.repository;

import java.io.IOException;

import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;

public class RepositoryClient extends AbstractWebService{

	private static RepositoryClientConfig config;
	
	public static void main(String... args) throws IOException{
		
		ConfigDeserializer<RepositoryClientConfig> configLoader = new ConfigDeserializer<RepositoryClientConfig>();
		RepositoryClientConfig templateConf = new RepositoryClientConfig("localhost", 8080, "http://repohost:8080", "user", "pwd", "DigitalArchives", "DAWorkspace", "\\staging");
		config = configLoader.load(templateConf, ConfigHelper.getRepositoryConfig());
		
		RepositoryClient client = new RepositoryClient();
		
		client.startService();
	}

	@Override
	protected String getHost() {
		return config.getHost();
	}

	@Override
	protected int getPort() {
		return config.getPort();
	}

	@Override
	protected String getWebResourcePackageName() {
		return this.getClass().getPackage().getName();
	}
}
