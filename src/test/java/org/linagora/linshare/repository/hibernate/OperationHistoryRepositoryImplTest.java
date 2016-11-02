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
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml", "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class OperationHistoryRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private OperationHistoryRepository operationHistoryRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;
	private User john;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-stat.sql", false);
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
		john = dates.getUser1();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void testFindOperationHistory() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain1 = john.getDomain();
		List<OperationHistory> result = operationHistoryRepository.findAll();
		assertEquals(21, result.size());
		result = operationHistoryRepository.find(jane, null, null, null, null);
		assertEquals(8, result.size());
		result = operationHistoryRepository.find(john, null, null, null, null);
		assertEquals(6, result.size());
		result = operationHistoryRepository.find(null, domain1, null, null, null);
		assertEquals(13, result.size());
		result = operationHistoryRepository.find(null, null, null, new GregorianCalendar(2042, 8, 15, 00, 00).getTime(),
				null);
		assertEquals(9, result.size());
		result = operationHistoryRepository.find(jane, domain1, ContainerQuotaType.USER,
				new GregorianCalendar(2042, 10, 8, 00, 00).getTime(), null);
		assertEquals(8, result.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateOperationHistory() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		OperationHistory o = null;
		for (int i = 0; i < 100; i++) {
			Long value = (long) (Math.random() * (10 - 200));
			OperationHistoryTypeEnum type = OperationHistoryTypeEnum.CREATE;
			if (i % 3 == 0) {
				value = -value;
				type = OperationHistoryTypeEnum.DELETE;
			}
			o = new OperationHistory(jane, jane.getDomain(), (long) (Math.random() * (10 - 200)), type,
					ContainerQuotaType.USER);
			operationHistoryRepository.create(o);
		}
		List<OperationHistory> result = operationHistoryRepository.find(jane, jane.getDomain(), ContainerQuotaType.USER,
				new Date(), null);
		assertEquals(103, result.size());
		result = operationHistoryRepository.find(jane, jane.getDomain(), ContainerQuotaType.USER, new Date(),
				OperationHistoryTypeEnum.CREATE);
		assertEquals(68, result.size());
		result = operationHistoryRepository.find(jane, jane.getDomain(), ContainerQuotaType.USER, new Date(),
				OperationHistoryTypeEnum.DELETE);
		assertEquals(35, result.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSumOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long result = operationHistoryRepository.sumOperationValue(john, null,
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, null);
		assertNotNull(result);
		assertEquals(1451, result);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCountOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long result = operationHistoryRepository.countOperationValue(jane, null,
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, null);
		assertNotNull(result);
		assertEquals(7, result);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSumCreateOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long result = operationHistoryRepository.sumOperationValue(jane, null,
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), OperationHistoryTypeEnum.CREATE, null);
		assertNotNull(result);
		assertEquals(1400, result);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCountCreatetOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long result = operationHistoryRepository.countOperationValue(jane, null,
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), OperationHistoryTypeEnum.CREATE, null);
		assertNotNull(result);
		assertEquals(6, result);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCountDeleteOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long result = operationHistoryRepository.countOperationValue(jane, null,
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), OperationHistoryTypeEnum.DELETE, null);
		assertNotNull(result);
		assertEquals(1, result);
		result = operationHistoryRepository.countOperationValue(jane, null, null, OperationHistoryTypeEnum.DELETE, null);
		assertEquals(2, result);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteBeforeDateOperationValue() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<OperationHistory> result = operationHistoryRepository.find(jane, null, null, null, null);
		assertEquals(8, result.size());
		operationHistoryRepository.deleteBeforeDateByAccount(new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), jane);
		result = operationHistoryRepository.find(jane, null, null, null, null);
		assertEquals(1, result.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindUuid() {
		List<AbstractDomain> result = operationHistoryRepository.findDomainBeforeDate(new GregorianCalendar(2042, 9, 16, 00, 00).getTime());
		for (AbstractDomain r : result) {
			System.out.println(" identifier domain : " + r.getUuid());
		}
		assertEquals(4, result.size());
	}
}
