package com.alten.hercules.utils;

/**
 * Class that contains elements to manage strings in the project.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public final class StrUtils {
	
	/**
	 * Regex pattern for letters
	 */
    private static final String LETTERS = "[A-ZÉa-zàáâãäåçèéêëìíîïðòóôõöùúûüýÿ]";
    /**
     * Regex pattern for a name
     */
    public static final String NAME_PATTERN = "^(" + LETTERS + "|(" + LETTERS + "[ -]" + LETTERS + "))+$";
    /**
     * Regex pattern for an email
     */
	public static final String EMAIL_PATTERN = "^[a-z]+\\.[a-z]+([1-9]|[1-9]\\d?)?@alten\\.com$";

	private StrUtils() {}
	
	/**
	 * Function that manages the case for a name
	 * @param str name
	 * @return formalized name
	 */
	public static String formaliseFirstname(String str) {
		char[] chars = str.toCharArray();
		boolean toUpperCase = true;
		
		for (int i = 0 ; i < chars.length ; i++)
			if (chars[i] == ' ' || chars[i] == '-')
				toUpperCase = true;
			else if (toUpperCase) {
				chars[i] = Character.toUpperCase(chars[i]);
				toUpperCase = false;
			} else chars[i] = Character.toLowerCase(chars[i]);
		
		return new String(chars);
	}
	
}
