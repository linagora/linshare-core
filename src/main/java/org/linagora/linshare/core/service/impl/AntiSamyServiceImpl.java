package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang.StringEscapeUtils;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AntiSamyService;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntiSamyServiceImpl implements AntiSamyService {

	private static final Logger logger = LoggerFactory
			.getLogger(AntiSamyServiceImpl.class);
	
	private final Policy policy;

	public AntiSamyServiceImpl(final Policy policy) {
		this.policy = policy;
	}
	
	@Override
	public String clean(String value)
			throws BusinessException {
		if (value == null)
			return null;

		try {
			CleanResults cr = new AntiSamy().scan(value, policy);

			if (cr.getNumberOfErrors() > 0)
				logger.warn("Striped invalid chracters in : " + value);

			return StringEscapeUtils.unescapeHtml(cr.getCleanHTML().trim());
		} catch (ScanException e) {
			throw new BusinessException(
				BusinessErrorCode.XSSFILTER_SCAN_FAILED,
				"Antisamy is not able to scan the field");
		} catch (PolicyException e) {
			throw new BusinessException(
				BusinessErrorCode.XSSFILTER_SCAN_FAILED,
				"Antisamy is not able to scan the field");
		}
	}
}
