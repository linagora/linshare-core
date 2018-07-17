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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml"
		})
public class WorkGroupEntryServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory
			.getLogger(WorkGroupEntryServiceImplTest.class);

	private static final String THREAD_1 = "TEST_THREAD_1";
	private static final String THREAD_2 = "TEST_THREAD_2";

	private LoadingServiceTestDatas datas;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ThreadService threadService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private SharedSpaceNodeService sharedSpaceNodeService;

	@Autowired
	private InitMongoService init;

	private List<WorkGroup> workGroups;

	private User jane;
	private User john;
	private Account root;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		root= datas.getRoot();
		init.init();
		this.createAllThreads();
		workGroups = threadService.findAll(root, root);
		logger.debug(LinShareTestConstants.END_SETUP);
		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		jane.setDomain(domain);
		john.setDomain(subDomain);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		this.deleteAllThreads();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}


	/*
	 * Helpers
	 */

	private void createAllThreads() throws BusinessException {
		WorkGroup thread2 = threadService.create(john, john, WorkGroupEntryServiceImplTest.THREAD_2);
		createSharedSpaceNode(john, thread2);
		WorkGroup thread1 = threadService.create(jane, jane, WorkGroupEntryServiceImplTest.THREAD_1);
		createSharedSpaceNode(jane, thread1);
	}

	private void createSharedSpaceNode(User user, WorkGroup workGroup) {
		SharedSpaceNode node = new SharedSpaceNode(workGroup.getName(), null, NodeType.WORK_GROUP);
		node.setUuid(workGroup.getLsUuid());
		sharedSpaceNodeService.create(user, user, node);
	}

	private void deleteAllThreads() throws BusinessException {
		for (WorkGroup workGroup : workGroups) {
			for (WorkgroupMember m : workGroup.getMyMembers()) {
				if (m.getAdmin()) {
					threadService.deleteThread(m.getUser(), m.getUser(), workGroup);
				}
			}
		}
	}

	/*
	 * Tests
	 */
	@Test
	public void testFindAllThread() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int count;
		Assert.assertEquals(workGroups.size(), 2);
		for (count = workGroups.size(); count < 10; ++count) {
			createSharedSpaceNode(john, threadService.create(john, john, WorkGroupEntryServiceImplTest.THREAD_1 + "_" + count));
		}
		User root = userRepository.findByMailAndDomain(LinShareConstants.rootDomainIdentifier, "root@localhost.localdomain");
		workGroups = threadService.findAll(root, root);
		Assert.assertEquals(workGroups.size(), count);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindWorkGroup() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		WorkGroup original = workGroups.get(0);
		WorkGroup found = threadService.findByLsUuidUnprotected(original.getLsUuid());
		Assert.assertEquals(found, original);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAdmin() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assert.assertTrue(threadService.hasAnyWhereAdmin(john));
		Assert.assertEquals(threadService.findAllWhereAdmin(john).size(), 1);
		threadService.create(datas.getUser1(), datas.getUser1(), WorkGroupEntryServiceImplTest.THREAD_1 + "_" + 1);
		Assert.assertEquals(threadService.findAllWhereAdmin(john).size(), 2);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindLatest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<WorkGroup> latests = threadService.findLatestWhereMember(john, 10);
		Assert.assertFalse(latests.isEmpty());
		logger.debug("Latests :");
		for (WorkGroup thread : latests) {
			logger.debug('\t' + thread.getName());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}
}
