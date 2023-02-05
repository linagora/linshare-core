/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.batches;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@Sql({
	
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql",
	"/import-tests-stat.sql"})
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class StatisticDailyUserBatchTest {

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	private User jane;

	@BeforeEach
	public void setUp (){
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
	}

	@Test
	public void testWithPurgedUser() {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(dailyUserBatch);
		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(jane, null, null,
				new Date());
		Assertions.assertEquals(3, listOperationHistory.size());
		accountRepository.delete(jane);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

}
