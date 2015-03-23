package org.linagora.linshare.core.batches.impl;

import java.util.Set;

import org.linagora.linshare.core.batches.UploadRequestEntryUrlBatch;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestEntryUrlBatchImpl implements
		UploadRequestEntryUrlBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestEntryUrlBatch.class);

	protected UploadRequestEntryUrlService service;

	public UploadRequestEntryUrlBatchImpl(
			UploadRequestEntryUrlService uploadRequestEntryUrlService) {
		super();
		this.service = uploadRequestEntryUrlService;
	}

	@Override
	public Set<UploadRequestEntryUrl> getAll(SystemAccount systemAccount) {
		logger.info("UploadRequestEntryUrlBatchImpl job starting ...");
		Set<UploadRequestEntryUrl> allExpired = service
				.findAllExpired(systemAccount);
		logger.info(allExpired.size()
				+ " upload request entrie(s) url have been found to be removed");
		return allExpired;
	}

	@Override
	public BatchResultContext<UploadRequestEntryUrl> execute(
			SystemAccount systemAccount, UploadRequestEntryUrl resource)
			throws BatchBusinessException, BusinessException {
		BatchResultContext<UploadRequestEntryUrl> context = new BatchResultContext<UploadRequestEntryUrl>(
				resource);
		try {
			logger.info("processing uREUrl : " + resource.getUuid());
			service.deleteUploadRequestEntryUrl(systemAccount, resource);
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
	public void notify(SystemAccount systemAccount,
			BatchResultContext<UploadRequestEntryUrl> context) {
		logger.info(
				"Outdated uREUrl was successfully removed after cleaning : ",
				context.getResource().getUuid());
	}

	@Override
	public void notifyError(SystemAccount systemAccount,
			BatchBusinessException exception) {
		logger.error(
				"An error occured while cleaning outdated upload request entry url. A BatchBusinessException has been thrown ",
				exception);
	}

	@Override
	public void terminate(SystemAccount systemAccount,
			Set<UploadRequestEntryUrl> all) {
		logger.info(all.size()
				+ " upload request entrie(s) url have been cleaned.");
		logger.info("UploadRequestEntryUrlBatchImpl job terminated.");

	}
}
