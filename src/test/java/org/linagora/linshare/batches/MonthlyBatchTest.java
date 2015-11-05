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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.MonthlyBatch;
import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.domain.entities.ThreadMonthlyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadMonthlyStatBusinessService;
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
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class MonthlyBatchTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private UserMonthlyStatBusinessService userMonthlyStatBusinessService;

	@Autowired
	private ThreadMonthlyStatBusinessService threadMonthlyStatBusinessService;

	@Autowired
	private DomainMonthlyStatBusinessService domainMonthlyStatBusinessService;

	@Autowired
	private MonthlyBatch monthlyStatBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp() throws Exception {
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		monthlyStatBatch.executeBatch();

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -1);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DATE, +2);
		Date b = calendar.getTime();
		List<UserMonthlyStat> listUserMonthlyStat = userMonthlyStatBusinessService.findBetweenTwoDates(null, a, b);
		List<ThreadMonthlyStat> listThreadMonthlyStat = threadMonthlyStatBusinessService.findBetweenTwoDates(null, a, b);
		List<DomainMonthlyStat> listDomainMonthlyStat = domainMonthlyStatBusinessService.findBetweenTwoDates(null, a, b);

		assertEquals(1, listUserMonthlyStat.size());
		assertEquals(2, listThreadMonthlyStat.size());
		assertEquals(2, listDomainMonthlyStat.size());

		UserMonthlyStat userMonthlyStat = listUserMonthlyStat.get(0);
		assertEquals(1200, (long) userMonthlyStat.getActualOperationSum());
		assertEquals(31, (long) userMonthlyStat.getOperationCount());
		assertEquals(4200, (long) userMonthlyStat.getAddOperationSum());
		assertEquals(17, (long) userMonthlyStat.getAddOperationCount());
		assertEquals(-3000, (long) userMonthlyStat.getDeleteOperationSum());
		assertEquals(14, (long) userMonthlyStat.getDeleteOperationCount());
		assertEquals(1200, (long) userMonthlyStat.getDiffOperationSum());

		ThreadMonthlyStat threadMonthlyStat = listThreadMonthlyStat.get(0);
		assertEquals(50, (long) threadMonthlyStat.getActualOperationSum());
		assertEquals(5, (long) threadMonthlyStat.getOperationCount());
		assertEquals(100, (long) threadMonthlyStat.getAddOperationSum());
		assertEquals(3, (long) threadMonthlyStat.getAddOperationCount());
		assertEquals(-50, (long) threadMonthlyStat.getDeleteOperationSum());
		assertEquals(2, (long) threadMonthlyStat.getDeleteOperationCount());
		assertEquals(50, (long) threadMonthlyStat.getDiffOperationSum());

		threadMonthlyStat = listThreadMonthlyStat.get(1);
		assertEquals(150, (long) threadMonthlyStat.getActualOperationSum());
		assertEquals(4, (long) threadMonthlyStat.getOperationCount());
		assertEquals(200, (long) threadMonthlyStat.getAddOperationSum());
		assertEquals(3, (long) threadMonthlyStat.getAddOperationCount());
		assertEquals(-50, (long) threadMonthlyStat.getDeleteOperationSum());
		assertEquals(1, (long) threadMonthlyStat.getDeleteOperationCount());
		assertEquals(150, (long) threadMonthlyStat.getDiffOperationSum());

		DomainMonthlyStat domainMonthlyStat = listDomainMonthlyStat.get(0);
		assertEquals(100, (long) domainMonthlyStat.getActualOperationSum());
		assertEquals(10, (long) domainMonthlyStat.getOperationCount());
		assertEquals(200, (long) domainMonthlyStat.getAddOperationSum());
		assertEquals(6, (long) domainMonthlyStat.getAddOperationCount());
		assertEquals(-100, (long) domainMonthlyStat.getDeleteOperationSum());
		assertEquals(4, (long) domainMonthlyStat.getDeleteOperationCount());
		assertEquals(100, (long) domainMonthlyStat.getDiffOperationSum());

		domainMonthlyStat = listDomainMonthlyStat.get(1);
		assertEquals(150, (long) domainMonthlyStat.getActualOperationSum());
		assertEquals(4, (long) domainMonthlyStat.getOperationCount());
		assertEquals(200, (long) domainMonthlyStat.getAddOperationSum());
		assertEquals(3, (long) domainMonthlyStat.getAddOperationCount());
		assertEquals(-50, (long) domainMonthlyStat.getDeleteOperationSum());
		assertEquals(1, (long) domainMonthlyStat.getDeleteOperationCount());
		assertEquals(150, (long) domainMonthlyStat.getDiffOperationSum());
	}
}
