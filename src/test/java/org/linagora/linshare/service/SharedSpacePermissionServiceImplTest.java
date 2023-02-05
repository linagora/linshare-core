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

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SharedSpacePermissionService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml", 
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpacePermissionServiceImplTest{
	private static Logger logger = LoggerFactory.getLogger(SharedSpacePermissionServiceImplTest.class);

	@Autowired
	@Qualifier("userService")
	private UserService userServ;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private SharedSpacePermissionService service;

	@Mock
	private Account authUser;

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		Mockito.when(authUser.isUser()).thenReturn(true);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testFind() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		addStubingUuid();
		SharedSpacePermission toFindPermission = service.findByUuid(authUser, authUser,
				"31cb4d80-c939-40f1-a79e-4d77392e0e0b");
		Assertions.assertNotNull(toFindPermission,"Permission has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindByRole() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		addStubingUuid();
		List<SharedSpacePermission> toFindPermission = service.findByRole(authUser, authUser, "ADMIN");
		Assertions.assertNotNull(toFindPermission,"Permission has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<SharedSpacePermission> toFindPermission = service.findAll(authUser, authUser);
		Assertions.assertNotNull(toFindPermission,"Permission has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	private void addStubingUuid() {
		Mockito.when(authUser.getLsUuid()).thenReturn("aebe1b64-39c0-11e5-9fa8-080027b8274b");
	}
}
