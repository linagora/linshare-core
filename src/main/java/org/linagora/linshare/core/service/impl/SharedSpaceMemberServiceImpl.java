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
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

public class SharedSpaceMemberServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberService {
	private final SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;

	private final SharedSpaceNodeBusinessService nodeBusinessService;

	private final SharedSpaceRoleBusinessService roleBusinessService;

	private final UserService userService;

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			SharedSpaceNodeBusinessService nodeBusinessService, SharedSpaceRoleBusinessService roleBusinessService,
			UserService userService, SharedSpaceMemberResourceAccessControl rac) {
		super(rac);
		this.sharedSpaceMemberBusinessService = sharedSpaceMemberBusinessService;
		this.nodeBusinessService = nodeBusinessService;
		this.roleBusinessService = roleBusinessService;
		this.userService = userService;
	}

	@Override
	public SharedSpaceMember find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space member uuid");
		SharedSpaceMember toFind = sharedSpaceMemberBusinessService.find(uuid);
		if (toFind == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND,
					"The Shared space member with uuid : " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				toFind);
		return toFind;
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, String accountUuid, String roleUuid,
			String nodeUuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(accountUuid, "Account uuid must be set.");
		Validate.notNull(roleUuid, "Role uuid must be set.");
		Validate.notNull(nodeUuid, "Node uuid must be set.");
		SharedSpaceNode toFindNode = nodeBusinessService.find(nodeUuid);
		Validate.notNull(toFindNode, "Node is not found.");
		GenericLightEntity nodeToPersist = new GenericLightEntity(nodeUuid, toFindNode.getName());
		SharedSpaceRole toFindRole = roleBusinessService.find(roleUuid);
		Validate.notNull(toFindRole, "Role in not found");
		GenericLightEntity roleToPersist = new GenericLightEntity(roleUuid, toFindRole.getName());
		User user = userService.findByLsUuid(accountUuid);
		Validate.notNull(user, "Missing required user");
		if (!checkAccountNotInNode(authUser, actor, user, toFindNode)) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, "The user with uuid : "
					+ user.getLsUuid() + " is already member of the node with uuid" + toFindNode.getUuid());
		}
		SharedSpaceAccount sharedSpaceAccount = new SharedSpaceAccount(user);
		GenericLightEntity accountLight = new GenericLightEntity(sharedSpaceAccount.getUuid(),
				sharedSpaceAccount.getName());
		sharedSpaceAccount.setUuid(user.getLsUuid());
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountLight);
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null);
		SharedSpaceMember toAdd = sharedSpaceMemberBusinessService.create(member);
		return toAdd;
	}

	@Override
	public SharedSpaceMember findMember(Account authUser, Account actor, Account possibleMember,
			SharedSpaceNode sharedSpaceNode) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(possibleMember, "possibleMember must be set.");
		Validate.notNull(sharedSpaceNode, "sharedSpaceNode must be set.");
		SharedSpaceMember foundMember = sharedSpaceMemberBusinessService
				.findByMemberAndSharedSpaceNode(possibleMember.getLsUuid(), sharedSpaceNode.getUuid());
		checkReadPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMember);
		return foundMember;
	}

	private boolean checkAccountNotInNode(Account authUser, Account actor, Account possibleMember,
			SharedSpaceNode sharedSpaceNode) throws BusinessException {
		return findMember(authUser, actor, possibleMember, sharedSpaceNode) == null;
	}

	@Override
	public SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember memberToDelete) {
		preChecks(authUser, actor);
		Validate.notNull(memberToDelete, "Missing required member to delete");
		Validate.notNull(memberToDelete.getUuid(), "Missing required member uuid to delete");
		SharedSpaceMember foundMemberToDelete = find(authUser, actor, memberToDelete.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToDelete);
		sharedSpaceMemberBusinessService.delete(foundMemberToDelete);
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
		return sharedSpaceMemberBusinessService.update(foundMemberToUpdate, memberToUpdate);
	}

	@Override
	public SharedSpaceMember updateRole(Account authUser, Account actor, String sharedSpaceMemberUuid, String roleUuid) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceMemberUuid, "Missing required sharedSpaceMemberUuid");
		Validate.notNull(roleUuid, "Missing required roleUuid");
		SharedSpaceMember foundMemberToUpdate = find(authUser, actor, sharedSpaceMemberUuid);
		SharedSpaceRole foundRole = roleBusinessService.find(roleUuid);
		checkUpdatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				foundMemberToUpdate);
		return sharedSpaceMemberBusinessService.updateRole(foundMemberToUpdate, new GenericLightEntity(foundRole.getUuid(), foundRole.getName()));
	}

	@Override
	public List<SharedSpaceMember> findAll(Account authUser, Account actor, String shareSpaceNodeUuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(shareSpaceNodeUuid, "Missing required shared space node uuid");
		checkListPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null);
		List<SharedSpaceMember> foundMembers = sharedSpaceMemberBusinessService
				.findBySharedSpaceNodeUuid(shareSpaceNodeUuid);
		return foundMembers;
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid) {
		preChecks(authUser, actor);
		Validate.notNull(sharedSpaceNodeUuid, "Missing required sharedSpaceNodeUuid");
		// TODO Check the user is admin to delete all Members
		List<SharedSpaceMember> foundMembersToDelete = findAll(authUser, actor, sharedSpaceNodeUuid);
		sharedSpaceMemberBusinessService.deleteAll(foundMembersToDelete);
		return foundMembersToDelete;
	}

}
