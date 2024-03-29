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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedSpaceMemberServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberService {

	protected static final String AUDIT_MEMBER = "_MEMBER";

	protected final SharedSpaceMemberBusinessService businessService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SharedSpaceMemberServiceImpl.class);

	protected final LogEntryService logEntryService;

	protected final UserRepository<User> userRepository;

	protected final NotifierService notifierService;

	protected final MailBuildingService mailBuildingService;

	protected final Map<NodeType, SharedSpaceMemberFragmentService> sharedSpaceBuildingService;

	protected  final DomainPermissionBusinessService domainPermissionBusinessService;

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
										NotifierService notifierService,
										MailBuildingService mailBuildingService,
										SharedSpaceMemberResourceAccessControl rac,
										LogEntryService logEntryService,
										UserRepository<User> userRepository,
										Map<NodeType, SharedSpaceMemberFragmentService> sharedSpaceBuildingService,
										SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
										DomainPermissionBusinessService domainPermissionBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.sharedSpaceBuildingService = sharedSpaceBuildingService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
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
	public SharedSpaceMember findMemberByNodeAndUuid(Account authUser, Account actor, String nodeUuid, String uuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "memberUuid must be set.");
		Validate.notEmpty(nodeUuid, "nodeUuid must be set.");
		SharedSpaceMember foundMember = businessService.findByNodeAndUuid(nodeUuid, uuid);
		if (foundMember == null) {
			String message = String.format(
					"The member with the UUID : %s is not a member of the node with the uuid : %s", uuid, nodeUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, message);
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
	}

	@Override
	public SharedSpaceMember findMemberByAccountUuid(Account authUser, Account actor, String userUuid, String nodeUuid)
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

	/**
	 * Get all SharedSpaces for one account.
	 * @param withRole : boolean if true, return the user role in the node
	 * @param parent : the list can be filtered by parentUuid (NullAble.)
	 * @param types : the list of node types you want to get (NullAble.)
	 */
	@Override
	public List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParentForUsers(Account authUser, Account actor, String accountUuid, boolean withRole, String parent, Set<NodeType> types) {
		preChecks(authUser, actor);
		Validate.notEmpty(accountUuid, "accountUuid must be set.");
		return businessService.findAllSharedSpacesByAccountAndParentForUsers(accountUuid, withRole, parent, types);
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
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(role);
		return create(authUser, actor, node, context, account);
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		Validate.notNull(node, "shared space node must be set");
		SharedSpaceMemberFragmentService service = getService(node.getNodeType());
		return service.create(authUser, actor, node, context, account);
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "shared space member uuid must be set");
		SharedSpaceMember foundMemberToDelete = businessService.find(uuid);
		Validate.notNull(foundMemberToDelete, "shared space member not found");
		SharedSpaceMemberFragmentService service = getService(foundMemberToDelete.getNode().getNodeType());
		return service.delete(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate) {
		return update(authUser, actor, memberToUpdate, false, false);
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, boolean force, Boolean propagate) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		SharedSpaceMember foundMemberToUpdate = null;
		/**
		 * TODO To ensure API compatibility, we fix the current api and we add a
		 * fallback to ensure old API usage is still working properly. At least ui-user
		 * 2.3.x is using it..
		 */
		try {
			foundMemberToUpdate = find(authUser, actor, memberToUpdate.getUuid());
		} catch (BusinessException e) {
			Validate.notNull(memberToUpdate.getAccount(), "You must set the account.");
			LOGGER.info("This is just a fallback to ensure old API usage : {}", e.getMessage());
			foundMemberToUpdate = findMemberByAccountUuid(authUser, actor, memberToUpdate.getAccount().getUuid(),
					memberToUpdate.getNode().getUuid());
		}
		SharedSpaceMemberFragmentService service = getService(foundMemberToUpdate.getNode().getNodeType());
		return service.update(authUser, actor, memberToUpdate, force, propagate);
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node, LogActionCause cause, List<SharedSpaceNodeNested> nodes) {
		Validate.notNull(node, "shared space node must be set");
		Validate.notEmpty(node.getUuid(), "shared space node uuid must be set");
		SharedSpaceMemberFragmentService service = getService(node.getNodeType());
		return service.deleteAllMembers(authUser, actor, node, cause, nodes);
	}

	@Override
	public List<SharedSpaceMember> deleteAllUserMemberships(Account authUser, Account actor, String userUuid) {
		preChecks(authUser, actor);
		Validate.notNull(userUuid, "Missing required sharedSpaceNodeUuid");
		List<SharedSpaceMember> foundMembersToDelete = businessService.findAllUserMemberships(userUuid);
		for (SharedSpaceMember member : foundMembersToDelete) {
			delete(authUser, actor, member.getUuid());
		}
		return foundMembersToDelete;
	}

	@Override
	public List<SharedSpaceMember> findAllUserMemberships(Account authUser, Account actor) {
		preChecks(authUser, actor);
		return businessService.findAllUserMemberships(actor.getLsUuid());
	}

	@Override
	public List<SharedSpaceNodeNested> findAllWorkGroupsInNode(Account authUser, Account actor, String parentUuid, String accountUuid){
		preChecks(authUser, actor);
		Validate.notEmpty(parentUuid, "ParentUuid must be set");
		Validate.notEmpty(accountUuid, "AccountUuid must be set");
		if (authUser.getRole().equals(Role.SUPERADMIN) ||
				(authUser.getRole().equals(Role.ADMIN) && isManager(actor, accountUuid))) {
			return businessService.findAllNodesByParent(parentUuid);
		} else if (authUser.getRole().equals(Role.SIMPLE)) {
			return businessService.findAllByParentAndAccount(accountUuid, parentUuid);
		}
		throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				"You are not authorized to use this service !!");
	}

	private boolean isManager(Account actor, String accountUuid) {
		SharedSpaceMember sharedSpaceMember = businessService.find(accountUuid);
		return sharedSpaceMember == null ||
				sharedSpaceMember.getNode() == null ||
				domainPermissionBusinessService.isAdminForThisDomain(actor, sharedSpaceMember.getNode().getDomainUuid());
	}

	private SharedSpaceMemberFragmentService getService(NodeType type) {
		Validate.notNull(type, "type must be set");
		SharedSpaceMemberFragmentService service = sharedSpaceBuildingService.get(type);
		Validate.notNull(service, "Node type not supported");
		return service;
	}

	@Override
	public List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnActiveMembers(Account authUser, Account actor, String nodeUuid, String pattern) {
		preChecks(authUser, actor);
		Validate.notEmpty(nodeUuid, "SharedSpace node uuid must be set");
		Validate.notEmpty(pattern, "pattern must be set");
		// Just to be sure the current actor is part of the shared space.
		findMemberByAccountUuid(authUser, actor, actor.getLsUuid(), nodeUuid);
		return businessService.autocompleteOnActiveMembers(nodeUuid, pattern);
	}

	@Override
	public List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnAssetAuthor(Account authUser, Account actor,
			String nodeUuid, String pattern) {
		preChecks(authUser, actor);
		Validate.notEmpty(nodeUuid, "SharedSpace node uuid must be set");
		Validate.notEmpty(pattern, "pattern must be set");
		// Just to be sure the current actor is part of the shared space.
		findMemberByAccountUuid(authUser, actor, actor.getLsUuid(), nodeUuid);
		return businessService.autocompleteOnAssetAuthor(nodeUuid, pattern);
	}

}
