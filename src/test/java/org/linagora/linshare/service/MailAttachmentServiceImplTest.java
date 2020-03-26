/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2019 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.utils.LinShareWiser;
import org.linagora.linshare.utils.TestMailResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
		"/import-tests-domain-quota-updates.sql" })
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml" })
public class MailAttachmentServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(MailAttachmentServiceImplTest.class);

	@Autowired
	protected MailAttachmentService attachmentService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;

	@Autowired
	private MailConfigRepository repository;

	@Autowired
	private DomainBusinessService domainBusinessService;

	private Account admin;

	private final String defaultMailAttachmentCid = "logo.linshare@linshare.org";

	@Autowired
	private MailBuildingService buildingService;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		admin = datas.getRoot();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createMailAttachmentTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		Assertions.assertNotNull(attachment);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findMailAttachmentTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		Assertions.assertNotNull(attachment);
		attachment = attachmentService.find(admin, attachment.getUuid());
		Assertions.assertNotNull(attachment);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateMailAttachmentsTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		Assertions.assertNotNull(attachment);
		MailConfig config = repository.findByUuid("946b190d-4c95-485f-bfe6-d288a2de1edd");
		MailAttachment mailAttach = new MailAttachment(false, false, Language.FRENCH, "test update", "update", config, "cid");
		mailAttach.setUuid(attachment.getUuid());
		attachmentService.update(admin, mailAttach);
		Assertions.assertTrue(attachment.getEnable() == false);
		Assertions.assertTrue(attachment.getEnableForAll() == false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllMailAttachmentsTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailConfig config = repository.findByUuid("946b190d-4c95-485f-bfe6-d288a2de1edd");
		List<MailAttachment> list = attachmentService.findAllByMailConfig(admin, config);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		Assertions.assertNotNull(attachment);
		Assertions.assertEquals(list.size() + 1, attachmentService.findAllByMailConfig(admin, config).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteMailAttachmentnTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachmentToDelete = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		attachmentService.create(admin, false, "Logo", false,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		MailConfig config = repository.findByUuid("946b190d-4c95-485f-bfe6-d288a2de1edd");
		List<MailAttachment> list = attachmentService.findAllByMailConfig(admin, config);
		Assertions.assertTrue(list.size() == 2);
		attachmentService.delete(admin, attachmentToDelete.getUuid());
		Assertions.assertEquals(list.size() - 1, attachmentService.findAllByMailConfig(admin, config).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMailAttachmentBuild() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		InputStream stream = getStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		//EnableForAll is disabled and the language != emailContext language -> not inserted
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", false,
				cfg.getUuid(), "Test mail attachment", "logo.mail.attachment.test", Language.FRENCH,
				tempFile, null);
		//EnableForAll is disabled and the language == emailContext language -> inserted
		MailAttachment attachment2 = attachmentService.create(admin, true, "Logo", false,
				cfg.getUuid(), "Test mail attachment", "logo.mail.attachment2.test", Language.ENGLISH,
				tempFile, null);
		//EnableForAll is enabled and the language != emailContext language -> inserted
		MailAttachment attachment3 = attachmentService.create(admin, true, "Logo", true,
				cfg.getUuid(), "Test mail attachment", "logo.mail.attachment3.test", Language.RUSSIAN,
				tempFile, null);
		//Enable is disabled and the language != emailContext language -> not inserted
		MailAttachment attachment4 = attachmentService.create(admin, false, "Logo", true,
				cfg.getUuid(), "Test mail attachment", "logo.mail.attachment4.test", Language.RUSSIAN,
				tempFile, null);
		// !cfg.getMailAttachments().isEmpty() -> default mail attachment is not inserted
		Assertions.assertFalse(cfg.getMailAttachments().isEmpty());
		MailContentType type = MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
		logger.info("Building mail {} ", type);
		List<TestMailResult> findErrors = Lists.newArrayList();
		List<ContextMetadata> contexts = buildingService.getAvailableVariables(type);
		for (int flavor = 0; flavor < contexts.size(); flavor++) {
			MailContainerWithRecipient build = buildingService.fakeBuild(type, cfg, Language.ENGLISH, flavor);
			Assertions.assertAll("Failure in mail attachment insertion", () -> {
				Assertions.assertFalse(build.getAttachments().containsKey(defaultMailAttachmentCid));
				Assertions.assertFalse(build.getAttachments().containsKey(attachment.getCid()));
				Assertions.assertTrue(build.getAttachments().containsKey(attachment2.getCid()));
				Assertions.assertTrue(build.getAttachments().containsKey(attachment3.getCid()));
				Assertions.assertFalse(build.getAttachments().containsKey(attachment4.getCid()));
			});
			findErrors.addAll(testMailGenerate(type, build));
			String subject = type + " : CONTEXT=" + flavor + " : " + "LANG=" + Language.FRENCH + " : ";
			build.setSubject(subject + build.getSubject());
		}
		if (!findErrors.isEmpty()) {
			for (TestMailResult result : findErrors) {
				logger.error(result.toString());
				logger.error(result.toString());
				if (logger.isTraceEnabled()) {
					logger.trace("StrPattern : {}", result.getStrPattern());
					logger.trace("Data : {}", result.getData());
				}
			}
		}
		Assertions.assertTrue(findErrors.isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	public List<TestMailResult> testMailGenerate(MailContentType type, MailContainer mailContainer) {
		Assertions.assertNotNull(mailContainer);
		logger.debug("Subject: {}", mailContainer.getSubject());
		logger.debug("Content: {}", mailContainer.getContent());
		Assertions.assertNotNull(mailContainer.getSubject());
		Assertions.assertNotNull(mailContainer.getContent());
		List<TestMailResult> findErrors = Lists.newArrayList();
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getSubject()));
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getContent()));
		return findErrors;
	}

	private InputStream getStream(String resourceName) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}
}
