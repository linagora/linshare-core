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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainQuotaBusinessServiceImpl extends GenericQuotaBusinessServiceImpl implements DomainQuotaBusinessService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final DomainQuotaRepository repository;
	private final ContainerQuotaRepository containerQuotaRepository;
	private final AbstractDomainRepository abstractDomainRepository;

	public DomainQuotaBusinessServiceImpl(
			final DomainQuotaRepository domainQuotaRepository,
			final AbstractDomainRepository abstractDomainRepository,
			final ContainerQuotaRepository ensembleQuotaRepository) {
		this.repository = domainQuotaRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.containerQuotaRepository = ensembleQuotaRepository;
	}

	@Override
	public DomainQuota find(AbstractDomain domain) throws BusinessException {
		return repository.find(domain);
	}

	@Override
	public DomainQuota find(String uuid) throws BusinessException {
		return repository.find(uuid);
	}

	@Override
	public List<DomainQuota> findAll() throws BusinessException {
		return repository.findAll();
	}

	@Override
	public List<DomainQuota> findAll(AbstractDomain parentDomain) throws BusinessException {
		return repository.findAllByParent(parentDomain);
	}

	@Override
	public DomainQuota create(DomainQuota entity) throws BusinessException {
		Validate.notNull(entity, "Domain quota must be set.");
		if (find(entity.getDomain()) != null) {
			throw new BusinessException("Domain " + entity.getDomain() + " already has a domain quota.");
		}else{
			return repository.create(entity);
		}
	}

	@Override
	public DomainQuota update(DomainQuota entity, DomainQuota dq) throws BusinessException {
		// quota
		if (needToRestore(entity.getQuotaOverride(), dq.getQuotaOverride())) {
			DomainQuota ancestor = repository.find(entity.getParentDomain());
			dq.setQuota(ancestor.getDefaultQuota());
		}
		entity.setQuota(dq.getQuota());
		entity.setQuotaOverride(dq.getQuotaOverride());

		// maintenance
		if (!entity.getMaintenance().equals(dq.getMaintenance())) {
			repository.cascadeMaintenanceMode(entity.getDomain(), dq.getMaintenance());
		}
		entity.setMaintenance(dq.getMaintenance());

		// default quota
		Boolean defaultQuotaOverride = entity.getDefaultQuotaOverride();
		if (defaultQuotaOverride != null) {
			if (!defaultQuotaOverride.equals(dq.getDefaultQuotaOverride())) {
				if (dq.getDefaultQuotaOverride()) {
					// from false to true => need to cascade
					repository.cascadeDefaultQuota(entity.getDomain(), dq.getDefaultQuota());
				} else {
					// from true to false => need to cascade
					// restore default value from parent ?
					if (entity.getParentDomain() != null) {
						DomainQuota ancestor = repository.find(entity.getParentDomain());
						dq.setDefaultQuota(ancestor.getDefaultQuota());
					}
					repository.cascadeDefaultQuota(entity.getDomain(), dq.getDefaultQuota());
				}
			} else if (!entity.getDefaultQuota().equals(dq.getDefaultQuota())) {
				repository.cascadeDefaultQuota(entity.getDomain(), dq.getDefaultQuota());
			}
			entity.setDefaultQuota(dq.getDefaultQuota());
			entity.setDefaultQuotaOverride(dq.getDefaultQuotaOverride());
		}
		return repository.update(entity);
	}

	@Override
	public DomainQuota updateByBatch(DomainQuota entity) throws BusinessException{
		Long sumCurrentValue = containerQuotaRepository.sumOfCurrentValue(entity);
		Long usedSpace = entity.getCurrentValue();
		entity.setLastValue(usedSpace);
		entity.setCurrentValue(usedSpace + sumCurrentValue);
		entity = repository.updateByBatch(entity);
		return entity;
	}

	@Override
	public AbstractDomain getUniqueRootDomain() {
		return abstractDomainRepository.getUniqueRootDomain();
	}

	@Override
	public DomainQuota findRootQuota() throws BusinessException {
		return find(abstractDomainRepository.getUniqueRootDomain());
	}

}
