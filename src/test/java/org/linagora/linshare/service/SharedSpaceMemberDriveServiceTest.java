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
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
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
public class SharedSpaceMemberDriveServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

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
	@Qualifier("sharedSpaceMemberDriveService")
	private SharedSpaceMemberFragmentService ssMemberDriveService;

	@Autowired
	@Qualifier("sharedSpaceRoleService")
	private SharedSpaceRoleService ssRoleService;

	private Account john;

	private Account jane;

	private SharedSpaceRole adminDriveRole;

	private SharedSpaceRole creatorDriveRole;

	private SharedSpaceRole adminWorkgroupRole;

	private SharedSpaceRole reader;

	private LinShareWiser wiser;

	private LoadingServiceTestDatas datas;


	public SharedSpaceMemberDriveServiceTest() {
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
		adminWorkgroupRole = ssRoleService.getAdmin(root, root);
		adminDriveRole = ssRoleService.getDriveAdmin(root, root);
		creatorDriveRole = ssRoleService.findByName(root, root, "DRIVE_CREATOR");
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
	public void testAddMembertoDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assert.assertNotNull("Drive not created", drive);
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode nestedWorkgroup = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, nestedWorkgroup);
		Assert.assertNotNull("node not created", expectedNode);
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assert.assertThat(workgroupMember.getRole().getUuid(), CoreMatchers.is(adminWorkgroupRole.getUuid()));
		Assert.assertThat(driveMember.isNested(), CoreMatchers.is(false));
		Assert.assertThat(workgroupMember.isNested(), CoreMatchers.is(true));
		Assert.assertThat(expectedNode.getParentUuid(), CoreMatchers.is(drive.getUuid()));
		// Add a member to the Drive
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(creatorDriveRole, reader);
		SharedSpaceMemberDrive addedDriveMember = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		workgroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(),
				expectedNode.getUuid());
		Assert.assertNotNull("Member not added to the drive", addedDriveMember);
		Assert.assertNotNull("Member not added to the workgroup", workgroupMember);
		Assert.assertThat(addedDriveMember.getRole().getUuid(), CoreMatchers.is(creatorDriveRole.getUuid()));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateMemberOnTheDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assert.assertNotNull("Drive not created", drive);
		ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), drive.getUuid());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminDriveRole, adminWorkgroupRole);
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assert.assertNotNull("node not created", expectedNode);
		// Add new member
		SharedSpaceMemberDrive memberToUpdate = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		Assert.assertNotNull("Member not added to the drive", memberToUpdate);
		// Update member on the Drive
		memberToUpdate.setNestedRole(new GenericLightEntity(reader));
		memberToUpdate.setRole(new GenericLightEntity(creatorDriveRole));
		SharedSpaceMemberDrive updated = (SharedSpaceMemberDrive) ssMemberDriveService.update(john, john,
				memberToUpdate, true);
		Assert.assertThat(updated.getRole().getUuid(), CoreMatchers.is(creatorDriveRole.getUuid()));
		try {
			ssMemberDriveService.create(jane, jane, drive, context, new SharedSpaceAccount((User) john));
		} catch (BusinessException ex) {
			Assert.assertThat(ex.getErrorCode(), CoreMatchers.is(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN));
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteMemberOnTheDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assert.assertNotNull("Drive not created", drive);
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
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
		// Add new member
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminDriveRole, adminWorkgroupRole);
		SharedSpaceMemberDrive addedDriveMember = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		Assert.assertNotNull("Member not added to the drive", addedDriveMember);
		Assert.assertNotNull("Member not added to the workgroup", workgroupMember);
		// Delete member on the Drive
		ssMemberDriveService.delete(john, john, addedDriveMember.getUuid());
		try {
			ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), addedDriveMember.getUuid());
		} catch (BusinessException ex) {
			Assert.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, ex.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}
}
