package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

	public boolean uploadFile(String fileName, String storePath, boolean setReadOnly) throws ClientProtocolException, IOException{
		if (uploadFile(fileName, storePath)){
			if (setReadOnly){
				return setReadOnly(fileName, storePath);	
			}
			return true;
		}
		return false;
	}

	public boolean setReadOnly(String fileName, String storePath) throws ClientProtocolException, IOException{
		HttpClient httpclient = new DefaultHttpClient();
		try{
			fileName = URLEncoder.encode(fileName, "UTF-8");
			HttpPut httpput = new HttpPut(StringHelper.trimLastSlash(baseURL) + "/" + StringHelper.trimFirstSlash(storePath) + "/" + fileName + "?readonly=true");
		
			HttpResponse resp = httpclient.execute(httpput);
			if (resp.getStatusLine().toString().contains("OK") && resp.getStatusLine().toString().contains("200")){
				log.info("[" + fileName + "] is now read only");
				return true;
			}
			return false;
		} finally {
			try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
		}
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
			if (response.getStatusLine().toString().contains("Created") && response.getStatusLine().toString().contains("201")) {
				log.debug("Response content length: " + resEntity.getContentLength());
				log.info(String.format("Uploaded [%s] ok", fileName));
				EntityUtils.consume(resEntity);
				return true;
			}
			EntityUtils.consume(resEntity);
		} finally {
			try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
		}

		return false;
	}
}
