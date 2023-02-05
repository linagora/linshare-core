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
package org.linagora.linshare.repository.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ 
	
	"/import-tests-stat.sql",
	"/import-tests-operationHistory.sql"})
@ContextConfiguration(locations = { "classpath:springContext-test.xml", "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class OperationHistoryRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private OperationHistoryRepository operationHistoryRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;
	private User john;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
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
