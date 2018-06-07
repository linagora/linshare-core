/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.service;

import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", 
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceRoleServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
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
	private InitMongoService initMongoService;

	@Autowired
	private SharedSpaceRoleService service;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		datas = new LoadingServiceTestDatas(userRepo);
		datas.loadUsers();
		authUser = datas.getRoot();
		initMongoService.init();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void findByNameDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceRole toFindRole = service.findByName(authUser, authUser, "ADMIN");
		Assert.assertNotNull("Role has not been found.", toFindRole);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findByUuidDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceRole toFindRole = service.find(authUser, authUser, "77a699fe-faca-46a5-97c0-1b46a0f5cd14");
		Assert.assertNotNull("Role has not been found.", toFindRole);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllExistingDefaultRoles() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<SharedSpaceRole> toFindRoles = service.findAll(authUser, authUser);
		Assert.assertNotNull("Roles has not been not found", toFindRoles);
		logger.info(LinShareTestConstants.END_TEST);
	}
}
