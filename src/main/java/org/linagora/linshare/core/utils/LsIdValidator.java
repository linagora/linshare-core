package org.linagora.linshare.core.utils;

import java.util.regex.Pattern;

public class LsIdValidator {

	static String allowedCharacters = "a-z A-Z 0-9 _ . -";

	static Pattern formatValidator = Pattern.compile("^[a-zA-Z0-9_.-]{4,}$");

	static public boolean isValid(String value) {
		return formatValidator.matcher(value).matches();
	}

	public static String getAllowedCharacters() {
		return allowedCharacters;
	}
}
