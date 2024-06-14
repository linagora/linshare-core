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

import java.util.Arrays;
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
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
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
public class SharedSpaceMemberServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceMemberServiceImplTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private SharedSpaceRole adminRole;

	private SharedSpaceRole readerRole;

	private SharedSpaceRole readerDriveRole;

	private LightSharedSpaceRole lightReaderRoleToPersist;

	private Account root;

	private Account system;

	private User john;

	private User jane;

	private WorkGroup workGroup;

	private GenericLightEntity lightNodePersisted;

	private SharedSpaceNode node;

	private SharedSpaceAccount accountJhon;

	private SharedSpaceAccount accountJane;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	@Autowired
	@Qualifier("sharedSpaceNodeBusinessService")
	private SharedSpaceNodeBusinessService nodeBusinessService;

	@Autowired
	@Qualifier("sharedSpaceMemberService")
	private SharedSpaceMemberService service;

	@Autowired
	@Qualifier("sharedSpaceMemberBusinessService")
	SharedSpaceMemberBusinessService memberBusinessService;

	@Autowired
	private MailBuildingService buildingService;

	@Autowired
	private WorkGroupNodeService workGroupNodeService;

	@Autowired
	private ThreadService threadService;

	@Autowired
	private ThreadRepository threadRepository;

	public SharedSpaceMemberServiceImplTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByDomainAndMail(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		system = userRepository.getBatchSystemAccount();
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		adminRole = roleBusinessService.findByName("ADMIN");
		readerRole = roleBusinessService.findByName("READER");
		readerDriveRole = roleBusinessService.findByName("WORK_SPACE_READER");
		lightReaderRoleToPersist = new LightSharedSpaceRole(readerRole);
		Validate.notNull(adminRole, "adminRole must be set");
		workGroup = threadService.create(john, john, "WG-nodeTest");
		node = new SharedSpaceNode("nodeTest", NodeType.WORK_GROUP);
		node.setUuid(workGroup.getLsUuid());
		node.setDomainUuid(john.getDomainId());
		nodeBusinessService.create(node);
		lightNodePersisted = new GenericLightEntity(node.getUuid(), node.getUuid());
		accountJhon = new SharedSpaceAccount(john);
		accountJane = new SharedSpaceAccount(jane);
		SharedSpaceMember johnMemberShip = service.create(john, john, node, adminRole,
				accountJhon);
		Assertions.assertNotNull(johnMemberShip, "John has not been added as a member of his shared space");

		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		threadRepository.delete(workGroup);
		nodeBusinessService.delete(node);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testFind() {
		SharedSpaceMember created = service.create(john, john, node, adminRole, accountJane);
		SharedSpaceMember found = service.find(john, john, created.getUuid());
		Assertions.assertEquals(created.getUuid(), found.getUuid());
	}

	@Test
	public void testCreate() {
		// John add Jane as an admin of the shared space node
		SharedSpaceMember janeMembership = service.create(john, john, node, adminRole, accountJane);
		Assertions.assertNotNull(janeMembership, "Jane has not been added as a member of John's shared space");
	}

	@Test
	public void testAutocompleteForExistingMembers() {
		Account user3 = userRepository.findByMail("user3@linshare.org");

		SharedSpaceMember janeMembership = service.create(john, john, node, adminRole, accountJane);
		SharedSpaceMember user3Membership = service.create(john, john, node, adminRole, new SharedSpaceAccount(user3));

		String wg_uuid = node.getUuid();
		List<SharedSpaceMember> list = memberBusinessService.findBySharedSpaceNodeUuid(wg_uuid);
		for (SharedSpaceMember sharedSpaceMember : list) {
			logger.info(sharedSpaceMember .toString());
		}
		// john, jane, user3
		Assertions.assertEquals(3, list.size());

		workGroupNodeService.create(
				john, john, workGroup,
				new WorkGroupFolder(new AccountMto(john), "folder john name", null, workGroup.getLsUuid()),
				false, false);
		workGroupNodeService.create(
				jane, jane, workGroup,
				new WorkGroupFolder(new AccountMto(jane), "folder jane name", null, workGroup.getLsUuid()),
				false, false);
		List<WorkGroupNode> findAll = workGroupNodeService.findAll(john, john, workGroup, null, true, null);
		for (WorkGroupNode folder : findAll) {
			logger.debug(folder.toString());
		}
		Assertions.assertEquals(2, findAll.size());

		List<WorkgroupMemberAutoCompleteResultDto> autocomplete = memberBusinessService.autocompleteOnActiveMembers(wg_uuid, "jane");
		Assertions.assertEquals(1, autocomplete.size());
		Assertions.assertEquals(janeMembership.getUuid(), autocomplete.get(0).getSharedSpaceMemberUuid());
		logger.info(autocomplete.get(0).toString());

		autocomplete = memberBusinessService.autocompleteOnActiveMembers(wg_uuid, "user2");
		Assertions.assertEquals(1, autocomplete.size());
		Assertions.assertEquals(janeMembership.getUuid(), autocomplete.get(0).getSharedSpaceMemberUuid());

		autocomplete = memberBusinessService.autocompleteOnActiveMembers(wg_uuid, "linshare.org");
		Assertions.assertEquals(3, autocomplete.size());

		autocomplete = memberBusinessService.autocompleteOnActiveMembers(wg_uuid, "user");
		Assertions.assertEquals(3, autocomplete.size());

		service.delete(john, john, janeMembership.getUuid());
		autocomplete = memberBusinessService.autocompleteOnActiveMembers(wg_uuid, "user");
		Assertions.assertEquals(2, autocomplete.size());

		autocomplete = memberBusinessService.autocompleteOnAssetAuthor(wg_uuid, "user");
		for (WorkgroupMemberAutoCompleteResultDto sharedSpaceMember : autocomplete) {
			logger.debug(sharedSpaceMember.toString());
		}
		Assertions.assertEquals(2, autocomplete.size());
		autocomplete = memberBusinessService.autocompleteOnAssetAuthor(wg_uuid, "jane");
		Assertions.assertEquals(1, autocomplete.size());

		service.delete(john, john, user3Membership.getUuid());
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
		SharedSpaceNode node = nodeBusinessService.find(lightNodePersisted.getUuid());
		service.deleteAllMembers(john, john, node, LogActionCause.WORK_GROUP_DELETION, null);
		foundMembers = service.findAll(root, root, lightNodePersisted.getUuid());
		Assertions.assertEquals(0, foundMembers.size(), "There are members left in the shared space node");
	}
	
	@Test
	public void testDeleteAllWorkgroupMembersByMemberNotAllowed() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		SharedSpaceNode wg = new SharedSpaceNode("wg", NodeType.WORK_GROUP);
		wg.setDomainUuid(john.getDomainId());
		// test delete a workgroup by a user which has not a permission 
		nodeBusinessService.create(wg);
		service.create(john, john, wg, adminRole, accountJhon);
		service.create(john, john, wg, readerRole, accountJane);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.deleteAllMembers(jane, jane, wg, LogActionCause.WORK_GROUP_DELETION, null);
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, exception.getErrorCode());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteAllWorkgroupMembersByNotMember() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test delete a workgroup by a user which is not a member of it
		SharedSpaceNode wg =nodeBusinessService.create(new SharedSpaceNode("wg", NodeType.WORK_GROUP));
		wg.setDomainUuid(john.getDomainId());
		service.create(john, john, wg, adminRole, accountJhon);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.deleteAllMembers(jane, jane, wg, LogActionCause.WORK_GROUP_DELETION, null);
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, exception.getErrorCode());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteAllByNotMember() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// test delete a drive by a user which is not a member of it
		SharedSpaceNode drive = nodeBusinessService.create(new SharedSpaceNode("drive", NodeType.WORK_SPACE));
		drive.setDomainUuid(john.getDomainId());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(readerDriveRole, adminRole);
		service.create(john, john, drive, context, accountJhon);
		SharedSpaceNode nested1 = nodeBusinessService.create(new SharedSpaceNode("nested1", drive.getUuid(), NodeType.WORK_GROUP));
		nested1.setDomainUuid(john.getDomainId());
		SharedSpaceNode nested2 = nodeBusinessService.create(new SharedSpaceNode("nested2", drive.getUuid(), NodeType.WORK_GROUP));
		nested2.setDomainUuid(john.getDomainId());
		service.create(john, john, nested1, adminRole, accountJhon);
		service.create(john, john, nested2, adminRole, accountJhon);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.deleteAllMembers(jane, jane, drive, LogActionCause.WORK_SPACE_DELETION, Arrays.asList(new SharedSpaceNodeNested(nested1), new SharedSpaceNodeNested(nested2)));
		});
		Assertions.assertEquals(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, exception.getErrorCode());
		logger.info(LinShareTestConstants.END_TEST);
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
			memberToSendToUpdate.setRole(new LightSharedSpaceRole("wrongUuid", "READER", node.getNodeType()));
			service.update(john, john, memberToSendToUpdate);
		} catch (BusinessException e) {
			Assertions.assertEquals(e.getErrorCode(), BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND);
		}
	}

}
