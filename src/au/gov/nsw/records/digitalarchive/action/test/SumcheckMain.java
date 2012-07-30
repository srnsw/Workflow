package au.gov.nsw.records.digitalarchive.action.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

public class SumcheckMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//SumcheckAction sa = new SumcheckAction();
//		WorkflowCache data = WorkflowCacheFactory.getWorkflowCache(1);
//		sa.prepare(data, 1);
//		sa.processAction();
		
		FileInputStream fis = new FileInputStream( new File("config.xml") );
		final String calculatedMd5 = DigestUtils.md5Hex( fis );
		System.out.println("MD5:" + calculatedMd5);
	}

}
