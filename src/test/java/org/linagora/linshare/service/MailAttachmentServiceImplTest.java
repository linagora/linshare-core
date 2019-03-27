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
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.core.service.NotifierService;
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
@Sql({"/import-tests-default-domain-quotas.sql",
	"/import-tests-domain-quota-updates.sql"})
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
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

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	LoadingServiceTestDatas datas;

	@Autowired
	private MailConfigRepository repository;

	@Autowired
	private DomainBusinessService domainBusinessService;

	private Account admin;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private MailBuildingService buildingService;

	@Autowired
	private NotifierService notifierService;

	private String recipientForSendMail = "felton.gumper@int6.linshare.dev";

	private boolean sendMail = false;

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
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		Assert.assertNotNull(attachment);
		Document aDocument = attachment.getDocument();
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findMailAttachmentTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		Assert.assertNotNull(attachment);
		attachment = attachmentService.find(admin, attachment.getUuid());
		Assert.assertNotNull(attachment);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateMailAttachmentsTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		Assert.assertNotNull(attachment);
		MailConfig config = repository.findByUuid("946b190d-4c95-485f-bfe6-d288a2de1edd");
		MailAttachment mailAttach = new MailAttachment(false, attachment.getDocument(), false, 1, "test update",
				"update", config, admin.getDomain(), "cid", "alt");
		attachmentService.update(admin, attachment, mailAttach);
		Assert.assertTrue(attachment.getEnable() == false);
		Assert.assertTrue(attachment.getOverride() == false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllMailAttachmentsTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		List<MailAttachment> list = attachmentService.findAllByDomain(admin, admin.getDomain());
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		Assert.assertNotNull(attachment);
		Assert.assertEquals(list.size() + 1, attachmentService.findAllByDomain(admin, admin.getDomain()).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteMailAttachmentnTest() throws IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachmentToDelete = attachmentService.create(admin, true, "Logo", true,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		MailAttachment attachment = attachmentService.create(admin, false, "Logo", false,
				"946b190d-4c95-485f-bfe6-d288a2de1edd", "Test mail attachment", "Logo", "logo.mail.attachment.test", 1,
				tempFile, null);
		List<MailAttachment> list = attachmentService.findAllByDomain(admin, admin.getDomain());
		Assert.assertTrue(list.size() == 2);
		attachmentService.delete(admin, attachmentToDelete);
		Assert.assertEquals(list.size() - 1, attachmentService.findAllByDomain(admin, admin.getDomain()).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testBuildOneMail() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		MailAttachment attachment = attachmentService.create(admin, true, "Logo", true,
				cfg.getUuid(), "Test mail attachment", "Logo", "logo.mail.attachment.test", Language.FRENCH.toInt(),
				tempFile, null);
		Assert.assertTrue(!cfg.getMailAttachments().isEmpty());

		MailContentType type = MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
		logger.info("Building mail {} ", type);
		List<TestMailResult> findErrors = Lists.newArrayList();

		List<ContextMetadata> contexts = buildingService.getAvailableVariables(type);
		for (int flavor = 0; flavor < contexts.size(); flavor++) {
			MailContainerWithRecipient build = buildingService.fakeBuild(type, cfg, Language.FRENCH, flavor);
			findErrors.addAll(testMailGenerate(type, build));
			String subject = type + " : CONTEXT=" + flavor + " : " + "LANG=" + Language.FRENCH + " : ";
			build.setSubject(subject + build.getSubject());
			sendMail(build);
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
		Assert.assertTrue(findErrors.isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private void sendMail(MailContainerWithRecipient mail) {
		if (sendMail) {
			mail.setRecipient(recipientForSendMail);
			mail.setFrom("linshare-noreply@linagora.com");
			notifierService.sendNotification(mail);
		}
	}

	public List<TestMailResult> testMailGenerate(MailContentType type, MailContainer mailContainer) {
		Assert.assertNotNull(mailContainer);
		logger.debug("Subject: {}", mailContainer.getSubject());
		logger.debug("Content: {}", mailContainer.getContent());
		Assert.assertNotNull(mailContainer.getSubject());
		Assert.assertNotNull(mailContainer.getContent());
		List<TestMailResult> findErrors = Lists.newArrayList();
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getSubject()));
		findErrors.addAll(LinShareWiser.testMailGenerate(type, mailContainer.getContent()));
		return findErrors;
	}
}
