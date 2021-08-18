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

public class GroupLdapPatternServiceImpl extends GenericAdminServiceImpl implements GroupLdapPatternService {

	protected GroupPatternRepository groupPatternRepository;

	protected LdapGroupProviderRepository ldapGroupProviderRepository;

	protected final AbstractDomainRepository abstractDomainRepository;

	public GroupLdapPatternServiceImpl(GroupPatternRepository groupPatternRepository,
			LdapGroupProviderRepository ldapGroupProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository) {
		super(sanitizerInputHtmlBusinessService);
		this.groupPatternRepository = groupPatternRepository;
		this.ldapGroupProviderRepository = ldapGroupProviderRepository;
		this.abstractDomainRepository = abstractDomainRepository;
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
		// TODO AUDIT
		return createdGroupPattern;
	}

	@Override
	public GroupLdapPattern update(Account authUser, GroupLdapPattern groupLdapPattern) throws BusinessException {
		preChecks(authUser);
		Validate.notNull(groupLdapPattern, "Group Ldap Pattern must be set");
		Validate.notEmpty(groupLdapPattern.getUuid(), "Group Ldap Pattern UUID must be set");
		GroupLdapPattern pattern = groupPatternRepository.find(groupLdapPattern.getUuid());
		if (pattern == null) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_NOT_FOUND, "no such group pattern");
		}
		if (pattern.getSystem()) {
			throw new BusinessException(BusinessErrorCode.GROUP_LDAP_PATTERN_CANNOT_BE_UPDATED,
					"System group patterns cannot be updated");
		}
		Validate.notEmpty(groupLdapPattern.getLabel(), "Pattern's label must be set.");
		Validate.notEmpty(groupLdapPattern.getDescription(), "Pattern's description must be set.");
		Validate.notNull(groupLdapPattern.getSearchPageSize(), "Pattern's search page size must be set.");
		Validate.notNull(groupLdapPattern.getSearchAllGroupsQuery(), "Pattern's search all groups query must be set.");
		Validate.notEmpty(groupLdapPattern.getSearchGroupQuery(), "Pattern's search group query must be set.");

		pattern.setLabel(sanitize(groupLdapPattern.getLabel()));
		pattern.setDescription(sanitize(groupLdapPattern.getDescription()));
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
		groupPatternRepository.delete(pattern);
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
