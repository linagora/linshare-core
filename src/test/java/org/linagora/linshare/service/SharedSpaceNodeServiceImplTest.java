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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
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
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;
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

	@Autowired
	@Qualifier("sharedSpaceMemberBusinessService")
	private SharedSpaceMemberBusinessService memberBusinessService;

	@Autowired
	private SharedSpaceMemberMongoRepository memberRepository;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	private Account john;

	private Account foo;

	private Account jane;

	private Account amy;

	private Account root;

	private SharedSpaceNode node1;

	private SharedSpaceNode node2;

	private SharedSpaceNode node3;

	private SharedSpaceNode node4;

	private SharedSpaceNode node5;

	private SharedSpaceNode node6;

	private SharedSpaceNode node7;

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
		jane= userRepo.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		amy = userRepo.findByMail(LinShareTestConstants.AMY_WOLSH_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
	}

	@Test
	public void create() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My first node", NodeType.WORK_GROUP);
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
		SharedSpaceNode node = new SharedSpaceNode("My first node", NodeType.WORK_GROUP);
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
	public void deleteWorkgroupByNotMember() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test member delete a workgroup where he is not a membership
		SharedSpaceNode johnWorkGroup = service.create(john, john, new SharedSpaceNode("john workgroup", NodeType.WORK_GROUP));
		SharedSpaceNode janeWorkGroup = service.create(jane, jane, new SharedSpaceNode("jane workgroup", NodeType.WORK_GROUP));
		BusinessException e = assertThrows(BusinessException.class, () -> {
			service.delete(jane, jane, johnWorkGroup);
		});
		Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_FORBIDDEN, e.getErrorCode());
		service.delete(john, john, johnWorkGroup);
		service.delete(jane, jane, janeWorkGroup);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		VersioningParameters param = new VersioningParameters(false);
		SharedSpaceNode nodeToUpdate = new SharedSpaceNode(john.getDomainId(), "nodeName ToUpdate", null, NodeType.WORK_GROUP, param, "optional description", new SharedSpaceAccount(john));
		SharedSpaceNode createdNodeToUpdate = service.create(john, john, nodeToUpdate);
		SharedSpaceNode updatedNode = new SharedSpaceNode(john.getDomainId(), "nodeName Updated", null, NodeType.WORK_GROUP, param, "optional description", new SharedSpaceAccount(john));
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
		SharedSpaceNode node = new SharedSpaceNode("My first node", NodeType.WORK_GROUP);
		node = service.create(john, john, node);
		Assertions.assertEquals(foundNodes.size() + 1, service.findAll(root, root).size());
		service.delete(john, john, node);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllWithPagination() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 2);
		PageContainer<SharedSpaceNodeNested> foundNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Long foundNodesSize = foundNodes.getPageResponse().getTotalElements();
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.DRIVE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.DRIVE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.DRIVE));
		String domain = null;
		PageContainer<SharedSpaceNodeNested> allNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(foundNodesSize + 6, allNodes.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooNodes = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(2, fooNodes.getPageResponse().getTotalElements());
//		Assertions.assertAll("One or both retrieved nodes do not correspond to foo's persisted nodes", () -> {
//			Assertions.assertEquals(node3.getUuid(), fooNodes.getPageResponse().getContent().get(1).getUuid());
//			Assertions.assertEquals(node4.getUuid(), fooNodes.getPageResponse().getContent().get(0).getUuid());
//		});
		//FindAll nodes by foo's account
		List<SharedSpaceNodeNested> nodes = service.findAllByAccount(root, foo);
		Assertions.assertEquals(nodes.size(), fooNodes.getPageResponse().getTotalElements());
		fooNodes.getPageResponse().getContent().forEach(node -> Assertions.assertTrue(nodes.contains(node)));
		// Find Foo's Drives (Filter by nodeType)
		PageContainer<SharedSpaceNodeNested> fooDrives = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.DRIVE), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(1, fooDrives.getPageResponse().getTotalElements());
		fooDrives.getPageResponse().getContent().forEach(node -> Assertions.assertTrue(node.isDrive()));
		// Find Foo's WorkGroups (Filter by nodeType)
		PageContainer<SharedSpaceNodeNested> fooWGs = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.WORK_GROUP), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(1, fooWGs.getPageResponse().getTotalElements());
		fooWGs.getPageResponse().getContent().forEach(node -> Assertions.assertTrue(node.isWorkGroup()));
		// Filter by Role
		node7 = service.create(foo, foo, new SharedSpaceNode("My drive", null, NodeType.DRIVE));
		SharedSpaceMember member = memberBusinessService.findByAccountAndNode(foo.getLsUuid(), node7.getUuid());
		Assertions.assertTrue(member.isDriveAdmin());
		member.setRole(new LightSharedSpaceRole(roleBusinessService.findByName(Role.DRIVE_READER.toString())));
		memberRepository.save(member);
		Assertions.assertTrue(member.isDriveReader());
		fooDrives = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.DRIVE), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(2, fooDrives.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooDriveReaderRole = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.DRIVE), Sets.newHashSet(Role.DRIVE_READER.toString()), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(1, fooDriveReaderRole.getPageResponse().getTotalElements());
		// Filter by Name
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByName = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, "SECOND NODE", container);
		Assertions.assertEquals(2, fooSharedSpaceByName.getPageResponse().getTotalElements());
		service.delete(root, root, node1);
		service.delete(root, root, node2);
		service.delete(foo, foo, node3);
		service.delete(foo, foo, node4);
		service.delete(jane, jane, node5);
		service.delete(jane, jane, node6);
		service.delete(root, root, node7);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllDomainAdministrationForPagingSharedSpaces() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 10);
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.DRIVE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.DRIVE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.DRIVE));
		node7 = service.create(amy, amy, new SharedSpaceNode("foo2 first node", null, NodeType.DRIVE));
		// Filter SharedSpaces by domain by an ADMIN -> It will return just sharedSpaces of his domain
		john.setRole(org.linagora.linshare.core.domain.constants.Role.ADMIN);
		userRepo.update((User) john);
		Assertions.assertTrue(john.hasAdminRole());
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByAdmin = service.findAll(john, john, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByAdmin.getPageResponse().getContent()) {
			Assertions.assertEquals(john.getDomainId(), sharedSpaceNodeNested.getDomainUuid());
		}
		// Filter SharedSpaces by domain by an ADMIN with empty domains list -> it will return the domain's sharedSpaces where he s admin
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByAdminEmptyDomainList = service.findAll(john, john, null, Lists.newArrayList(), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByAdminEmptyDomainList.getPageResponse().getContent()) {
			Assertions.assertEquals(john.getDomainId(), sharedSpaceNodeNested.getDomainUuid());
		}
		// Filter SharedSpaces by domain by a ROOT -> it will return both foo2 and john's domains' sharedSpaces
		List<String> domainUuids = Lists.newArrayList(john.getDomainId(), amy.getDomainId());
		Set<String> returnedDoaminsUuids = Sets.newHashSet();
		Account account = null;
		String name = null;
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByRoot = service.findAll(root, root, account, Lists.newArrayList(), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, name, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByRoot.getPageResponse().getContent()) {
			returnedDoaminsUuids.add(sharedSpaceNodeNested.getDomainUuid());
			logger.debug("This is the domain uuid added to the set to return: {}", sharedSpaceNodeNested.getDomainUuid());
		}
		logger.debug("This is the list of the existent domains uuids: {}", domainUuids);
		logger.debug("This is the list of the final returned domains uuids: {}", returnedDoaminsUuids);
//		Assertions.assertTrue(returnedDoaminsUuids.containsAll(domainUuids));
		service.delete(root, root, node1);
		service.delete(root, root, node2);
		service.delete(foo, foo, node3);
		service.delete(foo, foo, node4);
		service.delete(jane, jane, node5);
		service.delete(jane, jane, node6);
		service.delete(amy, amy, node7);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFailFindAllSharedSpacesWithPagination() throws BusinessException {
		// Test failure of findAll SharedSpaces with wrong pageNumber and wrong  pageSize
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 2);
		PageContainer<SharedSpaceNodeNested> foundNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Long foundNodesSize = foundNodes.getPageResponse().getTotalElements();
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.DRIVE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.DRIVE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.DRIVE));
		PageContainer<SharedSpaceNodeNested> allNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(foundNodesSize + 6, allNodes.getPageResponse().getTotalElements());
		Long allNodesSize = allNodes.getPageResponse().getTotalElements();
		// PageContainer with wrongPageNumber
		PageContainer<SharedSpaceNodeNested> wrongPageNumberContainer = new PageContainer<SharedSpaceNodeNested>(5, 3);
		BusinessException e = assertThrows(BusinessException.class, () -> {
			service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, wrongPageNumberContainer);
		});
		Assertions.assertEquals(BusinessErrorCode.WRONG_PAGE_PARAMETERS, e.getErrorCode());
		// Search with wrongPageNumber for an empty list of elements
		service.delete(foo, foo, node3);
		service.delete(foo, foo, node4);
		PageContainer<SharedSpaceNodeNested> fooNodes = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, container);
		Assertions.assertEquals(0, fooNodes.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooNodesWrongPageNumberContainer = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, wrongPageNumberContainer);
		Assertions.assertEquals(fooNodesWrongPageNumberContainer.getPageResponse().getTotalElements(), fooNodes.getPageResponse().getTotalElements());
		// PageContainer with wrongPageSize we will return all found elements
		PageContainer<SharedSpaceNodeNested> wrongPageSizeContainer = new PageContainer<SharedSpaceNodeNested>(0, 30);
		PageContainer<SharedSpaceNodeNested> allNodesWrongPageSizeContainer = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, wrongPageSizeContainer);
		Assertions.assertEquals(allNodesWrongPageSizeContainer.getPageResponse().getTotalElements(),
				allNodesSize -2);
		service.delete(john, john, node1);
		service.delete(john, john, node2);
		service.delete(jane, jane, node5);
		service.delete(jane, jane, node6);
		logger.info(LinShareTestConstants.END_TEST);
	}
}
