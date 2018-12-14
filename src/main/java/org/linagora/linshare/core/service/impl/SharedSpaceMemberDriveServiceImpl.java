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
import org.linagora.linshare.core.business.service.DriveMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.DriveWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.DriveWarnNewMemberEmailContext;
import org.linagora.linshare.core.notifications.context.DriveWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.SharedSpaceMemberDriveService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.google.common.collect.Lists;

public class SharedSpaceMemberDriveServiceImpl extends SharedSpaceMemberServiceImpl implements SharedSpaceMemberDriveService {

	private final SharedSpaceNodeBusinessService nodeBusinessService;

	public SharedSpaceMemberDriveServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			DriveMemberBusinessService driveMemberBusinessService,
			SharedSpaceNodeBusinessService nodeBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository,
				driveMemberBusinessService);
		this.nodeBusinessService = nodeBusinessService;
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		checkCreateMemberPermission(authUser, actor, node, context, account);
		return createWithoutCheckPermission(authUser, actor, node, context.getRole(), context.getNestedRole(), account);
	}

	@Override
	public SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, SharedSpaceRole nestedRole, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		Validate.notNull(nestedRole, "Drive role must be set.");
		User newMember = userRepository.findByLsUuid(account.getUuid());
		if (newMember == null) {
			String message = String.format("The account with the UUID : %s is not existing", account.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		SharedSpaceMemberDrive member = new SharedSpaceMemberDrive(new SharedSpaceNodeNested(node),
				new GenericLightEntity(role.getUuid(), role.getName()), account,
				new GenericLightEntity(nestedRole.getUuid(), nestedRole.getName()));
		member.setNestedRole(new GenericLightEntity(nestedRole.getUuid(), nestedRole.getName()));
		SharedSpaceMember toAdd = driveMemberBusinessService.create(member);
		// Add the new member to all workgroups inside the drive
		List<SharedSpaceNode> nestedWorkgroups = nodeBusinessService.findByParentUuidAndType(node.getUuid());
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		for (SharedSpaceNode wgNode : nestedWorkgroups) {
			if (checkMemberNotInNode(account.getUuid(), wgNode.getUuid())) {
				SharedSpaceMember wgMember = createWithoutCheckPermission(authUser, actor, wgNode, nestedRole, account);
				nestedMembers.add(wgMember);
			}
		}
		if (!actor.getLsUuid().equals(account.getUuid())) {
			notify(new DriveWarnNewMemberEmailContext(member, actor, newMember, nestedMembers));
		}
		saveLog(authUser, actor, LogAction.CREATE, toAdd);
		return toAdd;
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, boolean force) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
		GenericLightEntity wgRole = ((SharedSpaceMemberDrive) memberToUpdate).getNestedRole();
		Validate.notNull(wgRole, "The nested role must be set");
		SharedSpaceMemberDrive foundMemberToUpdate = (SharedSpaceMemberDrive) findMemberByUuid(authUser, actor,
				memberToUpdate.getAccount().getUuid(), memberToUpdate.getNode().getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		SharedSpaceMember updated = driveMemberBusinessService.update(foundMemberToUpdate,
				(SharedSpaceMemberDrive) memberToUpdate);
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		// No propagation if nested role is not updated
		if (!((SharedSpaceMemberDrive) updated).getNestedRole().equals(wgRole)) {
			// Update the member on all workgroups inside the drive
			if (force) {
				nestedMembers = businessService.findAllMembersByParentAndAccount(foundMemberToUpdate.getAccount().getUuid(),
						updated.getNode().getUuid());
			} else {
				nestedMembers = businessService.findAllMembersWithNoConflictedRoles(foundMemberToUpdate.getAccount().getUuid(),
						updated.getNode().getUuid(), wgRole.getUuid());
			}
			for (SharedSpaceMember wgFoundMember : nestedMembers) {
				SharedSpaceMember wgMemberToUpdate = new SharedSpaceMember(wgFoundMember);
				wgMemberToUpdate.setRole(wgRole);
				SharedSpaceMember updatedWgMember = businessService.update(wgFoundMember, wgMemberToUpdate);
				User user = userRepository.findByLsUuid(wgFoundMember.getAccount().getUuid());
				notify(new WorkGroupWarnUpdatedMemberEmailContext(updatedWgMember, user, actor));
				saveUpdateLog(authUser, actor, LogAction.UPDATE, wgFoundMember, updatedWgMember);
			}
		}
		User member = userRepository.findByLsUuid(foundMemberToUpdate.getAccount().getUuid());
		if (!actor.getLsUuid().equals(member.getLsUuid())) {
			notify(new DriveWarnUpdatedMemberEmailContext(foundMemberToUpdate, member, actor, nestedMembers));
		}
		saveUpdateLog(authUser, actor, LogAction.UPDATE, foundMemberToUpdate, updated);
		return updated;
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notNull(uuid, "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = find(authUser, actor, uuid);
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		businessService.delete(foundMemberToDelete);
		saveLog(authUser, actor, LogAction.DELETE, foundMemberToDelete);
		// Delete the member on all workgroups inside the drive
		List<SharedSpaceNodeNested> nestedWorkgroups = businessService.findAllByParentAndAccount(
				foundMemberToDelete.getAccount().getUuid(), foundMemberToDelete.getNode().getUuid());
		for (SharedSpaceNodeNested wgNode : nestedWorkgroups) {
			SharedSpaceMember wgFoundMember = businessService
					.findByAccountAndNode(foundMemberToDelete.getAccount().getUuid(), wgNode.getUuid());
			businessService.delete(wgFoundMember);
			saveLog(authUser, actor, LogAction.DELETE, wgFoundMember);
		}
		User user = userRepository.findByLsUuid(foundMemberToDelete.getAccount().getUuid());
		if (!actor.getLsUuid().equals(user.getLsUuid())) {
			notify(new DriveWarnDeletedMemberEmailContext(foundMemberToDelete, (User) actor, user));
		}
		return foundMemberToDelete;
	}

	@Override
	public List<SharedSpaceMember> deleteAllDriveMembers(Account authUser, Account actor, String sharedSpaceNodeUuid) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceNodeUuid, "Missing required sharedSpaceNodeUuid");
		List<SharedSpaceMember> foundMembersToDelete = businessService.findBySharedSpaceNodeUuid(sharedSpaceNodeUuid);
		if (foundMembersToDelete != null && !foundMembersToDelete.isEmpty()) {
			checkDeletePermission(authUser, actor, SharedSpaceMember.class,
					BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, foundMembersToDelete.get(0));
		}
		businessService.deleteAll(foundMembersToDelete);
		for (SharedSpaceMember member : foundMembersToDelete) {
			saveLog(authUser, actor, LogAction.DELETE, member);
		}
		return foundMembersToDelete;
	}
}
