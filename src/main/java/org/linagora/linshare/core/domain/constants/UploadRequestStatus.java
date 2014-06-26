package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UploadRequestStatus {
	STATUS_CREATED,
	STATUS_CANCELED,
	STATUS_ENABLED,
	STATUS_CLOSED,
	STATUS_ARCHIVED,
	STATUS_DELETED;

	public static UploadRequestStatus fromString(String s) {
		try {
			return UploadRequestStatus.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_TECHNICAL_PERMISSION_TYPE, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
