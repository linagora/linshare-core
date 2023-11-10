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
			Optional<String> actorEmail,
			Optional<String> recipientEmail,
			Optional<String> relatedAccount,
			Optional<String> resource,
			Optional<String> relatedResource,
			Optional<String> resourceName,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<AuditLogEntry> container);
}
