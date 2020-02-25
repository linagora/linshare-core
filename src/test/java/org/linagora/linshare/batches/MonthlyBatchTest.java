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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatisticBusinessService;
import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.domain.entities.ThreadMonthlyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
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
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class MonthlyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserMonthlyStatBusinessService userMonthlyStatBusinessService;

	@Autowired
	private ThreadMonthlyStatBusinessService threadMonthlyStatBusinessService;

	@Autowired
	private DomainMonthlyStatBusinessService domainMonthlyStatBusinessService;

	@Autowired
	private ThreadWeeklyStatisticBusinessService threadWeeklyBusinessService;

	@Autowired
	private DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	@Autowired
	@Qualifier("statisticMonthlyUserBatch")
	private GenericBatch monthlyUserBatch;

	@Autowired
	@Qualifier("statisticMonthlyThreadBatch")
	private GenericBatch monthlyThreadBatch;

	@Autowired
	@Qualifier("statisticMonthlyDomainBatch")
	private GenericBatch monthlyDomainBatch;

	@Autowired
	private ThreadRepository threadRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	private Date currentDate;

	@BeforeEach
	public void setUp() throws Exception {
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -1);
		currentDate = calendar.getTime();
		jane = dates.getUser2();
	}

	@Test
	public void testUserMonthlyStat() {
		Date beginDate = new GregorianCalendar(2042, 8, 1, 00, 00).getTime();
		Date endDate =  new GregorianCalendar(2042, 8, 30, 00, 00).getTime();
		Date untilDate = new GregorianCalendar(2042, 9, 30, 23, 59).getTime();

		BatchRunContext batchRunContext = new BatchRunContext();
		monthlyUserBatch.execute(batchRunContext, jane.getLsUuid(), 10, 1);
		List<UserMonthlyStat> batchListUserMonthlyStat = userMonthlyStatBusinessService.findBetweenTwoDates(null,
				getFirstDayOfLastMonth(), getLastDayOfLastMonth());
		assertEquals(1, batchListUserMonthlyStat.size());
		userMonthlyStatBusinessService.deleteBeforeDate(getLastDayOfLastMonth());
		userMonthlyStatBusinessService.create(jane, beginDate, endDate);
		List<UserMonthlyStat> listUserMonthlyStat = userMonthlyStatBusinessService.findBetweenTwoDates(null, currentDate, untilDate);

		assertEquals(1, listUserMonthlyStat.size());

		UserMonthlyStat userMonthlyStat = listUserMonthlyStat.get(0);
		assertEquals(1200, (long) userMonthlyStat.getActualOperationSum());
		assertEquals(31, (long) userMonthlyStat.getOperationCount());
		assertEquals(4200, (long) userMonthlyStat.getCreateOperationSum());
		assertEquals(17, (long) userMonthlyStat.getCreateOperationCount());
		assertEquals(-3000, (long) userMonthlyStat.getDeleteOperationSum());
		assertEquals(14, (long) userMonthlyStat.getDeleteOperationCount());
		assertEquals(1200, (long) userMonthlyStat.getDiffOperationSum());
	}

	@Test
	public void testThreadMonthlyStat() {
		Date beginDate = new GregorianCalendar(2042, 10, 1, 00, 00).getTime();
		Date endDate = new GregorianCalendar(2042, 10, 30, 00, 00).getTime();
		Date untilDate = new GregorianCalendar(2042, 11, 1, 00, 00).getTime();

		List<String> listThreadIdentifier = threadWeeklyBusinessService.findUuidAccountBetweenTwoDates(
				beginDate,
				endDate);
		assertEquals(2, listThreadIdentifier.size());
		threadMonthlyStatBusinessService.create(threadRepository.findByLsUuid(listThreadIdentifier.get(0)), beginDate, endDate);
		List<ThreadMonthlyStat> listThreadMonthlyStat = threadMonthlyStatBusinessService.findBetweenTwoDates(null, currentDate, untilDate);
		assertEquals(1, listThreadMonthlyStat.size());
		ThreadMonthlyStat threadMonthlyStat = listThreadMonthlyStat.get(0);
		assertEquals(100, (long) threadMonthlyStat.getActualOperationSum());
		assertEquals(10, (long) threadMonthlyStat.getOperationCount());
		assertEquals(200, (long) threadMonthlyStat.getCreateOperationSum());
		assertEquals(6, (long) threadMonthlyStat.getCreateOperationCount());
		assertEquals(-100, (long) threadMonthlyStat.getDeleteOperationSum());
		assertEquals(4, (long) threadMonthlyStat.getDeleteOperationCount());
		assertEquals(100, (long) threadMonthlyStat.getDiffOperationSum());
	}

	@Test
	public void testDomainMonthlyStat() {
		Date beginDate = new GregorianCalendar(2042, 8, 1, 00, 00).getTime();
		Date endDate =  new GregorianCalendar(2042, 8, 30, 00, 00).getTime();
		Date untilDate = new GregorianCalendar(2042, 9, 30, 23, 59).getTime();

		List<String> listDomainIdentifier = domainWeeklyStatBusinessService.findIdentifierDomainBetweenTwoDates(
				beginDate,
				endDate);
		assertEquals(1, listDomainIdentifier.size());
		domainMonthlyStatBusinessService.create(jane.getDomain(), beginDate, endDate);
		List<DomainMonthlyStat> listDomainMonthlyStat = domainMonthlyStatBusinessService.findBetweenTwoDates(null, currentDate,
				untilDate);
		assertEquals(1, listDomainMonthlyStat.size());
		DomainMonthlyStat domainMonthlyStat = listDomainMonthlyStat.get(0);
		assertEquals(50, (long) domainMonthlyStat.getActualOperationSum());
		assertEquals(5, (long) domainMonthlyStat.getOperationCount());
		assertEquals(100, (long) domainMonthlyStat.getCreateOperationSum());
		assertEquals(3, (long) domainMonthlyStat.getCreateOperationCount());
		assertEquals(-50, (long) domainMonthlyStat.getDeleteOperationSum());
		assertEquals(2, (long) domainMonthlyStat.getDeleteOperationCount());
		assertEquals(50, (long) domainMonthlyStat.getDiffOperationSum());
	}

	private Date getLastDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		int nbDay = dateCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DATE, nbDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalendar.set(GregorianCalendar.MINUTE, 59);
		dateCalendar.set(GregorianCalendar.SECOND, 59);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 999);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		dateCalendar.set(GregorianCalendar.DATE, 1);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

}
