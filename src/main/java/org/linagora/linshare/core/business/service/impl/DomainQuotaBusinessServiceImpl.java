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

import java.util.Date;

import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;

public class DomainQuotaBusinessServiceImpl
		implements DomainQuotaBusinessService {

	private final DomainQuotaRepository repository;
	private final OperationHistoryRepository operationHistoryRepository;

	public DomainQuotaBusinessServiceImpl(
			final DomainQuotaRepository domainQuotaRepository, final OperationHistoryRepository operationHistoryRepository) {
		this.repository = domainQuotaRepository;
		this.operationHistoryRepository = operationHistoryRepository;
	}

	@Override
	public DomainQuota find(AbstractDomain domain) throws BusinessException {
		return repository.find(domain);
	}

	@Override
	public boolean exist(AbstractDomain domain) {
		return find(domain) != null;
	}

	@Override
	public DomainQuota createOrUpdate(AbstractDomain domain, Date today) {
		long sumOperationValue = operationHistoryRepository.sumOperationValue(null, domain, today, null, null);
		DomainQuota entity;
		if (!exist(domain)) {
			DomainQuota parentDomainQuota = repository.find(domain.getParentDomain());
			if (parentDomainQuota != null) {
				Long quota = parentDomainQuota.getQuota();
				Long quotaWarning = parentDomainQuota.getQuotaWarning();
				Long tailFileMax = parentDomainQuota.getFileSizeMax();
				entity = new DomainQuota(domain, domain.getParentDomain(), quota, quotaWarning, tailFileMax,
						sumOperationValue, (long) 0);
				entity = repository.create(entity);
			} else {
				throw new BusinessException(" parent domain of " + domain.getIdentifier()+" does not have a quota yet");
			}
		} else {
			entity = find(domain);
			entity = repository.update(entity, sumOperationValue);
		}
		return entity;
	}

	@Override
	public DomainQuota create(DomainQuota entity) throws BusinessException {
		AbstractDomain domain = entity.getDomain();
		if (exist(domain)) {
			throw new BusinessException("It must be only one DomainQuota for any entity");
		}else{
			return repository.create(entity);
		}
	}

	@Override
	public DomainQuota update(DomainQuota entity, Long sumOperationValue) throws BusinessException {
		return repository.update(entity, sumOperationValue);
	}

	@Override
	public DomainQuota update(DomainQuota entity) throws BusinessException {
		return repository.update(entity);
	}
}
