package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UploadPropositionMatchType {

	ALL, ANY, TRUE;

	public static UploadPropositionMatchType fromString(String s) {
		try {
			return UploadPropositionMatchType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(
					TechnicalErrorCode.NO_SUCH_UPLOAD_PROPOSITION_MATCH_TYPE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}

}
