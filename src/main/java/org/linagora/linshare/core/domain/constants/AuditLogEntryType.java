package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum AuditLogEntryType {

	SHARE,
	DOCUMENT,
	GUEST,
	THREAD,
	USER;

	public static AuditLogEntryType fromString(String s) {
		try {
			return AuditLogEntryType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
