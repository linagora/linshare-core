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

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.linagora.linshare.core.business.service.TechnicalAccountBusinessService;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.TechnicalAccountRepository;

import com.google.common.collect.Sets;

public class TechnicalAccountBusinessServiceImpl implements
		TechnicalAccountBusinessService {

	private final AbstractDomainRepository abstractDomainRepository;

	private final TechnicalAccountRepository technicalAccountRepository;
	
	public TechnicalAccountBusinessServiceImpl(
			AbstractDomainRepository abstractDomainRepository,
			TechnicalAccountRepository technicalAccountRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.technicalAccountRepository = technicalAccountRepository;
	}

	@Override
	public TechnicalAccount find(String uuid) {
		return technicalAccountRepository.findByLsUuid(uuid);
	}

	@Override
	public Set<TechnicalAccount> findAll(String domainId) {
		List<TechnicalAccount> list = technicalAccountRepository.findByDomain(domainId);
		// Dirty hook.
		Set<TechnicalAccount> res = Sets.newHashSet();
		res.addAll(list);
		return res;
	}

	@Override
	public TechnicalAccount create(String domainId, TechnicalAccount account) throws BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		if (!domain.isRootDomain()) {
			throw new BusinessException(BusinessErrorCode.TECHNICAL_ACCOUNT_FORBIDEN,
					"Technical account should be created only in the root domain");
		}
		account.setCmisLocale(SupportedLanguage.toLanguage(domain.getDefaultTapestryLocale()).getTapestryLocale());
		account.setCreationDate(new Date());
		account.setModificationDate(new Date());
		if (account.getMail() != null) {
			if (technicalAccountRepository.findByMailAndDomain(domainId, account.getMail()) != null) {
				throw new BusinessException(BusinessErrorCode.TECHNICAL_ACCOUNT_ALREADY_EXISTS,
						"Technical account already exists");
			}
		} else {
			account.setMail(UUID.randomUUID().toString());
		} 
		return this.create(domain, account);
	}

	@Override
	public TechnicalAccount create(AbstractDomain domain,
			TechnicalAccount account) throws BusinessException {
		account.setDomain(domain);
		return technicalAccountRepository.create(account);
	}

	@Override
	public TechnicalAccount update(TechnicalAccount account)
			throws BusinessException {
		return technicalAccountRepository.update(account);
	}

	@Override
	public void delete(TechnicalAccount account) throws BusinessException {
		technicalAccountRepository.delete(account);
	}
}
