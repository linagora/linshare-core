package org.linagora.linshare.core.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

public class StringPredicates {
	
	public static Predicate isNotBlank() {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				return StringUtils.isNotBlank((String) object);
			}
		};
	}
}
