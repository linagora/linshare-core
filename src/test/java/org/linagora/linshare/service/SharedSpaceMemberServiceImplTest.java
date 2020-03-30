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

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
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
public class SharedSpaceMemberServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(RecipientFavouriteRepositoryImplTest.class);

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private SharedSpaceRole adminRole;

	private SharedSpaceRole readerRole;

	private GenericLightEntity lightReaderRoleToPersist;

	private Account root;

	private Account system;

	private User john;

	private User jane;

	private GenericLightEntity lightNodePersisted;

	private SharedSpaceNode node;

	private SharedSpaceAccount accountJhon;

	private SharedSpaceAccount accountJane;

	private LoadingServiceTestDatas datas;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	@Autowired
	@Qualifier("sharedSpaceNodeBusinessService")
	private SharedSpaceNodeBusinessService nodeBusinessService;

	@Autowired
	InitMongoService initService;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService service;

	@Autowired
	@Qualifier("sharedSpaceMemberBusinessService")
	SharedSpaceMemberBusinessService memberBusinessService;

	private LinShareWiser wiser;

	@Autowired
	private MailBuildingService buildingService;

	public SharedSpaceMemberServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		root = datas.getRoot();
		system = datas.getSystem();
		john = datas.getUser1();
		jane = datas.getUser2();
		initService.init();
		adminRole = roleBusinessService.findByName("ADMIN");
		readerRole = roleBusinessService.findByName("READER");
		lightReaderRoleToPersist = new GenericLightEntity(readerRole.getUuid(), readerRole.getName());
		Validate.notNull(adminRole, "adminRole must be set");
		node = new SharedSpaceNode("nodeTest", "parentuuidTest", NodeType.DRIVE);
		nodeBusinessService.create(node);
		lightNodePersisted = new GenericLightEntity(node.getUuid(), node.getUuid());
		accountJhon = new SharedSpaceAccount(john);
		accountJane = new SharedSpaceAccount(jane);
		SharedSpaceMember johnMemberShip = service.createWithoutCheckPermission(john, john, node, adminRole,
				accountJhon);
		Assertions.assertNotNull(johnMemberShip, "John has not been added as a member of his shared space");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	public void testFind() {
		SharedSpaceMember toCreate = service.create(john, john, node, adminRole, accountJane);
		SharedSpaceMember tofound = service.find(john, john, toCreate.getUuid());
		Assertions.assertEquals(toCreate.getUuid(), tofound.getUuid());
	}

	@Test
	public void testCreate() {
		// John add Jane as an admin of the shared space node
		SharedSpaceMember janeMembership = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertNotNull(janeMembership, "Jane has not been added as a member of John's shared space");
	}

	@Test
	public void testAvoidDuplicatesMembers() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(memberToCreate.getAccount().getUuid(), jane.getLsUuid(), 
				"The account referenced in this member is not john's");
		try {
			service.create(john, john, node, adminRole, accountJane);
			Assertions.assertTrue(false, "An exception should be thrown to prevent the creation of a member with same accountuuid and nodeuuid");
		} catch (BusinessException e) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, e.getErrorCode(),
					"The thrown error code is not the one of an already exist ShareSpaceMember");
		}
	}

	@Test
	public void testDelete() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(memberToCreate.getAccount().getUuid(), jane.getLsUuid(), 
				"The account referenced in this member is not john's");
		SharedSpaceMember deletedMember = service.delete(john, john, memberToCreate.getUuid());
		Assertions.assertNotNull(deletedMember, "No member deleted");
		try {
			service.find(john, john, memberToCreate.getUuid());
			Assertions.assertTrue(false, "An exception should be thrown because the member should not be found");
		} catch (BusinessException e) {
			Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e.getErrorCode(),
					"The member is found in the database. It has not been deleted");
		}
	}

	/**
	 * This Test shows the field replyTo in MailContainerWithRecipient is null in case the root user who did the action 
	 */
	@Test
	public void testDeleteNoReplyToMail() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(memberToCreate.getAccount().getUuid(), jane.getLsUuid(),
				"The account referenced in this member is not john's");
		memberBusinessService.delete(memberToCreate);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(john, john, memberToCreate.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, exception.getErrorCode(),
				"The member is found in the database. It has not been deleted");
		User user = userRepository.findByLsUuid(jane.getLsUuid());
		MailContainerWithRecipient mail = buildingService
				.build(new WorkGroupWarnDeletedMemberEmailContext(memberToCreate, root, user));
		Assertions.assertNull(mail.getReplyTo());
	}

	/**
	 * This Test shows the field replyTo in MailContainerWithRecipient is null in case the system who did the action 
	 */
	@Test
	public void testDeleteNoReplyToMailSystemEmail() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(memberToCreate.getAccount().getUuid(), jane.getLsUuid(),
				"The account referenced in this member is not john's");
		memberBusinessService.delete(memberToCreate);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.find(john, john, memberToCreate.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, exception.getErrorCode(),
				"The member is found in the database. It has not been deleted");
		User user = userRepository.findByLsUuid(jane.getLsUuid());
		MailContainerWithRecipient mail = buildingService
				.build(new WorkGroupWarnDeletedMemberEmailContext(memberToCreate, system, user));
		Assertions.assertNull(mail.getReplyTo());
	}

	@Test
	public void testDeleteAll() {
		service.create(john, john, node, adminRole, accountJane);
		List<SharedSpaceMember> foundMembers = service.findAll(root, root, lightNodePersisted.getUuid());
		Assertions.assertTrue(foundMembers.size() > 0, "No members have been created");
		service.deleteAllMembers(john, john, lightNodePersisted.getUuid());
		foundMembers = service.findAll(root, root, lightNodePersisted.getUuid());
		Assertions.assertEquals(0, foundMembers.size(), "There are members left in the shared space node");
	}

	@Test
	public void testUpdate() {
		SharedSpaceMember createdMember = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(createdMember.getAccount().getUuid(), jane.getLsUuid(),
				"The account referenced in this shared space member is not jane");
		SharedSpaceMember memberToSendToUpdate = new SharedSpaceMember(createdMember);
		memberToSendToUpdate.setRole(lightReaderRoleToPersist);
		SharedSpaceMember updatedMember = service.update(john, john, memberToSendToUpdate);
		Assertions.assertNotEquals(createdMember.getRole(), updatedMember.getRole(),"The member has not been updated");
		Assertions.assertNotEquals(createdMember.getModificationDate(),
				updatedMember.getModificationDate(), "The member has not been updated");
	}
	
	@Test
	public void testUpdateWithWrongRole() {
		SharedSpaceMember createdMember = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertEquals(createdMember.getAccount().getUuid(), jane.getLsUuid(), 
				"The account referenced in this shared space member is not jane");
		SharedSpaceMember memberToSendToUpdate = new SharedSpaceMember(createdMember);
		try {
			memberToSendToUpdate.setRole(new GenericLightEntity("wrongUuid", "READER"));
			service.update(john, john, memberToSendToUpdate);
		} catch (BusinessException e) {
			Assertions.assertEquals(e.getErrorCode(), BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND);
		}
	}

}
