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
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.UploadPropositionOLD;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadPropositionRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.mongo.entities.UploadProposition;
import org.linagora.linshare.mongo.entities.UploadPropositionContact;
import org.linagora.linshare.mongo.entities.logs.UploadPropositionAuditLogEntry;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.mongo.repository.UploadPropositionMongoRepository;

public class MigrateUploadPropositionToMongoUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected UserRepository<User> userRepository;

	protected AbstractDomainRepository abstractDomainRepository;

	protected UploadPropositionRepository uploadPropositionRepository;

	protected UploadPropositionMongoRepository uploadPropositionMongoRepository;

	protected TechnicalAccountService technicalAccountService;

	protected LogEntryService logEntryService;

	public MigrateUploadPropositionToMongoUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository, UserRepository<User> userRepository,
			AbstractDomainRepository abstractDomainRepository,
			UploadPropositionRepository uploadPropositionRepository,
			UploadPropositionMongoRepository uploadPropositionMongoRepository,
			TechnicalAccountService technicalAccountService,
			LogEntryService logEntryService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.userRepository = userRepository;
		this.abstractDomainRepository = abstractDomainRepository;
		this.uploadPropositionRepository = uploadPropositionRepository;
		this.uploadPropositionMongoRepository = uploadPropositionMongoRepository;
		this.technicalAccountService = technicalAccountService;
		this.logEntryService = logEntryService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<UploadPropositionOLD> oldUploadPropositions = uploadPropositionRepository.findAll();
		return oldUploadPropositions.stream().map(UploadPropositionOLD::getUuid).collect(Collectors.toList());
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		UploadPropositionOLD oldUploadProposition = uploadPropositionRepository.findByUuid(identifier);
		BatchResultContext<UploadPropositionOLD> res = new BatchResultContext<UploadPropositionOLD>(
				oldUploadProposition);
		console.logDebug(batchRunContext, total, position,
				"Processing UploadProposition : " + oldUploadProposition.toString());
		createNewUploadProposition(oldUploadProposition);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadPropositionOLD> res = (BatchResultContext<UploadPropositionOLD>) context;
		UploadPropositionOLD resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + "has been moved to mongo database");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<UploadPropositionOLD> res = (BatchResultContext<UploadPropositionOLD>) exception
				.getContext();
		UploadPropositionOLD resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed",
				batchRunContext);
		logger.error("Error occured while migrating the UploadProposition : " + resource, exception);
	}

	private void createNewUploadProposition(UploadPropositionOLD oldUploadProposition) {
		if (oldUploadProposition.getStatus() == null
				|| UploadPropositionStatus.SYSTEM_PENDING.equals(oldUploadProposition.getStatus())) {
			oldUploadProposition.setStatus(UploadPropositionStatus.USER_PENDING);
		}
		Account targetedAccount = userRepository.findByMail(oldUploadProposition.getRecipientMail());
		TechnicalAccount technicalActor = technicalAccountService.create(targetedAccount, initTechnicalAccount());
		UploadProposition uploadProposition = new UploadProposition(oldUploadProposition.getUuid(),
				oldUploadProposition.getDomain().getUuid(), oldUploadProposition.getStatus(),
				oldUploadProposition.getSubject(), oldUploadProposition.getBody(),
				new UploadPropositionContact(oldUploadProposition.getFirstName(), oldUploadProposition.getLastName(),
						oldUploadProposition.getMail()),
				targetedAccount.getLsUuid(), oldUploadProposition.getCreationDate(),
				oldUploadProposition.getModificationDate());
		UploadProposition created = uploadPropositionMongoRepository.insert(uploadProposition);
		UploadPropositionAuditLogEntry log = new UploadPropositionAuditLogEntry(technicalActor, targetedAccount,
				LogAction.CREATE, AuditLogEntryType.UPLOAD_PROPOSITION, created.getUuid(), created);
		logEntryService.insert(log);
		technicalAccountService.delete(targetedAccount, technicalActor);
	}

	private TechnicalAccount initTechnicalAccount() {
		TechnicalAccount technicalAccount = new TechnicalAccount();
		technicalAccount.setLastName("Technical Account for UploadProposition");
		technicalAccount.setPassword("secret");
		technicalAccount.setLocale(SupportedLanguage.ENGLISH);
		return technicalAccount;
	}
}
