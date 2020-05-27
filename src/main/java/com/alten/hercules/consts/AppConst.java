package com.alten.hercules.consts;

public class AppConst {
	
	private AppConst() {}
	
	public static final String CLIENT_HOST = "http://localhost:4200/";
	public static final String MISSION_SHEET_URI = CLIENT_HOST + "mission-sheet/";
	public static final String PASSWORD_CREATION_URI = CLIENT_HOST + "password-creation/";
	public static final String EMAIL_PATTERN = "^[a-z]+\\.[a-z]+([1-9]|[1-9]\\d?)?@alten\\.com$";
	
}
