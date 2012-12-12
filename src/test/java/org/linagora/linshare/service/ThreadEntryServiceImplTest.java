/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linshare.service;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class ThreadEntryServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(ThreadEntryServiceImplTest.class);
	
	private static final String THREAD_1 = "TEST_THREAD_1";
	private static final String THREAD_2 = "TEST_THREAD_2";
	
	private LoadingServiceTestDatas datas;
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@SuppressWarnings("rawtypes")
	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserAndDomainMultiService userAndDomainMultiService;	
	
	@Autowired
	private AbstractDomainService abstractDomainService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ThreadService threadService;
	
	@Autowired
	private ThreadEntryService threadEntryService;
	
	private List<Thread> threads;
	
	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		datas = new LoadingServiceTestDatas(functionalityRepository, abstractDomainRepository, domainPolicyRepository, userRepository, userService);
		datas.loadUsers();
		this.createAllThreads();
		threads = threadService.findAll();
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		this.deleteAllThreads();
		datas.deleteUsers();
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	
	/*
	 * Helpers
	 */
	
	private void createAllThreads() throws BusinessException {
		threadService.create(datas.getUser1(), ThreadEntryServiceImplTest.THREAD_1);
		threadService.create(datas.getUser2(), ThreadEntryServiceImplTest.THREAD_2);
	}
	
	private void deleteAllThreads() {
		for (Thread thread : threads) {
			for (ThreadMember m : thread.getMyMembers()) {
				if (m.getAdmin()) {
					threadService.deleteThread(m.getUser(), thread);
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
		Assert.assertEquals(threads.size(), 2);
		for (count = threads.size(); count < 10; ++count) {
			threadService.create(datas.getUser1(), ThreadEntryServiceImplTest.THREAD_1 + "_" + count);
		}
		threads = threadService.findAll();
		Assert.assertEquals(threads.size(), count);
		
		logger.info(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindThread() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Thread original = threads.get(0);
		Thread found = threadService.findByLsUuid(original.getLsUuid());
		Assert.assertEquals(found, original);
		
		logger.info(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindAdmin() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Assert.assertTrue(threadService.hasAnyThreadWhereIsAdmin(datas.getUser1()));
		Assert.assertEquals(threadService.getThreadListIfAdmin(datas.getUser1()).size(), 1);
		threadService.create(datas.getUser1(), ThreadEntryServiceImplTest.THREAD_1 + "_" + 1);
		Assert.assertEquals(threadService.getThreadListIfAdmin(datas.getUser1()).size(), 2);

		logger.info(LinShareTestConstants.END_TEST);
	}

}
