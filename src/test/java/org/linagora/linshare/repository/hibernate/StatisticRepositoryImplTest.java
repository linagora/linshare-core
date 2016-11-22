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
package org.linagora.linshare.repository.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatisticBusinessService;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.ThreadWeeklyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainDailyStatisticRepository;
import org.linagora.linshare.core.repository.StatisticRepository;
import org.linagora.linshare.core.repository.ThreadDailyStatisticRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.ThreadWeeklyStatisticRepository;
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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-start-embedded-ldap.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class StatisticRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private ThreadRepository threadRepo;

	@Autowired
	private DomainDailyStatisticRepository domainDailyStatRepository;

	@Autowired
	private ThreadWeeklyStatisticRepository threadWeeklyStatRepository;

	@Autowired
	private ThreadWeeklyStatisticBusinessService threadWeeklyStatBusinessService;

	@Autowired
	private ThreadDailyStatisticRepository threadDailyStatRepository;

	@Autowired
	private StatisticRepository statRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void testDomainDailyStat() {
		AbstractDomain domain = jane.getDomain();
		List<DomainDailyStat> result = domainDailyStatRepository.findAll();
		assertEquals(4, result.size());
		result = domainDailyStatRepository.findBetweenTwoDates(null, domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime(), null);
		assertEquals(1, result.size());
		long re = domainDailyStatRepository.sumOfActualOperationSum(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(50, re);
		re = domainDailyStatRepository.sumOfCreateOperationCount(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(3, re);
		re = domainDailyStatRepository.sumOfCreateOperationSum(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(100, re);
		re = domainDailyStatRepository.sumOfDeleteOperationCount(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(2, re);
		re = domainDailyStatRepository.sumOfDeleteOperationSum(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(-50, re);
		re = domainDailyStatRepository.sumOfDiffOperationSum(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(50, re);
		re = domainDailyStatRepository.sumOfOperationCount(domain, null,
				new GregorianCalendar(2042, 8, 10, 00, 00).getTime(),
				new GregorianCalendar(2042, 8, 13, 00, 00).getTime());
		assertEquals(5, re);
	}

	@Test
	public void testThreadDailyAndWeeklyStat() {
		Account thread = accountRepository.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274e");
		List<ThreadDailyStat> listDaily = threadDailyStatRepository.findAll();
		assertEquals(7, listDaily.size());
		List<ThreadDailyStat> listDailyForThreadTest = threadDailyStatRepository.findBetweenTwoDates(thread, null, null,
				null, null, null);
		assertEquals(3, listDailyForThreadTest.size());
		List<ThreadWeeklyStat> result = threadWeeklyStatRepository.findAll();
		assertEquals(3, result.size());
		Thread t = threadRepo.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274e");
		threadWeeklyStatBusinessService.create(t, new GregorianCalendar(2042, 9, 30, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 9, 00, 00).getTime());
		result = threadWeeklyStatRepository.findBetweenTwoDates(thread, null, null, null, null, null);
		assertEquals(2, result.size());
		long re = threadWeeklyStatRepository.sumOfActualOperationSum(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(400, re);
		re = threadWeeklyStatRepository.sumOfCreateOperationCount(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(12, re);
		re = threadWeeklyStatRepository.sumOfCreateOperationSum(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(600, re);
		re = threadWeeklyStatRepository.sumOfDeleteOperationCount(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(6, re);
		re = threadWeeklyStatRepository.sumOfDeleteOperationSum(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(-200, re);
		re = threadWeeklyStatRepository.sumOfDiffOperationSum(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(400, re);
		re = threadWeeklyStatRepository.sumOfOperationCount(null, thread,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		assertEquals(18, re);
	}

	@Test
	public void testStatistic() {
		Account account = jane;
		List<Statistic> result = statRepository.findAll();
		assertEquals(29, result.size());
		result = statRepository.findBetweenTwoDates(account, null, null, null, null, null);
		assertEquals(10, result.size());
		result = statRepository.findBetweenTwoDates(null, null, null, null, null, StatisticType.WORK_GROUP_WEEKLY_STAT);
		assertEquals(3, result.size());
		result = statRepository.findBetweenTwoDates(null, null, null, null, null, StatisticType.DOMAIN_DAILY_STAT);
		assertEquals(4, result.size());
	}
}
