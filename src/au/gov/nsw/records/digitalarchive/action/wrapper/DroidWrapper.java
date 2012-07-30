package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

/**
 * The simplification usage of Droid's functionality
 * @author wisanup
 *
 */
public class DroidWrapper {

	private BinarySignatureIdentifier droid;

	/**
	 * Create an instance of DroidWrapper
	 * @param signatureLocaion the location of Droid's signature file
	 */
	public DroidWrapper(String signatureLocaion){
		droid = new BinarySignatureIdentifier();
		droid.setSignatureFile(signatureLocaion);
		droid.init();
	}

	/**
	 * Identify the file format of the file in the given location
	 * @param fileLocation the location of the file to identify the mime-type
	 * @return the result of identification
	 * @throws IOException 
	 */
	public IdentificationResult getIdentification(String fileLocation) throws IOException{

		File file = new File(fileLocation);
		URI resourceUri = file.toURI();

		InputStream in = new FileInputStream(file);
		RequestIdentifier identifier = new RequestIdentifier(resourceUri);
		identifier.setParentId(1L);

		RequestMetaData metaData = new RequestMetaData(file.length(), file.lastModified(), fileLocation);

		IdentificationRequest request = new FileSystemIdentificationRequest(metaData, identifier);
		try{
			request.open(in);
			IdentificationResultCollection results = droid.matchBinarySignatures(request);
			if (results.getResults().size() > 0){
				return results.getResults().iterator().next();
			}else{
				return null;
			}
		}finally{
			request.close();
		}


	}
}
