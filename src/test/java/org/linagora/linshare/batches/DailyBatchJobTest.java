/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
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

package org.linagora.linshare.batches;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({
	"/import-tests-account.sql",
	"/import-tests-stat.sql",
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DailyBatchJobTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

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
	@Qualifier("statisticWeeklyUserBatch")
	private GenericBatch weeklyUserBatch;

	@Autowired
	@Qualifier("statisticWeeklyThreadBatch")
	private GenericBatch weeklyThreadBatch;

	@Autowired
	@Qualifier("statisticWeeklyDomainBatch")
	private GenericBatch weeklyDomainBatch;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;

	@Autowired
	@Qualifier("statisticDailyDomainBatch")
	private GenericBatch dailyDomainBatch;

	@Autowired
	@Qualifier("computeSubDomainQuotaBatch")
	private GenericBatch computeSubDomainQuotaBatch;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;

	@BeforeEach
	public void setUp() {
		jane = userRepository.findByMail("user2@linshare.org");;
	}

	@Test
	public void test() throws JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(dailyUserBatch);
		batches.add(dailyThreadBatch);
		batches.add(dailyDomainBatch);
		batches.add(computeSubDomainQuotaBatch);
		batches.add(weeklyUserBatch);
		batches.add(weeklyThreadBatch);
		batches.add(weeklyDomainBatch);
		batches.add(monthlyUserBatch);
		batches.add(monthlyThreadBatch);
		batches.add(monthlyDomainBatch);

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(null, null, null,
				yesterday());
		// assertNotEquals(0, listOperationHistory.size());
		List<UserDailyStat> listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null, new Date(),
				new Date());
		assertEquals(0, listUserDailyStat.size());
		List<ThreadDailyStat> listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null, new Date(),
				new Date());
		assertEquals(0, listThreadDailyStat.size());

		// running all batches.
		assertTrue(batchRunner.execute(batches), "At least one batch failed.");

		listOperationHistory = operationHistoryBusinessService.find(null, null, null, yesterday());
		assertEquals(0, listOperationHistory.size());
		listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null,
				new GregorianCalendar(2042, 9, 01, 00, 00).getTime(),
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime());
		assertEquals(1, listUserDailyStat.size());
		UserDailyStat userDailyStat = listUserDailyStat.get(0);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(5, (long) userDailyStat.getOperationCount());
		assertEquals(50, (long) userDailyStat.getActualOperationSum());
		assertEquals(3, (long) userDailyStat.getCreateOperationCount());
		assertEquals(100, (long) userDailyStat.getCreateOperationSum());
		assertEquals(2, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) userDailyStat.getDiffOperationSum());
		AccountQuota quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		// user quota 496 + 300 from delete operation (see import-tests-operationHistory.sql)
		assertEquals(796, (long) quota.getCurrentValue());
		assertEquals(496, (long) quota.getLastValue());
		assertEquals(1900, (long) quota.getQuota());
		assertEquals(1300, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());
		listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null,
				new GregorianCalendar(2042, 10, 01, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 16, 00, 00).getTime());
		assertEquals(5, listThreadDailyStat.size());
		ThreadDailyStat threadDailyStat = listThreadDailyStat.get(0);
		assertEquals(5, (long) threadDailyStat.getOperationCount());
		assertEquals(50, (long) threadDailyStat.getActualOperationSum());
		assertEquals(3, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(100, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(2, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) threadDailyStat.getDiffOperationSum());
		accountQuotaBusinessService.createOrUpdate(threadDailyStat.getAccount(), new Date());
		quota = accountQuotaBusinessService.find(threadDailyStat.getAccount());
		assertNotNull(quota);
		assertEquals(0, (long) quota.getCurrentValue());
		assertEquals(0, (long) quota.getLastValue());
		assertEquals(2000, (long) quota.getQuota());
		assertEquals(1500, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());
		List<DomainDailyStat> listDomaindailyStat = domainDailyStatBusinessService.findBetweenTwoDates(jane.getDomain(),
				null, null);
		assertEquals(5, listDomaindailyStat.size());
		listDomaindailyStat = domainDailyStatBusinessService.findBetweenTwoDates(jane.getDomain(),
				new GregorianCalendar(2042, 8, 8, 00, 00).getTime(), new GregorianCalendar(2042, 8, 11, 00, 00).getTime());
		assertEquals(1, listDomaindailyStat.size());
		DomainDailyStat domainDailyStat = listDomaindailyStat.get(0);
		assertEquals(jane.getDomain(), domainDailyStat.getDomain());
		assertEquals(5, (long) domainDailyStat.getOperationCount());
		assertEquals(50, (long) domainDailyStat.getActualOperationSum());
		assertEquals(3, (long) domainDailyStat.getCreateOperationCount());
		assertEquals(100, (long) domainDailyStat.getCreateOperationSum());
		assertEquals(2, (long) domainDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) domainDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) domainDailyStat.getDiffOperationSum());
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}
}
