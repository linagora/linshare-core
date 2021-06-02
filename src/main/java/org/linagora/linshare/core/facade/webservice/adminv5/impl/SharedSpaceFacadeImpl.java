/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.SharedSpaceFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.utils.PageContainer;

import com.google.common.base.Strings;

public class SharedSpaceFacadeImpl extends AdminGenericFacadeImpl implements SharedSpaceFacade {

	private final SharedSpaceNodeService nodeService;
	
	private final SharedSpaceMemberService memberService;
	
	private final AuditLogEntryService auditLogEntryService;

	public SharedSpaceFacadeImpl(AccountService accountService,
			SharedSpaceNodeService nodeService,
			SharedSpaceMemberService memberService,
			AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.nodeService = nodeService;
		this.memberService = memberService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public SharedSpaceNode find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return nodeService.find(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceNode create(String actorUuid, SharedSpaceNode node) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(node, "Missing required input shared space node.");
		SharedSpaceNode toCreate = new SharedSpaceNode(node.getName(), node.getParentUuid(), node.getNodeType(),
				node.getVersioningParameters(), node.getDescription(), new SharedSpaceAccount(actor));
		return nodeService.create(authUser, actor, toCreate);
	}

	@Override
	public SharedSpaceNode delete(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			node = nodeService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(node, "node must be set");
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.delete(authUser, actor, node);
	}

	@Override
	public SharedSpaceNode updatePartial(String actorUuid, PatchDto patchNode, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(patchNode, "Missind required input patch node.");
		Validate.notNull(patchNode.getName(), "The name of the attribute to update must be set .");
		Validate.notNull(patchNode.getValue(),"The value of the attribute to update must be set.");
		patchNode.setUuid(uuid);
		return nodeService.updatePartial(authUser, actor, patchNode);
	}
	
	@Override
	public SharedSpaceNode update(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(node, "Missind required input shared space node.");
		if (!Strings.isNullOrEmpty(uuid)) {
			node.setUuid(uuid);
		} else {
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.update(authUser, actor, node);
	}

	@Override
	public List<SharedSpaceMember> members(String actorUuid, String uuid, String accountUuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Validate.notEmpty(uuid, "Missing required shared space node");
		return nodeService.findAllMembers(authUser, authUser, uuid, accountUuid);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllMyNodes(String actorUuid, boolean withRole, String parent) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(parent)) {
			SharedSpaceNode node = nodeService.find(authUser, actor, parent);
			if (!NodeType.DRIVE.equals(node.getNodeType())) {
				throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
						"The requested shared space is not supported");
			}
		} else {
			parent = null;
		}
		return memberService.findAllSharedSpacesByAccountAndParent(authUser, actor, actor.getLsUuid(), withRole, parent);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAll(String actorUuid, String accountUuid, Integer pageNumber,
			Integer pageSize) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		User actor = getActor(authUser, actorUuid);
		PageContainer<SharedSpaceNodeNested> container = new PageContainer<SharedSpaceNodeNested>(pageNumber, pageSize);
		Account account = null;
		if (!Strings.isNullOrEmpty(accountUuid)) {
			account = accountService.findByLsUuid(accountUuid);
		}
		return nodeService.findAll(authUser, actor, account, container);
	}

	@Override
	public Set<AuditLogEntryUser> findAllSharedSpaceAudits(String sharedSpaceUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate, String resourceUuid) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(sharedSpaceUuid, "shared space uuid required");
		User actor = (User) getActor(authUser, null);
		SharedSpaceNode sharedSpace = nodeService.find(authUser, actor, sharedSpaceUuid);
		return auditLogEntryService.findAllSharedSpaceAudits(authUser, actor, sharedSpace.getUuid(), resourceUuid, actions, types, beginDate, endDate);
	}
}
