package org.linagora.linshare.core.batches.impl;

import java.util.Set;

import org.linagora.linshare.core.batches.UploadRequestEntryUrlBatch;
import org.linagora.linshare.core.batches.generics.impl.GenericBatchImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.ResourceContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestEntryUrlBatchImpl extends
		GenericBatchImpl<UploadRequestEntryUrl> implements
		UploadRequestEntryUrlBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestEntryUrlBatch.class);

	protected UploadRequestEntryUrlService service;

	public UploadRequestEntryUrlBatchImpl(
			UploadRequestEntryUrlService uploadRequestEntryUrlService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = uploadRequestEntryUrlService;
	}

	@Override
	public Set<UploadRequestEntryUrl> getAll() {
		logger.info("UploadRequestEntryUrlBatchImpl job starting ...");
		Set<UploadRequestEntryUrl> allExpired = service
				.findAllExpired(getSystemAccount());
		logger.info("The system has found " + allExpired.size()
				+ " expired upload request entrie(s) url");
		return allExpired;
	}

	@Override
	public BatchResultContext<UploadRequestEntryUrl> execute(Context c,
			long total, long position) throws BatchBusinessException,
			BusinessException {
		UploadRequestEntryUrl resource = getResource(c);
		BatchResultContext<UploadRequestEntryUrl> context = new BatchResultContext<UploadRequestEntryUrl>(
				resource);
		try {
			logInfo(total, position,
					"processing uREUrl : " + resource.getUuid());
			service.deleteUploadRequestEntryUrl(getSystemAccount(), resource);
		} catch (BusinessException businessException) {
			String msg = "Error while trying to delete outdated upload request entry url ";
			logError(total, position, msg);
			logger.error(msg, businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, msg);
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchResultContext<UploadRequestEntryUrl> context,
			long total, long position) {
		logInfo(total, position, "Outdated uREUrl was successfully removed : "
				+ context.getResource().getUuid());
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			UploadRequestEntryUrl resource, long total,
			long position) {
		logError(total, position, "cleaning eREUrl has failed : " + resource.getUuid());
		logger.error(
				"An error occured while cleaning outdated upload request entry url "
						+ resource.getUuid()
						+ ". A BatchBusinessException has been thrown ",
				exception);
	}

	@Override
	public void terminate(Set<UploadRequestEntryUrl> all,
			long errors, long unhandled_errors, long total) {
		long success = total - errors - unhandled_errors;
		logger.info(success
				+ " upload request entrie(s) url have been removed.");
		if (errors > 0) {
			logger.error(errors
					+ " upload request entrie(s) url failed to be removed.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " upload request entrie(s) url failed to be removed (unhandled error).");
		}
		logger.info("UploadRequestEntryUrlBatchImpl job terminated.");
	}

	@Override
	public UploadRequestEntryUrl getResource(Context c) {
		@SuppressWarnings("unchecked")
		ResourceContext<UploadRequestEntryUrl> rc = (ResourceContext<UploadRequestEntryUrl>)c;
		return rc.getRessource();
	}
}
