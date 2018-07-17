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
package org.linagora.linshare.core.facade.webservice.user.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

public class SharedSpaceMemberFacadeImpl extends GenericFacadeImpl implements SharedSpaceMemberFacade {
	private final SharedSpaceMemberService sharedSpaceMemberService;

	private final SharedSpaceNodeService sharedSpaceNodeService;

	private final SharedSpaceRoleService sharedSpaceRoleService;

	private final UserService userService;

	public SharedSpaceMemberFacadeImpl(SharedSpaceMemberService sharedSpaceMemberService,
			AccountService accountService,
			SharedSpaceNodeService sharedSpaceNodeService,
			SharedSpaceRoleService sharedSpaceRoleService,
			UserService userService) {
		super(accountService);
		this.sharedSpaceMemberService = sharedSpaceMemberService;
		this.sharedSpaceNodeService = sharedSpaceNodeService;
		this.sharedSpaceRoleService = sharedSpaceRoleService;
		this.userService = userService;
	}

	public SharedSpaceMember find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required uuid");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return sharedSpaceMemberService.find(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceMember create(String actorUuid, SharedSpaceMember member) throws BusinessException {
		Validate.notNull(member, "Shared space member must be set.");
		Validate.notNull(member.getAccount(), "Account must be set.");
		Validate.notNull(member.getRole(), "Role must be set.");
		Validate.notNull(member.getNode(), "Node must be set.");
		Validate.notNull(member.getAccount().getUuid(), "Account uuid must be set.");
		Validate.notNull(member.getRole().getUuid(), "Role uuid must be set.");
		Validate.notNull(member.getNode().getUuid(), "Node uuid must be set.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceNode foundSharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, member.getNode().getUuid());
		SharedSpaceRole foundSharedSpaceRole = sharedSpaceRoleService.find(authUser, actor, member.getRole().getUuid());
		User foundUser = userService.findByLsUuid(member.getAccount().getUuid());
		Validate.notNull(foundUser, "Missing required user");
		Validate.notNull(foundSharedSpaceRole, "Missing required role");
		Validate.notNull(foundSharedSpaceNode, "Missing required node");
		GenericLightEntity nodeToPersist = new GenericLightEntity(foundSharedSpaceNode.getUuid(), foundSharedSpaceNode.getName());
		GenericLightEntity roleToPersist = new GenericLightEntity(foundSharedSpaceRole.getUuid(), foundSharedSpaceRole.getName());
//		if (!checkAccountNotInNode(authUser, actor, user, toFindNode)) {
//			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, "The user with uuid : "
//					+ user.getLsUuid() + " is already member of the node with uuid" + toFindNode.getUuid());
//		}
		SharedSpaceAccount sharedSpaceAccount = new SharedSpaceAccount(foundUser);
		GenericLightEntity accountLight = new GenericLightEntity(sharedSpaceAccount.getUuid(),
				sharedSpaceAccount.getName());
		SharedSpaceMember toAddMember = sharedSpaceMemberService.create(authUser, actor, nodeToPersist, roleToPersist, accountLight);
		return toAddMember;
	}
}
