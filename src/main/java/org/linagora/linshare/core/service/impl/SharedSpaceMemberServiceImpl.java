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
import org.linagora.linshare.core.notifications.context.EmailContext;
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

	protected final SharedSpaceMemberBusinessService businessService;

	private final LogEntryService logEntryService;

	protected final UserRepository<User> userRepository;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;
	
	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository) {
		super(rac);
		this.businessService = businessService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
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
	public SharedSpaceMember findMemberByUuid(Account authUser, Account actor, String userUuid, String nodeUuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(userUuid, "userUuid must be set.");
		Validate.notEmpty(nodeUuid, "nodeUuid must be set.");
		SharedSpaceMember foundMember = businessService.findByAccountAndNode(userUuid, nodeUuid);
		if (foundMember == null) {
			String message = String.format(
					"The account with the UUID : %s is not a member of the node with the uuid : %s", userUuid,
					nodeUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
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
	public List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid) {
		return businessService.findAllByAccountAndRole(accountUuid, roleUuid);
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

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node, SharedSpaceRole role,
			SharedSpaceAccount account) throws BusinessException {
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node uuid must be set.");
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null, node);
		User newMember = userRepository.findByLsUuid(account.getUuid());
		if (newMember == null) {
			String message = String.format(
					"The account with the UUID : %s is not existing", account.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		if (!checkMemberNotInNode(account.getUuid(), node.getUuid())) {
			String message = String.format(
					"The account with the UUID : %s is already a member of the node with the uuid : %s",
					account.getUuid(), node.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, message);
		}
		SharedSpaceMember member = createWithoutCheckPermission(authUser, actor, node, role, account);
		WorkGroupWarnNewMemberEmailContext context = new WorkGroupWarnNewMemberEmailContext(member, actor, newMember);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return member;
	}

	protected void notify(EmailContext context) {
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
	}

	protected SharedSpaceMemberAuditLogEntry createLog(Account authUser, Account actor, SharedSpaceMember resource) {
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, resource);
		logEntryService.insert(log);
		return log;
	}

	protected SharedSpaceMemberAuditLogEntry updateLog(Account authUser, Account actor, SharedSpaceMember resource, SharedSpaceMember resourceUpdated) {
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, resource);
		log.setResourceUpdated(resourceUpdated);
		logEntryService.insert(log);
		return log;
	}

	@Override
	public SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		SharedSpaceMember member = new SharedSpaceMember(new SharedSpaceNodeNested(node),
				new GenericLightEntity(role.getUuid(), role.getName()), account);
		SharedSpaceMember toAdd = businessService.create(member);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, toAdd);
		logEntryService.insert(log);
		return toAdd;
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notNull(uuid, "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = find(authUser, actor, uuid);
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		businessService.delete(foundMemberToDelete);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP_MEMBER, foundMemberToDelete);
		logEntryService.insert(log);
		User user = userRepository.findByLsUuid(foundMemberToDelete.getAccount().getUuid());
		WorkGroupWarnDeletedMemberEmailContext context = new WorkGroupWarnDeletedMemberEmailContext(foundMemberToDelete, actor, user);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return foundMemberToDelete;
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
		SharedSpaceMember foundMemberToUpdate = findMemberByUuid(authUser, actor, memberToUpdate.getAccount().getUuid(), memberToUpdate.getNode().getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		SharedSpaceMember updated = businessService.update(foundMemberToUpdate, memberToUpdate);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP_MEMBER, foundMemberToUpdate);
		log.setResourceUpdated(updated);
		logEntryService.insert(log);
		User user = userRepository.findByLsUuid(foundMemberToUpdate.getAccount().getUuid());
		WorkGroupWarnUpdatedMemberEmailContext context = new WorkGroupWarnUpdatedMemberEmailContext(updated, user,
				actor);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return updated;
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
					AuditLogEntryType.WORKGROUP_MEMBER, member));
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
			delete(authUser, actor, member.getUuid());
			logs.add(new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
					AuditLogEntryType.WORKGROUP_MEMBER, member));
		}
		if (logs != null && !logs.isEmpty()) {
			logEntryService.insert(logs);
		}
		return foundMembersToDelete;
	}

	private boolean checkMemberNotInNode(String possibleMemberUuid, String nodeUuid) {
		return businessService.findByAccountAndNode(possibleMemberUuid, nodeUuid) == null;
	}

}
