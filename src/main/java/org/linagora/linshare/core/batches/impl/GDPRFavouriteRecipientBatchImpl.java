/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

	public GDPRFavouriteRecipientBatchImpl(
			AccountRepository<Account> accountRepository,
			TimeService timeService,
			GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository,
			RecipientFavouriteRepository recipientFavouriteRepository) {
		super(accountRepository);
		this.timeService = timeService;
		this.GDPRExternalRecipientFavouriteRepository = GDPRExternalRecipientFavouriteRepository;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return GDPRExternalRecipientFavouriteRepository.findUuidByExpirationDateLessThan(timeService.previousYear());
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
