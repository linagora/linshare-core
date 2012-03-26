package org.linagora.linShare.view.tapestry.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSSFilter {
	
	private static final String MESSAGE_KEY = "error.code.XSSFilter.scan_failed";
	
	private static final Logger logger = LoggerFactory.getLogger(XSSFilter.class);

    private Messages messages;
	
	private Policy antiSamyPolicy;
	
	private ShareSessionObjects shareSessionObjects;
	
	private Form form;

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
	}
	
	/**
	 * Scan a String to find XSS and clean it
	 * @param value the String to scan
	 * @return the cleaned string or null if cleaning failed
	 */
	public String clean(String value) {
		String cleaned = null;
		CleanResults cr = null;
		AntiSamy as = new AntiSamy();
		boolean failed = false;
		
		if (value == null)
			return null;
				
		try {
			cr = as.scan(value, antiSamyPolicy);
			cleaned = cr.getCleanHTML().trim();

            // AntiSamy (> 1.4) encodes intl chars as html entities
            cleaned = StringEscapeUtils.unescapeHtml(cleaned);
		} catch (ScanException e) {
			failed = true;
			logger.error("Antisany is not able to scan the subject field");
			e.printStackTrace();
		} catch (PolicyException e) {
			failed = true;
			logger.error("Antisany is not able to get the antiSamy policy");
			e.printStackTrace();
		}
		if (failed) {
			if (form != null) {
				form.recordError(messages.get(MESSAGE_KEY));
			}
			if (shareSessionObjects != null) {
				shareSessionObjects.addError(messages.get(MESSAGE_KEY));
			}
		}
		
		return cleaned;
	}
	
}
