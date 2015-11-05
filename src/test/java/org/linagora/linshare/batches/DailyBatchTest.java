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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.DailyBatch;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DailyBatchTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private DailyBatch dailyBatch;

	@Autowired
	private UserDailyStatBusinessService userDailyStatBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp (){
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		dailyBatch.executeBatch();

		List<UserDailyStat> listUserDailyStat = userDailyStatBusinessService.findBetweenTwoDates(null, null, null);
		List<ThreadDailyStat> listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null, null, null);
		List<DomainDailyStat> listDomainDailyStat = domainDailyStatBusinessService.findBetweenTwoDates(null, null, null);

		assertEquals(2, listUserDailyStat.size());
		assertEquals(2, listThreadDailyStat.size());
		assertEquals(4, listDomainDailyStat.size());

		UserDailyStat userDaily = listUserDailyStat.get(0);
		assertEquals(700, (long) userDaily.getActualOperationSum());
		assertEquals(7, (long) userDaily.getOperationCount());
		assertEquals(1200, (long) userDaily.getAddOperationSum());
		assertEquals(5, (long) userDaily.getAddOperationCount());
		assertEquals(-500, (long) userDaily.getDeleteOperationSum());
		assertEquals(2, (long) userDaily.getDeleteOperationCount());
		assertEquals(700, (long) userDaily.getDiffOperationSum());

		userDaily = listUserDailyStat.get(1);
		assertEquals(200, (long) userDaily.getActualOperationSum());
		assertEquals(1, (long) userDaily.getOperationCount());
		assertEquals(200, (long) userDaily.getAddOperationSum());
		assertEquals(1, (long) userDaily.getAddOperationCount());
		assertEquals(0, (long) userDaily.getDeleteOperationSum());
		assertEquals(0, (long) userDaily.getDeleteOperationCount());
		assertEquals(200, (long) userDaily.getDiffOperationSum());

		ThreadDailyStat threadDaily = listThreadDailyStat.get(0);
		assertEquals(100, (long) threadDaily.getActualOperationSum());
		assertEquals(2, (long) threadDaily.getOperationCount());
		assertEquals(400, (long) threadDaily.getAddOperationSum());
		assertEquals(1, (long) threadDaily.getAddOperationCount());
		assertEquals(-300, (long) threadDaily.getDeleteOperationSum());
		assertEquals(1, (long) threadDaily.getDeleteOperationCount());
		assertEquals(100, (long) threadDaily.getDiffOperationSum());

		threadDaily = listThreadDailyStat.get(1);
		assertEquals(200, (long) threadDaily.getActualOperationSum());
		assertEquals(1, (long) threadDaily.getOperationCount());
		assertEquals(200, (long) threadDaily.getAddOperationSum());
		assertEquals(1, (long)threadDaily.getAddOperationCount());
		assertEquals(0, (long) threadDaily.getDeleteOperationSum());
		assertEquals(0, (long) threadDaily.getDeleteOperationCount());
		assertEquals(200, (long) threadDaily.getDiffOperationSum());

		DomainDailyStat domainDaily = listDomainDailyStat.get(0);
		assertEquals(-100, (long) domainDaily.getActualOperationSum());
		assertEquals(3, (long) domainDaily.getOperationCount());
		assertEquals(400, (long) domainDaily.getAddOperationSum());
		assertEquals(1, (long) domainDaily.getAddOperationCount());
		assertEquals(-500, (long) domainDaily.getDeleteOperationSum());
		assertEquals(2, (long) domainDaily.getDeleteOperationCount());
		assertEquals(-100, (long) domainDaily.getDiffOperationSum());

		domainDaily = listDomainDailyStat.get(1);
		assertEquals(800, (long) domainDaily.getActualOperationSum());
		assertEquals(4, (long) domainDaily.getOperationCount());
		assertEquals(800, (long) domainDaily.getAddOperationSum());
		assertEquals(4, (long) domainDaily.getAddOperationCount());
		assertEquals(0, (long) domainDaily.getDeleteOperationSum());
		assertEquals(0, (long) domainDaily.getDeleteOperationCount());
		assertEquals(800, (long) domainDaily.getDiffOperationSum());

		domainDaily = listDomainDailyStat.get(2);
		assertEquals(100, (long) domainDaily.getActualOperationSum());
		assertEquals(2, (long) domainDaily.getOperationCount());
		assertEquals(400, (long) domainDaily.getAddOperationSum());
		assertEquals(1, (long) domainDaily.getAddOperationCount());
		assertEquals(-300, (long) domainDaily.getDeleteOperationSum());
		assertEquals(1, (long) domainDaily.getDeleteOperationCount());
		assertEquals(100, (long) domainDaily.getDiffOperationSum());

		domainDaily = listDomainDailyStat.get(3);
		assertEquals(400, (long) domainDaily.getActualOperationSum());
		assertEquals(2, (long) domainDaily.getOperationCount());
		assertEquals(400, (long) domainDaily.getAddOperationSum());
		assertEquals(2, (long) domainDaily.getAddOperationCount());
		assertEquals(0, (long) domainDaily.getDeleteOperationSum());
		assertEquals(0, (long) domainDaily.getDeleteOperationCount());
		assertEquals(400, (long) domainDaily.getDiffOperationSum());
	}
}
