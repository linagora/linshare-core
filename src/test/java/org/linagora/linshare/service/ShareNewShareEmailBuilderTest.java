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

package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ "/import-tests-document-entry-setup.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class ShareNewShareEmailBuilderTest {

	private static Logger logger = LoggerFactory.getLogger(ShareNewShareEmailBuilderTest.class);

	@Autowired
	@Qualifier("shareService")
	private ShareService shareService;

	@Autowired
	@Qualifier("documentEntryBusinessService")
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	@Qualifier("documentEntryService")
	private DocumentEntryService documentEntryService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private DomainBusinessService domainBusinessService;

	@Autowired
	protected MailAttachmentService attachmentService;

	private LoadingServiceTestDatas datas;

	private User owner;

	private Account actor;

	private Account admin;

	private LinShareWiser wiser;

	public ShareNewShareEmailBuilderTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		owner = datas.getUser1();
		actor = (Account) owner;
		admin = datas.getRoot();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateNewSharesFiles() throws BusinessException, IOException {
		User recipient = datas.getUser2();
		List<String> documents = new  ArrayList<String>();
		documents.add("bfaf3fea-c64a-4ee0-bae8-b1482f1f6401");
		documents.add("fd87394a-41ab-11e5-b191-080027b8274b");
		ShareContainer shareContainer = new ShareContainer();
		shareContainer.addShareRecipient(recipient);
		shareContainer.addDocumentUuid(documents);
		Assertions.assertNotNull(shareService.create(actor, owner, shareContainer));
		wiser.checkGeneratedMessages();
	}

	@Test
	public void testCreateNewSharesFiles_MailAttachment() throws BusinessException, IOException {
		User recipient = datas.getUser3();
		MailConfig cfg = domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-test", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		// EnableForAll is disabled and the language == emailContext language -> inserted
		attachmentService.create(admin, true, "Logo", false, cfg.getUuid(),
				"Test mail attachment", "logo.mail.attachment2.test", Language.ENGLISH, tempFile, null);
		Assertions.assertFalse(cfg.getMailAttachments().isEmpty());
		List<String> documents = new  ArrayList<String>();
		documents.add("bfaf3fea-c64a-4ee0-bae8-b1482f1f6401");
		documents.add("fd87394a-41ab-11e5-b191-080027b8274b");
		ShareContainer shareContainer = new ShareContainer();
		shareContainer.addShareRecipient(recipient);
		shareContainer.addDocumentUuid(documents);
		Assertions.assertNotNull(shareService.create(actor, owner, shareContainer));
		wiser.checkGeneratedMessages();
	}
}
