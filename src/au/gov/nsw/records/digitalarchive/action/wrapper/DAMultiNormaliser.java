package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.gov.naa.digipres.xena.kernel.normalise.AbstractNormaliser;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserManager;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;
import au.gov.naa.digipres.xena.plugin.office.OfficeToXenaOooNormaliser;

public class DAMultiNormaliser extends AbstractNormaliser {

	private List<AbstractNormaliser> normalisers;
	
	public DAMultiNormaliser(){
		normalisers = new ArrayList<AbstractNormaliser>();
		
		//normalisers.add(new OfficeTextNormaliser());
		normalisers.add(new OfficeToXenaOooNormaliser());
		normalisers.add(new OfficeToXenaOooNormaliser());
	}
	
	/**
	 * @param normaliserManager The new value to set normaliserManager to.
	 */
	public void setNormaliserManager(NormaliserManager normaliserManager) {
		super.setNormaliserManager(normaliserManager);
		for (AbstractNormaliser norm : normalisers){
			norm.setNormaliserManager(normaliserManager);
		}
	}
	
	
	@Override
	public String getOutputFileExtension() {
		// TODO ??
		return "err";
	}

	@Override
	public boolean isConvertible() {
		for (AbstractNormaliser norm : normalisers){
			if (!norm.isConvertible()){
				return false;
			}
		}
		return true;
	}

	@Override
	public void parse(InputSource input, NormaliserResults results,
			boolean convertOnly) throws IOException, SAXException {
		for (AbstractNormaliser norm : normalisers){
			norm.parse(input, results, convertOnly);
		}
	}

	@Override
	public String getName() {
		String name = "";
		for (AbstractNormaliser norm : normalisers){
			name = name + "," + norm.getName();
		}
		return name;
	}

}
