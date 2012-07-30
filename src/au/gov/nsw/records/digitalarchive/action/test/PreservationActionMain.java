package au.gov.nsw.records.digitalarchive.action.test;

import au.gov.nsw.records.digitalarchive.action.preservation.PreservationAction;

public class PreservationActionMain {

	public static void main(String[] args){

		PreservationAction pa = new PreservationAction();
		
		pa.prepare(null, 0);

	}
}
