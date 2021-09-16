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
import org.linagora.linshare.core.domain.entities.LdapDriveFilter;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DriveProviderRepository;
import org.linagora.linshare.core.repository.LdapDriveFilterRepository;
import org.linagora.linshare.core.service.LdapDriveFilterService;
import org.linagora.linshare.mongo.entities.logs.DriveFilterAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.LdapDriveFilterMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

public class LdapDriveFilterServiceImpl extends GenericAdminServiceImpl implements LdapDriveFilterService {

	protected LdapDriveFilterRepository drivePatternRepository;

	protected DriveProviderRepository driveProviderRepository;

	protected final AbstractDomainRepository abstractDomainRepository;

	private final AuditAdminMongoRepository auditAdminMongoRepository;

	public LdapDriveFilterServiceImpl(LdapDriveFilterRepository drivePatternRepository,
			DriveProviderRepository driveProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository,
			AuditAdminMongoRepository auditAdminMongoRepository) {
		super(sanitizerInputHtmlBusinessService);
		this.drivePatternRepository = drivePatternRepository;
		this.driveProviderRepository = driveProviderRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.auditAdminMongoRepository = auditAdminMongoRepository;
	}

	@Override
	public LdapDriveFilter find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Uuid must be set");
		LdapDriveFilter groupLdapPattern = drivePatternRepository.find(uuid);
		if (groupLdapPattern == null) {
			throw new BusinessException(BusinessErrorCode.DRIVE_LDAP_FILTER_NOT_FOUND, "Drive ldap filter identifier not found.");
		}
		return groupLdapPattern;
	}

	@Override
	public List<LdapDriveFilter> findAll() throws BusinessException {
		return drivePatternRepository.findAll();
	}

	@Override
	public LdapDriveFilter create(Account authUser, LdapDriveFilter driveLdapPattern) throws BusinessException {
		preChecks(authUser);
		Validate.notEmpty(driveLdapPattern.getLabel());
		Validate.notEmpty(driveLdapPattern.getSearchAllGroupsQuery());
		Validate.notEmpty(driveLdapPattern.getSearchGroupQuery());
		Collection<LdapAttribute> collection = driveLdapPattern.getAttributes().values();
		for (LdapAttribute e : collection) {
			if (e.getAttribute() == null) {
				throw new BusinessException(BusinessErrorCode.LDAP_ATTRIBUTE_CONTAINS_NULL,
						"Attribute must be not null");
			}
		}
		driveLdapPattern.setLabel(sanitize(driveLdapPattern.getLabel()));
		driveLdapPattern.setDescription(sanitize(driveLdapPattern.getDescription()));
		LdapDriveFilter createdDrivePattern = drivePatternRepository.create(driveLdapPattern);
		LdapDriveFilterMto driveFilterMto = new LdapDriveFilterMto(createdDrivePattern);
		DriveFilterAuditLogEntry log = new DriveFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.CREATE, AuditLogEntryType.DRIVE_FILTER, driveFilterMto);
		auditAdminMongoRepository.insert(log);
		return createdDrivePattern;
	}

	@Override
	public LdapDriveFilter update(Account authUser, LdapDriveFilter ldapDriveFilter) throws BusinessException {
		preChecks(authUser);
		Validate.notNull(ldapDriveFilter, "Drive Ldap filter must be set");
		Validate.notEmpty(ldapDriveFilter.getUuid(), "Drive Ldap filter UUID must be set");
		LdapDriveFilter driveFilter = drivePatternRepository.find(ldapDriveFilter.getUuid());
		LdapDriveFilterMto driveFilterToUpdateMto = new LdapDriveFilterMto(driveFilter);
		DriveFilterAuditLogEntry log = new DriveFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.UPDATE, AuditLogEntryType.DRIVE_FILTER, driveFilterToUpdateMto);
		if (driveFilter == null) {
			throw new BusinessException(BusinessErrorCode.DRIVE_LDAP_FILTER_NOT_FOUND, "no such drive filter");
		}
		if (driveFilter.getSystem()) {
			throw new BusinessException(BusinessErrorCode.DRIVE_LDAP_FILTER_CANNOT_BE_UPDATED,
					"System drive filters cannot be updated");
		}
		Validate.notEmpty(ldapDriveFilter.getLabel(), "Drive Ldap filter's label must be set.");
		Validate.notEmpty(ldapDriveFilter.getDescription(), "Drive Ldap filter's description must be set.");
		Validate.notNull(ldapDriveFilter.getSearchPageSize(), "Drive Ldap filter's search page size must be set.");
		Validate.notNull(ldapDriveFilter.getSearchAllGroupsQuery(), "Drive Ldap filter's search all groups query must be set.");
		Validate.notEmpty(ldapDriveFilter.getSearchGroupQuery(), "Drive Ldap filter's search group query must be set.");

		driveFilter.setLabel(sanitize(ldapDriveFilter.getLabel()));
		driveFilter.setDescription(sanitize(ldapDriveFilter.getDescription()));
		driveFilter.setSearchPageSize(ldapDriveFilter.getSearchPageSize());
		driveFilter.setSearchAllGroupsQuery(ldapDriveFilter.getSearchAllGroupsQuery());
		driveFilter.setSearchGroupQuery(ldapDriveFilter.getSearchGroupQuery());
		driveFilter.setGroupPrefix(ldapDriveFilter.getGroupPrefix());

		driveFilter.getAttributes().get(LdapDriveFilter.GROUP_NAME).setAttribute(ldapDriveFilter.getAttributes().get(LdapDriveFilter.GROUP_NAME).getAttribute());
		driveFilter.getAttributes().get(LdapDriveFilter.GROUP_MEMBER).setAttribute(ldapDriveFilter.getAttributes().get(LdapDriveFilter.GROUP_MEMBER).getAttribute());
		driveFilter.getAttributes().get(LdapDriveFilter.MEMBER_LAST_NAME).setAttribute(ldapDriveFilter.getAttributes().get(LdapDriveFilter.MEMBER_LAST_NAME).getAttribute());
		driveFilter.getAttributes().get(LdapDriveFilter.MEMBER_FIRST_NAME).setAttribute(ldapDriveFilter.getAttributes().get(LdapDriveFilter.MEMBER_FIRST_NAME).getAttribute());
		driveFilter.getAttributes().get(LdapDriveFilter.MEMBER_MAIL).setAttribute(ldapDriveFilter.getAttributes().get(LdapDriveFilter.MEMBER_MAIL).getAttribute());
		driveFilter = drivePatternRepository.update(driveFilter);
		LdapDriveFilterMto driveFilterUpdatedMto = new LdapDriveFilterMto(driveFilter);
		log.setResourceUpdated(driveFilterUpdatedMto);
		auditAdminMongoRepository.insert(log);
		return driveFilter;
	}

	@Override
	public LdapDriveFilter delete(Account authUser, LdapDriveFilter driveLdapFilter)
			throws BusinessException {
		Validate.notNull(driveLdapFilter, "DriveLdapFilter must be set");
		LdapDriveFilter driveFilter = find(driveLdapFilter.getUuid());
		if (driveProviderRepository.isUsed(driveFilter)) {
			throw new BusinessException(
					BusinessErrorCode.DOMAIN_PATTERN_STILL_IN_USE,
					"Cannot delete this drive filter because is still used by domains");
		}
		if (driveFilter.getSystem()) {
			throw new BusinessException(BusinessErrorCode.DRIVE_LDAP_FILTER_CANNOT_BE_REMOVED,
					"System drive filter cannot be removed");
		}
		LdapDriveFilterMto driveFilterMto = new LdapDriveFilterMto(driveFilter);
		DriveFilterAuditLogEntry log = new DriveFilterAuditLogEntry(authUser, LinShareConstants.rootDomainIdentifier,
				LogAction.DELETE, AuditLogEntryType.DRIVE_FILTER, driveFilterMto);
		drivePatternRepository.delete(driveFilter);
		auditAdminMongoRepository.insert(log);
		return driveFilter;
	}

	@Override
	public List<LdapDriveFilter> findAllPublicDrivePatterns() {
		return drivePatternRepository.findAllPublicGroupLdapPatterns();
	}

	@Override
	public List<LdapDriveFilter> findAllSystemDriveLdapPatterns() {
		return drivePatternRepository.findAllSystemGroupLdapPatterns();
	}

	@Override
	public List<AbstractDomain> findAllDomainsByDriveFilter(Account authUser, LdapDriveFilter domainDriveFilter) {
		preChecks(authUser);
		Validate.notNull(domainDriveFilter, "domainDriveFilter must be set.");
		List<AbstractDomain> domains = abstractDomainRepository.findAllDomainsByDriveFilter(domainDriveFilter);
		return domains;
	}
}
