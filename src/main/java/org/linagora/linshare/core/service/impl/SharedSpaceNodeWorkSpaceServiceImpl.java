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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.service.fragment.impl.AbstractSharedSpaceFragmentServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

import com.google.common.collect.Lists;

public class SharedSpaceNodeWorkSpaceServiceImpl extends AbstractSharedSpaceFragmentServiceImpl {

	public SharedSpaceNodeWorkSpaceServiceImpl(
			AbstractResourceAccessControl<Account, Account, SharedSpaceNode> rac,
			SharedSpaceNodeBusinessService businessService,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService,
			SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService,
			ThreadService threadService,
			ThreadRepository threadRepository,
			FunctionalityReadOnlyService functionalityService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService,
			SharedSpaceMemberBusinessService memberWorkSpaceService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, businessService, memberBusinessService, memberService, ssRoleService, logEntryService, threadService,
				threadRepository, functionalityService, accountQuotaBusinessService, workGroupNodeService, memberWorkSpaceService, sanitizerInputHtmlBusinessService);
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORKSPACE_FORBIDDEN, node);
		if (!(NodeType.WORK_SPACE.equals(node.getNodeType()))) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not create this kind of sharedSpace with this method.");
		}
		node.setName(sanitize(node.getName()));
		node.setDomainUuid(actor.getDomainId());
		node.setAuthor((actor.isSystem()) ? new SharedSpaceAccount(actor) : new SharedSpaceAccount((User) actor));
		SharedSpaceNode created = super.create(authUser, actor, node);
		// TODO: workaround to avoid to rely on account type
		if (!(actor instanceof SystemAccount)) {
			SharedSpaceRole workSpaceRole = ssRoleService.getWorkSpaceAdmin(authUser, actor);
			SharedSpaceRole workGroupRole = ssRoleService.getAdmin(authUser, actor);
			SharedSpaceMemberContext context = new SharedSpaceMemberContext(workSpaceRole, workGroupRole);
			memberService.create(authUser, actor, created, context, new SharedSpaceAccount((User) actor));
		}
		return created;
	}

	@Override
	public SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "missing required node to delete.");
		Validate.notEmpty(node.getUuid(), "missing required node uuid to delete");
		SharedSpaceNode foundedNodeToDel = find(authUser, actor, node.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORKSPACE_FORBIDDEN,
				foundedNodeToDel);
		List<SharedSpaceNodeNested> nodes = findAllWorkgroupsInNode(authUser, actor, foundedNodeToDel);
		List<AuditLogEntryUser> logs = deleteNestedWorkgroups(authUser, actor, nodes);
		memberService.deleteAllMembers(authUser, actor, foundedNodeToDel, LogActionCause.WORK_SPACE_DELETION, nodes);
		businessService.delete(foundedNodeToDel);
		SharedSpaceNodeAuditLogEntry workSpaceLog = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.WORK_SPACE, foundedNodeToDel);
		logs.add(workSpaceLog);
		logEntryService.insert(logs);
		return foundedNodeToDel;
	}

	private List<AuditLogEntryUser> deleteNestedWorkgroups(Account authUser, Account actor, List<SharedSpaceNodeNested> nodes) {
		List<AuditLogEntryUser> logs = Lists.newArrayList();
		for (SharedSpaceNodeNested nested : nodes) {
			WorkGroup workGroup = threadService.find(authUser, authUser, nested.getUuid());
			threadService.deleteThread(authUser, authUser, workGroup);
			SharedSpaceNode foundNestedWgToDelete = find(authUser, actor, nested.getUuid());
			memberService.deleteAllMembers(authUser, actor, foundNestedWgToDelete, LogActionCause.WORK_SPACE_DELETION,
					null);
			SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.DELETE,
					AuditLogEntryType.WORKGROUP, foundNestedWgToDelete);
			businessService.delete(foundNestedWgToDelete);
			log.setCause(LogActionCause.WORK_SPACE_DELETION);
			logs.add(log);
		}
		return logs;
	}
}
