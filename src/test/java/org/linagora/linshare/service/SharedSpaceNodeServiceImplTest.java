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

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-default-domain-quotas.sql",
	"/import-tests-quota-other.sql" })
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
public class SharedSpaceNodeServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepo;

	private final static String SHARED_SPACE_NODE_1 = "WORKG_GROUP_NAME_1";

	private final static String SHARED_SPACE_NODE_2 = "WORKG_GROUP_NAME_2";

	private Account authUser, jane, bart, root;

	private SharedSpaceAccount accountJane, accountBart;

	private SharedSpaceRole adminRole, readerRole, contributorRole;

	private List<SharedSpaceNode> sharedSpaceNodes;

	private LoadingServiceTestDatas datas;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	@Autowired
	private InitMongoService init;

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService service;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService memberService;

	private LinShareWiser wiser;

	public SharedSpaceNodeServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepo);
		datas.loadUsers();
		init.init();
		root = datas.getRoot();
		authUser = datas.getUser1();
		jane = datas.getUser2();
		bart = datas.getUser3();
		sharedSpaceNodes = this.getNodes();
		adminRole = roleBusinessService.findByName("ADMIN");
		contributorRole = roleBusinessService.findByName("CONTRIBUTOR");
		readerRole = roleBusinessService.findByName("READER");
		accountJane = new SharedSpaceAccount((User) jane);
		accountBart = new SharedSpaceAccount((User) bart);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
	}

	@Test
	public void create() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assertions.assertNotNull(sharedSpaceNodes, "node not created");
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		Assertions.assertEquals(expectedNode.getUuid(), node.getUuid());
		Assertions.assertNotNull(node.getQuotaUuid());
		Assertions.assertEquals(expectedNode.getUuid(), node.getUuid(), "The created node is different");
		Assertions.assertNotNull(node.getQuotaUuid(), "No quota has been found");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void find() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode toFindNode = service.find(authUser, authUser, sharedSpaceNodes.get(0).getUuid());
		Assertions.assertNotNull(toFindNode, "Node has not been found.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void delete() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode(SharedSpaceNodeServiceImplTest.SHARED_SPACE_NODE_2, null,
				NodeType.WORK_GROUP);
		SharedSpaceNode toDelete = service.create(authUser, authUser, node);
		service.delete(authUser, authUser, toDelete);
		try {
			service.find(authUser, authUser, toDelete.getUuid());
			Assertions.fail("An exception should be thrown because the node is found.");
		} catch (BusinessException e) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e.getErrorCode(),
					"The node has been found in the data base. but it has not been deleted");
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode createdNode = getNodes().get(0);
		SharedSpaceMember janeMemberShip = memberService.createWithoutCheckPermission(authUser, authUser, createdNode,
				readerRole, accountJane);
		SharedSpaceMember bartMemberShip = memberService.createWithoutCheckPermission(authUser, authUser, createdNode,
				contributorRole, accountBart);
		SharedSpaceNode toUpdate = new SharedSpaceNode();
		toUpdate.setVersioningParameters(new VersioningParameters(true));
		toUpdate.setName(SHARED_SPACE_NODE_2);
		toUpdate.setUuid(createdNode.getUuid());
		SharedSpaceNode updatedNode = service.update(authUser, authUser, toUpdate);
		Assertions.assertEquals(SHARED_SPACE_NODE_2, updatedNode.getName(), "The shared space node is not updated.");
		Assertions.assertEquals(true, updatedNode.getVersioningParameters().getEnable());
		janeMemberShip = memberService.find(authUser, authUser, janeMemberShip.getUuid());
		bartMemberShip = memberService.find(authUser, authUser, bartMemberShip.getUuid());
		Assertions.assertEquals(janeMemberShip.getNode().getName(), SHARED_SPACE_NODE_2);
		Assertions.assertEquals(bartMemberShip.getNode().getName(), SHARED_SPACE_NODE_2);
		logger.info(LinShareTestConstants.END_TEST);
	}

	/*
	 * Helpers
	 */
	private List<SharedSpaceNode> getNodes() {
		SharedSpaceNode node1 = new SharedSpaceNode(SharedSpaceNodeServiceImplTest.SHARED_SPACE_NODE_1, null,
				NodeType.WORK_GROUP, new VersioningParameters(false));
		SharedSpaceNode node2 = new SharedSpaceNode(SharedSpaceNodeServiceImplTest.SHARED_SPACE_NODE_2, null,
				NodeType.WORK_GROUP, new VersioningParameters(false));
		service.create(authUser, authUser, node1);
		service.create(authUser, authUser, node2);
		List<SharedSpaceNode> nodes = new ArrayList<SharedSpaceNode>();
		nodes.add(node1);
		nodes.add(node2);
		return nodes;
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<SharedSpaceNode> foundNodes = service.findAll(root, root);
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		service.create(authUser, authUser, node);
		Assertions.assertEquals(foundNodes.size() + 1, service.findAll(root, root).size(),
				"The number of founded node is different with expeted nodes");
		logger.info(LinShareTestConstants.END_TEST);
	}

}
