package au.gov.nsw.records.digitalarchive.action.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import au.gov.nsw.records.digitalarchive.action.AbstractAction;
import au.gov.nsw.records.digitalarchive.kernel.activerecord.WorkflowCache;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;
import au.gov.nsw.records.digitalarchive.utils.thread.CoreThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The SMTP mail sender action. This action send email to the configured recipients with the configured message. 
 * @author wisanup
 *
 */
@XStreamAlias("mailNotification")
public class EmailNotificationAction extends AbstractAction {

	private String to;
	private String subject;
	private String message;
	private WorkflowCache cache;
	private boolean includeLastError;

	private EmailNotificationActionConfig config;
	
	@XStreamOmitField
	private List<String> replaceableContentVairables;
	
	private static final Log log = LogFactory.getLog(EmailNotificationAction.class);

	@Override
	public void processAction() {
		
		List<String> recipients = new ArrayList<String>();
		for (String recipient: to.split(",")){
			recipients.add(recipient);
		}
		
		sendEmail(recipients, subject, message);
	}

	/**
	 * Send email to the given recipients with the given subject and message 
	 * @param recipients the list of recipient email to be sent to
	 * @param subject the subject of the email to be sent
	 * @param message the message content of the email to be sent
	 */
	public void sendEmail(List<String> recipients, String subject, String message){
		try {
			log.info(String.format("Sending email to [%s] ", recipients.toString()));
			HtmlEmail email = new HtmlEmail();

			email.setHostName(config.getMailHost());
			if (!config.getMailPassword().isEmpty()){
				email.setAuthentication(config.getMailUser(), config.getMailPassword());
			}
			email.setSmtpPort(config.getMailPort());
			email.setFrom(config.getMailUser());
			for (String to: recipients){
				email.addTo(to);
			}
			email.setSubject(String.format("[%d - %s] %s", cache.getLongId(), cache.getString(WorkflowCache.NAME), subject));

			if (includeLastError){
				message = message + "\n\nError:\n" + cache.getString(WorkflowCache.LASTERROR);
			}
			email.setTextMsg(message);
			//email.setHtmlMsg(htmlBody);

			email.setDebug(false);
			email.send();
			log.info(String.format("Email sent to [%s] ", recipients.toString()));
			// OK we're done here
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					log.info(String.format("Action [%s] executed for [%s] ", getName(), workflowName));
					listener.onActionFinished(EmailNotificationAction.this);
				}
			});
			
		} catch (final EmailException e) {
			log.error("Encountered an error while sending an email",e);
			CoreThreadFactory.getCallBackExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					listener.onActionError(EmailNotificationAction.this, e.getMessage());
				}
			});
		}
	}
	
	@Override
	public void prepare(WorkflowCache cache, int actionSetId) {
		super.prepare(cache, actionSetId);
		this.cache = cache;
		
		if (config == null){
			ConfigDeserializer<EmailNotificationActionConfig> configLoader = new ConfigDeserializer<EmailNotificationActionConfig>();
			EmailNotificationActionConfig templateConf = new EmailNotificationActionConfig("localhost", "api@records.nsw.gov.au", "api_passwd", 25);
			config = configLoader.load(templateConf, ConfigHelper.getMailNotificationConfig());
		}
		
		replaceableContentVairables = new ArrayList<String>();
		
		replaceableContentVairables.add(to);
		replaceableContentVairables.add(subject);
		replaceableContentVairables.add(message);	
	}

	/**
	 * Set the recipients of the sending email
	 * @param to the recipients of the sending email
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Set the subject of the sending email
	 * @param subject the subject of the sending email
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Set the content of the sending mail
	 * @param text the content of the sending mail
	 */
	public void setText(String text) {
		this.message = text;
	}
}
