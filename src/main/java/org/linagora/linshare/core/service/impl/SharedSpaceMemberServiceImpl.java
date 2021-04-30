/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
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
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
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

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			Map<NodeType, SharedSpaceMemberFragmentService> sharedSpaceBuildingService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.sharedSpaceBuildingService = sharedSpaceBuildingService;
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
	 * Get all nested nodes on top level (with no parent) 
	 * @param withRole boolean if true, return the user role in the node
	 */
	@Override
	public List<SharedSpaceNodeNested> findAllNodesOnTopByAccount(Account authUser, Account actor, String accountUuid, boolean withRole, String parent) {
		preChecks(authUser, actor);
		Validate.notEmpty(accountUuid, "accountUuid must be set.");
		return businessService.findAllNestedNodeByAccountUuid(accountUuid, withRole, parent);
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
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node) {
		Validate.notNull(node, "shared space node must be set");
		Validate.notEmpty(node.getUuid(), "shared space node uuid must be set");
		SharedSpaceMemberFragmentService service = getService(node.getNodeType());
		return service.deleteAllMembers(authUser, actor, node);
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
		if (authUser.getRole().equals(Role.SUPERADMIN)) {
			return businessService.findAllNodesByParent(parentUuid);
		} else if (authUser.getRole().equals(Role.SIMPLE)) {
			return businessService.findAllByParentAndAccount(accountUuid, parentUuid);
		}
		throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				"You are not authorized to use this service !!");
	}

	private SharedSpaceMemberFragmentService getService(NodeType type) {
		Validate.notNull(type, "type must be set");
		SharedSpaceMemberFragmentService service = sharedSpaceBuildingService.get(type);
		Validate.notNull(service, "Node type not supported");
		return service;
	}
}
