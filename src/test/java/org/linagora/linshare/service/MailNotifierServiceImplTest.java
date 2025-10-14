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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
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

	private static final String LINSHARE_MAIL = "linShare@yourdomain.com";
	private static final String SUBJECT = "Test Subject";
	private static final Language LOCALE = Language.ENGLISH;
	private static final String FROM_USER = "sender@example.com";
	private static final String FROM_DOMAIN = LINSHARE_MAIL;
	private static final String VALID_RECIPIENT_1 = "valid1@example.com";
	private static final String VALID_RECIPIENT_2 = "valid2@example.com";
	private static final String INVALID_RECIPIENT = "invalid@example.com";

	private static final List<String> EMPTY_LIST = Collections.emptyList();
	private static final List<String> SINGLE_INVALID_RECIPIENT_LIST = List.of(INVALID_RECIPIENT);
	private static final List<String> SINGLE_VALID_RECIPIENT_LIST = List.of(VALID_RECIPIENT_1);
	private static final List<String> MULTIPLE_VALID_RECIPIENTS_LIST = List.of(VALID_RECIPIENT_1, VALID_RECIPIENT_2);
	private static final List<String> MIXED_RECIPIENTS_LIST = List.of(VALID_RECIPIENT_1, INVALID_RECIPIENT, VALID_RECIPIENT_2);
	private static final List<String> MULTIPLE_INVALID_WITH_VALID_LIST = List.of(INVALID_RECIPIENT, VALID_RECIPIENT_1);
	private static final List<MailContainerWithRecipient> LIST_WITH_NULL_ELEMENTS = Arrays.asList(
			null,
			createMailContainer(VALID_RECIPIENT_1),
			null
	);

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
				return !"invalid@example.com".equals(recipient);
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

	/**
	 * Test to send notifications with various scenarios in a batch mode.
	 *
	 * @param testName Descriptive name of the test scenario
	 * @param mailContainers List of mail containers to send (may contain null elements)
	 * @param expectedFailedRecipients List of recipient addresses expected to fail
	 * @param expectedSuccessfulRecipients List of recipient addresses expected to succeed
	 */
	@ParameterizedTest
	@MethodSource("provideSendNotificationScenarios")
	void sendNotification_toMailContainers(
			@Nonnull final String testName,
			@Nonnull final List<MailContainerWithRecipient> mailContainers,
			@Nonnull final List<String> expectedFailedRecipients,
			@Nonnull final List<String> expectedSuccessfulRecipients) throws MessagingException {
		LOGGER.info("Running test: {}", testName);
		final List<String> failedRecipients = this.mailNotifierService.sendNotification(mailContainers);
		assertThat(failedRecipients)
				.containsExactlyInAnyOrderElementsOf(expectedFailedRecipients);
		assertEquals(expectedSuccessfulRecipients.size(), this.wiser.getMessages().size(),
				"Number of successful emails should match expected successful recipients count");
		if (!expectedSuccessfulRecipients.isEmpty()) {
			for (final WiserMessage wMsg : this.wiser.getMessages()) {
				final MimeMessage msg = wMsg.getMimeMessage();
				assertNotNull(msg, "Message should not be null");
				assertEquals(1, msg.getRecipients(MimeMessage.RecipientType.TO).length,
						"Only one recipient 'TO' can be transmitted through 'MailContainerWithRecipient'");
				final String actualRecipient = msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString();
				assertTrue(expectedSuccessfulRecipients.contains(actualRecipient),
						"Recipient '" + actualRecipient + "' should be in expected successful recipients list");
				assertFalse(expectedFailedRecipients.contains(actualRecipient),
						"Recipient '" + actualRecipient + "' should not be in failed recipients list");
				assertEquals(SUBJECT, msg.getSubject(), "Subject should match");
				assertEquals(FROM_DOMAIN, msg.getFrom()[0].toString(), "From address should match");
			}
		}
	}

	private static Stream<Arguments> provideSendNotificationScenarios() {
		return Stream.of(
				Arguments.of(
						"Empty list of recipients",
						EMPTY_LIST,
						EMPTY_LIST,
						0
				),
				Arguments.of(
						"List with valid recipient and null elements",
						LIST_WITH_NULL_ELEMENTS,
						EMPTY_LIST,
						1
				),
				Arguments.of(
						"Single invalid recipient",
						createMailContainers(SINGLE_INVALID_RECIPIENT_LIST),
						SINGLE_INVALID_RECIPIENT_LIST,
						0
				),
				Arguments.of(
						"Single valid recipient",
						createMailContainers(SINGLE_VALID_RECIPIENT_LIST),
						EMPTY_LIST,
						1
				),
				Arguments.of(
						"Mixed valid and invalid recipients",
						createMailContainers(MIXED_RECIPIENTS_LIST),
						SINGLE_INVALID_RECIPIENT_LIST,
						2
				),
				Arguments.of(
						"Multiple invalid recipients",
						createMailContainers(MULTIPLE_INVALID_WITH_VALID_LIST),
						SINGLE_INVALID_RECIPIENT_LIST,
						1
				),
				Arguments.of(
						"All valid recipients",
						createMailContainers(MULTIPLE_VALID_RECIPIENTS_LIST),
						EMPTY_LIST,
						2
				)
		);
	}

	private static List<MailContainerWithRecipient> createMailContainers(List<String> recipients) {
		return recipients.stream()
				.map(MailNotifierServiceImplTest::createMailContainer)
				.collect(Collectors.toList());
	}

	/**
	 * Test to send notification to a single recipient with detailed email verification.
	 * Kept as separate test for detailed content verification
	 */
	@Test
	void sendNotification_SingleEmail_ShouldSendCorrectly() throws MessagingException {
		LOGGER.info(LinShareTestConstants.BEGIN_TEST);
		final String recipient = "johndoe@unknow.com";
		final MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(LOCALE);
		mailContainer.setSubject(SUBJECT);
		mailContainer.setContent("");
		mailContainer.setFrom(FROM_DOMAIN);
		mailContainer.setReplyTo(FROM_USER);
		mailContainer.setRecipient(recipient);
		this.mailNotifierService.sendNotification(mailContainer);
		assertEquals(1, wiser.getMessages().size(), "Should have 1 successful email");
		WiserMessage wMsg = wiser.getMessages().get(0);
		MimeMessage msg = wMsg.getMimeMessage();
		assertNotNull(msg, "message was null");
		assertEquals(SUBJECT, msg.getSubject(), "'Subject' did not match");
		assertEquals(FROM_DOMAIN, msg.getFrom()[0].toString(), "'From' address did not match");
		assertEquals(recipient,
				msg.getRecipients(MimeMessage.RecipientType.TO)[0].toString(), "'To' address did not match");
		assertEquals(FROM_USER, msg.getReplyTo()[0].toString(), "'ReplyTo' address did not match");
		LOGGER.debug(LinShareTestConstants.END_TEST);
	}

	private static MailContainerWithRecipient createMailContainer(@Nonnull final String recipient) {
		final MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(LOCALE);
		mailContainer.setSubject(SUBJECT);
		mailContainer.setContent("Test content for " + recipient);
		mailContainer.setFrom(FROM_DOMAIN);
		mailContainer.setReplyTo(FROM_USER);
		mailContainer.setRecipient(recipient);
		return mailContainer;
	}

}