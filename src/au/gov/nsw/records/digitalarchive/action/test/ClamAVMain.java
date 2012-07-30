package au.gov.nsw.records.digitalarchive.action.test;

import au.gov.nsw.records.digitalarchive.action.wrapper.ClamAVSocket;

public class ClamAVMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.setProperty("org.jboss.netty.debug", "true");
		ClamAVSocket avSocket = new ClamAVSocket("10.0.0.22", 3310);
		//avSocket.scan("/home/wisanup/api.records.nsw.gov.au/");
		//avSocket.scan("/home/wisanup/");
		avSocket.scan("/var/www");
		//System.out.println("Scann : " + avSocket.scan("/home/wisanup"));
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
