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
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountQuotaBusinessServiceImpl extends GenericQuotaBusinessServiceImpl implements AccountQuotaBusinessService {

	final private static Logger logger = LoggerFactory
			.getLogger(AccountQuotaBusinessServiceImpl.class);

	protected final AccountQuotaRepository repository;

	protected final OperationHistoryRepository operationHistoryRepository;

	protected final ContainerQuotaRepository containerQuotaRepository;

	public AccountQuotaBusinessServiceImpl(final AccountQuotaRepository repository,
			final OperationHistoryRepository operationHistoryRepository,
			final ContainerQuotaRepository containerQuotaRepository) {
		this.repository = repository;
		this.operationHistoryRepository = operationHistoryRepository;
		this.containerQuotaRepository = containerQuotaRepository;
	}

	@Override
	public AccountQuota find(String uuid) throws BusinessException {
		return repository.find(uuid);
	}

	@Override
	public AccountQuota find(Account account) throws BusinessException {
		return repository.find(account);
	}

	@Override
	public AccountQuota createOrUpdate(Account account, Date date) throws BusinessException {
		Long sumOperationValue = operationHistoryRepository.sumOperationValue(account, null, date, null, null);
		AccountQuota entity = find(account);
		if (entity == null) {
			ContainerQuota cq = containerQuotaRepository.find(account.getDomain(), account.getContainerQuotaType());
			if (cq == null) {
				throw new BusinessException(
						account.getDomain().getUuid() + " domain does not have a ensemble quota yet");
			}
			logger.debug("container : " + cq.toString());
			entity = new AccountQuota(
						account.getDomain(),
						account.getDomain().getParentDomain(),
						account, cq);
			entity = repository.create(entity);
		}
		Long usedSpace = entity.getCurrentValue();
		entity.setLastValue(usedSpace);
		entity.setCurrentValue(sumOperationValue + usedSpace);
		entity = repository.updateByBatch(entity);
		return entity;
	}

	@Override
	public AccountQuota create(AccountQuota entity) throws BusinessException {
		if (find(entity.getAccount()) != null) {
			throw new BusinessException("It must be only one AccountQuota for any entity");
		} else {
			return repository.create(entity);
		}
	}

	@Override
	public AccountQuota update(AccountQuota entity, AccountQuota dto) throws BusinessException {
		if (needToRestore(entity.getMaxFileSizeOverride(), dto.getMaxFileSizeOverride())) {
			ContainerQuota ancestor = containerQuotaRepository.find(entity.getDomain(),
					entity.getAccount().getContainerQuotaType());
			dto.setMaxFileSize(ancestor.getMaxFileSize());
		}
		entity.setMaxFileSize(dto.getMaxFileSize());
		entity.setMaxFileSizeOverride(dto.getMaxFileSizeOverride());
		if (needToRestore(entity.getQuotaOverride(), dto.getQuotaOverride())) {
			ContainerQuota ancestor = containerQuotaRepository.find(entity.getDomain(),
					entity.getAccount().getContainerQuotaType());
			dto.setQuota(ancestor.getDefaultAccountQuota());
		}
		entity.setQuota(dto.getQuota());
		entity.setQuotaOverride(dto.getQuotaOverride());

		return repository.update(entity);
	}

	@Override
	public List<String> findDomainUuidByBatchModificationDate(Date startDate) {
		return repository.findDomainUuidByBatchModificationDate(startDate);
	}

	@Override
	public List<AccountQuota> findAll() throws BusinessException {
		return repository.findAll();
	}
}
