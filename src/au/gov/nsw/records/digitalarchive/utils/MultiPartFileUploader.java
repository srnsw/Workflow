package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MultiPartFileUploader {

	private static final Log log = LogFactory.getLog(MultiPartFileUploader.class);
	
	private String baseURL = "http://localhost:8080/Repository/repository/upload/da/xx";
	
	public MultiPartFileUploader(String baseURL){
		this.baseURL = baseURL;
	}
	
	public boolean uploadFile(String fileName, String storePath) throws ClientProtocolException, IOException{
		 HttpClient httpclient = new DefaultHttpClient();
	        try {
	            HttpPost httppost = new HttpPost(StringHelper.trimLastSlash(baseURL) + "/" + StringHelper.trimFirstSlash(storePath));

	            FileBody bin = new FileBody(new File(fileName));
	            //StringBody comment = new StringBody("A binary file of some kind");

	            MultipartEntity reqEntity = new MultipartEntity();
	            reqEntity.addPart("content", bin);
	           // reqEntity.addPart("comment", comment);

	            httppost.setEntity(reqEntity);
	            
	            log.info(String.format("Uploading [%s] to [%s]", fileName, storePath));
	            log.debug("Executing request " + httppost.getRequestLine());
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity resEntity = response.getEntity();
	            
	            log.info(String.format("Uploaded [%s] with [%s]", fileName, response.getStatusLine()));
	            if (resEntity != null) {
	                log.debug("Response content length: " + resEntity.getContentLength());
	                
	                StringWriter writer = new StringWriter();
	                IOUtils.copy(resEntity.getContent(), writer, "UTF-8");
	                String responseBody = writer.toString();
	                log.info(String.format("Uploaded [%s] with [%s]", fileName, responseBody));
	                
	                if (responseBody.startsWith("created")){
	                	return true;
	                }
	            }
	            EntityUtils.consume(resEntity);
	        } finally {
	            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
	        }
	        
	        return false;
	}
}
