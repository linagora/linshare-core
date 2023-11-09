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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class DomainPermissionBusinessServiceImpl implements
		DomainPermissionBusinessService {

	private final DomainBusinessService domainBusinessService;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public DomainPermissionBusinessServiceImpl(
			final DomainBusinessService domainBusinessService) {
		super();
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public boolean isAdminForThisDomain(Account actor, AbstractDomain domain) {
		if (!(actor.hasSuperAdminRole() || actor.hasSystemAccountRole())) {
			if (!domain.isManagedBy(actor)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isAdminForThisDomain(Account actor, String domain) {
		return !StringUtils.isBlank(domain) && isAdminForThisDomain(actor, domainBusinessService.find(domain));
	}

	@Override
	public List<String> checkDomainAdministrationForListingSharedSpaces(Account actor, List<String> domains) {
		List<String> allowedDomainUuids = Lists.newArrayList();
		if (Role.SUPERADMIN.equals(actor.getRole())) {
			if (!CollectionUtils.isEmpty(domains)) {
				for (String uuid : domains) {
					AbstractDomain domain = domainBusinessService.findById(uuid);
					allowedDomainUuids.add(domain.getUuid());
				}
			}
		} else {
			if (CollectionUtils.isEmpty(domains)) {
				allowedDomainUuids = getAdministratedDomainsIdentifiers(actor, actor.getDomainId());
			} else {
				for (String uuid : domains) {
					AbstractDomain domain = domainBusinessService.findById(uuid);
					if (isAdminForThisDomain(actor, domain)) {
						allowedDomainUuids.add(domain.getUuid());
					} else {
						logger.debug("You are not admin of this domain: {}", uuid);
						throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN,
								"You are not admin for this domain: " + domain.getUuid());
					}
				}
			}
		}
		return allowedDomainUuids;
	}

	@Override
	public boolean isAdminForThisUploadRequest(Account actor, UploadRequest request) {
		return isAdminForThisDomain(actor, request.getUploadRequestGroup().getAbstractDomain())
				|| isOwner(actor, request);
	}

	@Override
	public List<String> getAdministratedDomainsIdentifiers(Account actor, String domainUuid) {
		if (!(actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			return Lists.newArrayList();
		}
		return findRecursivelyDomainsIdentifiers(domainUuid);
	}

	private List<String> findRecursivelyDomainsIdentifiers(String uuid) {
		List<String> list = Lists.newArrayList();
		list.add(uuid);
		List<String> identifiers = domainBusinessService.getSubDomainsByDomainIdentifiers(uuid);
		for (String identifier : identifiers) {
			list.addAll(findRecursivelyDomainsIdentifiers(identifier));
		}
		return list;
	}

	@Override
	public List<AbstractDomain> getMyAdministratedDomains(Account actor) {
		if (!(actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			return Lists.newArrayList();
		}
		return findRecursivelyDomains(actor.getDomain());
	}

	private List<AbstractDomain> findRecursivelyDomains(AbstractDomain root) {
		List<AbstractDomain> list = Lists.newArrayList();
		list.add(root);
		List<AbstractDomain> abstractDomains = domainBusinessService.getSubDomainsByDomain(root.getUuid());
		for (AbstractDomain sub : abstractDomains) {
			list.addAll(findRecursivelyDomains(sub));
		}
		return list;
	}

	private boolean isOwner(Account actor, UploadRequest request) {
		return request.getUploadRequestGroup().getOwner().equals(actor);
	}
}
