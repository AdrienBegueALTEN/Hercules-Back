package com.alten.hercules.consts;

/**
 * Class that contains useful String constant used through the project.
 * @author mfoltz, rjesson, abegue, jbaudot
 *
 */
public class AppConst {
	
	private AppConst() {}
	
	/**
	 * Beginning of the URL with the host and port
	 */
	public static final String CLIENT_HOST = "http://localhost:4200/";
	
	/**
	 * URL for a mission sheet
	 */
	public static final String MISSION_SHEET_URL = CLIENT_HOST + "mission-sheet/";
	
	/**
	 * URL for the login
	 */
	public static final String LOGIN_URL = CLIENT_HOST + "login/";
	
	/**
	 * Regex pattern for an email
	 */
	public static final String EMAIL_PATTERN = "^[a-z]+\\.[a-z]+([1-9]|[1-9]\\d?)?@alten\\.com$";
	
}
