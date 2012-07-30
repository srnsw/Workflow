package au.gov.nsw.records.digitalarchive.action.wrapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration file for XENA
 * @author wisanup
 *
 */
@XStreamAlias("xena")
public class XenaConfig {

	private String plugin_path;
	private String open_office_location;
	
	/**
	 * Creates an instance of this class
	 * @param plugin_path
	 * @param open_office_location
	 */
	public XenaConfig(String plugin_path, String open_office_location) {
		super();
		this.plugin_path = plugin_path;
		this.open_office_location = open_office_location;
	}

	/**
	 * Get the location of the XENA's plugins 
	 * @return the location of the XENA's plugins
	 */
	public String getPlugin_path() {
		return plugin_path;
	}

	/**
	 * Set the location of the XENA's plugins
	 * @param plugin_path the location of the XENA's plugins
	 */
	public void setPlugin_path(String plugin_path) {
		this.plugin_path = plugin_path;
	}

	/**
	 *  Get the installation location of Open Office 
	 * @return the installation location of Open Office 
	 */
	public String getOpen_office_location() {
		return open_office_location;
	}

	/**
	 * Set the installation location of Open Office 
	 * @param open_office_location the installation location of Open Office 
	 */
	public void setOpen_office_location(String open_office_location) {
		this.open_office_location = open_office_location;
	}	
}
