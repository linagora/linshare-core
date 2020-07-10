/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.fragment.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.core.service.impl.GenericServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberWorkgroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;

public abstract class AbstractSharedSpaceMemberFragmentServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberFragmentService {

	protected static final String AUDIT_MEMBER = "_MEMBER";

	protected final SharedSpaceMemberBusinessService businessService;

	protected final LogEntryService logEntryService;

	protected final UserRepository<User> userRepository;

	protected final NotifierService notifierService;

	protected final MailBuildingService mailBuildingService;

	protected final SharedSpaceRoleService roleService;
	
	protected final SharedSpaceNodeBusinessService nodeBusinessService;

	public AbstractSharedSpaceMemberFragmentServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			SharedSpaceRoleService roleService,
			SharedSpaceNodeBusinessService nodeBusinessService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.roleService = roleService;
		this.nodeBusinessService = nodeBusinessService;
	}

	protected void checkCreateMemberPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		Validate.notNull(context.getRole(), "Role must be set.");
		Validate.notNull(node, "Node uuid must be set.");
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null, node);
		if (memberExistsInNode(account.getUuid(), node.getUuid())) {
			String message = String.format(
					"The account with the UUID : %s is already a member of the node with the uuid : %s",
					account.getUuid(), node.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, message);
		}
	}

	protected boolean memberExistsInNode(String accountUuid, String nodeUuid) {
		return businessService.findByAccountAndNode(accountUuid, nodeUuid) != null;
	}

	protected void notify(EmailContext context) {
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
	}
	/**
	 * Save audit log for create and delete actions.
	 * 
	 * @param authUser
	 * @param actor
	 * @param action
	 * @param resource
	 * @param auditType
	 * @return SharedSpaceMemberAuditLogEntry
	 */
	protected SharedSpaceMemberAuditLogEntry saveLogForCreateAndDelete(Account authUser, Account actor, LogAction action,
			SharedSpaceMember resource, AuditLogEntryType auditType) {
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, action,
				auditType, resource);
		List<String> members = businessService.findMembersUuidBySharedSpaceNodeUuid(resource.getNode().getUuid());
		log.addRelatedAccounts(members);
		logEntryService.insert(log);
		return log;
	}

	/**
	 * Save audit log for update actions
	 * 
	 * @param authUser
	 * @param actor
	 * @param action
	 * @param resource
	 * @param resourceUpdated
	 * @param auditType
	 * @return SharedSpaceMemberAuditLogEntry
	 */
	protected SharedSpaceMemberAuditLogEntry saveUpdateLog(Account authUser, Account actor, LogAction action,
			SharedSpaceMember resource, SharedSpaceMember resourceUpdated, AuditLogEntryType auditType) {
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(authUser, actor, action, auditType,
				resource);
		log.setResourceUpdated(resourceUpdated);
		List<String> members = businessService.findMembersUuidBySharedSpaceNodeUuid(resource.getNode().getUuid());
		log.addRelatedAccounts(members);
		logEntryService.insert(log);
		return log;
	}
	
	/**
	 * Method to add a member in a workgroup without check permission of the actor
	 * @param authUser {@link Account} the authenticated user
	 * @param actor    {@link Account} the actor of the action
	 * @param node     {@link SharedSpaceNode} Workgroup
	 * @param role     {@link SharedSpaceRole} role of the new member
	 * @param account  {@link SharedSpaceAccount} the account of the added member
	 * @return {@link SharedSpaceMemberWorkgroup}
	 * @throws BusinessException
	 */
	protected SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		checkRoleTypeIntegrity(authUser, actor, role.getUuid(), node.getNodeType());
		SharedSpaceMember memberWg = new SharedSpaceMemberWorkgroup(new SharedSpaceNodeNested(node),
				new LightSharedSpaceRole(role), account);
		String parentUuid = node.getParentUuid();
		/**
		 * If the member is added to nested workgroup, set the [nested] field to true
		 */
		boolean isInNestedWorkgroup = (parentUuid != null) && (!memberExistsInNode(account.getUuid(), node.getUuid()));
		memberWg.setNested(isInNestedWorkgroup);
		SharedSpaceMember toAdd = businessService.create(memberWg);
		saveLogForCreateAndDelete(authUser, actor, LogAction.CREATE, toAdd, AuditLogEntryType.WORKGROUP_MEMBER);
		return toAdd;
	}
	
	/**
	 * Check that the updated role type corresponds to the right node type of the member
	 * @param authUser
	 * @param actor
	 * @param roleUuid
	 * @param type {@link NodeType} 
	 */
	protected void checkRoleTypeIntegrity(Account authUser, Account actor, String roleUuid, NodeType type) {
		List<SharedSpaceRole> roles = roleService.findRolesByNodeType(authUser, actor, type);
		SharedSpaceRole role = roleService.find(authUser, actor, roleUuid);
		if (!roles.toString().contains(role.toString())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
					"Please verify the shared space member's role");
		}
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, boolean force) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
		SharedSpaceNode foundSharedSpaceNode = nodeBusinessService.find(memberToUpdate.getNode().getUuid());
		checkRoleTypeIntegrity(authUser, actor, memberToUpdate.getRole().getUuid(), foundSharedSpaceNode.getNodeType());
		SharedSpaceMember foundMemberToUpdate = businessService.findByAccountAndNode(memberToUpdate.getAccount().getUuid(),
				memberToUpdate.getNode().getUuid());
		if (foundMemberToUpdate == null) {
			String message = String.format(
					"The account with the UUID : %s is not a member of the node with the uuid : %s", memberToUpdate.getAccount().getUuid(),
					memberToUpdate.getNode().getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		return update(authUser, actor, memberToUpdate, foundMemberToUpdate, force);
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notNull(uuid, "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = businessService.find(uuid);
		if (foundMemberToDelete == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND,
					"The Shared space member with uuid : " + uuid + " is not found");
		}
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		return delete(authUser, actor, foundMemberToDelete);
	}

	protected abstract SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember foundMemberToDelete);

	protected abstract SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, SharedSpaceMember foundMemberToUpdate,
			boolean force);

}
