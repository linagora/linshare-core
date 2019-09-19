/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2019 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
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
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.core.service.impl.GenericServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;

public abstract class AbstractSharedSpaceMemberFragmentServiceImpl extends GenericServiceImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberFragmentService {

	protected static final String AUDIT_MEMBER = "_MEMBER";

	protected final SharedSpaceMemberBusinessService businessService;

	protected final LogEntryService logEntryService;

	protected final UserRepository<User> userRepository;

	protected final NotifierService notifierService;

	protected final MailBuildingService mailBuildingService;

	public AbstractSharedSpaceMemberFragmentServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository) {
		super(rac);
		this.businessService = businessService;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	protected void checkCreateMemberPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceMemberContext context, SharedSpaceAccount account) throws BusinessException {
		Validate.notNull(context.getRole(), "Role must be set.");
		Validate.notNull(node, "Node uuid must be set.");
		checkCreatePermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null, node);
		if (!checkMemberNotInNode(account.getUuid(), node.getUuid())) {
			String message = String.format(
					"The account with the UUID : %s is already a member of the node with the uuid : %s",
					account.getUuid(), node.getUuid());
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, message);
		}
	}

	protected boolean checkMemberNotInNode(String possibleMemberUuid, String nodeUuid) {
		return businessService.findByAccountAndNode(possibleMemberUuid, nodeUuid) == null;
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

	protected SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, SharedSpaceAccount account) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(role, "Role must be set.");
		Validate.notNull(node, "Node must be set.");
		SharedSpaceMember memberWg = new SharedSpaceMember(new SharedSpaceNodeNested(node),
				new GenericLightEntity(role.getUuid(), role.getName()), account);
		String parentUuid = node.getParentUuid();
		boolean isDriveMember = (parentUuid != null) && (!checkMemberNotInNode(account.getUuid(), parentUuid));
		memberWg.setNested(isDriveMember);
		SharedSpaceMember toAdd = businessService.create(memberWg);
		saveLogForCreateAndDelete(authUser, actor, LogAction.CREATE, toAdd, AuditLogEntryType.WORKGROUP_MEMBER);
		return toAdd;
	}

	@Override
	public SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, boolean force) {
		preChecks(authUser, actor);
		Validate.notNull(memberToUpdate, "Missing required member to update");
		Validate.notNull(memberToUpdate.getUuid(), "Missing required member uuid to update");
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
