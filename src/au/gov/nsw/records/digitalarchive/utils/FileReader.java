package au.gov.nsw.records.digitalarchive.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class FileReader {

	 public static String read(String file) throws IOException {
		    StringBuilder text = new StringBuilder();
		    String NL = System.getProperty("line.separator");
		    Scanner scanner = new Scanner(new FileInputStream(file));
		    try {
		      while (scanner.hasNextLine()){
		        text.append(scanner.nextLine() + NL);
		      }
		    }
		    finally{
		      scanner.close();
		    }
		    return text.toString();
		  }
}
