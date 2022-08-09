/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.AuditEntryField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface AuditLogEntryService {

	Set<AuditLogEntryUser> findAllForUsers(Account authUser, Account actor, List<LogAction> action, List<AuditLogEntryType> type,
			boolean forceAll, String beginDate, String endDate);

	Set<AuditLogEntryUser> findAll(Account authUser, Account actor, String entryUuid, List<LogAction> action,
			List<AuditLogEntryType> type, String beginDate, String endDate);

	Set<AuditLogEntry> findAllForAdmins(Account actor, List<LogAction> action, List<AuditLogEntryType> type, boolean forceAll,
			String beginDate, String endDate);

	Set<AuditLogEntryUser> findAllContactLists(Account actor, Account owner, String contactListUuid);

	Set<AuditLogEntryUser> findAllAuditsOfGroup(Account authUser, Account actor, String requestUuid, boolean all,
			List<LogAction> action, List<AuditLogEntryType> type);

	Set<AuditLogEntryAdmin> findAll(Account actor, String domainUuid, List<LogAction> action);

	Set<MailAttachmentAuditLogEntry> findAllAudits(Account authUser, String domainUuid, List<LogAction> actions);

	Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(Account authUser, List<String> domains, List<LogAction> actions);

	Set<MailAttachmentAuditLogEntry> findAllAuditsByRoot(Account authUser, List<LogAction> actions);

	Set<AuditLogEntryUser> findAllUploadRequestAudits(Account authUser, Account actor, String uploadRequestUuid,
			List<LogAction> actions, List<AuditLogEntryType> types);

	Set<AuditLogEntryUser> findAllUploadRequestEntryAudits(Account authUser, Account actor, String uploadRequestEntryUuid,
			List<LogAction> actions);

	Set<AuditLogEntryUser> findAllSharedSpaceAudits(Account authUser, User actor, String sharedSpaceUuid, String nodeUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate);

	Set<AuditLogEntryUser> findAllModeratorAudits(Account authUser, Account actor, String ModeratorUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate);

	AuditLogEntry find(Account authUser, AbstractDomain domain, String uuid);

	PageContainer<AuditLogEntry> findAll(
			Account authUser,
			AbstractDomain domain, boolean includeNestedDomains,
			Set<String> domains,
			SortOrder sortOrder, AuditEntryField sortField,
			Set<LogAction> logActions,
			Set<AuditLogEntryType> resourceTypes,
			Set<AuditGroupLogEntryType> resourceGroups,
			Set<AuditLogEntryType> excludedTypes,
			Optional<String> authUserUuid, Optional<String> actorUuid,
			Optional<String> relatedAccount,
			Optional<String> resource,
			Optional<String> relatedResource,
			Optional<String> resourceName,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<AuditLogEntry> container);
}
