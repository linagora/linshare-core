package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UploadPropositionRuleFieldType {

	SENDER_EMAIL,
	RECIPIENT_EMAIL,
	RECIPIENT_DOMAIN,
	SUBJECT;

	public static UploadPropositionRuleFieldType fromString(String s) {
		try {
			return UploadPropositionRuleFieldType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(
					TechnicalErrorCode.NO_SUCH_UPLOAD_PROPOSITION_RULE_FIELD_TYPE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
