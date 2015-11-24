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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainDailyStatRepository;
import org.linagora.linshare.core.repository.StatisticRepository;
import org.linagora.linshare.core.repository.ThreadMonthlyStatRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.ThreadWeeklyStatRepository;
import org.linagora.linshare.core.repository.UserDailyStatRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.UserWeeklyStatRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadWeeklyStat;

@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class StatRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private UserDailyStatRepository userDailyStatRepository;

	@Autowired
	private UserWeeklyStatRepository userWeeklyStatRepository;

	@Autowired
	private DomainDailyStatRepository domainDailyStatRepository;

	@Autowired
	private ThreadWeeklyStatRepository threadWeeklyStatRepository;

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
	public void testUserWeeklyStat() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,-30);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,+60);
		Date b = calendar.getTime();
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		List<UserWeeklyStat> result = userWeeklyStatRepository.findAll();
		assertEquals(3, result.size());
		result = userWeeklyStatRepository.findBetweenTwoDates(account, null, null, null, null, null);
		assertEquals(3, result.size());
		result = userWeeklyStatRepository.findBetweenTwoDates(null, domain, null, null, null, null);
		assertEquals(1, result.size());
		long re = userWeeklyStatRepository.sumOfActualOperationSum(null, account, a, b);
		assertEquals(1700, re);
		re = userWeeklyStatRepository.sumOfCreateOperationCount(null, account, a, b);
		assertEquals(22, re);
		re = userWeeklyStatRepository.sumOfCreateOperationSum(null, account, a, b);
		assertEquals(5700, re);
		re = userWeeklyStatRepository.sumOfDeleteOperationCount(null, account, a, b);
		assertEquals(18, re);
		re = userWeeklyStatRepository.sumOfDeleteOperationSum(null, account, a, b);
		assertEquals(-4000, re);
		re = userWeeklyStatRepository.sumOfDiffOperationSum(null, account, a, b);
		assertEquals(1700, re);
		re = userWeeklyStatRepository.sumOfOperationCount(null, account, a, b);
		assertEquals(40, re);
	}

	@Test
	public void testUserDailyStat() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,-7);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,+14);
		Date b = calendar.getTime();
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		List<UserDailyStat> result = userDailyStatRepository.findAll();
		assertEquals(7, result.size());
		result = userDailyStatRepository.findBetweenTwoDates(account, null, null, null, null, null);
		assertEquals(7, result.size());
		long re = userDailyStatRepository.sumOfActualOperationSum(null, account, a, b);
		assertEquals(1250, re);
		re = userDailyStatRepository.sumOfCreateOperationCount(null, account, a, b);
		assertEquals(19, re);
		re = userDailyStatRepository.sumOfCreateOperationSum(null, account, a, b);
		assertEquals(3400, re);
		re = userDailyStatRepository.sumOfDeleteOperationCount(null, account, a, b);
		assertEquals(19, re);
		re = userDailyStatRepository.sumOfDeleteOperationSum(null, account, a, b);
		assertEquals(-2150, re);
		re = userDailyStatRepository.sumOfDiffOperationSum(null, account, a, b);
		assertEquals(1250, re);
		re = userDailyStatRepository.sumOfOperationCount(null, account, a, b);
		assertEquals(38, re);
	}

	@Test
	public void testDomainDailyStat() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,-7);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,+14);
		Date b = calendar.getTime();
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		List<DomainDailyStat> result = domainDailyStatRepository.findAll();
		assertEquals(4, result.size());
		result = domainDailyStatRepository.findBetweenTwoDates(null, domain, null, null, null, null);
		assertEquals(1, result.size());
		long re = domainDailyStatRepository.sumOfActualOperationSum(domain, null, a, b);
		assertEquals(150, re);
		re = domainDailyStatRepository.sumOfCreateOperationCount(domain, null, a, b);
		assertEquals(3, re);
		re = domainDailyStatRepository.sumOfCreateOperationSum(domain, null, a, b);
		assertEquals(200, re);
		re = domainDailyStatRepository.sumOfDeleteOperationCount(domain, null, a, b);
		assertEquals(1, re);
		re = domainDailyStatRepository.sumOfDeleteOperationSum(domain, null, a, b);
		assertEquals(-50, re);
		re = domainDailyStatRepository.sumOfDiffOperationSum(domain, null, a, b);
		assertEquals(150, re);
		re = domainDailyStatRepository.sumOfOperationCount(domain, null, a, b);
		assertEquals(4, re);
	}

	@Test
	public void testThreadWeeklyStat() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,-7);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,+14);
		Date b = calendar.getTime();
		Account account = accountRepository.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274ef");
		AbstractDomain domain = jane.getDomain();
		List<ThreadWeeklyStat> result = threadWeeklyStatRepository.findAll();
		assertEquals(3, result.size());
		result = threadWeeklyStatRepository.findBetweenTwoDates(account, null, null, null, null, null);
		assertEquals(3, result.size());
		long re = threadWeeklyStatRepository.sumOfActualOperationSum(null, account, a, b);
		assertEquals(50, re);
		re = threadWeeklyStatRepository.sumOfCreateOperationCount(null, account, a, b);
		assertEquals(3, re);
		re = threadWeeklyStatRepository.sumOfCreateOperationSum(null, account, a, b);
		assertEquals(100, re);
		re = threadWeeklyStatRepository.sumOfDeleteOperationCount(null, account, a, b);
		assertEquals(2, re);
		re = threadWeeklyStatRepository.sumOfDeleteOperationSum(null, account, a, b);
		assertEquals(-50, re);
		re = threadWeeklyStatRepository.sumOfDiffOperationSum(null, account, a, b);
		assertEquals(50, re);
		re = threadWeeklyStatRepository.sumOfOperationCount(null, account, a, b);
		assertEquals(5, re);
	}

	@Test
	public void testStatistic() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,-30);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH,+60);
		Date b = calendar.getTime();
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		List<Statistic> result = statRepository.findAll();
		assertEquals(24, result.size());
		result = statRepository.findBetweenTwoDates(account, null, null, null, null, null);
		assertEquals(10, result.size());
		result = statRepository.findBetweenTwoDates(account, null, null, a, b, StatisticType.THREAD_MONTHLY_STAT);
		assertEquals(0, result.size());
	}
}
