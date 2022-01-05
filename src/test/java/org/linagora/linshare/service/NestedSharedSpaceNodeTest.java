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
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
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
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class NestedSharedSpaceNodeTest {

	private static Logger logger = LoggerFactory.getLogger(NestedSharedSpaceNodeTest.class);

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
	@Qualifier("sharedSpaceRoleService")
	private SharedSpaceRoleService ssRoleService;

	private Account john;

	private Account jane;

	private Account foo;

	private SharedSpaceRole adminDriveRole;

	private SharedSpaceRole writerDriveRole;

	private SharedSpaceRole readerDriveRole;

	private SharedSpaceRole adminWorkgroupRole;

	private SharedSpaceRole contributor;

	private SharedSpaceRole reader;

	public NestedSharedSpaceNodeTest() {
		super();
	}

	@BeforeEach
	public void init() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		Account root = userRepository.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		foo = userRepository.findByMail(LinShareTestConstants.FOO_ACCOUNT);
		adminWorkgroupRole = ssRoleService.getAdmin(root, root);
		adminDriveRole = ssRoleService.getDriveAdmin(root, root);
		writerDriveRole = ssRoleService.findByName(root, root, "DRIVE_WRITER");
		readerDriveRole = ssRoleService.findByName(root, root, "DRIVE_READER");
		contributor = ssRoleService.findByName(root, root, "CONTRIBUTOR");
		reader = ssRoleService.findByName(root, root, "READER");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(drive, "Drive not created");
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
		// Check John got admin role on the drive
		Assertions.assertEquals(adminDriveRole.getUuid(), driveMember.getRole().getUuid());
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assertions.assertEquals(adminWorkgroupRole.getUuid(), workgroupMember.getRole().getUuid());
		Assertions.assertEquals(false, driveMember.isNested());
		Assertions.assertEquals(true, workgroupMember.isNested());
		Assertions.assertEquals(drive.getUuid(), expectedNode.getParentUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFailCreateWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		// Add Jane as member to the drive with READER role
		ssMemberService.create(john, john, drive, new SharedSpaceMemberContext(readerDriveRole, reader),
				new SharedSpaceAccount((User) jane));
		try {
			SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Forbidden WorkGroup", drive.getUuid(),
					NodeType.WORK_GROUP);
			// Try to create a workgroup with Jane
			ssNodeService.create(jane, jane, workGroupInsideDrive);
			Assertions.assertFalse(true, "An error should be raised : Jane cannot create workgroups");
		} catch (BusinessException e) {
			// Fail to create because no good rights
			Assertions.assertEquals(BusinessErrorCode.WORK_GROUP_FORBIDDEN, e.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateWorkGroupsInsideDriveAsCreator() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		// Add Jane as member to the drive with READER role
		ssMemberService.create(john, john, drive, new SharedSpaceMemberContext(writerDriveRole, reader),
				new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		SharedSpaceNode expectedNode = ssNodeService.create(jane, jane, workGroupInsideDrive);
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(),
				expectedNode.getUuid());
		// Check Jane is admin the default role for the workgroups
		Assertions.assertEquals(adminWorkgroupRole.getUuid(), workgroupMember.getRole().getUuid());
		Assertions.assertEquals(true, workgroupMember.isNested());
		Assertions.assertEquals(drive.getUuid(), expectedNode.getParentUuid());
		List<SharedSpaceMember> nodeMembers = ssMemberService.findAll(jane, jane, expectedNode.getUuid());
		Assertions.assertEquals(2, nodeMembers.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		Assertions.assertNotNull(drive, "Drive not created");
		SharedSpaceMember driveMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), drive.getUuid());
		// Check John got admin role on the drive
		Assertions.assertEquals(adminDriveRole.getUuid(), driveMember.getRole().getUuid());
		// Create 2 workgroups inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode node2 = new SharedSpaceNode("workgroup2 DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		ssNodeService.create(john, john, node);
		ssNodeService.create(john, john, node2);
		List<SharedSpaceNodeNested> workGroupMembers = ssMemberService.findAllSharedSpacesByAccountAndParentForUsers(john, john, john.getLsUuid(), false, drive.getUuid(), null);
		Assertions.assertEquals(2, workGroupMembers.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		// Create 2 workgroups inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		node = ssNodeService.create(john, john, node);
		SharedSpaceNode node2 = new SharedSpaceNode("workgroup2 DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		ssNodeService.create(john, john, node2);
		List<SharedSpaceNodeNested> workGroupMembers = ssMemberService.findAllSharedSpacesByAccountAndParentForUsers(john, john, john.getLsUuid(), false, drive.getUuid(), null);
		Assertions.assertEquals(2, workGroupMembers.size());
		// Test the delete Method
		ssNodeService.delete(john, john, node);
		workGroupMembers = ssMemberService.findAllSharedSpacesByAccountAndParentForUsers(john, john, john.getLsUuid(), false, drive.getUuid(), null);
		Assertions.assertEquals(1, workGroupMembers.size());
		try {
			ssNodeService.find(john, john, node.getUuid());
			Assertions.assertFalse(true, "ERROR : The node should not exist anymore");
		} catch (BusinessException e) {
			// Fail to find the node
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND, e.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateWorkgroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.WORK_SPACE));
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		node = ssNodeService.create(john, john, node);
		node.setName("Updated workgroupName");
		// Test the update Method
		node = ssNodeService.update(john, john, node);
		Assertions.assertEquals("Updated workgroupName", node.getName());
		logger.info(LinShareTestConstants.END_TEST);
	}


	@Test
	public void testAddExternalMemberToWorkgroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("John Drive", NodeType.WORK_SPACE));
		// Add Jane as member to the drive with READER role
		SharedSpaceMemberDrive janeMemberDrive = (SharedSpaceMemberDrive) ssMemberService.create(john, john, drive,
				new SharedSpaceMemberContext(writerDriveRole, adminWorkgroupRole), new SharedSpaceAccount((User) jane));
		Assertions.assertEquals(drive.getUuid(), janeMemberDrive.getNode().getUuid());
		SharedSpaceNode nestedWorkgroup = ssNodeService.create(jane, jane,
				new SharedSpaceNode("Nested_WorkGroup", drive.getUuid(), NodeType.WORK_GROUP));
		// Add Justin to the nested workgroup [Nested_WorkGroup]
		SharedSpaceMember justinMemberNestedWg = ssMemberService.create(jane, jane, nestedWorkgroup, reader,
				new SharedSpaceAccount((User) foo));
		// Check Justin has reader role in the workgroup
		Assertions.assertEquals(reader.getUuid(), justinMemberNestedWg.getRole().getUuid());

		// Check Justin is a member on created NESTED workgroup
		Assertions.assertEquals(true, justinMemberNestedWg.isNested());

		// Check Justin is a member on created NESTED workgroup but the workgroup is not seen as nester for him. because he is not part of the drive
		Assertions.assertEquals(false, justinMemberNestedWg.isSeeAsNested());
		Assertions.assertEquals(drive.getUuid(), nestedWorkgroup.getParentUuid());
		List<SharedSpaceNodeNested> justinSharedSpaces = ssMemberService.findAllSharedSpacesByAccountAndParentForUsers(foo, foo, foo.getLsUuid(), false, nestedWorkgroup.getParentUuid(), null);
		Assertions.assertEquals(1, justinSharedSpaces.size());
		ssNodeService.delete(john, john, drive);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSoftUpdateRoles() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest8", NodeType.WORK_SPACE));
		// Add Jane as member to the drive with READER role
		SharedSpaceMemberDrive janeMember = (SharedSpaceMemberDrive) ssMemberService.create(john, john, drive,
				new SharedSpaceMemberContext(writerDriveRole, reader), new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		ssNodeService.create(john, john, workGroupInsideDrive);
		janeMember.setNestedRole(new LightSharedSpaceRole(contributor));
		janeMember = (SharedSpaceMemberDrive)ssMemberService.update(john, john, janeMember, false, false);
		Assertions.assertEquals(contributor.getUuid(), janeMember.getNestedRole().getUuid());
		List<SharedSpaceNodeNested> workgroupsInsideDrive = ssMemberService.findAllWorkGroupsInNode(jane, jane, drive.getUuid(), jane.getLsUuid());
		for (SharedSpaceNodeNested sharedSpaceNodeNested : workgroupsInsideDrive) {
			SharedSpaceMember janeWorkGroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(), sharedSpaceNodeNested.getUuid());
			Assertions.assertEquals(reader.getUuid(), janeWorkGroupMember.getRole().getUuid());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForceUpdateRoles() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest8", NodeType.WORK_SPACE));
		// Add Jane as member to the drive with READER role
		SharedSpaceMemberDrive janeMember = (SharedSpaceMemberDrive) ssMemberService.create(john, john, drive,
				new SharedSpaceMemberContext(writerDriveRole, reader), new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		ssNodeService.create(jane, jane, workGroupInsideDrive);
		janeMember.setNestedRole(new LightSharedSpaceRole(contributor));
		janeMember = (SharedSpaceMemberDrive)ssMemberService.update(john, john, janeMember, true, false);
		Assertions.assertEquals(contributor.getUuid(), janeMember.getNestedRole().getUuid());
		List<SharedSpaceNodeNested> workgroupsInsideDrive = ssMemberService.findAllWorkGroupsInNode(jane, jane, drive.getUuid(), jane.getLsUuid());
		for (SharedSpaceNodeNested sharedSpaceNodeNested : workgroupsInsideDrive) {
			SharedSpaceMember janeWorkGroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(), sharedSpaceNodeNested.getUuid());
			Assertions.assertEquals(contributor.getUuid(), janeWorkGroupMember.getRole().getUuid());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}
}
