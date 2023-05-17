/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Service to configure HTML Sanitizer which allows include HTML authored
 * by third-parties in LinShare while protecting against XSS
 * @see <a href="https://github.com/OWASP/java-html-sanitizer">OWASP</a>
 */
public class SanitizerInputHtmlBusinessServiceImpl implements SanitizerInputHtmlBusinessService {

	private final PolicyFactory policyFactory;

	public SanitizerInputHtmlBusinessServiceImpl() {
		this.policyFactory = new HtmlPolicyBuilder().toFactory();
	}

	/**
	 * This function clean all inputs that contains untrusted HTML that complies
	 * with default {@link PolicyFactory}.
	 * 
	 * @param entry It can contains untrusted HTML elements.
	 * @return String cleaned from all HTML and trimmed.
	 * @throws IllegalArgumentException
	 */
	@Override
	public String strictClean(String entry) throws IllegalArgumentException {
		entry = policyFactory.sanitize(entry).trim();
		if (entry.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.INVALID_FILENAME, "fileName is empty after been sanitized");
		}
		return unsanitizeSpecialChar(entry);
	}

	/**
	 * This function clean all inputs that contains untrusted HTML that complies
	 * with default {@link PolicyFactory}.
	 * 
	 * @param entry It can contains untrusted HTML elements.
	 * @return String cleaned from all HTML and trimmed. It could be an empty string.
	 */
	@Override
	public String clean(String entry) throws IllegalArgumentException {
		return unsanitizeSpecialChar(policyFactory.sanitize(entry).trim());
	}

	/**
	 * It can be used for sanitize the uploaded or updated file names
	 * 
	 * @param fileName contains untrusted HTML elements.
	 * @return String cleaned from all untrusted HTML and trimmed.
	 * @throws IllegalArgumentException
	 */
	@Override
	public String sanitizeFileName(String fileName) throws BusinessException {
		fileName = sanitizeSpecialChar(fileName);
		fileName = strictClean(fileName);
		return unsanitizeSpecialChar(fileName);
	}

	private String sanitizeSpecialChar(String fileName) {
		fileName = fileName.replace("\\", "_")
		.replace(":", "_")
		.replace("?", "_")
		.replace("^", "_")
		.replace(",", "_")
		.replace("<", "_")
		.replace(">", "_")
		.replace("*", "_")
		.replace("/", "_")
		.replace("\"", "_")
		.replace("|", "_");
		return fileName;
	}

	private String unsanitizeSpecialChar(String fileName) {
		// The OWASP HTMl sanitizer does not allows the "@" character. But on our policy
		// we need to use "@" on some documents name.
		// Some other characters are acceptable to be used in filenames
		return fileName.replace("&#64;", "@")
		.replace("&#39;", "'")
		.replace("&amp;", "&")
		.replace("&#43;", "+");
	}

	@Override
	public String sanitizeDuplicatedContactListName(String contactListName) throws BusinessException {
		contactListName = sanitizeSpecialChar(contactListName);
		return strictClean(contactListName).replace(";", "_");
	}

}
