/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
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
package org.linagora.linshare.batches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ 
	"/import-tests-document-entry-setup.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class AdvancedStatisticBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("advancedStatisticDailyBatch")
	private GenericBatch advancedStatisticDailyBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private WorkGroupNodeService workGroupNodeService;

	@Autowired
	protected WorkGroupNodeMongoRepository repository;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;

	@Autowired
	@Qualifier("sharedSpaceNodeBusinessService")
	private SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService memberService;

	@Autowired
	private ThreadService threadService;

	private User john;

	private User jane;

	public AdvancedStatisticBatchTest() {
		super();
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		createWorkgroupDocument();
		createUploadRequestEntry();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(advancedStatisticDailyBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

	private void createWorkgroupDocument() throws IOException {
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		tempFile.deleteOnExit();
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream, new FileOutputStream(tempFile));
		WorkGroup workGroup = threadService.create(jane, jane, "thread1");
		createSharedSpaceNode(jane, workGroup.getName(), workGroup.getLsUuid());
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		workGroupNodeService.create(jane, jane, workGroup, workGroupFolder, false, false);
		workGroupNodeService.create(jane, jane, workGroup, tempFile, "tempFile", workGroupFolder.getUuid(), false);
	}

	private void createSharedSpaceNode(Account account, String label, String groupUuid) {
		SharedSpaceRole adminRole = roleBusinessService.findByName("ADMIN");
		Validate.notNull(adminRole, "adminRole must be set");
		SharedSpaceNode node = new SharedSpaceNode(label, null, NodeType.WORK_GROUP);
		node.setUuid(groupUuid);
		sharedSpaceNodeBusinessService.create(node);
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminRole);
		memberService.create(jane, jane, node, context, new SharedSpaceAccount((User) account));
	}

	private void createUploadRequestEntry() throws IOException {
		Contact yoda = new Contact("yoda@linshare.org");
		UploadRequest ure = new UploadRequest();
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100000);
		ure.setMaxFileCount(Integer.valueOf(3));
		ure.setMaxFileSize((long) 100000);
		ure.setProtectedByPassword(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale(Language.ENGLISH);
		ure.setExpiryDate(new Date());
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		ure = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(UploadRequestStatus.ENABLED,
				ure.getStatus());
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.transferTo(stream, tempFile);
		uploadRequestEntryService.create(jane, jane, tempFile, "fileName", "", false, null,
				ure.getUploadRequestURLs().iterator().next());
	}

}
