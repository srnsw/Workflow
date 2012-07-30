package au.gov.nsw.records.digitalarchive.action.preservation;

import java.util.List;

import au.gov.nsw.records.digitalarchive.action.wrapper.XenaWrapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The file format conversion action implementing on top of XENA library and plugins
 * @author wisanup
 * @see <a href="http://sourceforge.net/projects/xena/">XENA website</a>
 */
@XStreamAlias("xena-normalisation")
public class XenaPreservationPath implements PreservationPath{
	
	@XStreamOmitField
	private static XenaWrapper xena = new XenaWrapper();
	

	@Override
	public boolean createPreservedFiles(List<String> inputFiles) {
		boolean isError = false;
		for (String f:inputFiles){
			if (createPreservedFile(f)==null){
				isError = true;
			}
		}
		return !isError;
	}

	@Override
	public String createPreservedFile(String inputFile) {
		
		return xena.cratePerservedFile(inputFile);
	}
}
