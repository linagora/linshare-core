/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.upgrade.v2_0;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class AccountQuotaUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected AbstractDomainRepository repository;

	protected AccountQuotaBusinessService accountQuotaBusinessService;

	protected ContainerQuotaBusinessService containerQuotaBusinessService;

	protected DocumentEntryRepository documentEntryRepository;

	protected ThreadEntryRepository threadEntryRepository;

	protected OperationHistoryBusinessService operationHistoryBusinessService;

	public AccountQuotaUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AccountQuotaBusinessService accountQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			DocumentEntryRepository documentEntryRepository,
			ThreadEntryRepository threadEntryRepository,
			OperationHistoryBusinessService operationHistoryBusinessService,
			AbstractDomainRepository repository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.documentEntryRepository = documentEntryRepository;
		this.threadEntryRepository = threadEntryRepository;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_0_ACCOUNT_QUOTA;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> uuids = accountRepository.findAllAccountWithMissingQuota();
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Account account = accountRepository.findByLsUuid(identifier);
		BatchResultContext<Account> res = new BatchResultContext<Account>(account);
		res.setProcessed(false);
		if (account == null) {
			res.setIdentifier(identifier);
			return res;
		}
		if (account.isInternal() || account.isGuest() || account.isWorkGroup()) {
			ContainerQuotaType quotaType = ContainerQuotaType.USER;
			long usedSpace = 0L;
			if (account.isWorkGroup()) {
				quotaType = ContainerQuotaType.WORK_GROUP;
				usedSpace = threadEntryRepository.getUsedSpace(account);
			} else {
				usedSpace = documentEntryRepository.getUsedSpace(account);
			}
			ContainerQuota containerQuota = containerQuotaBusinessService.find(account.getDomain(), quotaType);
			if (containerQuota == null) {
				throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "No container quota found for the domain : " + account.getDomainId());
			}
			AccountQuota userQuota = new AccountQuota(
					account.getDomain(),
					account.getDomain().getParentDomain(),
					account, containerQuota);
			accountQuotaBusinessService.create(userQuota);
			if (usedSpace > 0) {
				OperationHistory oh = new OperationHistory(account, account.getDomain(), usedSpace, OperationHistoryTypeEnum.CREATE, quotaType);
				operationHistoryBusinessService.create(oh);
			}
			res.setProcessed(true);
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) context;
		Account resource = res.getResource();
		if (resource != null) {
			if (res.getProcessed()) {
				logInfo(batchRunContext, total, position, "Quota was created for: " + resource.toString());
			} else {
				logInfo(batchRunContext, total, position, "Account skipped : " + resource.toString());
			}
		} else {
			logInfo(batchRunContext, total, position, "Account quota creation skipped, can not find related account : " + res.getIdentifier());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) exception.getContext();
		Account resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing domain : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
