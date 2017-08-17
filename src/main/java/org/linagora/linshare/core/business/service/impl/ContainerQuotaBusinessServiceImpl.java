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

import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;

public class ContainerQuotaBusinessServiceImpl extends GenericQuotaBusinessServiceImpl implements ContainerQuotaBusinessService {

	private final ContainerQuotaRepository repository;
	private final AccountQuotaRepository accountQuotaRepository;

	public ContainerQuotaBusinessServiceImpl(final ContainerQuotaRepository repository,
			final AccountQuotaRepository accountQuotaRepository) {
		this.repository = repository;
		this.accountQuotaRepository = accountQuotaRepository;
	}

	@Override
	public ContainerQuota find(AbstractDomain domain, ContainerQuotaType containerQuotaType) {
		return repository.find(domain, containerQuotaType);
	}

	@Override
	public List<ContainerQuota> findAll(AbstractDomain domain) {
		return repository.findAll(domain);
	}

	@Override
	public List<ContainerQuota> findAll() {
		return repository.findAll();
	}

	@Override
	public ContainerQuota find(String uuid) {
		return repository.find(uuid);
	}

	@Override
	public ContainerQuota create(ContainerQuota entity) throws BusinessException {
		AbstractDomain domain = entity.getDomain();
		ContainerQuotaType containerQuotaType = entity.getContainerQuotaType();
		if (find(domain, containerQuotaType) != null) {
			throw new BusinessException("It must be only one EnsembleQuota for any entity");
		} else {
			return repository.create(entity);
		}
	}

	@Override
	public ContainerQuota update(ContainerQuota entity, ContainerQuota dto) throws BusinessException {
		// quota
		if (needToRestore(entity.getQuotaOverride(), dto.getQuotaOverride())) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			dto.setQuota(ancestor.getDefaultQuota());
		}
		entity.setQuota(dto.getQuota());
		entity.setQuotaOverride(dto.getQuotaOverride());

		// maintenance
		if (!entity.getMaintenance().equals(dto.getMaintenance())) {
			repository.cascadeMaintenanceMode(entity, dto.getMaintenance());
		}
		entity.setMaintenance(dto.getMaintenance());

		cascadeMaxFileSize(entity, dto);
		cascadeAccountQuota(entity, dto);
		cascadeDefaultQuota(entity, dto);
		cascadeDefaultMaxFileSize(entity, dto);
		cascadeDefaultAccountQuota(entity, dto);

		return repository.update(entity);
	}

	private void cascadeDefaultAccountQuota(ContainerQuota entity, ContainerQuota dto) {
		Long toDefaultAccountQuota = dto.getDefaultAccountQuota();
		Boolean toDefaultAccountQuotaOverride = dto.getDefaultAccountQuotaOverride();
		if (needToRestore(entity.getDefaultAccountQuotaOverride(), toDefaultAccountQuotaOverride)) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			toDefaultAccountQuota = ancestor.getDefaultAccountQuota();
		}
		if (needCascade(entity.getDefaultAccountQuota(), toDefaultAccountQuota,
				entity.getDefaultAccountQuotaOverride(), toDefaultAccountQuotaOverride)) {
			repository.cascadeDefaultAccountQuota(entity.getDomain(), toDefaultAccountQuota, entity.getContainerQuotaType());
		}
		entity.setDefaultAccountQuota(toDefaultAccountQuota);
		entity.setDefaultAccountQuotaOverride(toDefaultAccountQuotaOverride);
	}

	private void cascadeAccountQuota(ContainerQuota entity, ContainerQuota dto) {
		Long toAccountQuota = dto.getAccountQuota();
		Boolean toAccountQuotaOverride = dto.getAccountQuotaOverride();
		if (needToRestore(entity.getAccountQuotaOverride(), toAccountQuotaOverride)) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			toAccountQuota = ancestor.getAccountQuota();
		}
		if (needCascade(entity.getAccountQuota(), toAccountQuota,
				entity.getAccountQuotaOverride(), toAccountQuotaOverride)) {
			repository.cascadeAccountQuota(entity, toAccountQuota);
		}
		entity.setAccountQuota(toAccountQuota);
		entity.setAccountQuotaOverride(toAccountQuotaOverride);
	}

	private void cascadeDefaultQuota(ContainerQuota entity, ContainerQuota dto) {
		Long toDefaultQuota = dto.getDefaultQuota();
		Boolean toDefaultQuotaOverride = dto.getDefaultQuotaOverride();
		if (needToRestore(entity.getDefaultQuotaOverride(), toDefaultQuotaOverride)) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			toDefaultQuota = ancestor.getDefaultQuota();
		}
		if (needCascade(entity.getDefaultQuota(), toDefaultQuota,
				entity.getDefaultQuotaOverride(), toDefaultQuotaOverride)) {
			repository.cascadeDefaultQuota(entity.getDomain(), toDefaultQuota, entity.getContainerQuotaType());
		}
		entity.setDefaultQuota(toDefaultQuota);
		entity.setDefaultQuotaOverride(toDefaultQuotaOverride);
	}

	private void cascadeDefaultMaxFileSize(ContainerQuota entity, ContainerQuota dto) {
		Long toDefaultMaxFileSize = dto.getDefaultMaxFileSize();
		Boolean toDefaultMaxFileSizeOverride = dto.getDefaultMaxFileSizeOverride();
		if (needToRestore(entity.getDefaultMaxFileSizeOverride(), toDefaultMaxFileSizeOverride)) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			toDefaultMaxFileSize = ancestor.getDefaultMaxFileSize();
		}
		if (needCascade(entity.getDefaultMaxFileSize(), toDefaultMaxFileSize,
				entity.getDefaultMaxFileSizeOverride(), toDefaultMaxFileSizeOverride)) {
			repository.cascadeDefaultMaxFileSize(entity.getDomain(), toDefaultMaxFileSize, entity.getContainerQuotaType());
		}
		entity.setDefaultMaxFileSize(toDefaultMaxFileSize);
		entity.setDefaultMaxFileSizeOverride(toDefaultMaxFileSizeOverride);
	}

	private void cascadeMaxFileSize(ContainerQuota entity, ContainerQuota dto) {
		Long toMaxFileSize = dto.getMaxFileSize();
		Boolean toMaxFileSizeOverride = dto.getMaxFileSizeOverride();
		if (needToRestore(entity.getMaxFileSizeOverride(), toMaxFileSizeOverride)) {
			ContainerQuota ancestor = repository.find(entity.getParentDomain(), entity.getContainerQuotaType());
			toMaxFileSize = ancestor.getMaxFileSize();
		}
		if (needCascade(entity.getMaxFileSize(), toMaxFileSize,
				entity.getMaxFileSizeOverride(), toMaxFileSizeOverride)) {
			repository.cascadeMaxFileSize(entity, toMaxFileSize);
		}
		entity.setMaxFileSize(toMaxFileSize);
		entity.setMaxFileSizeOverride(toMaxFileSizeOverride);
	}

	@Override
	public ContainerQuota updateByBatch(ContainerQuota quota) throws BusinessException {
		Long sumCurrentValue = accountQuotaRepository.sumOfCurrentValue(quota);
		Long usedSpace = quota.getCurrentValue();
		quota.setLastValue(usedSpace);
		quota.setCurrentValue(usedSpace + sumCurrentValue);
		quota = repository.updateByBatch(quota);
		return quota;
	}
}
