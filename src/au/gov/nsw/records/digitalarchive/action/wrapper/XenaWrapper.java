package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.naa.digipres.xena.core.Xena;
import au.gov.naa.digipres.xena.kernel.XenaInputSource;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;
import au.gov.naa.digipres.xena.kernel.plugin.PluginManager;
import au.gov.naa.digipres.xena.kernel.properties.PluginProperties;
import au.gov.naa.digipres.xena.kernel.properties.XenaProperty;
import au.gov.nsw.records.digitalarchive.utils.ConfigDeserializer;
import au.gov.nsw.records.digitalarchive.utils.ConfigHelper;

/**
 * The simplification usage of XENA library
 * @see <a href="http://sourceforge.net/projects/xena/">XENA website</a>
 * 
 * @author wisanup
 */
public class XenaWrapper {

	private static final Log log = LogFactory.getLog(XenaWrapper.class);
	private Xena xena;
	private XenaConfig config;

	/**
	 * Create an instance of this class
	 */
	public XenaWrapper(){
		xena = new Xena();
		
		// load plugins
		ConfigDeserializer<XenaConfig> configLoader = new ConfigDeserializer<XenaConfig>();
		XenaConfig templateConf = new XenaConfig("./assets/xenaplugins", "C:\\Program Files (x86)\\OpenOffice.org 3");
		config = configLoader.load(templateConf, ConfigHelper.getXenaConfig());

		try {
			//xena.loadPlugin("au.gov.nsw.records.digitalarchive.action.xena.exp.DAOfficePlugin");
			xena.loadPlugins(new File(config.getPlugin_path()));
			xena.setActiveFileNamer(new DAFileNamer());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		// specify Open Office location
		PluginManager mgr = xena.getPluginManager();
		for (PluginProperties prop:mgr.getPropertiesManager().getPluginProperties()){
			// get office plugin
			if (prop.getName().equalsIgnoreCase("Office")){
				for (XenaProperty xp:prop.getProperties()){
					if (xp.getName().equalsIgnoreCase("Office Location")){
						// Office Location here
						xp.setValue(config.getOpen_office_location());
						// save it
						mgr.getPropertiesManager().saveProperty(xp);
						// exit loop
						break;	
					}
				}
				// exit loop
				break;
			}
		}
		
		log.info("=== XENA Normalisers ===");
		for (Object element : xena.getPluginManager().getNormaliserManager().getAll()) {
			String name = element.getClass().getCanonicalName();
			if (!name.toLowerCase().contains("denormaliser")){
				log.info(name);
			}
		}
		log.info("=== XENA Normalisers ===");
	}


	/**
	 * Get the mime type of the specified file in the given location
	 * @param filePath the file to be examined
	 * @return the mime type of the given file
	 */
	public String getMostLikelyType(String filePath){		
		try {
			return xena.getMostLikelyType(new XenaInputSource(new File(filePath))).getMimeType();
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "unknown";
	}
	
	/**
	 * Generate the preserved format of the given file. The preserved file will be created at the same location of the original file 
	 * @param filePath the file to generate the preserved format
 	 * @see <a href="http://xena.sourceforge.net/help.php?page=normformats.html">XENA support formats</a>   
	 */
	public String cratePerservedFile(String filePath){
		
		try {
			File inputFile = new File(filePath);
			// TODO verify in UNIX OS
			String destinationDir = inputFile.getAbsolutePath().substring(0, inputFile.getAbsolutePath().lastIndexOf("\\"));
			NormaliserResults res = xena.normalise(new XenaInputSource(inputFile), new File(destinationDir), true);
			
			log.info(String.format("Normalaising [%s] ", filePath));
			
			if (!res.isNormalised()){
				log.warn(String.format("Xena did not normalise [%s] by [%s] plugin", filePath, res.getNormaliserClassName()));
				log.warn(String.format("Xena produced [%s]", res.getOutputFileName()));
			}else{
				log.info(String.format("Normalised [%s] to [%s] by [%s] plugin", filePath, res.getOutputFileName(), res.getNormaliserClassName()));
				return res.getOutputFileName();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
