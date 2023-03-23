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
package org.linagora.linshare.webservice.adminv5;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;

public interface AuditLogEntryRestService {

	AuditLogEntry find(String domainUuid, String uuid);

	Response findAll(
			String domainUuid, boolean includeNestedDomains,
			Set<String> domains,
			String sortOrder, String sortField,
			List<String> logActions,
			List<String> types,
			List<String> resourceGroups,
			List<String> excludedTypes,
			String authUser, String actor,
			String actorEmail,
			String relatedAccount,
			String resource,
			String relatedResource,
			String resourceName,
			String beginDate, String endDate,
			Integer pageNumber, Integer pageSize);
}