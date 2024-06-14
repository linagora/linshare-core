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
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.fragment.SharedSpaceFragmentService;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberWorkgroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
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
public class SharedSpaceMemberWorkSpaceServiceTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceMemberWorkSpaceServiceTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("sharedSpaceNodeService")
	private SharedSpaceNodeService ssNodeService;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService ssMemberService;

	@Autowired
	@Qualifier("sharedSpaceMemberWorkSpaceService")
	private SharedSpaceMemberFragmentService ssMemberWorkSpaceService;

	@Autowired
	@Qualifier("sharedSpaceRoleService")
	private SharedSpaceRoleService ssRoleService;

	@Autowired
	@Qualifier("sharedSpaceNodeWorkSpaceService")
	private SharedSpaceFragmentService sharedSpaceFragmentService;

	private Account john;

	private Account jane;

	private SharedSpaceRole adminWorkSpaceRole;

	private SharedSpaceRole writerWorkSpaceRole;

	private SharedSpaceRole adminWorkgroupRole;

	private SharedSpaceRole reader;

	private SharedSpaceRole writer;

	public SharedSpaceMemberWorkSpaceServiceTest() {
		super();
	}

	@BeforeEach
	public void init() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		final Account root = userRepository.findByDomainAndMail(LinShareTestConstants.ROOT_DOMAIN,
				LinShareTestConstants.ROOT_ACCOUNT);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		adminWorkgroupRole = ssRoleService.getAdmin(root, root);
		adminWorkSpaceRole = ssRoleService.getWorkSpaceAdmin(root, root);
		writerWorkSpaceRole = ssRoleService.findByName(root, root, "WORK_SPACE_WRITER");
		reader = ssRoleService.findByName(root, root, "READER");
		writer = ssRoleService.findByName(root, root, "WRITER");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testAddMembertoWorkSpace() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a workSpace as John
		SharedSpaceNode workSpace = ssNodeService.create(john, john, new SharedSpaceNode("WorkSpaceTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(workSpace, "WorkSpace not created");
		SharedSpaceMemberDrive workSpaceMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), workSpace.getUuid());
		// Create 1 workgroup inside a workSpace as John
		SharedSpaceNode nestedWorkgroup = new SharedSpaceNode("workgroup WorkSpaceTest", workSpace.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, nestedWorkgroup);
		Assertions.assertNotNull(expectedNode, "node not created");
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assertions.assertEquals(adminWorkgroupRole.getUuid(), workgroupMember.getRole().getUuid());
		Assertions.assertEquals(false, workSpaceMember.isNested());
		Assertions.assertEquals(true, workgroupMember.isNested());
		Assertions.assertEquals(workSpace.getUuid(), expectedNode.getParentUuid());
		// Add a member to the workSpace
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(writerWorkSpaceRole, reader);
		SharedSpaceMemberDrive addedWorkSpaceMember = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.create(john, john, workSpace,
				context, new SharedSpaceAccount((User) jane));
		workgroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(),
				expectedNode.getUuid());
		Assertions.assertNotNull(addedWorkSpaceMember, "Member not added to the workSpace");
		Assertions.assertNotNull(workgroupMember, "Member not added to the workgroup");
		Assertions.assertEquals(writerWorkSpaceRole.getUuid(), addedWorkSpaceMember.getRole().getUuid());
		ssNodeService.delete(john, john, workSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateMemberOnTheWorkSpace() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a workSpace as John
		SharedSpaceNode workSpace = ssNodeService.create(john, john, new SharedSpaceNode("WorkSpaceTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(workSpace, "WorkSpace not created");
		ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), workSpace.getUuid());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminWorkSpaceRole, adminWorkgroupRole);
		// Create 1 workgroup inside a workSpace as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup WorkSpaceTest", workSpace.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		// Add new member
		SharedSpaceMemberDrive memberToUpdate = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.create(john, john, workSpace,
				context, new SharedSpaceAccount((User) jane));
		Assertions.assertNotNull(memberToUpdate, "Member not added to the workSpace");
		// Update member on the WorkSpace
		memberToUpdate.setNestedRole(new LightSharedSpaceRole(reader));
		memberToUpdate.setRole(new LightSharedSpaceRole(writerWorkSpaceRole));
		SharedSpaceMemberDrive updated = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.update(john, john,
				memberToUpdate, true,  false);
		Assertions.assertEquals(writerWorkSpaceRole.getUuid(), updated.getRole().getUuid());
		try {
			ssMemberWorkSpaceService.create(jane, jane, workSpace, context, new SharedSpaceAccount((User) john));
		} catch (BusinessException ex) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, ex.getErrorCode());
		}
		ssNodeService.delete(john, john, workSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testPropagateUpdateNestedRolesOnWorkSpaceMembers() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a workSpace as John
		SharedSpaceNode workSpace = ssNodeService.create(john, john, new SharedSpaceNode("WorkSpaceTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(workSpace, "WorkSpace not created");
		ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), workSpace.getUuid());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminWorkSpaceRole, adminWorkgroupRole);
		// Add Jane to the created workSpace
		SharedSpaceMemberDrive secondWorkSpaceMember = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.create(john, john, workSpace,
				context, new SharedSpaceAccount((User) jane));
		Assertions.assertNotNull(secondWorkSpaceMember, "Member not added to the workSpace");
		Assertions.assertEquals(secondWorkSpaceMember.getNestedRole().getName(), adminWorkgroupRole.getName());
		Assertions.assertEquals(ssMemberService.findAll(john, john, workSpace.getUuid()).size(), 2);
		// Create first workgroup inside a workSpace
		SharedSpaceNode node = new SharedSpaceNode("workgroup WorkSpaceTest", workSpace.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		// Create second workgroup inside a workSpace as John
		SharedSpaceNode secondNode = new SharedSpaceNode("workgroup2 WorkSpaceTest", workSpace.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode secondExpectedNode = ssNodeService.create(john, john, secondNode);
		Assertions.assertNotNull(secondExpectedNode, "node not created");
		Assertions.assertEquals(sharedSpaceFragmentService.findAllWorkgroupsInNode(john, john, workSpace).size(), 2);
		// Update Jane on the second node and pristine will be set to false
		SharedSpaceMember janeMemberSecondNode = ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), secondExpectedNode.getUuid());
		janeMemberSecondNode.setRole(new LightSharedSpaceRole(writer));
		SharedSpaceMemberWorkgroup workGroupMember = (SharedSpaceMemberWorkgroup) ssMemberService.update(john, john, janeMemberSecondNode);
		Assertions.assertEquals(writer.getUuid(), workGroupMember.getRole().getUuid());
		Assertions.assertFalse(workGroupMember.isPristine());
		//check the pristine field for jane in the first node is true
		SharedSpaceMemberWorkgroup janeMemberFirstNode = (SharedSpaceMemberWorkgroup) ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), expectedNode.getUuid());
		Assertions.assertTrue(janeMemberFirstNode.isPristine());
		Assertions.assertEquals(janeMemberFirstNode.getRole().getName(), adminWorkgroupRole.getName());
		// Update Jane member on the WorkSpace with propagate flag is true
		secondWorkSpaceMember.setNestedRole(new LightSharedSpaceRole(reader));
		SharedSpaceMemberDrive updated = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.update(john, john,
				secondWorkSpaceMember, false,  true);
		Assertions.assertEquals(reader.getName(), updated.getNestedRole().getName());
		//check Update of Jane role on first nested node
		SharedSpaceMemberWorkgroup janeMemberFirstNodeUpdate = (SharedSpaceMemberWorkgroup) ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), expectedNode.getUuid());
		Assertions.assertEquals(reader.getName(), janeMemberFirstNodeUpdate.getRole().getName());
		//No role update for Jane on the second nested node
		SharedSpaceMemberWorkgroup janeMemberSecondNodeUpdate = (SharedSpaceMemberWorkgroup) ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), secondExpectedNode.getUuid());
		Assertions.assertFalse(reader.getName().equals(janeMemberSecondNodeUpdate.getRole().getName()));
		ssNodeService.delete(john, john, workSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteMemberOnTheWorkSpace() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a workSpace as John
		SharedSpaceNode workSpace = ssNodeService.create(john, john, new SharedSpaceNode("WorkSpaceTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(workSpace, "WorkSpace not created");
		SharedSpaceMemberDrive workSpaceMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), workSpace.getUuid());
		// Create 1 workgroup inside a workSpace as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup WorkSpaceTest", workSpace.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assertions.assertEquals(adminWorkgroupRole.getUuid(), workgroupMember.getRole().getUuid());
		Assertions.assertEquals(false, workSpaceMember.isNested());
		Assertions.assertEquals(true, workgroupMember.isNested());
		Assertions.assertEquals(workSpace.getUuid(), expectedNode.getParentUuid());
		// Add new member
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminWorkSpaceRole, adminWorkgroupRole);
		SharedSpaceMemberDrive addedWorkSpaceMember = (SharedSpaceMemberDrive) ssMemberWorkSpaceService.create(john, john, workSpace,
				context, new SharedSpaceAccount((User) jane));
		Assertions.assertNotNull(addedWorkSpaceMember, "Member not added to the workSpace");
		Assertions.assertNotNull(workgroupMember, "Member not added to the workgroup");
		// Delete member on the WorkSpace
		ssMemberWorkSpaceService.delete(john, john, addedWorkSpaceMember.getUuid());
		try {
			ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), addedWorkSpaceMember.getUuid());
		} catch (BusinessException ex) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, ex.getErrorCode());
		}
		ssNodeService.delete(john, john, workSpace);
		logger.info(LinShareTestConstants.END_TEST);
	}
}
