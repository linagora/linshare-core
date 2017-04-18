/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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
package org.linagora.linshare.mongodb;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class WorkGroupFolderServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private WorkGroupNodeService service;

	@Autowired
	protected WorkGroupNodeMongoRepository repository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ThreadService threadService;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private LoadingServiceTestDatas datas;
	private User jane;

	private boolean dryRun = false;

	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		repository.deleteAll();
		logger.debug("End tearDown");
	}

	@Test
	public void testCreation() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);

		List<WorkGroupNode> findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w);
		}
		Assert.assertEquals(2, findAll.size());
	}

	@Test
	public void testCreationStrict() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), true, dryRun);
		try {
			service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), true, dryRun);
			Assert.assertTrue(false);
		} catch (BusinessException e) {
			Assert.assertEquals(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS, e.getErrorCode());
		}
		Assert.assertEquals(2, repository.findAll().size());
	}

	@Test
	public void testCreation2() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		WorkGroupNode folder1 = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		logger.debug(folder1.toString());
		service.create(jane, jane, workGroup, folder1, false, dryRun);

		List<WorkGroupNode> findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w);
		}
		WorkGroupNode folder2 = new WorkGroupFolder(author, "folder2", null, workGroup.getLsUuid());
		logger.debug(folder2.toString());
		service.create(jane, jane, workGroup, folder2, false, dryRun);

		findAll = repository.findAll();
		for (WorkGroupNode w : findAll) {
			logger.debug(w);
		}

		Assert.assertEquals(3, findAll.size());
	}

	@Test
	public void testCreationNonStrict() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		WorkGroupNode create = service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		Assert.assertEquals("folder1 (1)", create.getName());
		WorkGroupNode create2 = service.create(jane, jane, workGroup, new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid()), false, dryRun);
		Assert.assertEquals("folder1 (2)", create2.getName());
		// three folders plus root folder
		Assert.assertEquals(4, repository.findAll().size());
	}

	@Test
	public void testCreationNonStrict2() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
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
		Assert.assertEquals(newName, create.getName());
		Assert.assertEquals(newName, "my.folder (6)");
	}

	@Test
	public void testUpdate1() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", workGroup.getLsUuid(), workGroup.getLsUuid());
		WorkGroupNode create = service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);

		WorkGroupNode update = new WorkGroupFolder();
		update.setName("folder-renamed");
		update.setUuid(create.getUuid());

		WorkGroupNode updated = service.update(jane, jane, workGroup, update);
		Assert.assertNotNull(updated);
		Assert.assertEquals("folder-renamed", updated.getName());
		Assert.assertEquals("folder-renamed", service.find(jane, jane, workGroup, create.getUuid(), false).getName());
		Assert.assertEquals(2, repository.findAll().size());
	}

	@Test
	public void testUpdateParentNotFound() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		AccountMto author = new AccountMto(jane);
		WorkGroupNode workGroupFolder = new WorkGroupFolder(author, "folder1", null, workGroup.getLsUuid());
		WorkGroupNode create = service.create(jane, jane, workGroup, workGroupFolder, false, dryRun);

		WorkGroupNode update = new WorkGroupFolder();
		update.setName(create.getName());
		update.setUuid(create.getUuid());
		update.setParent("fddsfsdf");
		// exception.expect(BusinessException.class);
		try {
			Assert.assertNotNull(service.update(jane, jane, workGroup, update));
			Assert.assertTrue(false);
		} catch (BusinessException e) {
			logger.debug(e.getMessage(), e);
			Assert.assertEquals(BusinessErrorCode.WORK_GROUP_NODE_NOT_FOUND, e.getErrorCode());
		}
	}

	@Test
	public void testUpdateParent() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		String workGroupUuid = workGroup.getLsUuid();
		AccountMto author = new AccountMto(jane);
		WorkGroupNode folder1 = new WorkGroupFolder(author, "folder1", null, workGroupUuid);
		folder1 = service.create(jane, jane, workGroup, folder1, false, dryRun);

		WorkGroupNode folder2 = new WorkGroupFolder(author, "folder2", null, workGroupUuid);
		folder2 = service.create(jane, jane, workGroup, folder2, false, dryRun);

		WorkGroupNode rootParent = DataAccessUtils.singleResult(repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid));

		Assert.assertEquals(3, repository.findAll().size());
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertEquals(rootParent.getUuid(), folder2.getParent());

		folder2.setParent(folder1.getUuid());
		folder2 = service.update(jane, jane, workGroup, folder2);
		Assert.assertNotNull(folder2);
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assert.assertEquals(folder1.getUuid(), folder2.getParent());

		folder2 = service.find(jane, jane, workGroup, folder2.getUuid(), false);
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assert.assertEquals(folder1.getUuid(), folder2.getParent());

	}

	@Test
	public void testUpdateRename() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
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

		Assert.assertEquals(",thread1," , nodea1.getPath());
		Assert.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,", nodeabc1.getPath());
		Assert.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,my.folder-a-b-c-1,", nodeabcd1.getPath());

		// just update without move.
		nodea1.setName("coucou");
		service.update(jane, jane, workGroup, nodea1);
		Assert.assertEquals(",thread1,", service.find(jane, jane, workGroup, nodea1.getUuid(), tree).getPath());
		Assert.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-1,", service.find(jane, jane, workGroup, nodeabc1.getUuid(), tree).getPath());

		// updating node moving folder abc1 from ab1 to a1.
		nodeabc1.setParent(nodea1.getUuid());
		nodeabc1.setName("coucou");
		WorkGroupNode updateNodeabc1 = service.update(jane, jane, workGroup, nodeabc1);

		Assert.assertEquals(",thread1,my.folder-a-1,", updateNodeabc1.getPath());
		Assert.assertEquals(nodea1.getUuid(), updateNodeabc1.getParent());

		Assert.assertEquals(",thread1,my.folder-a-1,my.folder-a-b-c-1,", service.find(jane, jane, workGroup, nodeabcd1.getUuid(), tree).getPath());
	}

	public WorkGroupNode createWrapper(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode, Boolean strict)
			throws BusinessException {
		WorkGroupNode node = service.create(actor, owner, workGroup, workGroupNode, strict, dryRun);
		node.setUuid(node.getName());
		node = repository.save(node);
		return node;
	}

}
