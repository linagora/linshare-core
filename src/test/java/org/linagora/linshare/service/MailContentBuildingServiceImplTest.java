/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.AnonymousDownloadEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.NewGuestEmailContext;
import org.linagora.linshare.core.notifications.context.NewSharingEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml"
		})
public class MailContentBuildingServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static final String IMPORT_LOCAL_TEST_SEG_UUID = "7d0ba756-ac50-4803-ba4f-c5bea7f46f5c";

	private static final String IMPORT_LOCAL_TEST_ASE_UUID = "3a2a4d4e-9939-4d12-8c72-6b4b5180cd87";

	private static final String IMPORT_TEST_GUEST_UUID = "46455499-f703-46a2-9659-24ed0fa0d63c";

	private static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImplTest.class);
	
	@Autowired
	private GuestRepository guestRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private MailBuildingService mailBuildingService;

	@Autowired
	private ShareEntryGroupRepository shareEntryGroupRepository;
	
	@Autowired
	private AnonymousShareEntryRepository anonymousShareEntryRepository;

	@Autowired
	private ShareEntryRepository shareEntryRepository;

	private User john;

	private User jane;

	@Autowired
	private NotifierService notifierService;

	/**
	 * If you want send generated emails to a webmail, enable this field and
	 * configure file : src/test/resources/linshare-test.properties
	 * with properties :
	 * - mail.smtp.host=
	 * - mail.smtp.port=
	 */
	private boolean sendMail = false;

	private String recipientForSendMail = "bart.simpson@int1.linshare.dev";

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail("user1@linshare.org");
		jane = userRepository.findByMail("user2@linshare.org");
		this.executeSqlScript("import-tests-mailbuildingservice.sql", false);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	private void testMailGenerate(MailContainer mailContainer){
		Assert.assertNotNull(mailContainer);
		Assert.assertNotNull(mailContainer.getSubject());
		Assert.assertNotNull(mailContainer.getContentHTML());
		Assert.assertNotNull(mailContainer.getContentTXT());
		Assert.assertFalse(mailContainer.getSubject().contains("${"));
		Assert.assertFalse(mailContainer.getContentHTML().contains("${"));
		Assert.assertFalse(mailContainer.getContentTXT().contains("${"));
	}

	@Test
	public void testBuildMailAnonymousDownload() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AnonymousShareEntry anonymousShareEntry = anonymousShareEntryRepository
				.findById(IMPORT_LOCAL_TEST_ASE_UUID);
		for (Language lang : Language.values()) {
			john.setExternalMailLocale(lang);
			MailContainerWithRecipient mail = mailBuildingService
					.build(new AnonymousDownloadEmailContext(anonymousShareEntry));
			testMailGenerate(mail);
			sendMail(mail);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailNewGuest() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = guestRepository.findByLsUuid(IMPORT_TEST_GUEST_UUID);
		for (Language lang : Language.values()) {
			john.setExternalMailLocale(lang);
			NewGuestEmailContext mailContext = new NewGuestEmailContext(john, guest, "password");
			MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
			testMailGenerate(mail);
			sendMail(mail);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailResetPassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = guestRepository.findByLsUuid(IMPORT_TEST_GUEST_UUID);
		for (Language lang : Language.values()) {
			john.setExternalMailLocale(lang);
			MailContainerWithRecipient mail =  mailBuildingService.buildResetPassword(guest, "password");
			testMailGenerate(mail);
			sendMail(mail);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailNewSharing() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailContainer mailContainer = new MailContainer(Language.ENGLISH);
		for (Language lang : Language.values()) {
			john.setExternalMailLocale(lang);
			EmailContext context = new NewSharingEmailContext(mailContainer, john, jane, getSeg().getShareEntries());
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			testMailGenerate(mail);
			sendMail(mail);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildMailNoDocumentHasBeenDownloaded()
			throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		ShareEntryGroup seg = getSeg();
		// Force initialization of tmp* members.
		seg.needNotification();
		for (Language lang : Language.values()) {
			john.setExternalMailLocale(lang);
			MailContainerWithRecipient mail = mailBuildingService
					.buildNoDocumentHasBeenDownloadedAcknowledgement(seg);
			testMailGenerate(mail);
			sendMail(mail);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private ShareEntryGroup getSeg() {
		ShareEntryGroup seg = shareEntryGroupRepository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID);
		Assert.assertNotNull(seg);
		return seg;
	}

	private void sendMail(MailContainerWithRecipient mail) {
		if (sendMail) {
			mail.setRecipient(recipientForSendMail);
			notifierService.sendNotification(mail);
		}
	}
}
