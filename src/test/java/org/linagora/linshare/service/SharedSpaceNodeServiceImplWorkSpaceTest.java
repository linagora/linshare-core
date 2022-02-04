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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
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
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceNodeServiceImplWorkSpaceTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceNodeServiceImplWorkSpaceTest.class);

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService service;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	
	@Autowired
	@Qualifier("sharedSpaceMemberWorkSpaceService")
	private SharedSpaceMemberFragmentService ssMemberWorkSpaceService;

	@Autowired
	@Qualifier("workgroupMemberService")
	private SharedSpaceMemberFragmentService ssMemberWorkgroupService;
	
	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService memberService;

	@Autowired
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Autowired
	private WorkGroupNodeService workGroupNodeService;

	@Autowired
	private ThreadService threadService;

	@Autowired
	private SharedSpaceRoleService ssRoleService;

	@Autowired
	private RootUserRepository rootUserRepository;

	@Autowired
	private FunctionalityService functionalityService;

	private User authUser, jane;

	private SharedSpaceRole adminWorkSpaceRole, readerWorkSpaceRole, reader, admin;

	public SharedSpaceNodeServiceImplWorkSpaceTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		activateWorkGroupCreation();
		Account root = userRepository.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		authUser = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		adminWorkSpaceRole = ssRoleService.getWorkSpaceAdmin(root, root);
		readerWorkSpaceRole = ssRoleService.findByName(root, root,"WORK_SPACE_READER");
		reader = ssRoleService.findByName(root, root, "READER");
		admin = ssRoleService.findByName(root, root, "ADMIN");
		logger.debug(LinShareTestConstants.END_SETUP);
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
	public void createWorkSpace() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "WorkSpace not created");
		assertThat(expectedNode.getAuthor().getFirstName()).isEqualTo(authUser.getFirstName());
		assertThat(expectedNode.getAuthor().getLastName()).isEqualTo(authUser.getLastName());
		service.delete(authUser, authUser, expectedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createWorkSpaceSpecialCharInName() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("EP_TEST_v233<script>alert(document.cookie)</script>",
				NodeType.WORK_SPACE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "WorkSpace not created");
		Assertions.assertEquals(expectedNode.getName(), "EP_TEST_v233");
		service.delete(authUser, authUser, expectedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateWorkSpaceSpecialCharInName() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My workSpace", NodeType.WORK_SPACE);
		SharedSpaceNode nodeToUpdate = service.create(authUser, authUser, node);
		Assertions.assertNotNull(nodeToUpdate, "workSpace not created");
		Assertions.assertEquals(nodeToUpdate.getName(), "My workSpace");
		nodeToUpdate.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		service.update(authUser, authUser, nodeToUpdate);
		Assertions.assertEquals(nodeToUpdate.getName(), "EP_TEST_v233");
		service.delete(authUser, authUser, nodeToUpdate);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createWorkSpaceAndFind() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My workSpace", NodeType.WORK_SPACE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "workSpace not created");
		Assertions.assertNotNull(service.find(authUser, authUser, expectedNode.getUuid()));
		service.delete(authUser, authUser, expectedNode);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createWorkSpaceAndWorkgroupAndFindAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int before = service.findAllByAccount(authUser, authUser).size();
		SharedSpaceNode workSpace = new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE);
		SharedSpaceNode expectedWorkSpace = service.create(authUser, authUser, workSpace);
		Assertions.assertNotNull(expectedWorkSpace, "WorkSpace not created");
		SharedSpaceNode workgroup = new SharedSpaceNode("My groups", NodeType.WORK_GROUP);
		service.create(authUser, authUser, workgroup);
		Assertions.assertNotNull(workgroup, "Workgroup not created");
		List<SharedSpaceNodeNested> ssNested = service.findAllByAccount(authUser, authUser);
		Assertions.assertEquals(before + 2, ssNested.size());
		service.delete(authUser, authUser, expectedWorkSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createWorkSpaceAndDelete() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode node = new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE);
		SharedSpaceNode expectedNode = service.create(authUser, authUser, node);
		Assertions.assertNotNull(expectedNode, "WorkSpace not created");
		int before = service.findAllByAccount(authUser, authUser).size();
		service.delete(authUser, authUser, expectedNode);
		List<SharedSpaceNodeNested> ssNested = service.findAllByAccount(authUser, authUser);
		Assertions.assertEquals(ssNested.size(), before - 1);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void allowNestedWgCreationFunctionilityDisabled() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode workSpace = new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE);
		SharedSpaceNode expectedWorkSpace = service.create(authUser, authUser, workSpace);
		Assertions.assertNotNull(expectedWorkSpace, "WorkSpace not created");
		Functionality workGroupCreationRightFunctionality = functionalityReadOnlyService.getWorkGroupCreationRight(authUser.getDomain());
		workGroupCreationRightFunctionality.getActivationPolicy().setStatus(false);
		SharedSpaceNode nestedWorkgroup = new SharedSpaceNode("My groups", expectedWorkSpace.getUuid(), NodeType.WORK_GROUP);
		service.create(authUser, authUser, nestedWorkgroup);
		Assertions.assertNotNull(nestedWorkgroup, "Workgroup not created");
		SharedSpaceNode workgroupOnRoot = new SharedSpaceNode("My groups", NodeType.WORK_GROUP);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.create(authUser, authUser, workgroupOnRoot);
		});
		Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_FORBIDDEN, exception.getErrorCode());
		Assertions.assertEquals("You are not authorized to create an entry.", exception.getMessage());
		service.delete(authUser, authUser, expectedWorkSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteWorkSpaceByNotMember() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test a workSpace member not allowed to delete a workSpace where he is not a membership 
		SharedSpaceNode johnWorkSpace = service.create(authUser, authUser, new SharedSpaceNode("john workSpace", NodeType.WORK_SPACE));
		service.create(jane, jane, new SharedSpaceNode("jane WorkSpace", NodeType.WORK_SPACE));
		BusinessException e = assertThrows(BusinessException.class, () -> {
			service.delete(jane, jane, johnWorkSpace);
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, e.getErrorCode());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteWorkSpaceByInvitedMemberWorkSpaceAdmin() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test an invited member with WorkSpace Admin role can delete his WorkSpace 
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		service.create(authUser, authUser, new SharedSpaceNode("nested-wg1", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 2);
		// Add a member to the WorkSpace
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminWorkSpaceRole, reader);
		ssMemberWorkSpaceService.create(authUser, authUser, workSpace,
				context, new SharedSpaceAccount((User) jane));
		service.delete(jane, jane, workSpace);
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, workSpace.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 0);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteWorkSpaceByInvitedMemberWorkSpaceReader() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test an invited member with WorkSpace Reader role NOT ALLOWED to delete his WorkSpace 
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		service.create(authUser, authUser, new SharedSpaceNode("nested-wg1", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 2);
		// Add a member to the WorkSpace
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(readerWorkSpaceRole, admin);
		ssMemberWorkSpaceService.create(authUser, authUser, workSpace,
				context, new SharedSpaceAccount((User) jane));
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			service.delete(jane, jane, workSpace);
		});
		Assertions.assertEquals(BusinessErrorCode.WORKSPACE_FORBIDDEN, e.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 2);
		// a member with Admin role in nested workgroup can delete his workgroup
		service.delete(jane, jane, nested1);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested1.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 1);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteNestedByInvitedMemberAdmin() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test an invited member in nested workgroup with admin role delete his wg 
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 1);
		// Add a member to the nested wg
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(admin);
		ssMemberWorkgroupService.create(authUser, authUser, nested1,
				context, new SharedSpaceAccount((User) jane));
		// a member with Admin role in nested workgroup can delete his workgroup
		service.delete(jane, jane, nested1);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested1.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 0);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteNestedWorkgroup() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test delete a nested wg by a member that is a membership of it but not in the workSpace
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 1);
		// Add a member to the nested wg
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(admin);
		SharedSpaceMember member = ssMemberWorkgroupService.create(authUser, authUser, nested1,
				context, new SharedSpaceAccount((User) jane));
		// a member with Admin role in nested workgroup can delete his workgroup
		service.delete(jane, jane, nested1);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested1.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		BusinessException e2 = Assertions.assertThrows(BusinessException.class, () -> {
			memberService.find(authUser, authUser, member.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e2.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 0);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteNestedWorkgroupByWorkSpaceMember() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test delete a nested wg by a workSpace member
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 1);
		// Add a member to the nested wg
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(admin);
		SharedSpaceMember localWgMember = ssMemberWorkgroupService.create(authUser, authUser, nested1,
				context, new SharedSpaceAccount((User) jane));
		// a workSpace member with WorkSpace Admin role can delete nested workgroup
		service.delete(authUser, authUser, nested1);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested1.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		BusinessException e2 = Assertions.assertThrows(BusinessException.class, () -> {
			memberService.find(authUser, authUser, localWgMember.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e2.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 0);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteWorkSpaceAndNestedWorkgroupByWorkSpaceMember() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test delete a workSpace and its wg by a workSpace member
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		// Add a local member to the nested wg
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(admin);
		SharedSpaceMember localWgMember = ssMemberWorkgroupService.create(authUser, authUser, nested1,
				context, new SharedSpaceAccount((User) jane));
		// a workSpace member with WorkSpace Admin role can delete workSpace and related nested workgroups
		service.delete(authUser, authUser, workSpace);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, workSpace.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		BusinessException e2 = Assertions.assertThrows(BusinessException.class, () -> {
			memberService.find(authUser, authUser, localWgMember.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e2.getErrorCode());
		Assertions.assertEquals(service.findAllByAccount(authUser, authUser, false, workSpace.getUuid()).size(), 0);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteWorkSpaceAndNestedWorkgroup() throws BusinessException, IOException {
		// test delete a workSpace and its wg
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create WorkSpace and nested workgroups
		SharedSpaceNode workSpace = service.create(authUser, authUser, new SharedSpaceNode("My WorkSpace", NodeType.WORK_SPACE));
		SharedSpaceNode nested1 = service.create(authUser, authUser, new SharedSpaceNode("first-nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		SharedSpaceNode nested2 = service.create(authUser, authUser, new SharedSpaceNode("second-nested-wg", workSpace.getUuid(), NodeType.WORK_GROUP));
		Assertions.assertNotNull(nested1);
		Assertions.assertNotNull(nested2);
		// Create workgroupNodes
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		File tempFile = File.createTempFile("linshare-default.properties", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		WorkGroup workGroup = threadService.find(authUser, authUser, nested1.getUuid());
		WorkGroupNode groupNode = workGroupNodeService.create(authUser, (User) authUser, workGroup, tempFile,
				tempFile.getName(), null, false);
		Assertions.assertNotNull(groupNode);
		// Add a local member to the nested wg
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(admin);
		SharedSpaceMember localWgMember = ssMemberWorkgroupService.create(authUser, authUser, nested1,
				context, new SharedSpaceAccount((User) jane));
		// delete th WorkSpace
		service.delete(authUser, authUser, workSpace);
		BusinessException e1 = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, workSpace.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e1.getErrorCode());
		BusinessException deletedMemberException = Assertions.assertThrows(BusinessException.class, () -> {
			memberService.find(authUser, authUser, localWgMember.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, deletedMemberException.getErrorCode());
		BusinessException deletedNestedWG1Exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested1.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, deletedNestedWG1Exception.getErrorCode());
		BusinessException deletedNestedWG2Exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(authUser, authUser, nested2.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, deletedNestedWG2Exception.getErrorCode());
		BusinessException deletedWorkGroupNodeException = Assertions.assertThrows(BusinessException.class, () -> {
			workGroupNodeService.find(authUser, authUser, workGroup, groupNode.getUuid(), false);
		});
		Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_NODE_NOT_FOUND, deletedWorkGroupNodeException.getErrorCode());
		logger.info(LinShareTestConstants.END_TEST);
	}

}
