/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import com.google.common.collect.Lists;

public class LinShareWiser extends Wiser {

	protected static Logger logger = LoggerFactory.getLogger(LinShareWiser.class);

	protected static final List<String> strPatterns = Lists.newArrayList();

	static {
		// \$\{[a-zA-Z]+\}
		strPatterns.add("\\$\\{[a-zA-Z]+\\}");
		// \?\?[a-zA-Z]+\?\?
		strPatterns.add("\\?\\?[a-zA-Z_]+\\?\\?");
		// \{[0-9]+\}
		strPatterns.add("\\{[0-9]+\\}");
		strPatterns.add("null");
		strPatterns.add("<<");
		strPatterns.add(">>");
	};

	public LinShareWiser() {
		super();
	}

	public LinShareWiser(int port) {
		super(port);
	}

	public static List<TestMailResult> testMailGenerate(MailContentType type, String data) {
		List<TestMailResult> findErrors = Lists.newArrayList();
		for (String pattern : strPatterns) {
			findErrors.addAll(findErrors(type, data, pattern));
		}
		return findErrors;
	}

	public static List<TestMailResult> findErrors(MailContentType type, String data, String strPattern) {
//		String strPattern = "\\$\\{[a-zA-Z]+\\}";
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(data);
		List<TestMailResult> results = Lists.newArrayList();
		List<String> allMatches = Lists.newArrayList();
		while (matcher.find()) {
			allMatches.add(matcher.group());
		}
		logger.debug(allMatches.toString());
		if(!allMatches.isEmpty()) {
			if (type != null) {
				logger.error("type tested : " + type.name());
			}
			logger.error("Variables found : " + allMatches.toString());
			results.add(new TestMailResult(type, data, strPattern, allMatches));
		}
		return results;
	}

	public void checkGeneratedMessages() {
		List<TestMailResult> findErrors = Lists.newArrayList();
		for (WiserMessage wiserMessage : this.messages) {
			String content = wiserMessage.toString();
			findErrors.addAll(testMailGenerate(null, content));
			try {
				MimeMessage mimeMessage = wiserMessage.getMimeMessage();
				String subject = mimeMessage.getSubject();
				if (subject != null) {
					findErrors.addAll(testMailGenerate(null, subject));
				}
			} catch (MessagingException e) {
				Assert.assertTrue(false);
			}
		}
		if (!findErrors.isEmpty()) {
			logger.error("checkGeneratedMessages results : ");
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
			}
		}
		Assert.assertTrue(findErrors.isEmpty());
	}
}
