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
package org.linagora.linshare.core.ldap.service.impl;


import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.ldap.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnNewMemberEmailContext;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnUpdatedMemberEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;

public class SharedSpaceMemberServiceImpl extends org.linagora.linshare.core.service.impl.SharedSpaceMemberServiceImpl
		implements SharedSpaceMemberService {

	private final SharedSpaceMemberBusinessService businessService;

	public SharedSpaceMemberServiceImpl(SharedSpaceMemberBusinessService businessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			SharedSpaceMemberResourceAccessControl rac,
			LogEntryService logEntryService,
			UserRepository<User> userRepository,
			Map<NodeType, SharedSpaceMemberFragmentService> sharedSpaceBuildingService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository,
				sharedSpaceBuildingService, sanitizerInputHtmlBusinessService, domainPermissionBusinessService);
		this.businessService = businessService;
	}

	@Override
	public SharedSpaceLDAPGroupMember create(Account actor, SharedSpaceLDAPGroupMember member) {
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(member, "The member must be set");
		Validate.notEmpty(member.getUuid(), "The uuid of the LDAP group member must be set");
		Validate.notNull(member.getAccount(), "The account of the LDAP group member must be set");
		Validate.notEmpty(member.getAccount().getUuid(), "The account uuid of the LDAP group member must be set");
		Validate.notNull(member.getNode(), "The node of the LDAP group member must be set");
		Validate.notEmpty(member.getNode().getUuid(), "The node uuid of the LDAP group member must be set");
		Validate.notEmpty(member.getExternalId(), "The external ID must be set");
		SharedSpaceLDAPGroupMember created = businessService.create(member);
		User newMember = userRepository.findByLsUuid(created.getAccount().getUuid());
		EmailContext context = new WorkGroupWarnNewMemberEmailContext(member, actor, newMember);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(actor, actor, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, created);
		businessService.addMembersToRelatedAccountsAndRelatedDomains(created.getNode().getUuid(), log);
		logEntryService.insert(log);
		return created;
	}

	@Override
	public SharedSpaceLDAPGroupMember update(Account actor, SharedSpaceLDAPGroupMember member) {
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(member, "The member must be set");
		Validate.notEmpty(member.getUuid(), "The uuid of the LDAP group member must be set");
		Validate.notNull(member.getAccount(), "The account of the LDAP group member must be set");
		Validate.notEmpty(member.getAccount().getUuid(), "The account uuid of the LDAP group member must be set");
		Validate.notNull(member.getNode(), "The node of the LDAP group member must be set");
		Validate.notEmpty(member.getNode().getUuid(), "The node uuid of the LDAP group member must be set");
		Validate.notEmpty(member.getExternalId(), "The external ID must be set");
		SharedSpaceLDAPGroupMember updated = businessService.update(member);
		User newMember = userRepository.findByLsUuid(updated.getAccount().getUuid());
		EmailContext context = new WorkGroupWarnUpdatedMemberEmailContext(member, newMember, actor);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail, true);
		SharedSpaceMemberAuditLogEntry log = new SharedSpaceMemberAuditLogEntry(actor, actor, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP_MEMBER, member);
		log.setResourceUpdated(updated);
		businessService.addMembersToRelatedAccountsAndRelatedDomains(member.getNode().getUuid(), log);
		logEntryService.insert(log);
		return updated;
	}

}
