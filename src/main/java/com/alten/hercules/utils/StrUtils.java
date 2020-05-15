package com.alten.hercules.utils;

public final class StrUtils {
	
    private static final String LETTERS = "[A-ZÉa-zàáâãäåçèéêëìíîïðòóôõöùúûüýÿ]";
    public static final String NAME_PATTERN = "^(" + LETTERS + "|(" + LETTERS + "[ -]" + LETTERS + "))+$";
	public static final String EMAIL_PATTERN = "^[a-z]+\\.[a-z]+([1-9]|[1-9]\\d?)?@alten\\.com$";

	private StrUtils() {}
	
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
