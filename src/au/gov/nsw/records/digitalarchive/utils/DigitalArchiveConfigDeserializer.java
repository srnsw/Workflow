package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.DigitalArchiveConfig;
import au.gov.nsw.records.digitalarchive.action.fileop.IsilonFileCopyAction;
import au.gov.nsw.records.digitalarchive.action.fileop.IsilonFileDeleteAction;
import au.gov.nsw.records.digitalarchive.action.fileop.IsilonFileMoveAction;
import au.gov.nsw.records.digitalarchive.action.helper.SleepAction;
import au.gov.nsw.records.digitalarchive.action.helper.SumcheckAction;
import au.gov.nsw.records.digitalarchive.action.identification.DroidFileIdentificationAction;
import au.gov.nsw.records.digitalarchive.action.identification.TikaFileIdentificationAction;
import au.gov.nsw.records.digitalarchive.action.identification.XenaFileIdentificationAction;
import au.gov.nsw.records.digitalarchive.action.metadata.ExifMetadataAction;
import au.gov.nsw.records.digitalarchive.action.metadata.TikaMetadataAction;
import au.gov.nsw.records.digitalarchive.action.notification.EmailNotificationAction;
import au.gov.nsw.records.digitalarchive.action.preservation.PreservationAction;
import au.gov.nsw.records.digitalarchive.action.qurantine.ClamAVAction;
import au.gov.nsw.records.digitalarchive.action.repository.RepositoryAction;
import au.gov.nsw.records.digitalarchive.kernel.processing.action.Action;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DigitalArchiveConfigDeserializer {
	
	private static final Log log = LogFactory.getLog(DigitalArchiveConfigDeserializer.class);
	
    /**
     * Create a configuration object from the specified file   
     * @param file the path of the XML configuration file
     * @return the configuration object
     */
	public static DigitalArchiveConfig load(String file){
		
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		
		// to show template of the config file, also assist XStream to realize the defined annotations
		List<Action> actions = new ArrayList<Action>();
		actions.add(new SleepAction());
		actions.add(new IsilonFileMoveAction());
		actions.add(new IsilonFileCopyAction());
		actions.add(new IsilonFileDeleteAction());
		actions.add(new ClamAVAction());
		actions.add(new DroidFileIdentificationAction(""));
		actions.add(new EmailNotificationAction());
		actions.add(new ExifMetadataAction());
		actions.add(new RepositoryAction());
		actions.add(new SumcheckAction());
		actions.add(new TikaFileIdentificationAction());
		actions.add(new TikaMetadataAction());
		actions.add(new PreservationAction());
		actions.add(new XenaFileIdentificationAction());
		
		DigitalArchiveConfig conf = new DigitalArchiveConfig(8888, "localhost", "root", "", "localhost", 1234, "da", null);
		log.info("Template configuration: \n" + xstream.toXML(conf));
		
		// load the actual xml
		return(DigitalArchiveConfig)xstream.fromXML(new File(file));
	}
}
