package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.gov.naa.digipres.xena.kernel.type.DefaultFileType;
import au.gov.naa.digipres.xena.kernel.type.Type;
import au.gov.naa.digipres.xena.plugin.office.OfficePlugin;
import au.gov.naa.digipres.xena.plugin.office.presentation.PowerpointFileType;
import au.gov.naa.digipres.xena.plugin.office.presentation.PptxFileType;
import au.gov.naa.digipres.xena.plugin.office.presentation.SxiFileType;
import au.gov.naa.digipres.xena.plugin.office.spreadsheet.ExcelFileType;
import au.gov.naa.digipres.xena.plugin.office.spreadsheet.SxcFileType;
import au.gov.naa.digipres.xena.plugin.office.spreadsheet.SylkFileType;
import au.gov.naa.digipres.xena.plugin.office.spreadsheet.XlsxFileType;
import au.gov.naa.digipres.xena.plugin.office.wordprocessor.DocxFileType;
import au.gov.naa.digipres.xena.plugin.office.wordprocessor.RtfFileType;
import au.gov.naa.digipres.xena.plugin.office.wordprocessor.SxwFileType;
import au.gov.naa.digipres.xena.plugin.office.wordprocessor.WordFileType;
import au.gov.naa.digipres.xena.plugin.office.wordprocessor.WordPerfectFileType;

public class DAOfficePlugin extends OfficePlugin {

	@Override
	public String getName() {
		return "DA_OFFICE";
	}
	
	@Override
	public Map<Object, Set<Type>> getNormaliserInputMap() {
		Map<Object, Set<Type>> def = new HashMap<Object, Set<Type>>();
		
		// Normaliser
		DAMultiNormaliser normaliser = new DAMultiNormaliser();
		Set<Type> normaliserSet = new HashSet<Type>();
		normaliserSet.add(new WordFileType());
		normaliserSet.add(new ExcelFileType());
		normaliserSet.add(new PowerpointFileType());
		normaliserSet.add(new SxiFileType());
		normaliserSet.add(new SxcFileType());
		normaliserSet.add(new SxwFileType());
		normaliserSet.add(new RtfFileType());
		normaliserSet.add(new SylkFileType());
		normaliserSet.add(new WordPerfectFileType());
		normaliserSet.add(new XlsxFileType());
		normaliserSet.add(new DocxFileType());
		normaliserSet.add(new PptxFileType());
		def.put(normaliser, normaliserSet);

		return def;
	}
	
	@Override
	public Map<Object, Set<Type>> getNormaliserOutputMap() {
		Map<Object, Set<Type>> outputMap = super.getNormaliserOutputMap();

		// Normaliser
		DAMultiNormaliser normaliser = new DAMultiNormaliser();
		Set<Type> normaliserSet = new HashSet<Type>();
		normaliserSet.add(new DefaultFileType());
		outputMap.put(normaliser, normaliserSet);

		return outputMap;
	}
}
