/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.business.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.domain.entities.UserWeeklyStat;
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
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@Sql({
	
	"/import-tests-stat.sql",
	"/import-tests-operationHistory.sql"})
@Transactional
public class UserStatBusinessServiceTest {

	private static Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImplTest.class);

	@Autowired
	private UserDailyStatBusinessService userDailyStatBusinessService;

	@Autowired
	private UserWeeklyStatBusinessService userWeeklyStatBusinessService;

	@Autowired
	private UserMonthlyStatBusinessService userMonthStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void test() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		Date start = new GregorianCalendar(2042, 07, 10, 00, 00).getTime();
		Date end = new GregorianCalendar(2042, 12, 19, 00, 00).getTime();
		List<UserDailyStat> listUserDaily = userDailyStatBusinessService.findBetweenTwoDates(jane, start, end);
		Assertions.assertEquals(5, listUserDaily.size());
		List<UserDailyStat> listUserDailyOctober = userDailyStatBusinessService.findBetweenTwoDates(jane,
				new GregorianCalendar(2042, 9, 01, 00, 00).getTime(),
				new GregorianCalendar(2042, 9, 30, 00, 00).getTime());
		Assertions.assertEquals(1, listUserDailyOctober.size());
		UserDailyStat entity = userDailyStatBusinessService
				.findBetweenTwoDates(jane, new GregorianCalendar(2042, 10, 16, 00, 00).getTime(), null).get(0);
		Assertions.assertEquals(jane, entity.getAccount());
		Assertions.assertEquals(5, (long) entity.getOperationCount());
		Assertions.assertEquals(50, (long) entity.getActualOperationSum());
		Assertions.assertEquals(3, (long) entity.getCreateOperationCount());
		Assertions.assertEquals(100, (long) entity.getCreateOperationSum());
		Assertions.assertEquals(2, (long) entity.getDeleteOperationCount());
		Assertions.assertEquals(-50, (long) entity.getDeleteOperationSum());
		Assertions.assertEquals(50, (long) entity.getDiffOperationSum());
		// WEEKLY STATISTIC
		List<UserWeeklyStat> listAllUserWeeklyStat = userWeeklyStatBusinessService.findBetweenTwoDates(jane, null,
				null);
		Assertions.assertEquals(3, listAllUserWeeklyStat.size());
		userWeeklyStatBusinessService.create(jane, new GregorianCalendar(2042, 10, 10, 00, 00).getTime(), new GregorianCalendar(2042, 10, 18, 00, 00).getTime());
		listAllUserWeeklyStat = userWeeklyStatBusinessService.findBetweenTwoDates(jane, null, null);
		Assertions.assertEquals(4, listAllUserWeeklyStat.size());
		List<UserWeeklyStat> listUserWeekly = userWeeklyStatBusinessService.findBetweenTwoDates(jane, new GregorianCalendar(2042, 10, 12, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 19, 00, 00).getTime());
		Assertions.assertEquals(1, listUserWeekly.size());
		UserWeeklyStat entityWeekly = listUserWeekly.get(0);
		Assertions.assertEquals(jane, entityWeekly.getAccount());
		Assertions.assertEquals(21, (long) entityWeekly.getOperationCount());
		Assertions.assertEquals(700, (long) entityWeekly.getActualOperationSum());
		Assertions.assertEquals(11, (long) entityWeekly.getCreateOperationCount());
		Assertions.assertEquals(1800, (long) entityWeekly.getCreateOperationSum());
		Assertions.assertEquals(10, (long) entityWeekly.getDeleteOperationCount());
		Assertions.assertEquals(-1100, (long) entityWeekly.getDeleteOperationSum());
		Assertions.assertEquals(700, (long) entityWeekly.getDiffOperationSum());
//		MONTHLY STATISTIC
		userMonthStatBusinessService.create(jane, new GregorianCalendar(2042, 10, 01, 00, 00).getTime(), new GregorianCalendar(2042, 10, 30, 00, 00).getTime());
		List<UserMonthlyStat> listUserMonthly = userMonthStatBusinessService.findBetweenTwoDates(jane, new GregorianCalendar(2042, 10, 29, 00, 00).getTime(),
				new GregorianCalendar(2042, 11, 10, 00, 00).getTime());
		Assertions.assertEquals(1, listUserMonthly.size());
		UserMonthlyStat entityMonthly = listUserMonthly.get(0);
		Assertions.assertEquals(jane, entityMonthly.getAccount());
		Assertions.assertEquals(30, (long) entityMonthly.getOperationCount());
		Assertions.assertEquals(1200, (long) entityMonthly.getActualOperationSum());
		Assertions.assertEquals(16, (long) entityMonthly.getCreateOperationCount());
		Assertions.assertEquals(3300, (long) entityMonthly.getCreateOperationSum());
		Assertions.assertEquals(14, (long) entityMonthly.getDeleteOperationCount());
		Assertions.assertEquals(-2100, (long) entityMonthly.getDeleteOperationSum());
		Assertions.assertEquals(1200, (long) entityMonthly.getDiffOperationSum());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
