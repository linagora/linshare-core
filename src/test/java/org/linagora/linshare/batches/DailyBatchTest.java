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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

// TODO: Full refactoring needed ! Tests are not human readable, neither understandable ! :(
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
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DailyBatchTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("statisticDailyDomainBatch")
	private GenericBatch dailyDomainBatch;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	private ContainerQuotaBusinessService ensembleQuotaBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private BatchHistoryBusinessService batchHistoryBusinessService;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp (){
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-quota.sql", false);
		this.executeSqlScript("import-tests-stat.sql", false);
		this.executeSqlScript("import-mails-hibernate3.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> listThreadIdentifier = dailyThreadBatch.getAll(batchRunContext);
		assertEquals(2, listThreadIdentifier.size());

		Thread thread1 = (Thread) accountRepository.findByLsUuid(listThreadIdentifier.get(0));
		Thread thread2 = (Thread) accountRepository.findByLsUuid(listThreadIdentifier.get(1));

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(thread1, null, null, new Date());
		assertEquals(3, listOperationHistory.size());

		listOperationHistory = operationHistoryBusinessService.find(thread2, null, null, new Date());
		assertEquals(2, listOperationHistory.size());

		dailyThreadBatch.execute(batchRunContext, thread1.getLsUuid(), listThreadIdentifier.size(), 0);

		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		List<ThreadDailyStat> listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(thread1, d.getTime(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		ThreadDailyStat threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(thread1, threadDailyStat.getAccount());
		assertEquals(3, (long) threadDailyStat.getOperationCount());
		assertEquals(300, (long) threadDailyStat.getActualOperationSum());
		assertEquals(2, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(600, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(1, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(300, (long) threadDailyStat.getDiffOperationSum());

		AccountQuota quota = accountQuotaBusinessService.find(thread1);
		assertNotNull(quota);
		assertEquals(1000, (long) quota.getCurrentValue());
		assertEquals(700, (long) quota.getLastValue());
		assertEquals(1000, (long) quota.getQuota());
		assertEquals(800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());

		dailyThreadBatch.execute(batchRunContext, thread2.getLsUuid(), listThreadIdentifier.size(), 1);

		batchHistoryBusinessService.findByUuid(thread2.getLsUuid());

		listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(thread2, d.getTime(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(thread2, threadDailyStat.getAccount());
		assertEquals(2, (long) threadDailyStat.getOperationCount());
		assertEquals(400, (long) threadDailyStat.getActualOperationSum());
		assertEquals(2, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(400, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(400, (long) threadDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(thread2);
		assertNotNull(quota);
		assertEquals(900, (long) quota.getCurrentValue());
		assertEquals(500, (long) quota.getLastValue());
		assertEquals(1300, (long) quota.getQuota());
		assertEquals(1000, (long) quota.getQuotaWarning());
		assertEquals(6, (long) quota.getMaxFileSize());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertNotEquals(0, listOperationHistory.size());

		dailyUserBatch.execute(batchRunContext, jane.getLsUuid(), 10, 1);

		List<UserDailyStat> listUserdailyStat = userdailyStatBusinessService.findBetweenTwoDates(jane, d.getTime(), new Date());

		assertEquals(1, listUserdailyStat.size());
		UserDailyStat userDailyStat = listUserdailyStat.get(0);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(3, (long) userDailyStat.getOperationCount());
		assertEquals(300, (long) userDailyStat.getActualOperationSum());
		assertEquals(2, (long) userDailyStat.getCreateOperationCount());
		assertEquals(600, (long) userDailyStat.getCreateOperationSum());
		assertEquals(1, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(300, (long) userDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		assertEquals(1100, (long) quota.getCurrentValue());
		assertEquals(800, (long) quota.getLastValue());
		assertEquals(1600, (long) quota.getQuota());
		assertEquals(1480, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertEquals(0, listOperationHistory.size());

		dailyDomainBatch.execute(batchRunContext, jane.getDomain().getUuid(), 20, 1);

		ContainerQuota ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.USER);
		quota = accountQuotaBusinessService.find(jane);
		assertNotNull(ensembleQuota);
		assertEquals(496, (long) ensembleQuota.getLastValue());
		// jane : 1100 + john 900 + last value = 2496
		assertEquals(2496, (long) ensembleQuota.getCurrentValue());
		assertEquals(1900, (long) ensembleQuota.getQuota());
		assertEquals(1300, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getDefaultMaxFileSize());

		ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.WORK_GROUP);
		assertNotNull(ensembleQuota);

		assertEquals(900, (long) ensembleQuota.getLastValue());
		// last value(900) + 1900 ? = 2800
		assertEquals(2800, (long) ensembleQuota.getCurrentValue());
		assertEquals(2000, (long) ensembleQuota.getQuota());
		assertEquals(1500, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getDefaultMaxFileSize());

		DomainQuota quotaD = domainQuotaBusinessService.find(jane.getDomain());
		assertNotNull(quotaD);
		assertEquals(1096, (long) quotaD.getLastValue());
		// container user 2496  + container workgroup 2800 = 5296
		// 5296 + lastvalue = 6392
		assertEquals(6392, (long) quotaD.getCurrentValue());
		assertEquals(1900, (long) quotaD.getQuota());
		assertEquals(1800, (long) quotaD.getQuotaWarning());

		quotaD = domainQuotaBusinessService.findRootQuota();
		assertNotNull(quotaD);
		// cf sql import-tests-quota.sql
		assertEquals(1096, (long) quotaD.getCurrentValue());
		assertEquals(100, (long) quotaD.getLastValue());
		assertEquals(2300, (long) quotaD.getQuota());
		assertEquals(2000, (long) quotaD.getQuotaWarning());
	}
}
