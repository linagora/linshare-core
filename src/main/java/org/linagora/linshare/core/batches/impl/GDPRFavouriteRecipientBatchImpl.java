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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GDPRExternalRecipientFavourite;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GDPRExternalRecipientFavouriteRepository;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.service.TimeService;

public class GDPRFavouriteRecipientBatchImpl extends GenericBatchImpl {

	private final TimeService timeService;
	private final GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository;
	private final RecipientFavouriteRepository recipientFavouriteRepository;
	private final boolean gdprActivated;

	public GDPRFavouriteRecipientBatchImpl(
			AccountRepository<Account> accountRepository,
			TimeService timeService,
			GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository,
			RecipientFavouriteRepository recipientFavouriteRepository,
			boolean gdprActivated) {
		super(accountRepository);
		this.timeService = timeService;
		this.GDPRExternalRecipientFavouriteRepository = GDPRExternalRecipientFavouriteRepository;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
		this.gdprActivated = gdprActivated;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return GDPRExternalRecipientFavouriteRepository.findUuidByExpirationDateLessThan(timeService.previousYear());
	}

	@Override
	public boolean needToRun() {
		return gdprActivated;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position) throws BatchBusinessException, BusinessException {
		GDPRExternalRecipientFavourite GDPRExternalRecipientFavourite = GDPRExternalRecipientFavouriteRepository.findByUuid(identifier);
		ResultContext resultContext = new BatchResultContext<>(GDPRExternalRecipientFavourite);

		RecipientFavourite recipientFavourite = recipientFavouriteRepository.findById(GDPRExternalRecipientFavourite.getRecipientFavouritePersistenceId());
		recipientFavourite.setRecipient(GDPRConstants.MAIL_ANONYMIZATION);
		recipientFavouriteRepository.delete(recipientFavourite);
		GDPRExternalRecipientFavouriteRepository.delete(GDPRExternalRecipientFavourite);

		resultContext.setProcessed(true);
		return resultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		BatchResultContext<GDPRExternalRecipientFavourite> resultContext = (BatchResultContext) context;
		GDPRExternalRecipientFavourite GDPRExternalRecipientFavourite = resultContext.getResource();
		console.logInfo(batchRunContext, total, position, "The Recipient Favourite " + GDPRExternalRecipientFavourite.getUuid() + " has been deleted.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		BatchResultContext<GDPRExternalRecipientFavourite> resultContext = (BatchResultContext) exception.getContext();
		GDPRExternalRecipientFavourite GDPRExternalRecipientFavourite = resultContext.getResource();
		console.logError(batchRunContext, total, position, "Anonymization for Recipient Favourite " + GDPRExternalRecipientFavourite.getUuid() + " has failed.");
	}
}
