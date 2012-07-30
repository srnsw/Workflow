package au.gov.nsw.records.digitalarchive.action.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The helper class to convert the Exif's native output format to the key:value format
 * @see <a href="http://www.sno.phy.queensu.ca/~phil/exiftool/">Exif tool website</a>
 * @author wisanup
 *
 */
public class ExifOutputConverter {

	private static final Log log = LogFactory.getLog(ExifOutputConverter.class);
	
	/**
	 * Convert the given Exif's output to the mapped key:value
	 * @param exifOutput the output of Exif's command-line execution
	 * @return
	 */
	public static Map<String, String> convert(String exifOutput){
		Map<String, String> output = new HashMap<String, String>();
		
		//Sample input data..
		
		//ExifTool Version Number         : 8.72
		//File Name                       : Series_001.jpg
		//Directory                       : .
		//File Size                       : 2.0 MB
         
		StringTokenizer line = new StringTokenizer(exifOutput, "\n");
		try{
			while(line.hasMoreTokens()){
				String pair = line.nextToken();
				// split at the first colon
				String key = pair.substring(0, pair.indexOf(":")-1).trim();
				String value = pair.substring(pair.indexOf(":")+1).trim();
				output.put(key, value);
				log.debug(String.format("Extracted [%s][%s]", key, value));
			}
		}catch(StringIndexOutOfBoundsException e){
			log.error(String.format("Error from input [%s]", exifOutput), e);
		}
		
		return output;
	}
}
