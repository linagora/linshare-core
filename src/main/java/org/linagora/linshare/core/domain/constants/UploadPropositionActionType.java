package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UploadPropositionActionType {

	ACCEPT, REJECT, MANUAL;

	public static UploadPropositionActionType fromString(String s) {
		try {
			return UploadPropositionActionType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(
					TechnicalErrorCode.NO_SUCH_UPLOAD_PROPOSITION_ACTION_TYPE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
