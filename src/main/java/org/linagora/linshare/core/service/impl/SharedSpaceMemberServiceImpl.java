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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnNewMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;

import com.google.common.collect.Lists;

public class SharedSpaceMemberServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberService {

	private final SharedSpaceMemberBusinessService businessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final LogEntryService logEntryService;

	private final UserRepository<User> userRepository;

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository) {
		super(rac);
		this.businessService = sharedSpaceMemberBusinessService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
	}

	@Override
	public SharedSpaceMember find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space member uuid");
		SharedSpaceMember toFind = businessService.find(uuid);
		if (toFind == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND,
					"The Shared space member with uuid : " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				toFind);
		return toFind;
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, User newMember, SharedSpaceNode node,
			SharedSpaceRole role) throws BusinessException {
		Validate.notNull(newMember, "newMemeber uuid must be set.");
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node uuid must be set.");
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null, node);
		if (!checkAccountNotInNode(newMember.getLsUuid(), node.getUuid())) {
			String message = String.format(
					"The account with the UUID : %s is already a member of the node with the uuid : %s",
					newMember.getLsUuid(), node.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, message);
		}
		SharedSpaceMember member = createWithoutCheckPermission(authUser, actor, node, role, newMember);
		WorkGroupWarnNewMemberEmailContext context = new WorkGroupWarnNewMemberEmailContext(member, actor, newMember);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return member;
	}

	@Override
	public SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, User newMember) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(newMember, "newMemeber uuid must be set.");
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		SharedSpaceMember member = new SharedSpaceMember(new SharedSpaceNodeNested(node),
				new GenericLightEntity(role.getUuid(), role.getName()),
				new SharedSpaceAccount(newMember));
		SharedSpaceMember toAdd = businessService.create(member);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.SHARED_SPACE_MEMBER, toAdd);
		logEntryService.insert(log);
		return toAdd;
	}

	@Override
	public SharedSpaceMember findMember(Account authUser, Account actor, Account possibleMember,
			SharedSpaceNode sharedSpaceNode) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(possibleMember, "possibleMember must be set.");
		Validate.notNull(sharedSpaceNode, "sharedSpaceNode must be set.");
		SharedSpaceMember foundMember = businessService.findByMemberAndSharedSpaceNode(possibleMember.getLsUuid(),
				sharedSpaceNode.getUuid());
		if (foundMember == null) {
			String message = String.format(
					"The account with the UUID : %s is not a member of the node with the uuid : %s",
					possibleMember.getLsUuid(), sharedSpaceNode.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
	}

	@Override
	public SharedSpaceMember findMemberByUuid(Account authUser, Account actor, String userUuid, String nodeUuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(userUuid, "userUuid must be set.");
		Validate.notEmpty(nodeUuid, "nodeUuid must be set.");
		SharedSpaceMember foundMember = businessService
				.findByMemberAndSharedSpaceNode(userUuid, nodeUuid);
		if (foundMember == null) {
			String message = String.format(
					"The account with the UUID : %s is not a member of the node with the uuid : %s",
					userUuid, nodeUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, String uuid, User userMember) {
		preChecks(authUser, actor);
		Validate.notNull(uuid, "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = find(authUser, actor, uuid);
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		businessService.delete(foundMemberToDelete);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.SHARED_SPACE_MEMBER, foundMemberToDelete);
		logEntryService.insert(log);
		WorkGroupWarnDeletedMemberEmailContext context = new WorkGroupWarnDeletedMemberEmailContext(foundMemberToDelete, actor, userMember);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return foundMemberToDelete;
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, User foundUser) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
		SharedSpaceMember foundMemberToUpdate = find(authUser, actor, memberToUpdate.getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		SharedSpaceMember updated = businessService.update(foundMemberToUpdate, memberToUpdate);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.SHARED_SPACE_MEMBER, foundMemberToUpdate);
		log.setResourceUpdated(updated);
		logEntryService.insert(log);
		WorkGroupWarnUpdatedMemberEmailContext context = new WorkGroupWarnUpdatedMemberEmailContext(updated, foundUser, actor);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return updated;
	}

	@Override
	public SharedSpaceMember updateRole(Account authUser, Account actor, String sharedSpaceMemberUuid,
			GenericLightEntity newRole, User foundUser) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceMemberUuid, "Missing required sharedSpaceMemberUuid");
		Validate.notNull(newRole, "Missing required newRole");
		Validate.notNull(newRole.getName(), "Missing required roleName");
		Validate.notNull(newRole.getUuid(), "Missing required roleUuid");
		SharedSpaceMember foundMemberToUpdate = find(authUser, actor, sharedSpaceMemberUuid);
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		SharedSpaceMember updatedMember = businessService.updateRole(foundMemberToUpdate, newRole);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.SHARED_SPACE_MEMBER, foundMemberToUpdate);
		log.setResourceUpdated(updatedMember);
		logEntryService.insert(log);
		WorkGroupWarnUpdatedMemberEmailContext context = new WorkGroupWarnUpdatedMemberEmailContext(updatedMember, foundUser, actor);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return updatedMember;
	}

	@Override
	public List<SharedSpaceMember> findAll(Account authUser, Account actor, String shareSpaceNodeUuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(shareSpaceNodeUuid, "Missing required shared space node uuid");
		checkListPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null, shareSpaceNodeUuid);
		List<SharedSpaceMember> foundMembers = businessService.findBySharedSpaceNodeUuid(shareSpaceNodeUuid);
		return foundMembers;
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceNodeUuid, "Missing required sharedSpaceNodeUuid");
		// TODO Check the user is admin to delete all Members
		List<SharedSpaceMember> foundMembersToDelete = findAll(authUser, actor, sharedSpaceNodeUuid);
		businessService.deleteAll(foundMembersToDelete);
		List<AuditLogEntryUser> logs = Lists.newArrayList();
		for (SharedSpaceMember member : foundMembersToDelete) {
			logs.add(new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
					AuditLogEntryType.SHARED_SPACE_MEMBER, member));
		}
		if (logs != null && !logs.isEmpty()) {
			logEntryService.insert(logs);
		}
		return foundMembersToDelete;
	}

	@Override
	public List<SharedSpaceMember> deleteAllUserMemberships(Account authUser, Account actor, String userUuid) {
		preChecks(authUser, actor);
		Validate.notNull(userUuid, "Missing required sharedSpaceNodeUuid");
		// TODO Check the user is admin to delete all Members
		List<SharedSpaceMember> foundMembersToDelete = businessService.findAllUserMemberships(userUuid);
		List<AuditLogEntryUser> logs = Lists.newArrayList();
		for (SharedSpaceMember member : foundMembersToDelete) {
			User userMember = userRepository.findByLsUuid(member.getAccount().getUuid());
			delete(authUser, actor, member.getNode().getUuid(), userMember);
			logs.add(new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
					AuditLogEntryType.SHARED_SPACE_MEMBER, member));
		}
		if (logs != null && !logs.isEmpty()) {
			logEntryService.insert(logs);
		}
		return foundMembersToDelete;
	}

	@Override
	public List<SharedSpaceMember> findByMemberName(Account authUser, Account actor, String name) {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Shared space member account name must be set.");
		List<SharedSpaceMember> foundMembers = businessService.findByMemberName(name);
		checkListPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null);
		return foundMembers;
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor, String accountUuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(accountUuid, "accountUuid must be set.");
		return businessService.findAllByAccount(accountUuid);
	}

	@Override
	public List<SharedSpaceMember> findByNode(Account authUser, Account actor, String ssnodeUuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(ssnodeUuid, "The shared space node uuid must be set.");
		return businessService.findBySharedSpaceNodeUuid(ssnodeUuid);
	}

	private boolean checkAccountNotInNode(String possibleMemberUuid, String nodeUuid) {
		return businessService.findByMemberAndSharedSpaceNode(possibleMemberUuid, nodeUuid) == null;
	}

	@Override
	public List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid) {
		return businessService.findAllByAccountAndRole(accountUuid, roleUuid);
	}
}
