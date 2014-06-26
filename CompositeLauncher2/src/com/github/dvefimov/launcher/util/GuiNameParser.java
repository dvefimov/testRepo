package com.github.dvefimov.launcher.util;

public class GuiNameParser {

	/**
	 * @param name
	 *            LC name
	 * @param type
	 *            LC type
	 * @return Name of LC for gui presentation <br/>
	 *         format of LC name is '(type) name'. <br/>
	 *         so DONT use type which name contains bracket!
	 */
	public String getUIPresentationLC(String type, String name) {
		return "(" + type + ") " + name;
	}
	
	/**
	 * 
	 * @param fullName name of LC use in gui presentation
	 * @return original LC name
	 */
	public String getConfOriginalName(String fullName) {
		int start = fullName.indexOf("(") + 1;
		int end = fullName.indexOf(") ");
		if(start < 0 || end < 0){
			return fullName; // could not parse this string;
		}
		return fullName.substring(end + 2);
	}

	/**
	 * 
	 * @param fullName name of LC use in gui presentation
	 * @return LC type
	 */
	public String getConfType(String fullName) {
		int start = fullName.indexOf("(") + 1;
		int end = fullName.indexOf(") ");
		if(start < 0 || end < 0){
			return null; // could not parse this string;
		}
		return fullName.substring(start, end);
	}
}
