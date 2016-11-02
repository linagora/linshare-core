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

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
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
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class DomainStatBusinessServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	@Autowired
	private DomainMonthlyStatBusinessService domainMonthlyStatBusinessService;

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	User jane;

	@Before
	public void setUp(){
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void test(){
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = jane.getDomain();

		List<DomainDailyStat> listDaily = domainDailyStatBusinessService.findBetweenTwoDates(domain, null, null);
		assertEquals(4, listDaily.size());
		domainWeeklyStatBusinessService.create(domain, new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 15, 00, 00).getTime());
		List<DomainWeeklyStat> listWeekly = domainWeeklyStatBusinessService.findBetweenTwoDates(domain, new GregorianCalendar(2042, 10, 8, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 16, 00, 00).getTime());
		assertEquals(2, listWeekly.size());
		DomainWeeklyStat entityWeekly = listWeekly.get(1);
		assertEquals(9, (long) entityWeekly.getOperationCount());
		assertEquals(200, (long) entityWeekly.getActualOperationSum());
		assertEquals(6, (long) entityWeekly.getCreateOperationCount());
		assertEquals(300, (long) entityWeekly.getCreateOperationSum());
		assertEquals(3, (long) entityWeekly.getDeleteOperationCount());
		assertEquals(-100, (long) entityWeekly.getDeleteOperationSum());
		assertEquals(200, (long) entityWeekly.getDiffOperationSum());
		listWeekly = domainWeeklyStatBusinessService.findBetweenTwoDates(domain, null, null);
		assertEquals(6, listWeekly.size());

		domainMonthlyStatBusinessService.create(domain, new GregorianCalendar(2042, 10, 1, 00, 00).getTime(), new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		List<DomainMonthlyStat> listMonthly = domainMonthlyStatBusinessService.findBetweenTwoDates(domain, new GregorianCalendar(2042, 10, 29, 00, 00).getTime(), new GregorianCalendar(2042, 11, 1, 00, 00).getTime());
		assertEquals(1, listMonthly.size());
		DomainMonthlyStat entityMonthly = listMonthly.get(0);
		assertEquals(14, (long) entityMonthly.getOperationCount());
		assertEquals(250, (long) entityMonthly.getActualOperationSum());
		assertEquals(9, (long) entityMonthly.getCreateOperationCount());
		assertEquals(400, (long) entityMonthly.getCreateOperationSum());
		assertEquals(5, (long) entityMonthly.getDeleteOperationCount());
		assertEquals(-150, (long) entityMonthly.getDeleteOperationSum());
		assertEquals(250, (long) entityMonthly.getDiffOperationSum());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}