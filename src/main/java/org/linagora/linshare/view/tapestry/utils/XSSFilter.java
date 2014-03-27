/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.view.tapestry.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
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
