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

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.WorkSpaceProviderRepository;
import org.linagora.linshare.core.repository.LdapWorkSpaceFilterRepository;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;
import org.linagora.linshare.mongo.entities.logs.WorkSpaceFilterAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.LdapWorkSpaceFilterMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

import com.google.common.base.Strings;

public class LdapWorkSpaceFilterServiceImpl extends GenericAdminServiceImpl implements LdapWorkSpaceFilterService {

	protected LdapWorkSpaceFilterRepository workSpacePatternRepository;

	protected WorkSpaceProviderRepository workSpaceProviderRepository;

	protected final AbstractDomainRepository abstractDomainRepository;

	private final AuditAdminMongoRepository auditAdminMongoRepository;

	public LdapWorkSpaceFilterServiceImpl(LdapWorkSpaceFilterRepository workSpaceFilterRepository,
			WorkSpaceProviderRepository workSpaceProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository,
			AuditAdminMongoRepository auditAdminMongoRepository) {
		super(sanitizerInputHtmlBusinessService);
		this.workSpacePatternRepository = workSpaceFilterRepository;
		this.workSpaceProviderRepository = workSpaceProviderRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.auditAdminMongoRepository = auditAdminMongoRepository;
	}

	@Override
	public LdapWorkSpaceFilter find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Uuid must be set");
		LdapWorkSpaceFilter groupLdapPattern = workSpacePatternRepository.find(uuid);
		if (groupLdapPattern == null) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_LDAP_FILTER_NOT_FOUND, "WorkSpace ldap filter identifier not found.");
		}
		return groupLdapPattern;
	}

	@Override
	public List<LdapWorkSpaceFilter> findAll() throws BusinessException {
		return workSpacePatternRepository.findAll();
	}

	@Override
	public LdapWorkSpaceFilter create(Account authUser, LdapWorkSpaceFilter workSpaceLdapFilter) throws BusinessException {
		preChecks(authUser);
		Validate.notEmpty(workSpaceLdapFilter.getLabel());
		Validate.notEmpty(workSpaceLdapFilter.getSearchAllGroupsQuery());
		Validate.notEmpty(workSpaceLdapFilter.getSearchGroupQuery());
		Collection<LdapAttribute> collection = workSpaceLdapFilter.getAttributes().values();
		for (LdapAttribute e : collection) {
			if (e.getAttribute() == null) {
				throw new BusinessException(BusinessErrorCode.LDAP_ATTRIBUTE_CONTAINS_NULL,
						"Attribute must be not null");
			}
		}
		workSpaceLdapFilter.setLabel(sanitize(workSpaceLdapFilter.getLabel()));
		workSpaceLdapFilter.setDescription(sanitize(workSpaceLdapFilter.getDescription()));
		LdapWorkSpaceFilter createdworkSpacePattern = workSpacePatternRepository.create(workSpaceLdapFilter);
		LdapWorkSpaceFilterMto workSpaceFilterMto = new LdapWorkSpaceFilterMto(createdworkSpacePattern);
		WorkSpaceFilterAuditLogEntry log = new WorkSpaceFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.CREATE, AuditLogEntryType.WORKSPACE_FILTER, workSpaceFilterMto);
		auditAdminMongoRepository.insert(log);
		return createdworkSpacePattern;
	}

	@Override
	public LdapWorkSpaceFilter update(Account authUser, LdapWorkSpaceFilter ldapWorkSpaceFilter) throws BusinessException {
		preChecks(authUser);
		Validate.notNull(ldapWorkSpaceFilter, "WorkSpace Ldap filter must be set");
		Validate.notEmpty(ldapWorkSpaceFilter.getUuid(), "WorkSpace Ldap filter UUID must be set");
		LdapWorkSpaceFilter workSpaceFilter = workSpacePatternRepository.find(ldapWorkSpaceFilter.getUuid());
		LdapWorkSpaceFilterMto workSpaceFilterToUpdateMto = new LdapWorkSpaceFilterMto(workSpaceFilter);
		WorkSpaceFilterAuditLogEntry log = new WorkSpaceFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.UPDATE, AuditLogEntryType.WORKSPACE_FILTER, workSpaceFilterToUpdateMto);
		if (workSpaceFilter == null) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_LDAP_FILTER_NOT_FOUND, "no such WorkSpace filter");
		}
		if (workSpaceFilter.getSystem()) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_LDAP_FILTER_CANNOT_BE_UPDATED,
					"System WorkSpace filters cannot be updated");
		}
		Validate.notEmpty(ldapWorkSpaceFilter.getLabel(), "WorkSpace Ldap filter's label must be set.");
		Validate.notNull(ldapWorkSpaceFilter.getSearchPageSize(), "WorkSpace Ldap filter's search page size must be set.");
		Validate.notNull(ldapWorkSpaceFilter.getSearchAllGroupsQuery(), "WorkSpace Ldap filter's search all groups query must be set.");
		Validate.notEmpty(ldapWorkSpaceFilter.getSearchGroupQuery(), "WorkSpace Ldap filter's search group query must be set.");

		workSpaceFilter.setLabel(sanitize(ldapWorkSpaceFilter.getLabel()));
		if (!Strings.isNullOrEmpty(ldapWorkSpaceFilter.getDescription())) {
			workSpaceFilter.setDescription(sanitize(ldapWorkSpaceFilter.getDescription()));
		}
		workSpaceFilter.setSearchPageSize(ldapWorkSpaceFilter.getSearchPageSize());
		workSpaceFilter.setSearchAllGroupsQuery(ldapWorkSpaceFilter.getSearchAllGroupsQuery());
		workSpaceFilter.setSearchGroupQuery(ldapWorkSpaceFilter.getSearchGroupQuery());
		workSpaceFilter.setGroupPrefix(ldapWorkSpaceFilter.getGroupPrefix());

		workSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.GROUP_NAME).setAttribute(ldapWorkSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.GROUP_NAME).getAttribute());
		workSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.GROUP_MEMBER).setAttribute(ldapWorkSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.GROUP_MEMBER).getAttribute());
		workSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_LAST_NAME).setAttribute(ldapWorkSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_LAST_NAME).getAttribute());
		workSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_FIRST_NAME).setAttribute(ldapWorkSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_FIRST_NAME).getAttribute());
		workSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_MAIL).setAttribute(ldapWorkSpaceFilter.getAttributes().get(LdapWorkSpaceFilter.MEMBER_MAIL).getAttribute());
		workSpaceFilter = workSpacePatternRepository.update(workSpaceFilter);
		LdapWorkSpaceFilterMto workSpaceFilterUpdatedMto = new LdapWorkSpaceFilterMto(workSpaceFilter);
		log.setResourceUpdated(workSpaceFilterUpdatedMto);
		auditAdminMongoRepository.insert(log);
		return workSpaceFilter;
	}

	@Override
	public LdapWorkSpaceFilter delete(Account authUser, LdapWorkSpaceFilter workSpaceLdapFilter)
			throws BusinessException {
		Validate.notNull(workSpaceLdapFilter, "workSpaceLdapFilter must be set");
		LdapWorkSpaceFilter workSpaceFilter = find(workSpaceLdapFilter.getUuid());
		if (workSpaceProviderRepository.isUsed(workSpaceFilter)) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_STILL_IN_USE,
					"Cannot delete this workSpace filter because is still used by domains");
		}
		if (workSpaceFilter.getSystem()) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_LDAP_FILTER_CANNOT_BE_REMOVED,
					"System WorkSpace filter cannot be removed");
		}
		LdapWorkSpaceFilterMto workSpaceFilterMto = new LdapWorkSpaceFilterMto(workSpaceFilter);
		WorkSpaceFilterAuditLogEntry log = new WorkSpaceFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.DELETE, AuditLogEntryType.WORKSPACE_FILTER, workSpaceFilterMto);
		workSpacePatternRepository.delete(workSpaceFilter);
		auditAdminMongoRepository.insert(log);
		return workSpaceFilter;
	}

	@Override
	public List<LdapWorkSpaceFilter> findAllPublicWorkSpaceFilters() {
		return workSpacePatternRepository.findAllPublicGroupLdapPatterns();
	}

	@Override
	public List<LdapWorkSpaceFilter> findAllSystemWorkSpaceLdapFilters() {
		return workSpacePatternRepository.findAllSystemGroupLdapPatterns();
	}

	@Override
	public List<AbstractDomain> findAllDomainsByWorkSpaceFilter(Account authUser, LdapWorkSpaceFilter domainWorkSpaceFilter) {
		preChecks(authUser);
		Validate.notNull(domainWorkSpaceFilter, "domainWorkSpaceFilter must be set.");
		List<AbstractDomain> domains = abstractDomainRepository.findAllDomainsByWorkSpaceFilter(domainWorkSpaceFilter);
		return domains;
	}
}
