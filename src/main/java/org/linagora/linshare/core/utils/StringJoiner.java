package org.linagora.linshare.core.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringJoiner {

	public static List<String> split(String s, String delim) {
		return Arrays.asList(s.split(delim));
	}
	
	public static String join(List<String> s, String delim) {
		return StringUtils.join(s.toArray(), delim);
	}
}
