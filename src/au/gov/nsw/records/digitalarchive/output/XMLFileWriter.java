package au.gov.nsw.records.digitalarchive.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.gov.nsw.records.digitalarchive.utils.DAConstants;

public class XMLFileWriter implements OutputWriter{

	private static final Log log = LogFactory.getLog(XMLFileWriter.class);

	@Override
	public void writeMetadata(Map<String, String> valueMap, String writerName,
			String writerVersion, String outputFile) {
		
		try {
			File xmlFile = new File(outputFile);
			// create or open the existing one for overwriting
			if (!xmlFile.isFile()){
				xmlFile.createNewFile();
			}

			StringBuffer content = new StringBuffer();
			for (String key:valueMap.keySet()){
				String formattedKey = key.replaceAll(" ", "").replaceAll("/", "-");
				String val = valueMap.get(key).replaceAll("\\r|\\n", "").trim();
				content.append(String.format(DAConstants.XML_ELEMENT, formattedKey, val, formattedKey)); 
			}
			String output = String.format(DAConstants.XML_FILE_TEMPLATE, writerName, content.toString(), writerName);
			log.debug(output);


			FileWriter fstream = new FileWriter(xmlFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			//Close the output stream
			out.close();
		} catch (IOException e) {
			log.error("Encountered an error while creating an XML file",e);
		}
	}
}
