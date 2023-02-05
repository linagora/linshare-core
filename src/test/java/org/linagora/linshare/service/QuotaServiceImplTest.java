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
package org.linagora.linshare.service;

import org.junit.jupiter.api.Assertions;

import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.QuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Sql({
	
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql" })
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml" })
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class QuotaServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(GroupPatternServiceImplTest.class);

	@Autowired
	QuotaService quotaService;

	@Autowired
	OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

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
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		Long accountConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(account, null, new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, null);
		Long domainConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, domain, new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, null);
		Long ensembleUserConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, domain, new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, ContainerQuotaType.USER);
		Long platformConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, null, new GregorianCalendar(2042, 9, 16, 00, 00).getTime(), null, null);
		Assertions.assertEquals(1100, (long) accountConsumptionOfDay);
		Assertions.assertEquals(2101, (long) domainConsumptionOfDay);
		Assertions.assertEquals(2101, (long) ensembleUserConsumptionOfDay);
		Assertions.assertEquals(3851, (long) platformConsumptionOfDay);
		logger.info(LinShareTestConstants.END_TEST);
	}
}
