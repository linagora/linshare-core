/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
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
package org.linagora.linshare.mongodb;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-domains-and-accounts.sql"})
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class WorkGroupFolderServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private WorkGroupNodeService service;

	@Autowired
	protected WorkGroupNodeMongoRepository repository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ThreadService threadService;

	@Autowired
	private SharedSpaceNodeService sharedSpaceNodeService;

	@Autowired
	private InitMongoService init;

	private User jane;

	private boolean dryRun = false;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		init.init();
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		repository.deleteAll();
		logger.debug("End tearDown");
	}

	@Test
	public void testCreation() throws BusinessException {
		logger.info("Begin coucou");
		AccountMto author = new AccountMto(jane);
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);
		List<WorkGroupNode> findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w.toString());
		}
		Assertions.assertEquals(2, findAll.size());
	}

	@Test
	public void testCreationStrict() throws BusinessException {
		logger.info("Begin coucou");
		AccountMto author = new AccountMto(jane);
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), true, dryRun);
		try {
			service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), true, dryRun);
			Assertions.assertTrue(false);
		} catch (BusinessException e) {
			Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS, e.getErrorCode());
		}
		Assertions.assertEquals(2, repository.findAll().size());
	}

	@Test
	public void testCreation2() throws BusinessException {
		logger.info("Begin coucou");
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		AccountMto author = new AccountMto(jane);
		WorkGroupNode folder1 = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		logger.debug(folder1.toString());
		service.create(jane, jane, workGroup, folder1, false, dryRun);

		List<WorkGroupNode> findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w.toString());
		}
		WorkGroupNode folder2 = new WorkGroupFolder(author, "folder2", null, workGroup.getLsUuid());
		logger.debug(folder2.toString());
		service.create(jane, jane, workGroup, folder2, false, dryRun);

		findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w.toString());
		}

		Assertions.assertEquals(3, findAll.size());
	}

	@Test
	public void testCreationNonStrict() throws BusinessException {
		logger.info("Begin coucou");
		AccountMto author = new AccountMto(jane);
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		WorkGroupNode create = service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		Assertions.assertEquals("folder1 (1)", create.getName());
		WorkGroupNode create2 = service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		Assertions.assertEquals("folder1 (2)", create2.getName());
		// three folders plus root folder
		Assertions.assertEquals(4, repository.findAll().size());
	}

	@Test
	public void testCreationNonStrict2() throws BusinessException {
		logger.info("Begin coucou");
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		AccountMto author = new AccountMto(jane);

		// Init
		String currName = "my.folder";
		boolean strict = true;
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, currName, null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder (1)", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "other folder 1", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "other folder 2", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "coucou", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "not.my.folder", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "myyfolder", null, workGroup.getLsUuid()), strict, dryRun);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder (5)", null, workGroup.getLsUuid()), strict, dryRun);

		String newName = "my.folder (6)";
		logger.info(newName);
		WorkGroupNode create = service.create(jane, jane, workGroup, new WorkGroupFolder(author, currName, null, workGroup.getLsUuid()), false, dryRun);
		Assertions.assertEquals(newName, create.getName());
		Assertions.assertEquals(newName, "my.folder (6)");
	}

	@Test
	public void testUpdate1() throws BusinessException {
		logger.info("Begin coucou");
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", workGroup.getLsUuid(), workGroup.getLsUuid());
		WorkGroupNode create = service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);

		WorkGroupNode update = new WorkGroupFolder();
		update.setName("folder-renamed");
		update.setUuid(create.getUuid());

		WorkGroupNode updated = service.update(jane, jane, workGroup, update);
		Assertions.assertNotNull(updated);
		Assertions.assertEquals("folder-renamed", updated.getName());
		Assertions.assertEquals("folder-renamed", service.find(jane, jane, workGroup, create.getUuid(), false).getName());
		Assertions.assertEquals(2, repository.findAll().size());
	}

	@Test
	public void testUpdateParentNotFound() throws BusinessException {
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		WorkGroupNode create = service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);
		WorkGroupNode update = new WorkGroupFolder();
		update.setName(create.getName());
		update.setUuid(create.getUuid());
		update.setParent("fddsfsdf");
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.update(jane, jane, workGroup, update);
		});
		Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_NODE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void testUpdateParent() throws BusinessException {
		logger.info("Begin coucou");
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		String workGroupUuid = workGroup.getLsUuid();
		AccountMto author = new AccountMto(jane);
		WorkGroupNode folder1 = new WorkGroupFolder(author, "folder1", null, workGroupUuid);
		folder1 = service.create(jane, jane, workGroup, folder1, false, dryRun);

		WorkGroupNode folder2 = new WorkGroupFolder(author, "folder2", null, workGroupUuid);
		folder2 = service.create(jane, jane, workGroup, folder2, false, dryRun);

		WorkGroupNode rootParent = DataAccessUtils.singleResult(repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid));

		Assertions.assertEquals(3, repository.findAll().size());
		Assertions.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assertions.assertEquals(rootParent.getUuid(), folder2.getParent());

		folder2.setParent(folder1.getUuid());
		folder2 = service.update(jane, jane, workGroup, folder2);
		Assertions.assertNotNull(folder2);
		Assertions.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assertions.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assertions.assertEquals(folder1.getUuid(), folder2.getParent());

		folder2 = service.find(jane, jane, workGroup, folder2.getUuid(), false);
		Assertions.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assertions.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assertions.assertEquals(folder1.getUuid(), folder2.getParent());

	}

	@Test
	public void testUpdateRename() throws BusinessException {
		logger.info("Begin coucou");
		SharedSpaceNode node = new SharedSpaceNode("thread1", null, NodeType.WORK_GROUP);
		node = sharedSpaceNodeService.create(jane, jane, node);
		WorkGroup workGroup = threadService.find(jane, jane, node.getUuid());
		AccountMto author = new AccountMto(jane);

		WorkGroupNode rootFolder = service.getRootFolder(jane, jane, workGroup);
		rootFolder.setUuid(rootFolder.getName());
		rootFolder.setParent(rootFolder.getName());
		rootFolder = repository.save(rootFolder);

		// Init
		boolean strict = true;
		boolean tree = false;
		WorkGroupNode nodea1 = createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-1", workGroup.getName(), workGroup.getLsUuid()), strict);
		createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-2", null, workGroup.getLsUuid()), strict);
		createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-3", null, workGroup.getLsUuid()), strict);

		WorkGroupNode nodeab1 = createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-1", nodea1.getUuid(), workGroup.getLsUuid()), strict);
		createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-2", nodea1.getUuid(), workGroup.getLsUuid()), strict);
		createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-3", nodea1.getUuid(), workGroup.getLsUuid()), strict);

		WorkGroupNode nodeabc1 = createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-c-1", nodeab1.getUuid(), workGroup.getLsUuid()), strict);
		WorkGroupNode nodeabcd1 = createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-c-d-1", nodeabc1.getUuid(), workGroup.getLsUuid()), strict);
		createWrapper(jane, jane, workGroup, new WorkGroupFolder(author, "my.folder-a-b-c-d-2", nodeabc1.getUuid(), workGroup.getLsUuid()), strict);

		Assertions.assertEquals(",thread1," , nodea1.getPath());
		Assertions.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,", nodeabc1.getPath());
		Assertions.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,my.folder-a-b-c-1,", nodeabcd1.getPath());

		// just update without move.
		nodea1.setName("coucou");
		service.update(jane, jane, workGroup, nodea1);
		Assertions.assertEquals(",thread1,", service.find(jane, jane, workGroup, nodea1.getUuid(), tree).getPath());
		Assertions.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,", service.find(jane, jane, workGroup, nodeabc1.getUuid(), tree).getPath());

		// Update description
		Assertions.assertNull(nodea1.getDescription());
		nodea1.setDescription("Test description");
		service.update(jane, jane, workGroup, nodea1);
		Assertions.assertEquals(nodea1.getDescription(), "Test description");

		// updating node moving folder abc1 from ab1 to a1.
		nodeabc1.setParent(nodea1.getUuid());
		nodeabc1.setName("coucou");
		WorkGroupNode updateNodeabc1 = service.update(jane, jane, workGroup, nodeabc1);

		Assertions.assertEquals(",thread1,my.folder-a-1,", updateNodeabc1.getPath());
		Assertions.assertEquals(nodea1.getUuid(), updateNodeabc1.getParent());

		Assertions.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-c-1,", service.find(jane, jane, workGroup, nodeabcd1.getUuid(), tree).getPath());
	}

	public WorkGroupNode createWrapper(Account actor, User owner, WorkGroup workGroup, WorkGroupNode workGroupNode, Boolean strict)
			throws BusinessException {
		WorkGroupNode node = service.create(actor, owner, workGroup, workGroupNode, strict, dryRun);
		node.setUuid(node.getName());
		node = repository.save(node);
		return node;
	}

}
