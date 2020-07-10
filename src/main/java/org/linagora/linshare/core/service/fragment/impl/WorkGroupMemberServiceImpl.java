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
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnDeletedMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnNewMemberEmailContext;
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
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

public class WorkGroupMemberServiceImpl extends AbstractSharedSpaceMemberFragmentServiceImpl{

	public WorkGroupMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			SharedSpaceRoleService roleService,
			SharedSpaceNodeBusinessService nodeService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository, roleService, nodeService, sanitizerInputHtmlBusinessService);
	}

	@Override
	public SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		checkCreateMemberPermission(authUser, actor, node, context, account);
		User user = userRepository.findByLsUuid(account.getUuid());
		if (user == null) {
			String message = String.format("The account with the UUID : %s is not existing", account.getUuid());
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, message);
		}
		SharedSpaceMember member = createWithoutCheckPermission(authUser, actor, node, context.getRole(), account);
		if (!actor.getLsUuid().equals(account.getUuid())) {
			notify(new WorkGroupWarnNewMemberEmailContext(member, actor, user));
		}
		return member;
	}

	@Override
	protected SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate,
			SharedSpaceMember foundMemberToUpdate, boolean force) {
		SharedSpaceMember updated = new SharedSpaceMember();
		updated = businessService.update(foundMemberToUpdate, memberToUpdate);
		User user = userRepository.findByLsUuid(foundMemberToUpdate.getAccount().getUuid());
		notify(new WorkGroupWarnUpdatedMemberEmailContext(updated, user, actor));
		saveUpdateLog(authUser, actor, LogAction.UPDATE, foundMemberToUpdate, updated,
				AuditLogEntryType.WORKGROUP_MEMBER);
		return updated;
	}

	@Override
	protected SharedSpaceMember delete(Account authUser, Account actor, SharedSpaceMember foundMemberToDelete) {
		businessService.delete(foundMemberToDelete);
		saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, foundMemberToDelete, AuditLogEntryType.WORKGROUP_MEMBER);
		User user = userRepository.findByLsUuid(foundMemberToDelete.getAccount().getUuid());
		notify(new WorkGroupWarnDeletedMemberEmailContext(foundMemberToDelete, actor, user));
		return foundMemberToDelete;
	}

	@Override
	public List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node) {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required shared space node");
		Validate.isTrue(NodeType.WORK_GROUP.equals(node.getNodeType()), "Node type need to be a Workgroup");
		List<SharedSpaceMember> foundMembersToDelete = businessService.findBySharedSpaceNodeUuid(node.getUuid());
		if (foundMembersToDelete == null || foundMembersToDelete.isEmpty()) {
			// There is no members on this Workgroup
			return null;
		}
		// We check the user has the right to delete members of this node
		// If he can delete one member, he can delete them all
		checkDeletePermission(authUser, actor, SharedSpaceMember.class,
				BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN, foundMembersToDelete.get(0));
		businessService.deleteAll(foundMembersToDelete);
		for (SharedSpaceMember member : foundMembersToDelete) {
			User user = userRepository.findByLsUuid(member.getAccount().getUuid());
			notify(new WorkGroupWarnDeletedMemberEmailContext(member, actor, user));
			saveLogForCreateAndDelete(authUser, actor, LogAction.DELETE, member, AuditLogEntryType.WORKGROUP_MEMBER);
		}
		return foundMembersToDelete;
	}

}
