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
package org.linagora.linshare.core.service.fragment.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.WorkSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.OnDeleteSharedSpaceContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkSpaceDeletedWarnEmailContext;
import org.linagora.linshare.core.notifications.context.WorkSpaceWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkSpaceWarnNewMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkSpaceWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
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
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

import com.google.common.collect.Lists;

public class WorkSpaceMemberServiceImpl extends AbstractSharedSpaceMemberFragmentServiceImpl {

	private final WorkSpaceMemberBusinessService workSpaceMemberBusinessService;

	public WorkSpaceMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			WorkSpaceMemberBusinessService workSpaceMemberBusinessService,
			SharedSpaceNodeBusinessService nodeBusinessService,
			SharedSpaceRoleService roleService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository, roleService, nodeBusinessService, sanitizerInputHtmlBusinessService);
		this.workSpaceMemberBusinessService = workSpaceMemberBusinessService;
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
	 * @param node       {@link SharedSpaceNode} WorkSpace
	 * @param role       {@link SharedSpaceRole} The role of the WorkSpace member,
	 *                   should be WORK_SPACE
	 * @param nestedRole {@link SharedSpaceRole} The role of the member in the
	 *                   workgroups inside the WORK_SPACE, should be WORK_GROUP
	 * @param account {@link SharedSpaceAccount} the account of the added member
	 * @return {@link SharedSpaceMemberDrive}
	 * @throws BusinessException
	 */
	protected SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node, SharedSpaceRole role,
			SharedSpaceRole nestedRole, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		Validate.notNull(nestedRole, "WorkSpace role must be set.");
		User newMember = userRepository.findByLsUuid(account.getUuid());
		if (newMember == null) {
			String message = String.format("The account with the UUID : %s is not existing", account.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkRoleTypeIntegrity(authUser, actor, role.getUuid(), node.getNodeType());
		checkRoleTypeIntegrity(authUser, actor, nestedRole.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceMemberDrive toAdd = workSpaceMemberBusinessService.create(account, node, role, nestedRole);
		// Add the new member to all workgroups inside the WorkSpace
		List<SharedSpaceNode> nestedWorkgroups = nodeBusinessService.findByParentUuidAndType(node.getUuid());
		for (SharedSpaceNode wgNode : nestedWorkgroups) {
			if (!memberExistsInNode(account.getUuid(), wgNode.getUuid())) {
				createWithoutCheckPermission(authUser, actor, wgNode, nestedRole, account);
			}
		}
		if (!actor.getLsUuid().equals(account.getUuid())) {
			List<SharedSpaceMember> nestedMembers = businessService.findLastFiveUpdatedNestedWorkgroups(node.getUuid(),
					newMember.getLsUuid());
			notify(new WorkSpaceWarnNewMemberEmailContext(toAdd, actor, newMember, nestedMembers));
		}
		saveLogForCreateAndDelete(authUser, actor, LogAction.CREATE, toAdd, AuditLogEntryType.WORK_SPACE_MEMBER);
		return toAdd;
	}

	@Override
	protected SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate,
			SharedSpaceMember foundMemberToUpdate, boolean force, Boolean propagate) {
		LightSharedSpaceRole wgRole = ((SharedSpaceMemberDrive) memberToUpdate).getNestedRole();
		Validate.notNull(wgRole, "The nested role must be set");
		checkRoleTypeIntegrity(authUser, actor, wgRole.getUuid(), NodeType.WORK_GROUP);
		SharedSpaceMember updated = workSpaceMemberBusinessService.update((SharedSpaceMemberDrive) foundMemberToUpdate,
				(SharedSpaceMemberDrive) memberToUpdate);
		List<SharedSpaceMember> nestedMembers = Lists.newArrayList();
		// No propagation if nested role is not updated
		if (!((SharedSpaceMemberDrive) updated).getNestedRole().equals(wgRole)) {
			// Update the member on all workgroups inside the WorkSpace
			if (force) {
				nestedMembers = businessService.findAllMembersByParentAndAccount(
						foundMemberToUpdate.getAccount().getUuid(), updated.getNode().getUuid());
			} else if (propagate) {
				nestedMembers = businessService.findAllMembersByParentAndAccountAndPristine(
						foundMemberToUpdate.getAccount().getUuid(), updated.getNode().getUuid(), true);
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
			notify(new WorkSpaceWarnUpdatedMemberEmailContext((SharedSpaceMemberDrive) foundMemberToUpdate, member, actor,
					nestedMembers));
		}
		saveUpdateLog(authUser, actor, LogAction.UPDATE, foundMemberToUpdate, updated, AuditLogEntryType.WORK_SPACE_MEMBER);
		return updated;
	}

	@Override
	protected SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember foundMemberToDelete) {
		businessService.delete(foundMemberToDelete);
		saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, foundMemberToDelete, AuditLogEntryType.WORK_SPACE_MEMBER);
		// Delete the member on all workgroups inside the WorkSpace
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
			notify(new WorkSpaceWarnDeletedMemberEmailContext(foundMemberToDelete, actor, user));
		}
		return foundMemberToDelete;
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node,
			LogActionCause cause, List<SharedSpaceNodeNested> nodes) {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required shared space node");
		Validate.isTrue(NodeType.WORK_SPACE.equals(node.getNodeType()), "Node type need to be a WorkSpace");
		List<SharedSpaceMember> foundMembersToDelete = businessService.findBySharedSpaceNodeUuid(node.getUuid());
		if (foundMembersToDelete == null || foundMembersToDelete.isEmpty()) {
			// There is no members on this WorkSpace
			return null;
		}
		// We check the user has the right to delete members of this node WorkSpace
		// If he can delete one WorkSpace member, he can delete them all
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMembersToDelete.get(0));
		OnDeleteSharedSpaceContainer container = new OnDeleteSharedSpaceContainer();
		for (SharedSpaceMember member : foundMembersToDelete) {
			User user = userRepository.findByLsUuid(member.getAccount().getUuid());
			SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, LogAction.DELETE,
					AuditLogEntryType.WORK_SPACE_MEMBER, member);
			if (!actor.getLsUuid().equals(member.getAccount().getUuid())) {
				EmailContext context = null;
				if (LogActionCause.WORK_SPACE_DELETION.equals(cause)) {
					context = new WorkSpaceDeletedWarnEmailContext(actor, user.getDomain(), member, nodes);
				} else {
					context = new WorkSpaceWarnDeletedMemberEmailContext(member, actor, user);
				}
				container.addMailContainersAddEmail(mailBuildingService.build(context));
			}
			businessService.addMembersToRelatedAccountsAndRelatedDomains(member.getNode().getUuid(), log);
			if (Objects.nonNull(cause)) {
				log.setCause(cause);
			}
			container.addLog(log);
		}
		workSpaceMemberBusinessService.deleteAll(foundMembersToDelete);
		notifierService.sendNotification(container.getMailContainers());
		logEntryService.insert(container.getLogs());
		return foundMembersToDelete;
	}
}
