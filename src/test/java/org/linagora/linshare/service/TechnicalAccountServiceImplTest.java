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

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class TechnicalAccountServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(TechnicalAccountServiceImplTest.class);

	@Autowired
	private TechnicalAccountService technicalAccountService;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	private Account root;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByMail(LinShareTestConstants.ROOT_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateTechnicalAccount() {
		TechnicalAccount technicalAccount = new TechnicalAccount();
		technicalAccount.setFirstName("technical");
		technicalAccount.setLastName("technical");
		technicalAccount.setMail("technical-account@linshare.org");
		technicalAccount.setPassword("Secret123!!!");
		technicalAccount.setEnable(true);
		technicalAccount.setRole(Role.DELEGATION);
		technicalAccount = technicalAccountService.create(root, technicalAccount);
		Assertions.assertNotNull(technicalAccount);
		Assertions.assertEquals(technicalAccount.getMail(), "technical-account@linshare.org");
	}

	@Test
	public void testCreateWithOveriddenMail() {
		TechnicalAccount technicalAccount = new TechnicalAccount();
		technicalAccount.setFirstName("technical");
		technicalAccount.setLastName("technical");
		technicalAccount.setPassword("Secret123!!!");
		technicalAccount.setEnable(true);
		technicalAccount.setRole(Role.DELEGATION);
		technicalAccount = technicalAccountService.create(root, technicalAccount);
		Assertions.assertNotNull(technicalAccount);
		Assertions.assertNotNull(technicalAccount.getMail(), "Mail should not be null");
		Assertions.assertTrue(UUID.fromString(technicalAccount.getMail()) instanceof UUID);
	}
}
