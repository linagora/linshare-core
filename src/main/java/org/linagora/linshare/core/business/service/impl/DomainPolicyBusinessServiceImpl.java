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

import org.linagora.linshare.core.business.service.DomainPolicyBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPolicyBusinessServiceImpl implements
		DomainPolicyBusinessService {

	private static final Logger logger = LoggerFactory
			.getLogger(DomainPolicyBusinessServiceImpl.class);

	final private DomainPolicyRepository domainPolicyRepository;
	final private AbstractDomainRepository abstractDomainRepository;
	final private DomainAccessPolicyRepository domainAccessPolicyRepository;

	public DomainPolicyBusinessServiceImpl(
			DomainPolicyRepository domainPolicyRepository,
			AbstractDomainRepository abstractDomainRepository,
			DomainAccessPolicyRepository domainAccessPolicyRepository) {
		super();
		this.domainPolicyRepository = domainPolicyRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.domainAccessPolicyRepository = domainAccessPolicyRepository;
	}

	@Override
	public DomainPolicy create(DomainPolicy domainPolicy)
			throws BusinessException {
		DomainAccessPolicy domainAccessPolicy = domainAccessPolicyRepository
				.create(domainPolicy.getDomainAccessPolicy());
		domainPolicy.setDomainAccessPolicy(domainAccessPolicy);
		return domainPolicyRepository.create(domainPolicy);
	}

	@Override
	public DomainPolicy update(DomainPolicy dto) throws BusinessException {
		DomainPolicy entity = domainPolicyRepository.findById(dto
				.getUuid());
		entity.setDescription(dto.getDescription());
		entity.setLabel(dto.getLabel());
		List<DomainAccessRule> rules = dto.getDomainAccessPolicy().getRules();
		entity.getDomainAccessPolicy().getRules().clear();
		for (DomainAccessRule domainAccessRule : rules) {
			entity.getDomainAccessPolicy().addRule(domainAccessRule);
		}
		return domainPolicyRepository.update(entity);
	}

	@Override
	public DomainPolicy delete(String identifier) throws BusinessException {
		if (!policyIsDeletable(identifier)) {
			throw new BusinessException(
					"Cannot delete policy because still used by domains");
		}
		DomainPolicy entity = domainPolicyRepository.findById(identifier);
		logger.debug("delete policy: " + identifier);
		DomainAccessPolicy domainAccessPolicy = entity.getDomainAccessPolicy();
		domainPolicyRepository.delete(entity);
		domainAccessPolicyRepository.delete(domainAccessPolicy);
		return entity;
	}

	@Override
	public boolean policyIsDeletable(String identifier) {
		for (AbstractDomain domain : abstractDomainRepository.findAll()) {
			if (domain.getPolicy().getUuid().equals(identifier)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DomainPolicy find(String identifier) {
		return domainPolicyRepository.findById(identifier);
	}

	@Override
	public List<DomainPolicy> findAll() {
		return domainPolicyRepository.findAll();
	}

	@Override
	public List<String> findAllIdentifiers() {
		return domainPolicyRepository.findAllIdentifiers();
	}

}