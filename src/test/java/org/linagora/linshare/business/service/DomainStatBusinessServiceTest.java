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
package org.linagora.linshare.business.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.domain.entities.DomainWeeklyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration( locations={"classpath:springContext-datasource.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml", "classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml", "classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml", "classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DomainStatBusinessServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	private DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	@Autowired
	private DomainMonthlyStatBusinessService domainMonthlyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	User jane;

	@Before
	public void setUp(){
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test(){
		AbstractDomain domain = jane.getDomain();
		GregorianCalendar calendre = new GregorianCalendar();
		calendre.add(GregorianCalendar.DATE, -1);
		Date a = calendre.getTime();
		calendre.add(GregorianCalendar.DATE, +2);
		Date b = calendre.getTime();
		domainDailyStatBusinessService.create(domain, new Date());
		List<DomainDailyStat> listDomain = domainDailyStatBusinessService.findBetweenTwoDates(domain, a, b);
		assertEquals(1, listDomain.size());
		DomainDailyStat entity = listDomain.get(0);
		assertEquals(4, (long) entity.getOperationCount());
		assertEquals(800, (long) entity.getActualOperationSum());
		assertEquals(4, (long) entity.getAddOperationCount());
		assertEquals(800, (long) entity.getAddOperationSum());
		assertEquals(0, (long) entity.getDeleteOperationCount());
		assertEquals(0, (long) entity.getDeleteOperationSum());
		assertEquals(800, (long) entity.getDiffOperationSum());

		GregorianCalendar calendre2 = new GregorianCalendar();
		calendre2.add(GregorianCalendar.DATE, -7);
		Date lastWeekBegin = calendre2.getTime();
		calendre2.add(GregorianCalendar.DATE, +14);
		Date lastWeekEnd = calendre2.getTime();
		domainWeeklyStatBusinessService.create(domain, lastWeekBegin, lastWeekEnd);
		List<DomainWeeklyStat> listWeekly = domainWeeklyStatBusinessService.findBetweenTwoDates(domain, a, b);
		assertEquals(1, listWeekly.size());
		DomainWeeklyStat entityWeekly = listWeekly.get(0);
		assertEquals(8, (long) entityWeekly.getOperationCount());
		assertEquals(950, (long) entityWeekly.getActualOperationSum());
		assertEquals(7, (long) entityWeekly.getAddOperationCount());
		assertEquals(1000, (long) entityWeekly.getAddOperationSum());
		assertEquals(1, (long) entityWeekly.getDeleteOperationCount());
		assertEquals(-50, (long) entityWeekly.getDeleteOperationSum());
		assertEquals(950, (long) entityWeekly.getDiffOperationSum());

		GregorianCalendar calendre3 = new GregorianCalendar();
		calendre3.add(GregorianCalendar.DATE, -30);
		Date lastMonthBegin = calendre3.getTime();
		calendre3.add(GregorianCalendar.DATE, +60);
		Date lastMonthEnd = calendre3.getTime();
		domainMonthlyStatBusinessService.create(domain, lastMonthBegin, lastMonthEnd);
		List<DomainMonthlyStat> listMonthly = domainMonthlyStatBusinessService.findBetweenTwoDates(domain, a, b);
		assertEquals(1, listMonthly.size());
		DomainMonthlyStat entityMonthly = listMonthly.get(0);
		assertEquals(12, (long) entityMonthly.getOperationCount());
		assertEquals(1100, (long) entityMonthly.getActualOperationSum());
		assertEquals(10, (long) entityMonthly.getAddOperationCount());
		assertEquals(1200, (long) entityMonthly.getAddOperationSum());
		assertEquals(2, (long) entityMonthly.getDeleteOperationCount());
		assertEquals(-100, (long) entityMonthly.getDeleteOperationSum());
		assertEquals(1100, (long) entityMonthly.getDiffOperationSum());
	}
}
