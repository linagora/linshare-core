/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.impl.StatisticDailyDomainBatchImpl;
import org.linagora.linshare.core.batches.impl.StatisticDailyThreadBatchImpl;
import org.linagora.linshare.core.batches.impl.StatisticDailyUserBatchImpl;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
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


@ExtendWith(SpringExtension.class)
@Transactional
@Sql({
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql"})
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
		"classpath:springContext-batches.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
// Use dirties context to reset the H2 database because of quota alteration 
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DailyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
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
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	private ContainerQuotaBusinessService containerQuotaBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	LoadingServiceTestDatas dates;
	private User jane;

	@BeforeEach
	public void setUp (){
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	/**
	 * Test batches : 
	 * {@link StatisticDailyThreadBatchImpl}
	 * {@link StatisticDailyUserBatchImpl}
	 * {@link StatisticDailyDomainBatchImpl}
	 */
	@Test
	public void testStatisticDailyBatchs() {
		/**
		 *  *************** Statistic Daily Thread Batch ***************
		 */
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> listThreadIdentifier = dailyThreadBatch.getAll(batchRunContext);
		// expected 2 workgroups
		assertEquals(2, listThreadIdentifier.size());
		// TODO: provide workgroups from LoadingServiceTestDatas
		WorkGroup workGroup_1 = (WorkGroup) accountRepository.findByLsUuid(listThreadIdentifier.get(0));
		WorkGroup workGroup_2 = (WorkGroup) accountRepository.findByLsUuid(listThreadIdentifier.get(1));
		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(workGroup_1, null, null,
				new Date());
		// expected 3 operations done for workgroup1 (@workgroup_id_20: "LINAGORA" from
		// import-tests-operationHistory.sql)
		assertEquals(3, listOperationHistory.size());
		// expected 2 operations done for workgroup2 (@workgroup_id_21 "RATP" from
		// import-tests-operationHistory.sql)
		listOperationHistory = operationHistoryBusinessService.find(workGroup_2, null, null, new Date());
		assertEquals(2, listOperationHistory.size());

		AccountQuota workgroup_1_quota = accountQuotaBusinessService.find(workGroup_1);
		AccountQuota workgroup_2_quota = accountQuotaBusinessService.find(workGroup_2);
		
		// assert database insertions from (import-tests-quota.sql)
		assertEquals(Long.valueOf(900), workgroup_1_quota.getCurrentValue());
		assertEquals(Long.valueOf(200), workgroup_1_quota.getLastValue());
		assertEquals(Long.valueOf(2000), workgroup_1_quota.getQuota());
		assertEquals(Long.valueOf(1500), workgroup_1_quota.getQuotaWarning());
		assertEquals(Long.valueOf(5), workgroup_1_quota.getMaxFileSize());
		// After batch execution , Daily thread statistics are created.
		// Batch for workGroup_1
		dailyThreadBatch.execute(batchRunContext, workGroup_1.getLsUuid(), listThreadIdentifier.size(), 0);
		// Assertions after batch execution workgroup_1 (20)
		assertEquals(Long.valueOf(1200), workgroup_1_quota.getCurrentValue());
		assertEquals(Long.valueOf(900), workgroup_1_quota.getLastValue());
		assertEquals(Long.valueOf(2000), workgroup_1_quota.getQuota());
		assertEquals(Long.valueOf(1500), workgroup_1_quota.getQuotaWarning());
		assertEquals(Long.valueOf(5), workgroup_1_quota.getMaxFileSize());

		// Date to request statistics from yesterday
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		
		// Get daily thread statistics generated by the batch. workgrou_1 (20)
		List<ThreadDailyStat> listThreaddailyStat_1 = threadDailyStatBusinessService.findBetweenTwoDates(workGroup_1,
				yesterday, new Date());
		// expected 1 statistic daily thread created
		assertEquals(1, listThreaddailyStat_1.size());
		ThreadDailyStat threadDailyStat_1 = listThreaddailyStat_1.get(0);
		assertEquals(workGroup_1, threadDailyStat_1.getAccount());
		assertEquals(Long.valueOf(3), threadDailyStat_1.getOperationCount());
		assertEquals(Long.valueOf(1200), threadDailyStat_1.getActualOperationSum());
		assertEquals(Long.valueOf(2), threadDailyStat_1.getCreateOperationCount());
		assertEquals(Long.valueOf(600), threadDailyStat_1.getCreateOperationSum());
		assertEquals(Long.valueOf(1), threadDailyStat_1.getDeleteOperationCount());
		assertEquals(Long.valueOf(-300), threadDailyStat_1.getDeleteOperationSum());
		assertEquals(Long.valueOf(300), threadDailyStat_1.getDiffOperationSum());

		// assert DB values for workgroup_2 (21)
		assertNotNull(workgroup_2_quota);
		assertEquals(500, workgroup_2_quota.getCurrentValue());
		assertEquals(200, workgroup_2_quota.getLastValue());
		assertEquals(1300, workgroup_2_quota.getQuota());
		assertEquals(1500, workgroup_2_quota.getQuotaWarning());
		assertEquals(6, workgroup_2_quota.getMaxFileSize());
		
		// Batch for workGroup_2 (21)
		dailyThreadBatch.execute(batchRunContext, workGroup_2.getLsUuid(), listThreadIdentifier.size(), 1);
		
		assertNotNull(workgroup_2_quota);
		assertEquals(900, workgroup_2_quota.getCurrentValue());
		assertEquals(500, workgroup_2_quota.getLastValue());
		assertEquals(1300, workgroup_2_quota.getQuota());
		assertEquals(1500, workgroup_2_quota.getQuotaWarning());
		assertEquals(6, workgroup_2_quota.getMaxFileSize());
		
		// generated statistics for workgroup_2
		List<ThreadDailyStat> listThreaddailyStat_2 = threadDailyStatBusinessService.findBetweenTwoDates(workGroup_2, yesterday, new Date());
		// Assertion about statistics generated for workroup_2
		assertEquals(1, listThreaddailyStat_2.size());
		ThreadDailyStat threadDailyStat_2 = listThreaddailyStat_2.get(0);
		assertEquals(workGroup_2, threadDailyStat_2.getAccount());
		assertEquals(2, threadDailyStat_2.getOperationCount());
		assertEquals(900, threadDailyStat_2.getActualOperationSum());
		assertEquals(2, threadDailyStat_2.getCreateOperationCount());
		assertEquals(400, threadDailyStat_2.getCreateOperationSum());
		assertEquals(0, threadDailyStat_2.getDeleteOperationCount());
		assertEquals(0, threadDailyStat_2.getDeleteOperationSum());
		assertEquals(400, threadDailyStat_2.getDiffOperationSum());

		/**
		 *  *************** Statistic Daily User Batch ***************
		 */
		// assert operationHistory insertion for Jane Smith (11) (import-tests-operationHistory.sql)
		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertNotEquals(8, listOperationHistory.size());
		// assert DB insertions see import-tests-quota.sql
		AccountQuota janeQuota = accountQuotaBusinessService.find(jane);
		assertNotNull(janeQuota);
		assertEquals(496, janeQuota.getCurrentValue());
		assertEquals(0, janeQuota.getLastValue());
		assertEquals(1900, janeQuota.getQuota());
		assertEquals(1300, janeQuota.getQuotaWarning());
		assertEquals(5, janeQuota.getMaxFileSize());
		// After batch user daily statistics are generated
		dailyUserBatch.execute(batchRunContext, jane.getLsUuid(), 10, 1);
		// assert quota after batch execution
		assertEquals(796, janeQuota.getCurrentValue());
		assertEquals(496, janeQuota.getLastValue());
		assertEquals(1900, janeQuota.getQuota());
		assertEquals(1300, janeQuota.getQuotaWarning());
		assertEquals(5, janeQuota.getMaxFileSize());
		
		List<UserDailyStat> listJanedailyStat = userdailyStatBusinessService.findBetweenTwoDates(jane, yesterday,
				new Date());
		assertEquals(1, listJanedailyStat.size());
		UserDailyStat userJaneDailyStat = listJanedailyStat.get(0);
		assertEquals(jane, userJaneDailyStat.getAccount());
		assertEquals(3, userJaneDailyStat.getOperationCount());
		assertEquals(796, userJaneDailyStat.getActualOperationSum());
		assertEquals(2, userJaneDailyStat.getCreateOperationCount());
		assertEquals(600, userJaneDailyStat.getCreateOperationSum());
		assertEquals(1, userJaneDailyStat.getDeleteOperationCount());
		assertEquals(-300, userJaneDailyStat.getDeleteOperationSum());
		assertEquals(300, userJaneDailyStat.getDiffOperationSum());

		// assert that after batch execution there is no operation History 
		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertEquals(0, listOperationHistory.size());
		
		/**
		 *  *************** Statistic Daily Domain Batch ***************
		 */
		
		// Assert DB insertions Quota for MyDomain (2)
		DomainQuota quotaDomain = domainQuotaBusinessService.find(jane.getDomain());
		assertNotNull(quotaDomain);
		assertEquals(500, quotaDomain.getLastValue());
		assertEquals(1096, quotaDomain.getCurrentValue());
		assertEquals(1900, quotaDomain.getQuota());
		assertEquals(1800, quotaDomain.getQuotaWarning());
		
		dailyDomainBatch.execute(batchRunContext, jane.getDomain().getUuid(), 20, 1);

		ContainerQuota containerUserQuota = containerQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.USER);
		assertNotNull(containerUserQuota);
		assertEquals(496, containerUserQuota.getLastValue());
		// sum of all users : jane : 796 (from getActualOperationSum after batach) + john 900 = 1696
		assertEquals(1696, containerUserQuota.getCurrentValue());
		assertEquals(Long.valueOf(1900), containerUserQuota.getQuota());
		assertEquals(1300, containerUserQuota.getQuotaWarning());
		assertEquals(5, containerUserQuota.getDefaultMaxFileSize());


		ContainerQuota containerWorkGroupQuota= containerQuotaBusinessService.find(jane.getDomain(), ContainerQuotaType.WORK_GROUP);
		assertNotNull(containerWorkGroupQuota);

		assertEquals(900, containerWorkGroupQuota.getLastValue());
		// sum of all workgroups : 900 + 1200 (from getActualOperationSum after batch)= 2100
		assertEquals(2100, containerWorkGroupQuota.getCurrentValue());
		assertEquals(2000, containerWorkGroupQuota.getQuota());
		assertEquals(1500, containerWorkGroupQuota.getQuotaWarning());
		assertEquals(5, containerWorkGroupQuota.getDefaultMaxFileSize());
		
		assertNotNull(quotaDomain);
		assertEquals(1096, quotaDomain.getLastValue());
		// container user 1696 + container workgroup 2100 = 3796
		assertEquals(3796, quotaDomain.getCurrentValue());
		assertEquals(1900, quotaDomain.getQuota());
		assertEquals(1800, quotaDomain.getQuotaWarning());

		quotaDomain = domainQuotaBusinessService.findRootQuota();
		assertNotNull(quotaDomain);
		// cf sql import-tests-quota.sql
		assertEquals(1096, quotaDomain.getCurrentValue());
		assertEquals(100, quotaDomain.getLastValue());
		assertEquals(2300, quotaDomain.getQuota());
		assertEquals(2000, quotaDomain.getQuotaWarning());
	}
}
