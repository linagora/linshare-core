/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceAuthor;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
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

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService memberService;

	@Autowired
	private RootUserRepository rootUserRepository;

	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository;

	private Account john;

	private Account foo;

	private Account jane;

	private Account amy;

	private Account root;

	private SharedSpaceRole adminRole;

	private SharedSpaceRole readerRole;

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
		adminRole = roleBusinessService.findByName("ADMIN");
		readerRole = roleBusinessService.findByName("READER");
		logger.debug(LinShareTestConstants.END_SETUP);
		activateWorkGroupCreation();
	}

	private void activateWorkGroupCreation() {
		Account actor = rootUserRepository.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		Functionality functionality = functionalityService.find(actor, LinShareTestConstants.ROOT_DOMAIN, "WORK_GROUP__CREATION_RIGHT");
		Policy activationPolicy = functionality.getActivationPolicy();
		activationPolicy.setStatus(true);
		functionality.setActivationPolicy(activationPolicy);
		functionalityService.update(actor, LinShareTestConstants.ROOT_DOMAIN, functionality);
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
		PageContainer<SharedSpaceNodeNested> foundNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Long foundNodesSize = foundNodes.getPageResponse().getTotalElements();
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.WORK_SPACE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.WORK_SPACE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.WORK_SPACE));
		String domain = null;
		PageContainer<SharedSpaceNodeNested> allNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(foundNodesSize + 6, allNodes.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooNodes = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
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
		PageContainer<SharedSpaceNodeNested> fooDrives = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.WORK_SPACE), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(1, fooDrives.getPageResponse().getTotalElements());
		fooDrives.getPageResponse().getContent().forEach(node -> Assertions.assertTrue(node.isWorkSpace()));
		// Find Foo's WorkGroups (Filter by nodeType)
		PageContainer<SharedSpaceNodeNested> fooWGs = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.WORK_GROUP), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(1, fooWGs.getPageResponse().getTotalElements());
		fooWGs.getPageResponse().getContent().forEach(node -> Assertions.assertTrue(node.isWorkGroup()));
		// Filter by Role
		node7 = service.create(foo, foo, new SharedSpaceNode("My drive", null, NodeType.WORK_SPACE));
		SharedSpaceMember member = memberBusinessService.findByAccountAndNode(foo.getLsUuid(), node7.getUuid());
		Assertions.assertTrue(member.isWorkSpaceAdmin());
		member.setRole(new LightSharedSpaceRole(roleBusinessService.findByName(Role.WORK_SPACE_READER.toString())));
		memberRepository.save(member);
		Assertions.assertTrue(member.isWorkSpaceReader());
		fooDrives = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.WORK_SPACE), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(2, fooDrives.getPageResponse().getTotalElements());
		PageContainer<SharedSpaceNodeNested> fooDriveReaderRole = service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(NodeType.WORK_SPACE), Sets.newHashSet(Role.WORK_SPACE_READER.toString()), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(1, fooDriveReaderRole.getPageResponse().getTotalElements());
		// Filter by Name
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByName = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, "SECOND NODE", null, null, container);
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
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.WORK_SPACE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.WORK_SPACE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.WORK_SPACE));
		node7 = service.create(amy, amy, new SharedSpaceNode("Amy's first node", null, NodeType.WORK_SPACE));
		// Filter SharedSpaces by domain by an ADMIN -> It will return just sharedSpaces of his domain
		john.setRole(org.linagora.linshare.core.domain.constants.Role.ADMIN);
		userRepo.update((User) john);
		Assertions.assertTrue(john.hasAdminRole());
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByAdmin = service.findAll(john, john, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByAdmin.getPageResponse().getContent()) {
			Assertions.assertEquals(john.getDomainId(), sharedSpaceNodeNested.getDomainUuid());
		}
		// Filter SharedSpaces by domain by an ADMIN with empty domains list -> it will return the domain's sharedSpaces where he s admin
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByAdminEmptyDomainList = service.findAll(john, john, null, Lists.newArrayList(), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByAdminEmptyDomainList.getPageResponse().getContent()) {
			Assertions.assertEquals(john.getDomainId(), sharedSpaceNodeNested.getDomainUuid());
		}
		// Filter SharedSpaces by domain by a ROOT -> it will return both foo2 and john's domains' sharedSpaces
		List<String> domainUuids = Lists.newArrayList(john.getDomainId(), amy.getDomainId());
		Set<String> returnedDoaminsUuids = Sets.newHashSet();
		Account account = null;
		String name = null;
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByDomainByRoot = service.findAll(root, root, account, Lists.newArrayList(), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, name, null, null, container);
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByDomainByRoot.getPageResponse().getContent()) {
			returnedDoaminsUuids.add(sharedSpaceNodeNested.getDomainUuid());
			logger.debug("This is the domain uuid added to the set to return: {}", sharedSpaceNodeNested.getDomainUuid());
		}
		logger.debug("This is the list of the existent domains uuids: {}", domainUuids);
		logger.debug("This is the list of the final returned domains uuids: {}", returnedDoaminsUuids);
		Assertions.assertTrue(CollectionUtils.isEqualCollection(domainUuids, returnedDoaminsUuids));
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
		PageContainer<SharedSpaceNodeNested> foundNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Long foundNodesSize = foundNodes.getPageResponse().getTotalElements();
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		node2 = service.create(john, john, new SharedSpaceNode("John's second node", null, NodeType.WORK_SPACE));
		node3 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		node4 = service.create(foo, foo, new SharedSpaceNode("Foo's second node", null, NodeType.WORK_SPACE));
		node5 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		node6 = service.create(jane, jane, new SharedSpaceNode("Jane fourth node", null, NodeType.WORK_SPACE));
		PageContainer<SharedSpaceNodeNested> allNodes = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, container);
		Assertions.assertEquals(foundNodesSize + 6, allNodes.getPageResponse().getTotalElements());
		Long allNodesSize = allNodes.getPageResponse().getTotalElements();
		// PageContainer with wrongPageNumber
		PageContainer<SharedSpaceNodeNested> wrongPageNumberContainer = new PageContainer<SharedSpaceNodeNested>(5, 3);
		BusinessException e = assertThrows(BusinessException.class, () -> {
			service.findAll(root, root, foo, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, wrongPageNumberContainer);
		});
		Assertions.assertEquals(BusinessErrorCode.WRONG_PAGE_PARAMETERS, e.getErrorCode());
		// Search with wrongPageNumber for an empty list of elements
		service.delete(foo, foo, node3);
		service.delete(foo, foo, node4);
		// PageContainer with wrongPageSize we will return all found elements
		PageContainer<SharedSpaceNodeNested> wrongPageSizeContainer = new PageContainer<SharedSpaceNodeNested>(0, 30);
		PageContainer<SharedSpaceNodeNested> allNodesWrongPageSizeContainer = service.findAll(root, root, null, null, SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, null, wrongPageSizeContainer);
		Assertions.assertEquals(allNodesWrongPageSizeContainer.getPageResponse().getTotalElements(),
				allNodesSize -2);
		service.delete(john, john, node1);
		service.delete(john, john, node2);
		service.delete(jane, jane, node5);
		service.delete(jane, jane, node6);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllSharedSpacesByMembersNumber() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 10);
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		memberService.create(john, john, node1, adminRole, new SharedSpaceAccount(jane));
		memberService.create(john, john, node1, adminRole, new SharedSpaceAccount(foo));
		Assertions.assertEquals(3, memberBusinessService.findBySharedSpaceNodeUuid(node1.getUuid()).size());
		node2 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		memberService.create(foo, foo, node2, adminRole, new SharedSpaceAccount(jane));
		Assertions.assertEquals(2, memberBusinessService.findBySharedSpaceNodeUuid(node2.getUuid()).size());
		node3 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		Assertions.assertEquals(1, memberBusinessService.findBySharedSpaceNodeUuid(node3.getUuid()).size());
		// Find SharedSpaces with members number greater than
		Integer greaterThanOrEqualTo = 1;
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByGreaterThanOrEqualTo = service.findAll(root, root, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, greaterThanOrEqualTo, null, container);
		Assertions.assertEquals(3, fooSharedSpaceByGreaterThanOrEqualTo.getPageResponse().getTotalElements());
		List<String> expectedNodeUuids = Lists.newArrayList(node1.getUuid(), node2.getUuid(), node3.getUuid());
		List<String> returnedNodeUuids = Lists.newArrayList();
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByGreaterThanOrEqualTo.getPageResponse().getContent()) {
			returnedNodeUuids.add(sharedSpaceNodeNested.getUuid());
		}
		logger.debug("This is the list of the final returned node uuids: {}", returnedNodeUuids);
		Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedNodeUuids, returnedNodeUuids));
		// Find SharedSpaces with members number less than
		Integer lessThanOrEqualTo = 3;
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByLessThanOrEqualTo = service.findAll(root, root, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, lessThanOrEqualTo, container);
		Assertions.assertEquals(3, fooSharedSpaceByGreaterThanOrEqualTo.getPageResponse().getTotalElements());
		List<String> expectedNodeUuidsForLessThanOrEqualTo = Lists.newArrayList(node1.getUuid(), node2.getUuid(), node3.getUuid());
		List<String> returnedNodeUuidsForLessThanOrEqualTo = Lists.newArrayList();
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByLessThanOrEqualTo.getPageResponse().getContent()) {
			returnedNodeUuidsForLessThanOrEqualTo.add(sharedSpaceNodeNested.getUuid());
		}
		logger.debug("This is the list of the final returned node uuids: {}", returnedNodeUuidsForLessThanOrEqualTo);
		Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedNodeUuidsForLessThanOrEqualTo, returnedNodeUuidsForLessThanOrEqualTo));
		// Find SharedSpaces with members number greater and less than
		PageContainer<SharedSpaceNodeNested> fooSharedSpaceByGreaterAndLessThanOrEqualTo = service.findAll(root, root, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, greaterThanOrEqualTo, lessThanOrEqualTo, container);
		Assertions.assertEquals(3, fooSharedSpaceByGreaterThanOrEqualTo.getPageResponse().getTotalElements());
		List<String> expectedNodeUuidsForGreaterAndLessThanOrEqualTo = Lists.newArrayList(node1.getUuid(), node2.getUuid(), node3.getUuid());
		List<String> returnedNodeUuidsForGreaterAndLessThanOrEqualTo = Lists.newArrayList();
		for (SharedSpaceNodeNested sharedSpaceNodeNested : fooSharedSpaceByGreaterAndLessThanOrEqualTo.getPageResponse().getContent()) {
			returnedNodeUuidsForGreaterAndLessThanOrEqualTo.add(sharedSpaceNodeNested.getUuid());
		}
		logger.debug("This is the list of the final returned node uuids: {}", returnedNodeUuidsForGreaterAndLessThanOrEqualTo);
		Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedNodeUuidsForGreaterAndLessThanOrEqualTo, returnedNodeUuidsForGreaterAndLessThanOrEqualTo));
		service.delete(root, root, node1);
		service.delete(root, root, node2);
		service.delete(root, root, node3);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindOrphanSharedSpaces() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 10);
		node1 = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		memberService.create(john, john, node1, adminRole, new SharedSpaceAccount(jane));
		memberService.create(john, john, node1, adminRole, new SharedSpaceAccount(foo));
		Assertions.assertEquals(3, memberBusinessService.findBySharedSpaceNodeUuid(node1.getUuid()).size());
		node2 = service.create(foo, foo, new SharedSpaceNode("Foo's first node", null, NodeType.WORK_GROUP));
		memberService.create(foo, foo, node2, adminRole, new SharedSpaceAccount(jane));
		Assertions.assertEquals(2, memberBusinessService.findBySharedSpaceNodeUuid(node2.getUuid()).size());
		node3 = service.create(jane, jane, new SharedSpaceNode("Jane third node", null, NodeType.WORK_GROUP));
		Assertions.assertEquals(1, memberBusinessService.findBySharedSpaceNodeUuid(node3.getUuid()).size());
		memberService.delete(root, root, memberBusinessService.findBySharedSpaceNodeUuid(node3.getUuid()).iterator().next().getUuid());
		Assertions.assertEquals(0, memberBusinessService.findBySharedSpaceNodeUuid(node3.getUuid()).size());
		// Find SharedSpaces with members number greater than
		Integer lessThanOrEqualTo = 1;
		PageContainer<SharedSpaceNodeNested> orphanShanredSpaces = service.findAll(root, root, null, Lists.newArrayList(john.getDomainId()), SortOrder.DESC, Sets.newHashSet(), Sets.newHashSet(), SharedSpaceField.creationDate, null, null, lessThanOrEqualTo, container);
		// Find saved node3 in order to get the last modification date after member deletion
		node3 = service.find(root, root, node3.getUuid());
		SharedSpaceNodeNested returnedNode = orphanShanredSpaces.getPageResponse().getContent().iterator().next();
		assertThat(returnedNode)
		.returns(node3.getUuid(), from(SharedSpaceNodeNested::getUuid))
		.returns(node3.getName(), from(SharedSpaceNodeNested::getName))
		.returns(node3.getParentUuid(), from(SharedSpaceNodeNested::getParentUuid))
		.returns(node3.getNodeType(), from(SharedSpaceNodeNested::getNodeType))
		.returns(node3.getDomainUuid(), from(SharedSpaceNodeNested::getDomainUuid))
		.returns(node3.getCreationDate(), from(SharedSpaceNodeNested::getCreationDate))
		.returns(node3.getModificationDate(), from(SharedSpaceNodeNested::getModificationDate)
				);
		service.delete(root, root, node1);
		service.delete(root, root, node2);
		service.delete(root, root, node3);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllShouldWorkWhenFilteringByRole() {
		String role = "MyRole";
		SharedSpaceAuthor sharedSpaceAuthor = new SharedSpaceAuthor(john.getFullName(), john.getMail());
		SharedSpaceRole sharedSpaceRole = new SharedSpaceRole(role, true, new GenericLightEntity(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_DOMAIN), sharedSpaceAuthor, NodeType.WORK_GROUP);
		sharedSpaceRoleMongoRepository.save(sharedSpaceRole);

		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(0, 10);
		SharedSpaceNode sharedSpaceNode = service.create(john, john, new SharedSpaceNode("John's first node", null, NodeType.WORK_GROUP));
		memberService.create(john, john, sharedSpaceNode, sharedSpaceRole, new SharedSpaceAccount(jane));
		memberService.create(john, john, sharedSpaceNode, adminRole, new SharedSpaceAccount(foo));

		PageContainer<SharedSpaceNodeNested> nodes = service.findAll(root, root, null, ImmutableList.of(john.getDomainId()), SortOrder.ASC, ImmutableSet.of(), ImmutableSet.of(role), SharedSpaceField.creationDate, null, 1, null, container);
		assertThat(nodes.getTotalElements()).isEqualTo(1);
	}
}
