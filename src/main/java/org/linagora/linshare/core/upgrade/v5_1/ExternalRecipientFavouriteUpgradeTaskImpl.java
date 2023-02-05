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
package org.linagora.linshare.core.upgrade.v5_1;

import java.util.List;
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GDPRExternalRecipientFavourite;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GDPRExternalRecipientFavouriteRepository;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class ExternalRecipientFavouriteUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final RecipientFavouriteRepository recipientFavouriteRepository;
	private final GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository;
	private final UserRepository<User> userRepository;
	private final boolean gdprActivated;

	public ExternalRecipientFavouriteUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			RecipientFavouriteRepository recipientFavouriteRepository,
			GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository,
			UserRepository<User> userRepository,
			boolean gdprActivated) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.recipientFavouriteRepository = recipientFavouriteRepository;
		this.GDPRExternalRecipientFavouriteRepository = GDPRExternalRecipientFavouriteRepository;
		this.userRepository = userRepository;
		this.gdprActivated = gdprActivated;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.OPTIONAL_POPULATE_EXTERNAL_FAVOURITE_RECIPIENT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return recipientFavouriteRepository.findAll()
			.stream()
			.map(RecipientFavourite::getPersistenceId)
			.map(String::valueOf)
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public boolean needToRun() {
		return gdprActivated;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		RecipientFavourite recipientFavourite = recipientFavouriteRepository.findById(Integer.valueOf(identifier));
		BatchResultContext<RecipientFavourite> batchResultContext = new BatchResultContext<>(recipientFavourite);
		batchResultContext.setProcessed(false);
		if (userRepository.findByMail(recipientFavourite.getRecipient()) == null) {
			GDPRExternalRecipientFavourite GDPRExternalRecipientFavourite = new GDPRExternalRecipientFavourite(recipientFavourite);
			if (GDPRExternalRecipientFavouriteRepository.load(GDPRExternalRecipientFavourite) == null) {
				GDPRExternalRecipientFavouriteRepository.create(GDPRExternalRecipientFavourite);
			}
		}
		batchResultContext.setProcessed(true);
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		BatchResultContext<RecipientFavourite> batchResultContext = (BatchResultContext<RecipientFavourite>) context;
		RecipientFavourite resource = batchResultContext.getResource();
		logInfo(batchRunContext, total, position, resource + " has been processed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		BatchResultContext<RecipientFavourite> batchResultContext = (BatchResultContext<RecipientFavourite>) exception.getContext();
		RecipientFavourite resource = batchResultContext.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while processing RecipientFavourite: {} . BatchBusinessException", resource, exception);
	}
}
