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
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.utils.LinShareWiser;
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
public class NestedSharedSpaceNodeTest extends AbstractTransactionalJUnit4SpringContextTests {

	private LinShareWiser wiser;

	private LoadingServiceTestDatas datas;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private InitMongoService initService;

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

	private Account justin;

	private SharedSpaceRole adminDriveRole;

	private SharedSpaceRole creatorDriveRole;

	private SharedSpaceRole readerDriveRole;

	private SharedSpaceRole adminWorkgroupRole;

	private SharedSpaceRole contributor;

	private SharedSpaceRole reader;

	public NestedSharedSpaceNodeTest() {
		super();
		wiser = new LinShareWiser(2525);
		
	}

	@Before
	public void init() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		initService.init();
		Account root = datas.getRoot();
		john = datas.getUser1();
		jane = datas.getUser2();
		justin = datas.getUser3();
		adminWorkgroupRole = ssRoleService.getAdmin(root, root);
		adminDriveRole = ssRoleService.getDriveAdmin(root, root);
		creatorDriveRole = ssRoleService.findByName(root, root, "DRIVE_CREATOR");
		readerDriveRole = ssRoleService.findByName(root, root, "DRIVE_READER");
		contributor = ssRoleService.findByName(root, root, "CONTRIBUTOR");
		reader = ssRoleService.findByName(root, root, "READER");
		logger.debug(LinShareTestConstants.END_SETUP);
	}  

	@After
	public void tearDown() {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assert.assertNotNull("Drive not created", drive);
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
		// Check John got admin role on the drive
		Assert.assertThat(driveMember.getRole().getUuid(), CoreMatchers.is(adminDriveRole.getUuid()));
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assert.assertNotNull("node not created", expectedNode);
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assert.assertThat(workgroupMember.getRole().getUuid(), CoreMatchers.is(adminWorkgroupRole.getUuid()));
		Assert.assertThat(driveMember.isNested(), CoreMatchers.is(false));
		Assert.assertThat(workgroupMember.isNested(), CoreMatchers.is(true));
		Assert.assertThat(expectedNode.getParentUuid(), CoreMatchers.is(drive.getUuid()));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFailCreateWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		// Add Jane as member to the drive with READER role
		ssMemberService.create(john, john, drive, new SharedSpaceMemberContext(readerDriveRole, reader),
				new SharedSpaceAccount((User) jane));
		try {
			SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Forbidden WorkGroup", drive.getUuid(),
					NodeType.WORK_GROUP);
			// Try to create a workgroup with Jane
			ssNodeService.create(jane, jane, workGroupInsideDrive);
			Assert.assertFalse("An error should be raised : Jane cannot create workgroups", true);
		} catch (BusinessException e) {
			// Fail to create because no good rights
			Assert.assertThat("Jane should not be able to create workgroups inside this drive", e.getErrorCode(),
					CoreMatchers.is(BusinessErrorCode.WORK_GROUP_FORBIDDEN));
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateWorkGroupsInsideDriveAsCreator() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		// Add Jane as member to the drive with READER role
		ssMemberService.create(john, john, drive, new SharedSpaceMemberContext(creatorDriveRole, reader),
				new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		SharedSpaceNode expectedNode = ssNodeService.create(jane, jane, workGroupInsideDrive);
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(),
				expectedNode.getUuid());
		// Check Jane is admin the default role for the workgroups
		Assert.assertThat(workgroupMember.getRole().getUuid(), CoreMatchers.is(adminWorkgroupRole.getUuid()));
		Assert.assertThat(workgroupMember.isNested(), CoreMatchers.is(true));
		Assert.assertThat(expectedNode.getParentUuid(), CoreMatchers.is(drive.getUuid()));
		List<SharedSpaceMember> nodeMembers = ssMemberService.findAll(jane, jane, expectedNode.getUuid());
		Assert.assertThat("ERROR : There are not 2 members in the workgroup", nodeMembers.size(), CoreMatchers.is(2));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assert.assertNotNull("Drive not created", drive);
		SharedSpaceMember driveMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), drive.getUuid());
		// Check John got admin role on the drive
		Assert.assertThat(adminDriveRole.getUuid(), CoreMatchers.is(driveMember.getRole().getUuid()));
		// Create 2 workgroups inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode node2 = new SharedSpaceNode("workgroup2 DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		ssNodeService.create(john, john, node);
		ssNodeService.create(john, john, node2);
		List<SharedSpaceNodeNested> workGroupMembers = ssNodeService.findAllWorkgroupsInNode(john, john,
				drive);
		Assert.assertThat(workGroupMembers.size(), CoreMatchers.is(2));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteWorkGroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		// Create 2 workgroups inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		node = ssNodeService.create(john, john, node);
		SharedSpaceNode node2 = new SharedSpaceNode("workgroup2 DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		ssNodeService.create(john, john, node2);
		List<SharedSpaceNodeNested> workGroupMembers = ssNodeService.findAllWorkgroupsInNode(john, john,
				drive);
		Assert.assertThat("ERROR : There is not 2 created workgroups", workGroupMembers.size(), CoreMatchers.is(2));
		// Test the delete Method
		ssNodeService.delete(john, john, node);
		workGroupMembers = ssNodeService.findAllWorkgroupsInNode(john, john, drive);
		Assert.assertThat(workGroupMembers.size(), CoreMatchers.is(1));
		try {
			ssNodeService.find(john, john, node.getUuid());
			Assert.assertFalse("ERROR : The node should not exist anymore", true);
		} catch (BusinessException e) {
			// Fail to find the node
			Assert.assertThat("ERROR : the node should not be found", e.getErrorCode(),
					CoreMatchers.is(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND));
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateWorkgroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		node = ssNodeService.create(john, john, node);
		node.setName("Updated workgroupName");
		// Test the update Method
		node = ssNodeService.update(john, john, node);
		Assert.assertThat("ERROR : Update failure!", node.getName(), CoreMatchers.is("Updated workgroupName"));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testAddExternalMemberToWorkgroupsInsideDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		// Add Jane as member to the drive with READER role
		ssMemberService.create(john, john, drive, new SharedSpaceMemberContext(creatorDriveRole, reader),
				new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		SharedSpaceNode expectedNode = ssNodeService.create(jane, jane, workGroupInsideDrive);
		SharedSpaceMember justinMember = ssMemberService.create(jane, jane, expectedNode, reader,
				new SharedSpaceAccount((User) justin));
		// Check Justin is reader in the workgroup and the sharedSpaceNode
		Assert.assertThat(justinMember.getRole().getUuid(), CoreMatchers.is(reader.getUuid()));
		Assert.assertThat(justinMember.isNested(), CoreMatchers.is(false));
		Assert.assertThat(expectedNode.getParentUuid(), CoreMatchers.is(drive.getUuid()));
		List<SharedSpaceNodeNested> justinNodes = ssMemberService.findAllByAccount(justin, justin, justin.getLsUuid(), false);
		Assert.assertThat("ERROR : Justin can see more nodes that allowed", justinNodes.size(), CoreMatchers.is(1));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSoftUpdateRoles() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest8", NodeType.DRIVE));
		// Add Jane as member to the drive with READER role
		SharedSpaceMemberDrive janeMember = (SharedSpaceMemberDrive) ssMemberService.create(john, john, drive,
				new SharedSpaceMemberContext(creatorDriveRole, reader), new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		ssNodeService.create(john, john, workGroupInsideDrive);
		janeMember.setNestedRole(new GenericLightEntity(contributor));
		janeMember = (SharedSpaceMemberDrive)ssMemberService.update(john, john, janeMember, false);
		Assert.assertThat("ERROR : Jane's role should be updated", janeMember.getNestedRole().getUuid(),
				CoreMatchers.is(contributor.getUuid()));
		List<SharedSpaceNodeNested> workgroupsInsideDrive = ssMemberService.findAllWorkGroupsInNode(jane, jane, drive.getUuid(), jane.getLsUuid());
		for (SharedSpaceNodeNested sharedSpaceNodeNested : workgroupsInsideDrive) {
			SharedSpaceMember janeWorkGroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(), sharedSpaceNodeNested.getUuid());
			Assert.assertThat("ERROR : Jane's role should be updated", janeWorkGroupMember.getRole().getUuid(),
					CoreMatchers.is(reader.getUuid()));
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForceUpdateRoles() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest8", NodeType.DRIVE));
		// Add Jane as member to the drive with READER role
		SharedSpaceMemberDrive janeMember = (SharedSpaceMemberDrive) ssMemberService.create(john, john, drive,
				new SharedSpaceMemberContext(creatorDriveRole, reader), new SharedSpaceAccount((User) jane));
		SharedSpaceNode workGroupInsideDrive = new SharedSpaceNode("Successful WorkGroup", drive.getUuid(),
				NodeType.WORK_GROUP);
		// Create a workgroup with Jane having the creator role
		ssNodeService.create(jane, jane, workGroupInsideDrive);
		janeMember.setNestedRole(new GenericLightEntity(contributor));
		janeMember = (SharedSpaceMemberDrive)ssMemberService.update(john, john, janeMember, true);
		Assert.assertThat("ERROR : Jane's role should be updated", janeMember.getNestedRole().getUuid(),
				CoreMatchers.is(contributor.getUuid()));
		List<SharedSpaceNodeNested> workgroupsInsideDrive = ssMemberService.findAllWorkGroupsInNode(jane, jane, drive.getUuid(), jane.getLsUuid());
		for (SharedSpaceNodeNested sharedSpaceNodeNested : workgroupsInsideDrive) {
			SharedSpaceMember janeWorkGroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(), sharedSpaceNodeNested.getUuid());
			Assert.assertThat("ERROR : Jane's role should be updated", janeWorkGroupMember.getRole().getUuid(),
					CoreMatchers.is(contributor.getUuid()));
		}
		logger.info(LinShareTestConstants.END_TEST);
	}
}
