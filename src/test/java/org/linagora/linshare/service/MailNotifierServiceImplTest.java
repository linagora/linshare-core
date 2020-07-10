/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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
package org.linagora.linshare.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-test.xml"
		})
public class MailNotifierServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(MailNotifierServiceImplTest.class);

	@Autowired
	private NotifierService mailNotifierService;

	private Wiser wiser;

	private static String LINSHARE_MAIL = "linShare@yourdomain.com";

	public MailNotifierServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		mailNotifierService.setHost("localhost");
		wiser.start();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testSendNotification() throws MessagingException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		String subject = "subject";
		Language locale = Language.ENGLISH;
		String fromUser = "foobar@foodomain.com";
		String fromDomain = LINSHARE_MAIL;
		String recipient = "johndoe@unknow.com";

		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(locale);
		mailContainer.setSubject(subject);
		mailContainer.setContent("");
		mailContainer.setFrom(fromDomain);
		mailContainer.setReplyTo(fromUser);
		mailContainer.setRecipient(recipient);

		mailNotifierService.sendNotification(mailContainer);

		if (wiser.getMessages().size() > 0) {
			WiserMessage wMsg = wiser.getMessages().get(0);
			MimeMessage msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(),"'Subject' did not match");
			assertEquals(LINSHARE_MAIL, msg.getFrom()[0].toString(),"'From' address did not match");
			assertEquals(recipient,
					msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "'To' address did not match");
			assertEquals(fromUser, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");
		} else {
			Assertions.fail();
		}

		mailNotifierService.sendNotification(fromDomain, fromUser, recipient, subject, "<span>htmlContent</span>", null,
				null, null);

		if (wiser.getMessages().size() > 0) {
			WiserMessage wMsg = wiser.getMessages().get(1);
			MimeMessage msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(), "'Subject' did not match");
			assertEquals( LINSHARE_MAIL, msg.getFrom()[0].toString(), "'From' address did not match");
			assertEquals(recipient,
					msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "'To' address did not match");
			assertEquals(fromUser, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");
		} else {
			logger.error("No mail received");
			Assertions.fail();
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSendAllNotifications() throws MessagingException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		String subject = "subject";
		String contentTxt = "content";
		Language locale = Language.ENGLISH;
		String fromUser = "foobar@foodomain.com";
		String fromDomain = LINSHARE_MAIL;
		String recipient = "johndoe@unknow.com";
		String recipient2 = "janesmith@unknow.com";

		MailContainer mailContainer = new MailContainer(locale, contentTxt, subject);

		MailContainerWithRecipient mailContainerWithRecipient = new MailContainerWithRecipient(mailContainer, recipient,
				LINSHARE_MAIL, fromDomain);
		MailContainerWithRecipient mailContainerWithRecipient2 = new MailContainerWithRecipient(mailContainer,
				recipient2, LINSHARE_MAIL, fromDomain);

		List<MailContainerWithRecipient> mailContainerWithRecipientList = new ArrayList<MailContainerWithRecipient>();

		mailContainerWithRecipientList.add(mailContainerWithRecipient);
		mailContainerWithRecipientList.add(mailContainerWithRecipient2);

		mailNotifierService.sendNotification(mailContainerWithRecipientList);

		if (wiser.getMessages().size() > 0) {
			WiserMessage wMsg = wiser.getMessages().get(0);
			MimeMessage msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(), "'Subject' did not match");
			assertEquals(fromDomain, msg.getFrom()[0].toString(), "'From' address did not match");
			assertEquals(recipient,
					msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "'To' address did not match");
			assertEquals(LINSHARE_MAIL, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");

			wMsg = wiser.getMessages().get(1);
			msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(), "'Subject' did not match");
			assertEquals(LINSHARE_MAIL, msg.getFrom()[0].toString(), "'From' address did not match");
			assertEquals(recipient2,
					msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "'To' address did not match");
			assertEquals(LINSHARE_MAIL, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");

		} else {
			logger.error("No mail received");
			Assertions.fail();
		}

		mailContainerWithRecipientList.get(0).setReplyTo(fromUser);
		mailContainerWithRecipientList.get(1).setReplyTo(fromUser);
		mailNotifierService.sendNotification(mailContainerWithRecipientList);

		if (wiser.getMessages().size() > 0) {
			WiserMessage wMsg = wiser.getMessages().get(2);
			MimeMessage msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(), "'Subject' did not match");
			assertEquals(fromDomain, msg.getFrom()[0].toString(), "'From' address did not match");
			assertEquals(recipient, msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(),
					"'To' address did not match");
			assertEquals(fromUser, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");

			wMsg = wiser.getMessages().get(3);
			msg = wMsg.getMimeMessage();

			assertNotNull(msg, "message was null");
			assertEquals(subject, msg.getSubject(), "'Subject' did not match");
			assertEquals(fromDomain, msg.getFrom()[0].toString(), "'From' address did not match");
			assertEquals(recipient2, msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(),
					"'To' address did not match");
			assertEquals(fromUser, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");
		} else {
			logger.error("No mail received");
			Assertions.fail();
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
