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

import javax.transaction.Transactional;

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
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", 
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
public class SharedSpaceRoleServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplTest.class);

	@Autowired
	@Qualifier("userService")
	private UserService userServ;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepo;

	private Account authUser;

	private LoadingServiceTestDatas datas;

	@Autowired
	private SharedSpaceRoleService service;

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepo);
		datas.loadUsers();
		authUser = datas.getRoot();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void findByNameDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceRole toFindRole = service.findByName(authUser, authUser, "ADMIN");
		Assertions.assertNotNull(toFindRole, "Role has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findByUuidDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceRole toFindRole = service.find(authUser, authUser, "234be74d-2966-41c1-9dee-e47c8c63c14e");
		Assertions.assertNotNull(toFindRole, "Role has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllExistingDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<SharedSpaceRole> toFindRoles = service.findAll(authUser, authUser);
		Assertions.assertNotNull(toFindRoles, "Roles has not been not found");
		logger.info(LinShareTestConstants.END_TEST);
	}
}
