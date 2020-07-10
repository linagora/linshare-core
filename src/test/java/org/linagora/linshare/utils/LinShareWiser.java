/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Assertions;
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
				Assertions.assertTrue(false);
			}
		}
		if (!findErrors.isEmpty()) {
			logger.error("checkGeneratedMessages results : ");
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
			}
		}
		Assertions.assertTrue(findErrors.isEmpty());
	}
}
