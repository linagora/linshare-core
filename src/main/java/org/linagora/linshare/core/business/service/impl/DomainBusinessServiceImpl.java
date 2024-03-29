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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.webservice.utils.PageContainer;

public class DomainBusinessServiceImpl implements DomainBusinessService {

	private AbstractDomainRepository repository;

	public DomainBusinessServiceImpl(AbstractDomainRepository repository) {
		this.repository = repository;
	}

	@Override
	public AbstractDomain getUniqueRootDomain() throws BusinessException {
		return repository.getUniqueRootDomain();
	}

	// TODO this method should never raise an exception.
	// This check should be in the service.
	@Override
	public AbstractDomain findById(String identifier) throws BusinessException {
		AbstractDomain domain = repository.findById(identifier);
		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,
					"The current domain does not exist : " + identifier);
		}
		return domain;
	}

	@Override
	public AbstractDomain find(String identifier) throws BusinessException {
		return repository.findById(identifier);
	}

	@Override
	public AbstractDomain update(AbstractDomain domain)
			throws BusinessException {
		return repository.update(domain);
	}

	@Override
	public List<AbstractDomain> loadRelativeDomains(WelcomeMessages welcomeMessage)
			throws BusinessException {
		return repository.loadDomainsForAWelcomeMessage(welcomeMessage);
	}

	@Override
	public List<AbstractDomain> loadRelativeDomains(MailConfig mailConfig)
			throws BusinessException {
		return repository.loadDomainsForAMailConfiguration(mailConfig);
	}

	@Override
	public AbstractDomain findGuestDomain(AbstractDomain domain)  throws BusinessException {
		if (domain.isRootDomain()) {
			return null;
		}
		// search GuestDomain among subdomains
		
		AbstractDomain guestDomain = repository.getGuestSubDomainByDomain(domain.getUuid());
		if (guestDomain != null) {
			return guestDomain;
		}
		// search among siblings
		if (domain.getParentDomain() != null) {
			return findGuestDomain(domain.getParentDomain());
		}
		throw new BusinessException(BusinessErrorCode.GUEST_FORBIDDEN, "No guest domain found");
	}

	private List<AbstractDomain> getMyDomainRecursively(AbstractDomain domain) {
		List<AbstractDomain> domains = new ArrayList<AbstractDomain>();
		if (domain != null) {
			domains.add(domain);
			List<AbstractDomain> abstractDomains = repository.getSubDomainsByDomain(domain.getUuid());
			for (AbstractDomain d : abstractDomains) {
				domains.addAll(getMyDomainRecursively(d));
			}
		}
		return domains;
	}

	@Override
	public List<String> getAllMyDomainIdentifiers(AbstractDomain domain) {
		List<String> domains = new ArrayList<String>();
		for (AbstractDomain abstractDomain : getMyDomainRecursively(domain)) {
			domains.add(abstractDomain.getUuid());
		}
		return domains;
	}

	@Override
	public List<String> getAllSubDomainIdentifiers(String domain) {
		return repository.getAllSubDomainIdentifiers(domain);
	}

	@Override
	public List<String> getSubDomainsByDomainIdentifiers(String uuid) throws BusinessException {
		return repository.getSubDomainsByDomainIdentifiers(uuid);
	}

	@Override
	public List<AbstractDomain> getSubDomainsByDomain(String uuid) throws BusinessException {
		return repository.getSubDomainsByDomain(uuid);
	}

	@Override
	public Set<AbstractDomain> getSubDomainsByDomainAsASet(String uuid) throws BusinessException {
		return new HashSet<AbstractDomain>(repository.getSubDomainsByDomain(uuid));
	}

	@Override
	public AbstractDomain create(AbstractDomain domain) {
		return repository.create(domain);
	}

	@Override
	public PageContainer<AbstractDomain> findAll(
			Optional<DomainType> domainType,
			Optional<String> name, Optional<String> description,
			Optional<AbstractDomain> parent,
			Optional<AbstractDomain> from,
			SortOrder sortOrder, DomainField sortField,
			PageContainer<AbstractDomain> container) {
		return repository.findAll(domainType, name, description, parent, from, sortOrder, sortField, container);
	}
}
