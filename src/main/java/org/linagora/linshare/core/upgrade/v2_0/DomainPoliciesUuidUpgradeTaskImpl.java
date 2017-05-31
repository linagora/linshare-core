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
import java.util.UUID;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class DomainPoliciesUuidUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected DomainPolicyRepository repository;

	public DomainPoliciesUuidUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			DomainPolicyRepository repository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_0_DOMAIN_POLICIES_UUID;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return repository.findAllIdentifiers();
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		DomainPolicy policy = repository.findById(identifier);
		BatchResultContext<DomainPolicy> res = new BatchResultContext<DomainPolicy>(policy);
		if (LinShareConstants.defaultDomainPolicyIdentifier.equals(policy.getLabel())) {
			policy.setUuid(LinShareConstants.defaultDomainPolicyIdentifier);
		} else {
			policy.setUuid(UUID.randomUUID().toString());
		}
		repository.update(policy);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<DomainPolicy> res = (BatchResultContext<DomainPolicy>) context;
		DomainPolicy resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<String> res = (BatchResultContext<String>) exception.getContext();
		String resource = res.getResource();
		logError(total, position, "The upgrade task : " + resource + " failed.", batchRunContext);
		logger.error("Error occured while updating the document : "
				+ resource +
				". BatchBusinessException", exception);
	}

}
