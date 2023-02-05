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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.fields.AuditEntryField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.AuditLogEntryFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.webservice.utils.PageContainer;

public class AuditLogEntryFacadeImpl extends AdminGenericFacadeImpl implements AuditLogEntryFacade {

	protected final AuditLogEntryService service;

	protected final AbstractDomainService abstractDomainService;

	public AuditLogEntryFacadeImpl(
			AccountService accountService,
			AuditLogEntryService service,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.service = service;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public AuditLogEntry find(String domainUuid, String uuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Missing domain uuid in the path.");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		return service.find(authUser, domain, uuid);
	}

	@Override
	public PageContainer<AuditLogEntry> findAll(
			String domainUuid, Boolean includeNestedDomains, Set<String> domains,
			SortOrder sortOrder, AuditEntryField sortField,
			Set<LogAction> logActions, Set<AuditLogEntryType> types,
			Set<AuditGroupLogEntryType> resourceGroups, Set<AuditLogEntryType> excludedTypes,
			Optional<String> authUserUuid, Optional<String> actorUuid,
			Optional<String> actorEmail,
			Optional<String> relatedAccountUuid,
			Optional<String> resourceUuid,
			Optional<String> relatedResourceUuid,
			Optional<String> resourceName,
			Optional<String> beginDate, Optional<String> endDate,
			Integer pageNumber, Integer pageSize) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Missing domain uuid in the path.");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		PageContainer<AuditLogEntry> container = new PageContainer<>(pageNumber, pageSize);
		return service.findAll(
			authUser,
			domain, includeNestedDomains, domains,
			sortOrder, sortField,
			logActions, types,
			resourceGroups,
			excludedTypes,
			authUserUuid, actorUuid,
			actorEmail,
			relatedAccountUuid,
			resourceUuid,
			relatedResourceUuid,
			resourceName,
			beginDate, endDate,
			container);
	}
}
