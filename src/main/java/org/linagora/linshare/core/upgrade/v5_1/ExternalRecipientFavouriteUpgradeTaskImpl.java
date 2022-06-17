/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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