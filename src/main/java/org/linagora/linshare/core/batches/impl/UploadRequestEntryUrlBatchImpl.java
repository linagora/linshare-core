package org.linagora.linshare.core.batches.impl;

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.batches.UploadRequestEntryUrlBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestEntryUrlBatchImpl implements
		UploadRequestEntryUrlBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestEntryUrlBatch.class);

	protected UploadRequestEntryUrlService uploadRequestEntryUrlService;

	private AccountRepository<Account> accountRepository;

	public UploadRequestEntryUrlBatchImpl(
			UploadRequestEntryUrlService uploadRequestEntryUrlService,
			AccountRepository<Account> accountRepository) {
		super();
		this.uploadRequestEntryUrlService = uploadRequestEntryUrlService;
		this.accountRepository = accountRepository;
	}

	@Override
	public Set<UploadRequestEntryUrl> getAll() {
		SystemAccount actor = accountRepository.getBatchSystemAccount();
		Set<UploadRequestEntryUrl> allExpired = new HashSet<>(
				uploadRequestEntryUrlService
						.findAllExpiredUploadRequestEntryUrl(actor));
		return allExpired;
	}

	@Override
	public BatchResultContext<UploadRequestEntryUrl> execute(
			UploadRequestEntryUrl resource) throws BatchBusinessException,
			BusinessException {
		BatchResultContext<UploadRequestEntryUrl> context = new BatchResultContext<UploadRequestEntryUrl>(
				resource);
		try {
			SystemAccount actor = accountRepository.getBatchSystemAccount();
			Set<UploadRequestEntryUrl> all = getAll();
			logger.info(all.size()
					+ " upload request entrie(s) url have been found to be removed");
			for (UploadRequestEntryUrl uREUrl : all) {
				uploadRequestEntryUrlService.deleteUploadRequestEntryUrl(actor,
						uREUrl);
				logger.info("uREUrl removed: " + uREUrl.getUuid());
			}
		} catch (BusinessException businessException) {
			logger.error(
					"Error while trying to delete outdated upload request entry url ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context,
					"Error while trying to delete outdated upload request entry url");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchResultContext<UploadRequestEntryUrl> context) {
		logger.info("notification after cleaning outdated ureurl success ",
				context.getResource());
	}

	@Override
	public void notifyError(BatchBusinessException exception) {
		logger.error("Error notification BatchBusinessException ", exception);
	}
}
