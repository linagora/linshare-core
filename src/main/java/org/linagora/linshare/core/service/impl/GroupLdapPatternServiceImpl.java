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
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.GroupPatternRepository;
import org.linagora.linshare.core.repository.LdapGroupProviderRepository;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.mongo.entities.logs.GroupFilterAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.LdapGroupFilterMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

import com.google.common.base.Strings;

public class GroupLdapPatternServiceImpl extends GenericAdminServiceImpl implements GroupLdapPatternService {

	protected GroupPatternRepository groupPatternRepository;

	protected LdapGroupProviderRepository ldapGroupProviderRepository;

	protected final AbstractDomainRepository abstractDomainRepository;

	private final AuditAdminMongoRepository auditAdminMongoRepository;

	public GroupLdapPatternServiceImpl(GroupPatternRepository groupPatternRepository,
			LdapGroupProviderRepository ldapGroupProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository,
			AuditAdminMongoRepository auditAdminMongoRepository) {
		super(sanitizerInputHtmlBusinessService);
		this.groupPatternRepository = groupPatternRepository;
		this.ldapGroupProviderRepository = ldapGroupProviderRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.auditAdminMongoRepository = auditAdminMongoRepository;
	}

	@Override
	public GroupLdapPattern find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Uuid must be set");
		GroupLdapPattern groupLdapPattern = groupPatternRepository.find(uuid);
		if (groupLdapPattern == null) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_NOT_FOUND, "Goup ldap pattern identifier no found.");
		}
		return groupLdapPattern;
	}

	@Override
	public List<GroupLdapPattern> findAll() throws BusinessException {
		return groupPatternRepository.findAll();
	}

	@Override
	public GroupLdapPattern create(Account authUser, GroupLdapPattern groupLdapPattern) throws BusinessException {
		preChecks(authUser);
		Validate.notEmpty(groupLdapPattern.getLabel());
		Validate.notEmpty(groupLdapPattern.getSearchAllGroupsQuery());
		Validate.notEmpty(groupLdapPattern.getSearchGroupQuery());
		Collection<LdapAttribute> collection = groupLdapPattern.getAttributes().values();
		for (LdapAttribute e : collection) {
			if (e.getAttribute() == null) {
				throw new BusinessException(BusinessErrorCode.LDAP_ATTRIBUTE_CONTAINS_NULL,
						"Attribute must be not null");
			}
		}
		groupLdapPattern.setLabel(sanitize(groupLdapPattern.getLabel()));
		groupLdapPattern.setDescription(sanitize(groupLdapPattern.getDescription()));
		GroupLdapPattern createdGroupPattern = groupPatternRepository.create(groupLdapPattern);
		LdapGroupFilterMto groupFilterMto = new LdapGroupFilterMto(createdGroupPattern);
		GroupFilterAuditLogEntry log = new GroupFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.CREATE, AuditLogEntryType.GROUP_FILTER, groupFilterMto);
		auditAdminMongoRepository.insert(log);
		return createdGroupPattern;
	}

	@Override
	public GroupLdapPattern update(Account authUser, GroupLdapPattern groupLdapPattern) throws BusinessException {
		preChecks(authUser);
		Validate.notNull(groupLdapPattern, "Group Ldap Pattern must be set");
		Validate.notEmpty(groupLdapPattern.getUuid(), "Group Ldap Pattern UUID must be set");
		GroupLdapPattern pattern = groupPatternRepository.find(groupLdapPattern.getUuid());
		LdapGroupFilterMto groupFilterMto = new LdapGroupFilterMto(pattern);
		GroupFilterAuditLogEntry log = new GroupFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.UPDATE, AuditLogEntryType.GROUP_FILTER, groupFilterMto);
		if (pattern == null) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_NOT_FOUND, "no such group pattern");
		}
		if (pattern.getSystem()) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_CANNOT_BE_UPDATED,
					"System group patterns cannot be updated");
		}
		Validate.notEmpty(groupLdapPattern.getLabel(), "Pattern's label must be set.");
		Validate.notNull(groupLdapPattern.getSearchPageSize(), "Pattern's search page size must be set.");
		Validate.notNull(groupLdapPattern.getSearchAllGroupsQuery(), "Pattern's search all groups query must be set.");
		Validate.notEmpty(groupLdapPattern.getSearchGroupQuery(), "Pattern's search group query must be set.");

		pattern.setLabel(sanitize(groupLdapPattern.getLabel()));
		if (!Strings.isNullOrEmpty(groupLdapPattern.getDescription())) {
			pattern.setDescription(sanitize(groupLdapPattern.getDescription()));
		}
		pattern.setSearchPageSize(groupLdapPattern.getSearchPageSize());
		pattern.setSearchAllGroupsQuery(groupLdapPattern.getSearchAllGroupsQuery());
		pattern.setSearchGroupQuery(groupLdapPattern.getSearchGroupQuery());
		pattern.setGroupPrefix(groupLdapPattern.getGroupPrefix());

		pattern.getAttributes().get(GroupLdapPattern.GROUP_NAME)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.GROUP_NAME).getAttribute());
		pattern.getAttributes().get(GroupLdapPattern.GROUP_MEMBER)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.GROUP_MEMBER).getAttribute());
		pattern.getAttributes().get(GroupLdapPattern.MEMBER_LAST_NAME)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.MEMBER_LAST_NAME).getAttribute());
		pattern.getAttributes().get(GroupLdapPattern.MEMBER_FIRST_NAME)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.MEMBER_FIRST_NAME).getAttribute());
		pattern.getAttributes().get(GroupLdapPattern.MEMBER_FIRST_NAME)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.MEMBER_FIRST_NAME).getAttribute());
		pattern.getAttributes().get(GroupLdapPattern.MEMBER_MAIL)
				.setAttribute(groupLdapPattern.getAttributes().get(GroupLdapPattern.MEMBER_MAIL).getAttribute());
		pattern = groupPatternRepository.update(pattern);
		LdapGroupFilterMto groupFilterUpdatedMto = new LdapGroupFilterMto(pattern);
		log.setResourceUpdated(groupFilterUpdatedMto);
		auditAdminMongoRepository.insert(log);
		return pattern;
	}

	@Override
	public GroupLdapPattern delete(Account authUser, GroupLdapPattern groupLdapPattern)
			throws BusinessException {
		Validate.notNull(groupLdapPattern, "GroupLdapPattern must be set");
		GroupLdapPattern pattern = find(groupLdapPattern.getUuid());
		if (ldapGroupProviderRepository.isUsed(pattern)) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_STILL_IN_USE,
					"Cannot delete this pattern because is still used by domains");
		}
		if (pattern.getSystem()) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_CANNOT_BE_REMOVED,
					"System group patterns cannot be removed");
		}
		LdapGroupFilterMto groupFilterMto = new LdapGroupFilterMto(pattern);
		GroupFilterAuditLogEntry log = new GroupFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.DELETE, AuditLogEntryType.GROUP_FILTER, groupFilterMto);
		groupPatternRepository.delete(pattern);
		auditAdminMongoRepository.insert(log);
		return pattern;
	}

	@Override
	public List<GroupLdapPattern> findAllPublicGroupPatterns() {
		return groupPatternRepository.findAllPublicGroupLdapPatterns();
	}

	@Override
	public List<GroupLdapPattern> findAllSystemGroupLdapPatterns() {
		return groupPatternRepository.findAllSystemGroupLdapPatterns();
	}

	@Override
	public List<AbstractDomain> findAllDomainsByGroupFilter(Account authUser, GroupLdapPattern domainGroupFilter) {
		preChecks(authUser);
		Validate.notNull(domainGroupFilter, "domainGroupFilter must be set.");
		List<AbstractDomain> domains = abstractDomainRepository.findAllDomainsByGroupFilter(domainGroupFilter);
		return domains;
	}
}
