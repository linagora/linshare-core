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
public class SharedSpaceMemberDriveServiceTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceMemberDriveServiceTest.class);

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

	@BeforeEach
	public void init() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
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

	@AfterEach
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
		Assertions.assertNotNull(drive, "Drive not created");
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode nestedWorkgroup = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, nestedWorkgroup);
		Assertions.assertNotNull(expectedNode, "node not created");
		SharedSpaceMember workgroupMember = ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(),
				expectedNode.getUuid());
		// Check John got the default role for the workgroups
		Assertions.assertEquals(adminWorkgroupRole.getUuid(), workgroupMember.getRole().getUuid());
		Assertions.assertEquals(false, driveMember.isNested());
		Assertions.assertEquals(true, workgroupMember.isNested());
		Assertions.assertEquals(drive.getUuid(), expectedNode.getParentUuid());
		// Add a member to the Drive
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(creatorDriveRole, reader);
		SharedSpaceMemberDrive addedDriveMember = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		workgroupMember = ssMemberService.findMemberByAccountUuid(jane, jane, jane.getLsUuid(),
				expectedNode.getUuid());
		Assertions.assertNotNull(addedDriveMember, "Member not added to the drive");
		Assertions.assertNotNull(workgroupMember, "Member not added to the workgroup");
		Assertions.assertEquals(creatorDriveRole.getUuid(), addedDriveMember.getRole().getUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateMemberOnTheDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assertions.assertNotNull(drive, "Drive not created");
		ssMemberService.findMemberByAccountUuid(john, john, john.getLsUuid(), drive.getUuid());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminDriveRole, adminWorkgroupRole);
		// Create 1 workgroup inside a drive as John
		SharedSpaceNode node = new SharedSpaceNode("workgroup DriveTest", drive.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceNode expectedNode = ssNodeService.create(john, john, node);
		Assertions.assertNotNull(expectedNode, "node not created");
		// Add new member
		SharedSpaceMemberDrive memberToUpdate = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		Assertions.assertNotNull(memberToUpdate, "Member not added to the drive");
		// Update member on the Drive
		memberToUpdate.setNestedRole(new GenericLightEntity(reader));
		memberToUpdate.setRole(new GenericLightEntity(creatorDriveRole));
		SharedSpaceMemberDrive updated = (SharedSpaceMemberDrive) ssMemberDriveService.update(john, john,
				memberToUpdate, true);
		Assertions.assertEquals(creatorDriveRole.getUuid(), updated.getRole().getUuid());
		try {
			ssMemberDriveService.create(jane, jane, drive, context, new SharedSpaceAccount((User) john));
		} catch (BusinessException ex) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, ex.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteMemberOnTheDrive() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// Create a drive as John
		SharedSpaceNode drive = ssNodeService.create(john, john, new SharedSpaceNode("DriveTest", NodeType.DRIVE));
		Assertions.assertNotNull(drive, "Drive not created");
		SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) ssMemberService.findMemberByAccountUuid(john, john,
				john.getLsUuid(), drive.getUuid());
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
		// Add new member
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(adminDriveRole, adminWorkgroupRole);
		SharedSpaceMemberDrive addedDriveMember = (SharedSpaceMemberDrive) ssMemberDriveService.create(john, john, drive,
				context, new SharedSpaceAccount((User) jane));
		Assertions.assertNotNull(addedDriveMember, "Member not added to the drive");
		Assertions.assertNotNull(workgroupMember, "Member not added to the workgroup");
		// Delete member on the Drive
		ssMemberDriveService.delete(john, john, addedDriveMember.getUuid());
		try {
			ssMemberService.findMemberByAccountUuid(john, john, jane.getLsUuid(), addedDriveMember.getUuid());
		} catch (BusinessException ex) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, ex.getErrorCode());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}
}
