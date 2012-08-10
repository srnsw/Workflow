package au.gov.nsw.records.digitalarchive.utils;

import java.io.File;
import java.util.regex.Pattern;

public class StringHelper {

	public static String trimFirstSlash(String str){
		if (str.startsWith("/")){
			return str.replaceFirst(Pattern.quote("/"), "");
		}
		return str;
	}
	
	public static String trimLastSlash(String str){
		if (str.endsWith("/")){
			return str.substring(0, str.length() - 1);
		}
		return str;
	}
	
	public static String trimAfterLastSlash(String str){
		if (str.lastIndexOf("/") > 0){
			return str.substring(0, str.lastIndexOf("/"));
		}
		return str;
	}
	
	public static String formatUnixPath(String str){
		// format Windows' backslash path to slash to comply with URL convention 
		return str.replaceAll(Pattern.quote("\\"), "/");
	}
	
	/**
	 * Isilon's directory string must start with '/ifs'
	 * @param input
	 * @return
	 */
	public static String formatIsilonPathPrefix(String input){
		if (!input.startsWith("/")){
			input = "/" + input;
		}
		if (!input.startsWith("/ifs")){
			input = "/ifs" + input;
		}
		return input;
	}
	
	/**
	 * Isilon's directory string must end with '/'
	 * Isilon's directory string must start with '/ifs'
	 * @param input
	 * @return
	 */
	public static String formatIsilonDirectoryString(String input){
		input = formatUnixPath(input);
		input = formatIsilonPathPrefix(input);
		if (!input.endsWith("/")){
			input = input + "/";
		}
		return input;
	}
	
	public static String joinDirectoryString(String prefix, String... paths ){
		StringBuffer buffer = new StringBuffer();

		if (!prefix.endsWith(String.valueOf(File.separatorChar))){
			buffer.append(prefix);
		}else{
			// remove the last file sep char
			buffer.append(prefix.substring(0, prefix.length() - 1));
		}
		for (String path:paths){
			buffer.append(File.separatorChar);
			buffer.append(path);
		}
		
		return buffer.toString();
	}
	
	public static String formatIsilonImmediateDirectoryFromFileString(String... paths){
		return "";
	}
}
