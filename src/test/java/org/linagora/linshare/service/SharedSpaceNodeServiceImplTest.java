/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Sets;


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
public class SharedSpaceNodeServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepo;

	private Account john;

	private Account foo;

	private Account root;

	private SharedSpaceNode node1;

	private SharedSpaceNode node2;

	private SharedSpaceNode node3;

	private SharedSpaceNode node4;

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService service;

	public SharedSpaceNodeServiceImplTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepo.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		john = userRepo.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		foo = userRepo.findByMail(LinShareTestConstants.FOO_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
	}

	@Test
	public void create() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		Assertions.assertNotNull(expectedNode.getQuotaUuid());
		service.delete(john, john, expectedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createSharedSpaceSpecialCharInName() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("EP_TEST_v233<script>alert(document.cookie)</script>", NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		Assertions.assertEquals(expectedNode.getName(), "EP_TEST_v233");
		service.delete(john, john, expectedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateSharedSpaceSpecialCharInName() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode nodeToUpdate = new SharedSpaceNode("nodeName ToUpdate", NodeType.WORK_GROUP);
		Assertions.assertEquals(nodeToUpdate.getName(), "nodeName ToUpdate");
		nodeToUpdate = service.create(john, john, nodeToUpdate);
		nodeToUpdate.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		service.update(john, john, nodeToUpdate);
		Assertions.assertEquals(nodeToUpdate.getName(), "EP_TEST_v233");
		service.delete(john, john, nodeToUpdate);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updatePartialSharedSpaceSpecialCharInName() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode nodeToUpdate = new SharedSpaceNode("nodeName ToUpdate", NodeType.WORK_GROUP);
		Assertions.assertEquals(nodeToUpdate.getName(), "nodeName ToUpdate");
		nodeToUpdate = service.create(john, john, nodeToUpdate);
		PatchDto patchDto = new PatchDto(nodeToUpdate.getUuid(), "name",
				"EP_TEST_v233<script>alert(document.cookie)</script>");
		SharedSpaceNode updated = service.updatePartial(john, john, patchDto);
		Assertions.assertEquals(updated.getName(), "EP_TEST_v233");
		service.delete(john, john, updated);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void find() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = service.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		SharedSpaceNode toFindNode = service.find(john, john, expectedNode.getUuid());
		Assertions.assertNotNull(toFindNode, "Node has not been found.");
		service.delete(john, john, toFindNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void delete() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("WORKG_GROUP_NAME", NodeType.WORK_GROUP);
		SharedSpaceNode toDelete = service.create(john, john, node);
		service.delete(john, john, toDelete);
		try {
			service.find(john, john, toDelete.getUuid());
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
		VersioningParameters param = new VersioningParameters(false);
		SharedSpaceNode nodeToUpdate = new SharedSpaceNode("nodeName ToUpdate", null, NodeType.WORK_GROUP, param, "optional description", new SharedSpaceAccount(john));
		SharedSpaceNode createdNodeToUpdate = service.create(john, john, nodeToUpdate);
		SharedSpaceNode updatedNode = new SharedSpaceNode("nodeName Updated", null, NodeType.WORK_GROUP, param, "optional description", new SharedSpaceAccount(john));
		updatedNode.setUuid(createdNodeToUpdate.getUuid());
		service.update(john, john, updatedNode);
		Assertions.assertEquals(updatedNode.getName(),
				service.update(john, john, updatedNode).getName(), "The shared space node is not updated.");
		service.delete(john, john, updatedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<SharedSpaceNode> foundNodes = service.findAll(root, root);
		SharedSpaceNode node = new SharedSpaceNode("My first node", "My parent nodeUuid", NodeType.WORK_GROUP);
		node = service.create(john, john, node);
		Assertions.assertEquals(foundNodes.size() + 1, service.findAll(root, root).size());
		service.delete(john, john, node);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllWithPagination() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 2);
		List<SharedSpaceNode> foundNodes = service.findAll(root, root);
		node1 = service.create(john, john, new SharedSpaceNode("John first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John second node", null, NodeType.DRIVE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo second node", null, NodeType.DRIVE));
		PageContainer<SharedSpaceNodeNested> allNodes = service.findAll(root, root, null, SortOrder.DESC, Sets.newHashSet(), SharedSpaceField.creationDate, container);
		Assertions.assertEquals(foundNodes.size() + 4, allNodes.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooNodes = service.findAll(root, root, foo, SortOrder.DESC, Sets.newHashSet(), SharedSpaceField.creationDate, container);
		Assertions.assertEquals(2, fooNodes.getPageResponse().getTotalElements());
		Assertions.assertAll("One or both retrieved nodes do not correspond to foo's persisted nodes", () -> {
			Assertions.assertEquals(node3.getUuid(), fooNodes.getPageResponse().getContent().get(1).getUuid());
			Assertions.assertEquals(node4.getUuid(), fooNodes.getPageResponse().getContent().get(0).getUuid());
		});
		service.delete(john, john, node1);
		service.delete(john, john, node2);
		service.delete(foo, foo, node3);
		service.delete(foo, foo, node4);
		logger.info(LinShareTestConstants.END_TEST);
	}

}
