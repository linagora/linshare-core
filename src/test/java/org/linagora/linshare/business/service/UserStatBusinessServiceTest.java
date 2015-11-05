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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath:springContext-datasource.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml", "classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml", "classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml", "classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class UserStatBusinessServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private UserDailyStatBusinessService userDailyStatBusinessService;

	@Autowired
	private UserWeeklyStatBusinessService userWeeklyStatBusinessService;

	@Autowired
	private UserMonthlyStatBusinessService userMonthStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

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
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -1);
		Date a = calendar.getTime();
		calendar.add(GregorianCalendar.DATE, +2);
		Date b = calendar.getTime();
		userDailyStatBusinessService.create(jane, new Date());
		List<UserDailyStat> listUserDaily = userDailyStatBusinessService.findBetweenTwoDates(jane, a, b);
		assertEquals(1, listUserDaily.size());
		UserDailyStat entity = listUserDaily.get(0);
		assertEquals(jane, entity.getAccount());
		assertEquals(7, (long) entity.getOperationCount());
		assertEquals(700, (long) entity.getActualOperationSum());
		assertEquals(5, (long) entity.getAddOperationCount());
		assertEquals(1200, (long) entity.getAddOperationSum());
		assertEquals(2, (long) entity.getDeleteOperationCount());
		assertEquals(-500, (long) entity.getDeleteOperationSum());
		assertEquals(700, (long) entity.getDiffOperationSum());

		GregorianCalendar calendar2 = new GregorianCalendar();
		calendar2.add(GregorianCalendar.DATE, -7);
		Date lastWeekbegin = calendar2.getTime();
		calendar2.add(GregorianCalendar.DATE, +14);
		Date lastWeekend = calendar2.getTime();
		userWeeklyStatBusinessService.create(jane, lastWeekbegin, lastWeekend);

		List<UserWeeklyStat> listUserWeekly = userWeeklyStatBusinessService.findBetweenTwoDates(jane, a, b);
		assertEquals(1, listUserWeekly.size());
		UserWeeklyStat entityWeekly = listUserWeekly.get(0);
		assertEquals(jane, entityWeekly.getAccount());
		assertEquals(45, (long) entityWeekly.getOperationCount());
		assertEquals(1950, (long) entityWeekly.getActualOperationSum());
		assertEquals(24, (long) entityWeekly.getAddOperationCount());
		assertEquals(4600, (long) entityWeekly.getAddOperationSum());
		assertEquals(21, (long) entityWeekly.getDeleteOperationCount());
		assertEquals(-2650, (long) entityWeekly.getDeleteOperationSum());
		assertEquals(1950, (long) entityWeekly.getDiffOperationSum());

		GregorianCalendar calendar3 = new GregorianCalendar();
		calendar3.add(GregorianCalendar.DATE, -30);
		Date lastMonthbegin = calendar3.getTime();
		calendar3.add(GregorianCalendar.DATE, +60);
		Date lastMonthend = calendar3.getTime();
		userMonthStatBusinessService.create(jane, lastMonthbegin, lastMonthend);

		List<UserMonthlyStat> listUserMonthly = userMonthStatBusinessService.findBetweenTwoDates(jane, a, b);
		assertEquals(1, listUserMonthly.size());
		UserMonthlyStat entityMonthly = listUserMonthly.get(0);
		assertEquals(jane, entityMonthly.getAccount());
		assertEquals(85, (long) entityMonthly.getOperationCount());
		assertEquals(3650, (long) entityMonthly.getActualOperationSum());
		assertEquals(46, (long) entityMonthly.getAddOperationCount());
		assertEquals(10300, (long) entityMonthly.getAddOperationSum());
		assertEquals(39, (long) entityMonthly.getDeleteOperationCount());
		assertEquals(-6650, (long) entityMonthly.getDeleteOperationSum());
		assertEquals(3650, (long) entityMonthly.getDiffOperationSum());
	}
}
