package au.gov.nsw.records.digitalarchive.action.test;

import au.gov.nsw.records.digitalarchive.action.notification.EmailNotificationAction;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.ActionListener;

public class EmailNotificationMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmailNotificationAction email = new EmailNotificationAction();
		
		email.setListener(new ActionListener() {
			
			@Override
			public void onActionFinished(Action action) {
				// TODO Auto-generated method stub
				System.out.println("onActionFinished");
			}
			
			@Override
			public void onActionError(Action action, String error) {
				// TODO Auto-generated method stub
				System.out.println("onActionError");
			}
		});
		
		email.prepare(null, 112);
		//email.setTo("wisanu.promthong@records.nsw.gov.au");
		email.setTo("airgear01@gmail.com");
		email.setSubject("Alert from DA system");
		email.setText("this is a test email");
		
		email.processAction();
		//email.commonMail();
	}

}
