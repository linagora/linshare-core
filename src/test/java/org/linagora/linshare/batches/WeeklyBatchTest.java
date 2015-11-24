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
package org.linagora.linshare.batches;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadWeeklyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class WeeklyBatchTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Qualifier("statisticWeeklyUserBatch")
	@Autowired
	private GenericBatch weeklyUserBatch;

	@Qualifier("statisticWeeklyThreadBatch")
	@Autowired
	private GenericBatch weeklyThreadBatch;

	@Qualifier("statisticWeeklyDomainBatch")
	@Autowired
	private GenericBatch weeklyDomainBatch;

	@Autowired
	private UserWeeklyStatBusinessService userWeeklyStatBusinessService;

	@Autowired
	private ThreadWeeklyStatBusinessService threadWeeklyStatBusinessService;

	@Autowired
	private DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	@Autowired
	private ThreadRepository threadRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;
	
	@Before
	public void setUp() throws Exception {
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		Thread thread = threadRepository.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274f");
		weeklyUserBatch.execute(jane.getLsUuid(), 10, 1);
		weeklyThreadBatch.execute(thread.getLsUuid(), 10, 1);

		List<String> listIdentifierDomain = weeklyDomainBatch.getAll();
		assertEquals(2, listIdentifierDomain.size());
		weeklyDomainBatch.execute(listIdentifierDomain.get(0), 2, 1);
		weeklyDomainBatch.execute(listIdentifierDomain.get(1), 2, 1);

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -1);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DATE, +2);
		Date b = calendar.getTime();
		List<UserWeeklyStat> listUserWeeklyStat = userWeeklyStatBusinessService.findBetweenTwoDates(null, a, b);
		List<ThreadWeeklyStat> listThreadWeeklyStat = threadWeeklyStatBusinessService.findBetweenTwoDates(null, a, b);
		List<DomainWeeklyStat> listDomainWeeklyStat = domainWeeklyStatBusinessService.findBetweenTwoDates(null, a, b);

		assertEquals(1, listUserWeeklyStat.size());
		assertEquals(1, listThreadWeeklyStat.size());
		assertEquals(2, listDomainWeeklyStat.size());

		UserWeeklyStat userWeeklyStat = listUserWeeklyStat.get(0);
		assertEquals(9, (long) userWeeklyStat.getOperationCount());
		assertEquals(200, (long) userWeeklyStat.getActualOperationSum());
		assertEquals(6, (long) userWeeklyStat.getAddOperationCount());
		assertEquals(300, (long) userWeeklyStat.getAddOperationSum());
		assertEquals(3, (long) userWeeklyStat.getDeleteOperationCount());
		assertEquals(-100, (long) userWeeklyStat.getDeleteOperationSum());
		assertEquals(200, (long) userWeeklyStat.getDiffOperationSum());

		ThreadWeeklyStat threadWeeklyStat = listThreadWeeklyStat.get(0);
		assertEquals(5, (long) threadWeeklyStat.getOperationCount());
		assertEquals(50, (long) threadWeeklyStat.getActualOperationSum());
		assertEquals(3, (long) threadWeeklyStat.getAddOperationCount());
		assertEquals(100, (long) threadWeeklyStat.getAddOperationSum());
		assertEquals(2, (long) threadWeeklyStat.getDeleteOperationCount());
		assertEquals(-50, (long) threadWeeklyStat.getDeleteOperationSum());
		assertEquals(50, (long) threadWeeklyStat.getDiffOperationSum());

		DomainWeeklyStat domainWeeklyStat = listDomainWeeklyStat.get(0);
		assertEquals(10, (long) domainWeeklyStat.getOperationCount());
		assertEquals(100, (long) domainWeeklyStat.getActualOperationSum());
		assertEquals(6, (long) domainWeeklyStat.getAddOperationCount());
		assertEquals(200, (long) domainWeeklyStat.getAddOperationSum());
		assertEquals(4, (long) domainWeeklyStat.getDeleteOperationCount());
		assertEquals(-100, (long) domainWeeklyStat.getDeleteOperationSum());
		assertEquals(100, (long) domainWeeklyStat.getDiffOperationSum());

		domainWeeklyStat = listDomainWeeklyStat.get(1);
		assertEquals(4, (long) domainWeeklyStat.getOperationCount());
		assertEquals(150, (long) domainWeeklyStat.getActualOperationSum());
		assertEquals(3, (long) domainWeeklyStat.getAddOperationCount());
		assertEquals(200, (long) domainWeeklyStat.getAddOperationSum());
		assertEquals(1, (long) domainWeeklyStat.getDeleteOperationCount());
		assertEquals(-50, (long) domainWeeklyStat.getDeleteOperationSum());
		assertEquals(150, (long) domainWeeklyStat.getDiffOperationSum());
	}
}
