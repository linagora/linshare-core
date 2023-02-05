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
package org.linagora.linshare.business.service;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
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
	
	"/import-tests-stat.sql"})
@Transactional
public class OperationHistoryBusinessServiceTest {

	private static Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImplTest.class);

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void testCreateOperationHistory() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		OperationHistory entity = new OperationHistory(account, domain, (long) 50, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(entity);
		List<OperationHistory> result = operationHistoryBusinessService.find(account, null, null, null);
		Assertions.assertEquals(1, result.size());
		result = operationHistoryBusinessService.find(account, null, null, new Date());
		Assertions.assertEquals(1, result.size());
		entity = result.get(0);
		Assertions.assertEquals(jane, entity.getAccount());
		Assertions.assertEquals(domain, entity.getDomain());
		Assertions.assertEquals(50, (long) entity.getOperationValue());
		Assertions.assertEquals(OperationHistoryTypeEnum.CREATE, entity.getOperationType());
		Assertions.assertEquals(ContainerQuotaType.USER, entity.getContainerQuotaType());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteOperationHistory() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		Account account = jane;
		AbstractDomain domain = jane.getDomain();
		OperationHistory entity = new OperationHistory(account, domain, (long) 50, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(entity);
		List<OperationHistory> result = operationHistoryBusinessService.find(account, null, null, null);
		Assertions.assertEquals(1, result.size());
		operationHistoryBusinessService.deleteBeforeDateByAccount(new Date(), account);
		result = operationHistoryBusinessService.find(account, null, null, null);
		Assertions.assertEquals(0, result.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
