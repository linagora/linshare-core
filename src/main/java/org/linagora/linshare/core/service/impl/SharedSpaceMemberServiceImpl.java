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
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnNewMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

public class SharedSpaceMemberServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberService {

	private final SharedSpaceMemberBusinessService businessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac) {
		super(rac);
		this.businessService = sharedSpaceMemberBusinessService;
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
	public SharedSpaceMember create(Account authUser, Account actor, GenericLightEntity nodeToPersist,
			GenericLightEntity roleToPersist, GenericLightEntity accountLight) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(accountLight, "Account uuid must be set.");
		Validate.notNull(roleToPersist, "Role uuid must be set.");
		Validate.notNull(nodeToPersist, "Node uuid must be set.");
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountLight);
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				member);
		SharedSpaceMember toAdd = businessService.create(member);
		return toAdd;
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, User newMember, SharedSpaceNode sharedSpaceNode,
			SharedSpaceRole role) throws BusinessException {
		GenericLightEntity nodeToPersist = new GenericLightEntity(sharedSpaceNode.getUuid(), sharedSpaceNode.getName());
		GenericLightEntity roleToPersist = new GenericLightEntity(role.getUuid(), role.getName());
		GenericLightEntity accountLight = new GenericLightEntity(newMember.getLsUuid(), newMember.getFullName());
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountLight);
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				member);
		SharedSpaceMember toAdd = businessService.create(member);
		// TODO
//		ThreadMemberAuditLogEntry log = new ThreadMemberAuditLogEntry(actor, owner, LogAction.CREATE,
//				AuditLogEntryType.WORKGROUP_MEMBER, member);
//		addMembersToLog(workGroup, log);
//		logEntryService.insert(log);
		WorkGroupWarnNewMemberEmailContext context = new WorkGroupWarnNewMemberEmailContext(member, actor, newMember);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		return toAdd;
	}

	@Override
	public SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor,
			GenericLightEntity nodeToPersist, GenericLightEntity roleToPersist, GenericLightEntity accountLight)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(accountLight, "Account uuid must be set.");
		Validate.notNull(roleToPersist, "Role uuid must be set.");
		Validate.notNull(nodeToPersist, "Node uuid must be set.");
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountLight);
		SharedSpaceMember toAdd = businessService.create(member);
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
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember memberToDelete) {
		preChecks(authUser, actor);
		Validate.notNull(memberToDelete, "Missing required member to delete");
		Validate.notNull(memberToDelete.getUuid(), "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = find(authUser, actor, memberToDelete.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		businessService.delete(foundMemberToDelete);
		return foundMemberToDelete;
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
		SharedSpaceMember foundMemberToUpdate = find(authUser, actor, memberToUpdate.getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		return businessService.update(foundMemberToUpdate, memberToUpdate);
	}

	@Override
	public SharedSpaceMember updateRole(Account authUser, Account actor, String sharedSpaceMemberUuid,
			GenericLightEntity newRole) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceMemberUuid, "Missing required sharedSpaceMemberUuid");
		Validate.notNull(newRole, "Missing required newRole");
		Validate.notNull(newRole.getName(), "Missing required roleName");
		Validate.notNull(newRole.getUuid(), "Missing required roleUuid");
		SharedSpaceMember foundMemberToUpdate = find(authUser, actor, sharedSpaceMemberUuid);
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		return businessService.updateRole(foundMemberToUpdate, newRole);
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
	public List<SharedSpaceMember> findAllByAccount(Account authUser, Account actor, String accountUuid) {
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

}
