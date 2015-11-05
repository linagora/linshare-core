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
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
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
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
		john = dates.getUser1();
	}

	@Test
	public void testCreateOperationHistory() {
		AbstractDomain domain1 = john.getDomain();
		AbstractDomain domain2 = jane.getDomain();
		Account account1 = jane;
		Account account2 = john;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, +15);
		Date a = calendar.getTime();
		List<OperationHistory> result = operationHistoryRepository.findAll();
		assertEquals(16, result.size());
		List<OperationHistory> result2 = operationHistoryRepository.find(account1, null, null, null);
		assertEquals(8, result2.size());
		List<OperationHistory> result3 = operationHistoryRepository.find(account2, null, null, null);
		assertEquals(3, result3.size());
		List<OperationHistory> result4 = operationHistoryRepository.find(null, domain1, null, null);
		assertEquals(5, result4.size());
		List<OperationHistory> result5 = operationHistoryRepository.find(null, domain2, null, null);
		assertEquals(5, result5.size());
		List<OperationHistory> result6 = operationHistoryRepository.find(null, null, null, a);
		assertEquals(11, result6.size());
		List<OperationHistory> result7 = operationHistoryRepository.find(account1, domain1, EnsembleType.USER, a);
		assertEquals(4, result7.size());
	}

	@Test
	public void testnewRepository() {
		Account account = john;
		AbstractDomain domain = jane.getDomain();
		OperationHistory o = new OperationHistory(account, domain, (long) 200, OperationHistoryTypeEnum.CREATE, EnsembleType.THREAD);
		operationHistoryRepository.create(o);
		List<OperationHistory> result = operationHistoryRepository.find(account, domain, EnsembleType.THREAD, null);
		assertEquals(1, result.size());
	}

	@Test
	public void testSumOperationValue() {
		Account account = john;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -10);
		Date a = calendar.getTime();
		long result = operationHistoryRepository.sumOperationValue(account, null, a, null);
		assertNotNull(result);
		assertEquals(200, result);
	}

	@Test
	public void testCountOperationValue() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -20);
		Date a = calendar.getTime();
		Account account = jane;
		long result = operationHistoryRepository.countOperationValue(account, null, a, null);
		assertNotNull(result);
		assertEquals(3, result);
	}

	@Test
	public void testSumCreattOperationValue() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -9);
		Date a = calendar.getTime();
		Account account = jane;
		long result = operationHistoryRepository.sumOperationValue(account, null, a, OperationHistoryTypeEnum.CREATE);
		assertNotNull(result);
		assertEquals(800, result);
	}

	@Test
	public void testCountCreatetOperationValue() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -9);
		Date a = calendar.getTime();
		Account account = jane;
		long result = operationHistoryRepository.countOperationValue(account, null, a, OperationHistoryTypeEnum.CREATE);
		assertNotNull(result);
		assertEquals(3, result);
	}

	@Test
	public void testCountDeleteOperationValue() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -9);
		Date a = calendar.getTime();
		Account account = jane;
		long result = operationHistoryRepository.countOperationValue(account, null, a, OperationHistoryTypeEnum.DELETE);
		assertNotNull(result);
		assertEquals(2, result);
	}

	@Test
	public void testDeleteBeforeDateOperationValue() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DAY_OF_MONTH, +60);
		Date b = calendar.getTime();
		Account account = jane;
		List<OperationHistory> result1 = operationHistoryRepository.find(account, null, null, b);
		operationHistoryRepository.deleteBeforeDate(a);
		List<OperationHistory> result2 = operationHistoryRepository.find(account, null, null, b);
		assertEquals(8,result1.size());
		assertEquals(2,result2.size());
	}
}
