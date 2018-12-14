/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.upgrade.v2_3;

import java.util.List;
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.JwtLongTimeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class NewPermanentTokenStructureUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected JwtLongTimeMongoRepository jwtLongTimeMongoRepository;
	protected UserRepository<User> actorRepository;
	protected AbstractDomainRepository abstractDomainRepository;

	public NewPermanentTokenStructureUpgradeTaskImpl(JwtLongTimeMongoRepository jwtLongTimeMongoRepository,
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			UserRepository<User> actorRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.jwtLongTimeMongoRepository = jwtLongTimeMongoRepository;
		this.actorRepository = actorRepository;
		this.abstractDomainRepository = abstractDomainRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_3_MIGRATE_PERMANENT_TOKEN_ENTITY_TO_NEW_STRUCTURE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<PermanentToken> tokens = jwtLongTimeMongoRepository.findAll();
		return tokens.stream().map(PermanentToken::getUuid).collect(Collectors.toList());
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		PermanentToken permanentToken = jwtLongTimeMongoRepository.findByUuid(identifier);
		BatchResultContext<PermanentToken> res = new BatchResultContext<PermanentToken>(permanentToken);
		console.logDebug(batchRunContext, total, position, "Processing audit : " + permanentToken.toString());
		Account actor = actorRepository.findByMail(permanentToken.getSubject());
		PermanentToken newPermanentToken = new PermanentToken(permanentToken.getUuid(),
				permanentToken.getCreationDate(), permanentToken.getIssuer(), permanentToken.getLabel(),
				permanentToken.getDescription(), new GenericLightEntity(actor.getLsUuid(), actor.getFullName()),
				permanentToken.getSubject(), new GenericLightEntity(actor.getDomain()));
		jwtLongTimeMongoRepository.delete(permanentToken);
		jwtLongTimeMongoRepository.insert(newPermanentToken);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<PermanentToken> res = (BatchResultContext<PermanentToken>) context;
		PermanentToken resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<PermanentToken> res = (BatchResultContext<PermanentToken>) exception.getContext();
		PermanentToken resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed",
				batchRunContext);
		logger.error("Error occured while migrating the permanent_Token : " + resource, exception);
	}

}
