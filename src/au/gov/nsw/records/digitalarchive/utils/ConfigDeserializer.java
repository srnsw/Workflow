package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigDeserializer <T> {

private static final Log log = LogFactory.getLog(ConfigDeserializer.class);

    /**
     * Create a configuration object from the specified file   
     * @param templateConfig the template of configuration should to be look alike
     * @param file the path of the XML configuration file
     * @return the configuration object
     */
	@SuppressWarnings("unchecked")
	public T load(T templateConfig, String file){
		
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		
		log.debug("Template configuration: \n" + xstream.toXML(templateConfig));
		
		// load the actual xml
		return(T)xstream.fromXML(new File(file));
	}
}
