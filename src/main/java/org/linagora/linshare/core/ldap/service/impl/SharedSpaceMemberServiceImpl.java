/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.ldap.service.impl;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
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
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(businessService, notifierService, mailBuildingService, rac, logEntryService, userRepository,
				sharedSpaceBuildingService, sanitizerInputHtmlBusinessService);
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
		List<String> members = businessService.findMembersUuidBySharedSpaceNodeUuid(created.getNode().getUuid());
		log.addRelatedAccounts(members);
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
		List<String> members = businessService.findMembersUuidBySharedSpaceNodeUuid(member.getNode().getUuid());
		log.addRelatedAccounts(members);
		logEntryService.insert(log);
		return updated;
	}

}
