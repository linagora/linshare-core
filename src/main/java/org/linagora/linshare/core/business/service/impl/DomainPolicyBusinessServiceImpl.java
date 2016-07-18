/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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