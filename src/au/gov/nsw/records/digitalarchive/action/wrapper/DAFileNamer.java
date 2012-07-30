package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.io.File;
import java.io.FileFilter;

import au.gov.naa.digipres.xena.kernel.XenaException;
import au.gov.naa.digipres.xena.kernel.XenaInputSource;
import au.gov.naa.digipres.xena.kernel.filenamer.AbstractFileNamer;
import au.gov.naa.digipres.xena.kernel.filenamer.FileNamerManager;
import au.gov.naa.digipres.xena.kernel.normalise.AbstractNormaliser;

/**
 * The Digital Archive specific file namer
 * @author wisanup
 * @see AbstractFileNamer
 */
public class DAFileNamer extends AbstractFileNamer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "SRNSW Digital Archive File Namer";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileFilter makeFileFilter() {
		 return FileNamerManager.DEFAULT_FILE_FILTER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File makeNewOpenFile(XenaInputSource xis, AbstractNormaliser normaliser,
			File destinationDir) throws XenaException {
		
		String fileName = xis.getFile().getName();
        String outputFileName = String.format("%s.xena.preserved.%s", fileName, normaliser.getOutputFileExtension());

        File outputFile = new File(destinationDir, outputFileName);
        return outputFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File makeNewXenaFile(XenaInputSource arg0, AbstractNormaliser arg1,
			File arg2) throws XenaException {
		return makeNewOpenFile(arg0, arg1, arg2);
		//throw new UnsupportedOperationException("SRNSW Digital Archive does not produce Xena file");
	}

}
