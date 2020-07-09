/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.core.service.fragment.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DriveMemberBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
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
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;

import com.google.common.collect.Lists;

public class DriveMemberServiceImpl extends AbstractSharedSpaceMemberFragmentServiceImpl {

	private final DriveMemberBusinessService driveMemberBusinessService;

	public DriveMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			DriveMemberBusinessService driveMemberBusinessService,
			SharedSpaceNodeBusinessService nodeBusinessService,
			SharedSpaceRoleService roleService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository, roleService, nodeBusinessService, sanitizerInputHtmlBusinessService);
		this.driveMemberBusinessService = driveMemberBusinessService;
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		checkCreateMemberPermission(authUser, actor, node, context, account);
		return create(authUser, actor, node, context.getRole(), context.getNestedRole(), account);
	}
	
	/**
	 * @param authUser   {@link Account} the authenticated user
	 * @param actor      {@link Account} the actor of the action
	 * @param node       {@link SharedSpaceNode} Drive
	 * @param role       {@link SharedSpaceRole} The role of the Drive member,
	 *                   should be DRIVE
	 * @param nestedRole {@link SharedSpaceRole} The role of the member in the
	 *                   workgroups inside the DRIVE, should be WORKGROUP
	 * @param account {@link SharedSpaceAccount} the account of the added member
	 * @return {@link SharedSpaceMemberDrive}
	 * @throws BusinessException
	 */
	protected SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node, SharedSpaceRole role,
			SharedSpaceRole nestedRole, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		Validate.notNull(nestedRole, "Drive role must be set.");
		User newMember = userRepository.findByLsUuid(account.getUuid());
		if (newMember == null) {
			String message = String.format("The account with the UUID : %s is not existing", account.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkRoleTypeIntegrity(authUser, actor, role.getUuid(), node.getNodeType());
		checkRoleTypeIntegrity(authUser, actor, nestedRole.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceMemberDrive toAdd = driveMemberBusinessService.create(account, node, role, nestedRole);
		// Add the new member to all workgroups inside the drive
		List<SharedSpaceNode> nestedWorkgroups = nodeBusinessService.findByParentUuidAndType(node.getUuid());
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		for (SharedSpaceNode wgNode : nestedWorkgroups) {
			if (!memberExistsInNode(account.getUuid(), wgNode.getUuid())) {
				SharedSpaceMember wgMember = createWithoutCheckPermission(authUser, actor, wgNode, nestedRole, account);
				nestedMembers.add(wgMember);
			}
		}
		if (!actor.getLsUuid().equals(account.getUuid())) {
			notify(new DriveWarnNewMemberEmailContext(toAdd, actor, newMember, nestedMembers));
		}
		saveLogForCreateAndDelete(authUser, actor, LogAction.CREATE, toAdd, AuditLogEntryType.WORKGROUP_MEMBER);
		return toAdd;
	}

	@Override
	protected SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate,
			SharedSpaceMember foundMemberToUpdate, boolean force) {
		LightSharedSpaceRole wgRole = ((SharedSpaceMemberDrive) memberToUpdate).getNestedRole();
		Validate.notNull(wgRole, "The nested role must be set");
		checkRoleTypeIntegrity(authUser, actor, wgRole.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceMember updated = driveMemberBusinessService.update((SharedSpaceMemberDrive) foundMemberToUpdate,
				(SharedSpaceMemberDrive) memberToUpdate);
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		// No propagation if nested role is not updated
		if (!((SharedSpaceMemberDrive) updated).getNestedRole().equals(wgRole)) {
			// Update the member on all workgroups inside the drive
			if (force) {
				nestedMembers = businessService.findAllMembersByParentAndAccount(
						foundMemberToUpdate.getAccount().getUuid(), updated.getNode().getUuid());
			} else {
				nestedMembers = businessService.findAllMembersWithNoConflictedRoles(
						foundMemberToUpdate.getAccount().getUuid(), updated.getNode().getUuid(), wgRole.getUuid());
			}
			for (SharedSpaceMember wgFoundMember : nestedMembers) {
				SharedSpaceMember wgMemberToUpdate = new SharedSpaceMember(wgFoundMember);
				wgMemberToUpdate.setRole(wgRole);
				SharedSpaceMember updatedWgMember = businessService.update(wgFoundMember, wgMemberToUpdate);
				User user = userRepository.findByLsUuid(wgFoundMember.getAccount().getUuid());
				notify(new WorkGroupWarnUpdatedMemberEmailContext(updatedWgMember, user, actor));
				saveUpdateLog(authUser, actor, LogAction.UPDATE, wgFoundMember, updatedWgMember, AuditLogEntryType.WORKGROUP_MEMBER);
			}
		}
		User member = userRepository.findByLsUuid(foundMemberToUpdate.getAccount().getUuid());
		if (!actor.getLsUuid().equals(member.getLsUuid())) {
			notify(new DriveWarnUpdatedMemberEmailContext((SharedSpaceMemberDrive) foundMemberToUpdate, member, actor,
					nestedMembers));
		}
		saveUpdateLog(authUser, actor, LogAction.UPDATE, foundMemberToUpdate, updated, AuditLogEntryType.DRIVE_MEMBER);
		return updated;
	}

	@Override
	protected SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember foundMemberToDelete) {
		businessService.delete(foundMemberToDelete);
		saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, foundMemberToDelete, AuditLogEntryType.DRIVE_MEMBER);
		// Delete the member on all workgroups inside the drive
		List<SharedSpaceNodeNested> nestedWorkgroups = businessService.findAllByParentAndAccount(
				foundMemberToDelete.getAccount().getUuid(), foundMemberToDelete.getNode().getUuid());
		for (SharedSpaceNodeNested wgNode : nestedWorkgroups) {
			SharedSpaceMember wgFoundMember = businessService
					.findByAccountAndNode(foundMemberToDelete.getAccount().getUuid(), wgNode.getUuid());
			businessService.delete(wgFoundMember);
			saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, wgFoundMember, AuditLogEntryType.WORKGROUP_MEMBER);
		}
		User user = userRepository.findByLsUuid(foundMemberToDelete.getAccount().getUuid());
		if (!actor.getLsUuid().equals(user.getLsUuid())) {
			notify(new DriveWarnDeletedMemberEmailContext(foundMemberToDelete, (User) actor, user));
		}
		return foundMemberToDelete;
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node) {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required shared space node");
		Validate.isTrue(NodeType.DRIVE.equals(node.getNodeType()), "Node type need to be a Drive");
		List<SharedSpaceMember> foundMembersToDelete = businessService.findBySharedSpaceNodeUuid(node.getUuid());
		if (foundMembersToDelete == null || foundMembersToDelete.isEmpty()) {
			// There is no members on this Drive
			return null;
		}
		// We check the user has the right to delete members of this node
		// If he can delete one member, he can delete them all
		checkDeletePermission(authUser, actor, SharedSpaceMember.class,
				BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, foundMembersToDelete.get(0));
		driveMemberBusinessService.deleteAll(foundMembersToDelete);
		for (SharedSpaceMember member : foundMembersToDelete) {
			User user = userRepository.findByLsUuid(member.getAccount().getUuid());
			notify(new DriveWarnDeletedMemberEmailContext(member, (User) actor, user));
			saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, member, AuditLogEntryType.DRIVE_MEMBER);
		}
		return foundMembersToDelete;
	}

}
