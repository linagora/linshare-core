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

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.repository.WorkGroupFolderMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

//TODO:  FMA fix tests
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
	private WorkGroupFolderService service;

	@Autowired
	protected WorkGroupFolderMongoRepository repository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ThreadService threadService;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private LoadingServiceTestDatas datas;
	private User jane;

	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
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
		WorkGroupFolder workGroupFolder = new WorkGroupFolder("folder1", null, workGroup.getLsUuid());
		service.create(jane, jane, workGroup, workGroupFolder);

		List<WorkGroupFolder> findAll = repository.findAll();
		for (WorkGroupFolder w : findAll) {
			logger.debug(w);
		}
		Assert.assertEquals(2, findAll.size());
	}

	@Test
	public void testCreation2() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		WorkGroupFolder folder1 = new WorkGroupFolder("folder1", null, workGroup.getLsUuid());
		logger.debug(folder1.toString());
		service.create(jane, jane, workGroup, folder1);

		List<WorkGroupFolder> findAll = repository.findAll();
		for (WorkGroupFolder w : findAll) {
			logger.debug(w);
		}
		WorkGroupFolder folder2 = new WorkGroupFolder("folder2", null, workGroup.getLsUuid());
		logger.debug(folder2.toString());
		service.create(jane, jane, workGroup, folder2);

		findAll = repository.findAll();
		for (WorkGroupFolder w : findAll) {
			logger.debug(w);
		}

		Assert.assertEquals(3, findAll.size());
	}

	@Test
	public void testUpdate1() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		WorkGroupFolder workGroupFolder = new WorkGroupFolder("folder1", null, workGroup.getLsUuid());
		WorkGroupFolder create = service.create(jane, jane, workGroup, workGroupFolder);

		WorkGroupFolder update = new WorkGroupFolder();
		update.setName("folder-renamed");
		update.setUuid(create.getUuid());

		WorkGroupFolder updated = service.update(jane, jane, workGroup, update);
		Assert.assertEquals("folder-renamed", updated.getName());
		Assert.assertEquals("folder-renamed", service.find(jane, jane, workGroup, create.getUuid()).getName());
		Assert.assertEquals(2, repository.findAll().size());
	}

	@Test
	public void testUpdateParentNotFound() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		WorkGroupFolder workGroupFolder = new WorkGroupFolder("folder1", null, workGroup.getLsUuid());
		WorkGroupFolder create = service.create(jane, jane, workGroup, workGroupFolder);

		WorkGroupFolder update = new WorkGroupFolder();
		update.setName(create.getName());
		update.setUuid(create.getUuid());
		update.setParent("fddsfsdf");
		// exception.expect(BusinessException.class);
		try {
			service.update(jane, jane, workGroup, update);
			Assert.assertTrue(false);
		} catch (BusinessException e) {
			logger.debug(e.getMessage(), e);
			Assert.assertEquals(BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND, e.getErrorCode());
		}
	}

	@Test
	public void testUpdateParent() throws BusinessException {
		logger.info("Begin coucou");
		Thread workGroup = threadService.create(jane, jane, "thread1");
		String workGroupUuid = workGroup.getLsUuid();

		WorkGroupFolder folder1 = new WorkGroupFolder("folder1", null, workGroupUuid);
		folder1 = service.create(jane, jane, workGroup, folder1);

		WorkGroupFolder folder2 = new WorkGroupFolder("folder2", null, workGroupUuid);
		folder2 = service.create(jane, jane, workGroup, folder2);

		WorkGroupFolder rootParent = DataAccessUtils.singleResult(repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid));

		Assert.assertEquals(3, repository.findAll().size());
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertEquals(rootParent.getUuid(), folder2.getParent());

		folder2.setParent(folder1.getUuid());
		folder2 = service.update(jane, jane, workGroup, folder2);
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assert.assertEquals(folder1.getUuid(), folder2.getParent());

		folder2 = service.find(jane, jane, workGroup, folder2.getUuid());
		Assert.assertEquals(rootParent.getUuid(), folder1.getParent());
		Assert.assertNotEquals(rootParent.getUuid(), folder2.getParent());
		Assert.assertEquals(folder1.getUuid(), folder2.getParent());

	}

	@Test
	public void testAddEntry() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		WorkGroupFolder folder1 = new WorkGroupFolder("folder1", null, workGroup.getLsUuid());
		folder1 = service.create(jane, jane, workGroup, folder1);
		ThreadEntry threadEntry = new ThreadEntry();
		threadEntry.setUuid("eb31e21c-38d3-4389-b147-ca49fc4e8ebe");
		threadEntry.setName("file1");
		threadEntry.setCreationDate(new GregorianCalendar());
		threadEntry.setModificationDate(new GregorianCalendar());
		threadEntry.setType("application/data");
		threadEntry.setSize(666L);
		WorkGroupFolder folder2 = service.addEntry(jane, jane, workGroup, folder1.getUuid(), threadEntry);
		Assert.assertEquals(1, folder2.getEntries().size());
		Assert.assertNotEquals(folder2.getParent(), folder2.getWorkGroup());
	}

	@Test
	public void testAddEntry2() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		ThreadEntry threadEntry = new ThreadEntry();
		threadEntry.setUuid("eb31e21c-38d3-4389-b147-ca49fc4e8ebe");
		threadEntry.setName("file1");
		threadEntry.setCreationDate(new GregorianCalendar());
		threadEntry.setModificationDate(new GregorianCalendar());
		threadEntry.setType("application/data");
		threadEntry.setSize(666L);
		WorkGroupFolder folder2 = service.addEntry(jane, jane, workGroup, null, threadEntry);
		Assert.assertEquals(1, folder2.getEntries().size());
		// no folder uuid specified, root folder uuid should be used as parent.
		// => root folder uuid = work group uuid
		Assert.assertEquals(folder2.getParent(), folder2.getWorkGroup());
	}

	@Test
	public void testGetFolder() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		ThreadEntry threadEntry = new ThreadEntry();
		threadEntry.setUuid("eb31e21c-38d3-4389-b147-ca49fc4e8ebe");
		threadEntry.setName("file1");
		threadEntry.setCreationDate(new GregorianCalendar());
		threadEntry.setModificationDate(new GregorianCalendar());
		threadEntry.setType("application/data");
		threadEntry.setSize(666L);
		WorkGroupFolder folder2 = service.addEntry(jane, jane, workGroup, null, threadEntry);
		WorkGroupFolder folder = service.getFolder(jane, jane, workGroup, threadEntry);
		Assert.assertEquals(folder2.getUuid(), folder.getUuid());
	}

	@Test
	public void testDelEntry() throws BusinessException {
		Thread workGroup = threadService.create(jane, jane, "thread1");
		ThreadEntry threadEntry = new ThreadEntry();
		threadEntry.setUuid("eb31e21c-38d3-4389-b147-ca49fc4e8ebe");
		threadEntry.setName("file1");
		threadEntry.setCreationDate(new GregorianCalendar());
		threadEntry.setModificationDate(new GregorianCalendar());
		threadEntry.setType("application/data");
		threadEntry.setSize(666L);
		WorkGroupFolder folder2 = service.addEntry(jane, jane, workGroup, null, threadEntry);
		logger.debug(folder2);
		Assert.assertEquals(1, folder2.getEntries().size());
		folder2 = service.delEntry(jane, jane, workGroup, threadEntry);
		logger.debug(folder2);
		Assert.assertEquals(0, folder2.getEntries().size());
	}

}
