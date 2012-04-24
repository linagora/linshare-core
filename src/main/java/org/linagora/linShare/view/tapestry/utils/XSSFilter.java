package org.linagora.linShare.view.tapestry.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSSFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(XSSFilter.class);
	
	private BusinessUserMessage warningMessage;

	private Messages messages;
	
	private Policy antiSamyPolicy;
	
	private ShareSessionObjects shareSessionObjects;
	
	private Form form;
	
	private boolean error;


	/**
	 * @param shareSessionObjects
	 * @param form
	 * @param antiSamyPolicy
	 */
	public XSSFilter(ShareSessionObjects shareSessionObjects, Form form, Policy antiSamyPolicy, Messages messages) {
		super();
		this.antiSamyPolicy = antiSamyPolicy;
		this.shareSessionObjects = shareSessionObjects;
		this.form = form;
		this.messages = messages;
		this.warningMessage = new BusinessUserMessage(BusinessUserMessageType.WARNING_TAGS_FOUND,
				MessageSeverity.WARNING);
		this.error = false;
	}

	/**	 
	 * @param antiSamyPolicy
	 */
	public XSSFilter(Policy antiSamyPolicy, Messages messages) {
		super();
		this.antiSamyPolicy = antiSamyPolicy;
		this.messages = messages;
		this.shareSessionObjects = null;
		this.form = null;
		this.error = false;
		this.warningMessage = new BusinessUserMessage(BusinessUserMessageType.WARNING_TAGS_FOUND,
				MessageSeverity.WARNING);
	}
	
	/**
	 * Scan a String to find XSS and clean it
	 * @param value the String to scan
	 * @return the cleaned string or null if cleaning failed
	 */
	public String clean(String value) throws BusinessException {
		String cleaned = null;
		CleanResults cr = null;
		AntiSamy as = new AntiSamy();
		boolean failed = false;	
		String msg = null;
		
		if (value == null)
			return null;
				
		try {
			cr = as.scan(value, antiSamyPolicy);
			error |= (cr.getNumberOfErrors() > 0);
			cleaned = cr.getCleanHTML().trim();

            // AntiSamy (> 1.4) encodes intl chars as html entities
            cleaned = StringEscapeUtils.unescapeHtml(cleaned);
		} catch (ScanException e) {
			failed = true;
			msg = "Antisany is not able to scan the field";
			logger.error(e.getMessage());
			logger.debug(e.toString());
		} catch (PolicyException e) {
			failed = true;
			msg = "Antisany is not able to get the antiSamy policy";
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		if (failed) {
			throw new BusinessException(BusinessErrorCode.XSSFILTER_SCAN_FAILED, msg);
		}
		
		return cleaned;
	}
	
	public boolean hasError() {
		return error;
	}	

    public BusinessUserMessage getWarningMessage() {
		return warningMessage;
	}
}
