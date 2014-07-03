package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UploadPropositionRuleOperatorType {

	CONTAIN, DO_NOT_CONTAIN, EQUAL, DO_NOT_EQUAL, BEGIN_WITH, END_WITH, TRUE;

	public static UploadPropositionRuleOperatorType fromString(String s) {
		try {
			return UploadPropositionRuleOperatorType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(
					TechnicalErrorCode.NO_SUCH_UPLOAD_PROPOSITION_OPERATOR_TYPE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
