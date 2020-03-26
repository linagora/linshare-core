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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatisticBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.domain.entities.WorkGroup;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ 
	"/import-tests-stat.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class WeeklyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
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
	private ThreadWeeklyStatisticBusinessService threadWeeklyStatBusinessService;

	@Autowired
	private DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	@Autowired
	private ThreadRepository threadRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@BeforeEach
	public void setUp() throws Exception {
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		WorkGroup workGroup = threadRepository.findByLsUuid("dabb0364-4f44-11ea-9fe7-eb275404c745");
		threadWeeklyStatBusinessService.create(workGroup, new GregorianCalendar(2042, 10, 1, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime());
		userWeeklyStatBusinessService.create(jane, new GregorianCalendar(2042, 10, 9, 00, 00).getTime(), new GregorianCalendar(2042, 10, 17, 00, 00).getTime());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -1);
		Date a = calendar.getTime();
		List<UserWeeklyStat> listUserWeeklyStat = userWeeklyStatBusinessService.findBetweenTwoDates(null, a, new GregorianCalendar(2042, 10, 18, 00, 00).getTime());
		List<ThreadWeeklyStat> listThreadWeeklyStat = threadWeeklyStatBusinessService.findBetweenTwoDates(null, a, new GregorianCalendar(2042, 10, 9, 00, 00).getTime());
		List<DomainWeeklyStat> listDomainWeeklyStat = domainWeeklyStatBusinessService.findBetweenTwoDates(null, a, new GregorianCalendar(2042, 9, 14, 00, 00).getTime());
		assertEquals(3, listUserWeeklyStat.size());
		assertEquals(1, listThreadWeeklyStat.size());
		assertEquals(3, listDomainWeeklyStat.size());

		UserWeeklyStat userWeeklyStat = listUserWeeklyStat.get(0);
		assertEquals(22, (long) userWeeklyStat.getOperationCount());
		assertEquals(700, (long) userWeeklyStat.getActualOperationSum());
		assertEquals(12, (long) userWeeklyStat.getCreateOperationCount());
		assertEquals(2700, (long) userWeeklyStat.getCreateOperationSum());
		assertEquals(10, (long) userWeeklyStat.getDeleteOperationCount());
		assertEquals(-2000, (long) userWeeklyStat.getDeleteOperationSum());
		assertEquals(700, (long) userWeeklyStat.getDiffOperationSum());

		ThreadWeeklyStat threadWeeklyStat = listThreadWeeklyStat.get(0);
		assertEquals(10, (long) threadWeeklyStat.getOperationCount());
		assertEquals(100, (long) threadWeeklyStat.getActualOperationSum());
		assertEquals(6, (long) threadWeeklyStat.getCreateOperationCount());
		assertEquals(200, (long) threadWeeklyStat.getCreateOperationSum());
		assertEquals(4, (long) threadWeeklyStat.getDeleteOperationCount());
		assertEquals(-100, (long) threadWeeklyStat.getDeleteOperationSum());
		assertEquals(100, (long) threadWeeklyStat.getDiffOperationSum());

		DomainWeeklyStat domainWeeklyStat = listDomainWeeklyStat.get(0);
		assertEquals(4, (long) domainWeeklyStat.getOperationCount());
		assertEquals(150, (long) domainWeeklyStat.getActualOperationSum());
		assertEquals(3, (long) domainWeeklyStat.getCreateOperationCount());
		assertEquals(200, (long) domainWeeklyStat.getCreateOperationSum());
		assertEquals(1, (long) domainWeeklyStat.getDeleteOperationCount());
		assertEquals(-50, (long) domainWeeklyStat.getDeleteOperationSum());
		assertEquals(150, (long) domainWeeklyStat.getDiffOperationSum());
	}
}
