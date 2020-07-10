/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.upgrade.v2_2;

import java.util.List;
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilterOLD;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadPropositionFilterRepository;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.mongo.entities.UploadPropositionRule;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.mongo.repository.UploadPropositionFilterMongoRepository;

public class MigrateUploadPropositionFilterToMongoUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected UploadPropositionFilterRepository uploadPropositionFilterRepository;

	protected UploadPropositionFilterMongoRepository uploadPropositionFilterMongoRepository;

	public MigrateUploadPropositionFilterToMongoUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			UploadPropositionFilterRepository uploadPropositionFilterRepository,
			UploadPropositionFilterMongoRepository uploadPropositionFilterMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.uploadPropositionFilterRepository = uploadPropositionFilterRepository;
		this.uploadPropositionFilterMongoRepository = uploadPropositionFilterMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<UploadPropositionFilterOLD> oldFilters = uploadPropositionFilterRepository.findAll();
		return oldFilters.stream().map(UploadPropositionFilterOLD::getUuid).collect(Collectors.toList());
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		UploadPropositionFilterOLD oldFilter = uploadPropositionFilterRepository.find(identifier);
		BatchResultContext<UploadPropositionFilterOLD> res = new BatchResultContext<UploadPropositionFilterOLD>(
				oldFilter);
		console.logDebug(batchRunContext, total, position,
				"Processing UploadPropositionFilter : " + oldFilter.toString());
		createNewUploadPropositionFilter(oldFilter);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadPropositionFilterOLD> res = (BatchResultContext<UploadPropositionFilterOLD>) context;
		UploadPropositionFilterOLD resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + "has been moved to mongo database");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadPropositionFilterOLD> res = (BatchResultContext<UploadPropositionFilterOLD>) exception
				.getContext();
		UploadPropositionFilterOLD resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + "failed",
				batchRunContext);
		logger.error("Error occured while migrating the UploadPropositionFilter : " + resource, exception);
	}

	private void createNewUploadPropositionFilter(UploadPropositionFilterOLD oldFilter) {
		List<UploadPropositionRule> rules = oldFilter.getRules().stream().map(oldRule -> convertRule(oldRule))
				.collect(Collectors.toList());
		UploadPropositionActionType actionType = oldFilter.getActions().iterator().next().getActionType();
		UploadPropositionFilter newFilter = new UploadPropositionFilter(oldFilter.getUuid(),
				oldFilter.getDomain().getUuid(), oldFilter.getName(), oldFilter.getMatch(), actionType,
				oldFilter.isEnable(), oldFilter.getOrder(), rules, oldFilter.getCreationDate(),
				oldFilter.getModificationDate());
		uploadPropositionFilterMongoRepository.insert(newFilter);
	}

	private UploadPropositionRule convertRule(
			org.linagora.linshare.core.domain.entities.UploadPropositionRule oldRule) {
		UploadPropositionRule newRule = new UploadPropositionRule();
		newRule.setField(oldRule.getField());
		newRule.setOperator(oldRule.getOperator());
		newRule.setValue(oldRule.getValue());
		return newRule;
	}
}
