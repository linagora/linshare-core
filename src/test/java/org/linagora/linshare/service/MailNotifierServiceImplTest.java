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
package org.linagora.linshare.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(MailNotifierServiceImplTest.class);

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
		LOGGER.debug(LinShareTestConstants.BEGIN_SETUP);
		mailNotifierService.setHost("localhost");
		wiser = new Wiser(2525) {
			@Override
			public boolean accept(String from, String recipient) {
				if ("invalid@example.com".equals(recipient)){
					return false;
				}
				return true;
			}
		};
		wiser.start();
		LOGGER.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() {
		LOGGER.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		LOGGER.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testSendNotification() throws MessagingException {
		LOGGER.info(LinShareTestConstants.BEGIN_TEST);

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
			LOGGER.error("No mail received");
			Assertions.fail();
		}
		LOGGER.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSendAllNotifications() throws MessagingException, BusinessException {
		LOGGER.info(LinShareTestConstants.BEGIN_TEST);

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
			LOGGER.error("No mail received");
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
			LOGGER.error("No mail received");
			Assertions.fail();
		}
		LOGGER.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * Tests the notification system's behavior when sending emails to both valid and invalid recipients.
	 * Verifies that:
	 * - Emails to valid recipients are successfully delivered.
	 * - Emails to invalid recipients are marked as failed.
	 */
	@Test
	void sendNotificationToInvalidAdressMail() throws MessagingException{
		LOGGER.info(LinShareTestConstants.BEGIN_TEST);

		final String subject = "subject";
		final Language locale = Language.ENGLISH;
		final String fromUser = "foobar@foodomain.com";
		final String fromDomain = LINSHARE_MAIL;
		final String validRecipient = "valid@example.com";
		final String invalidRecipient = "invalid@example.com";

		final MailContainerWithRecipient validMail = new MailContainerWithRecipient(locale);
		validMail.setSubject(subject);
		validMail.setContent("validContent");
		validMail.setFrom(fromDomain);
		validMail.setReplyTo(fromUser);
		validMail.setRecipient(validRecipient);

		final MailContainerWithRecipient invalidMail = new MailContainerWithRecipient(locale);
		invalidMail.setSubject(subject);
		invalidMail.setContent("invalidContent");
		invalidMail.setFrom(fromDomain);
		invalidMail.setReplyTo(fromUser);
		invalidMail.setRecipient(invalidRecipient);

		final List<MailContainerWithRecipient> mailContainerWithRecipients = new ArrayList<>();
		mailContainerWithRecipients.add(validMail);
		mailContainerWithRecipients.add(invalidMail);

		final List<String> failedRecipients = this.mailNotifierService.sendNotification(mailContainerWithRecipients);

		assertEquals(1, failedRecipients.size(), "Expected 1 failed recipient");
		assertTrue(failedRecipients.contains(invalidRecipient), "Failed recipients should contain " + invalidRecipient);

		assertEquals(1, this.wiser.getMessages().size(), "Expected 1 successful email");
		WiserMessage wiserMsg = this.wiser.getMessages().get(0);
		MimeMessage msg = wiserMsg.getMimeMessage();

		assertNotNull(msg, "Message should not be null");
		assertEquals(subject, msg.getSubject(), "Subject should match");
		assertEquals(validRecipient, msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "Recipient should match");

		LOGGER.debug(LinShareTestConstants.END_TEST);
	}
}
