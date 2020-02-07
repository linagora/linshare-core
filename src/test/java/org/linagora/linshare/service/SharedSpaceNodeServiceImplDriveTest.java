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

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceNodeServiceImplDriveTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplDriveTest.class);

	@Autowired
	private InitMongoService init;

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService service;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private Account authUser;

	private LoadingServiceTestDatas datas;

	public SharedSpaceNodeServiceImplDriveTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		init.init();
		authUser = datas.getUser1();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
	}

	@Test
	public void createDrive() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My Drive", NodeType.DRIVE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "Drive not created");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createDriveAndFind() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My Drive", NodeType.DRIVE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "Drive not created");
		Assertions.assertNotNull(service.find(authUser, authUser, expectedNode.getUuid()));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createDriveAndWorkgroupAndFindAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int before = service.findAllByAccount(authUser, authUser).size();
		SharedSpaceNode drive = new SharedSpaceNode("My Drive", NodeType.DRIVE);
		SharedSpaceNode expectedDrive = service.create(authUser, authUser, drive);
		Assertions.assertNotNull(expectedDrive, "Drive not created");
		SharedSpaceNode workgroup = new SharedSpaceNode("My groups", NodeType.WORK_GROUP);
		SharedSpaceNode expectedWorkgroup = service.create(authUser, authUser, workgroup);
		Assertions.assertNotNull(workgroup, "Workgroup not created");
		List<SharedSpaceNodeNested> ssNested = service.findAllByAccount(authUser, authUser);
		Assertions.assertEquals(before + 2, ssNested.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createDriveAndDelete() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My Drive", NodeType.DRIVE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "Drive not created");
		int before = service.findAllByAccount(authUser, authUser).size();
		SharedSpaceNode deleted = service.delete(authUser, authUser, expectedNode);
		List<SharedSpaceNodeNested> ssNested = service.findAllByAccount(authUser, authUser);
		Assertions.assertEquals(ssNested.size(), before - 1);
		logger.info(LinShareTestConstants.END_TEST);
	}

}
