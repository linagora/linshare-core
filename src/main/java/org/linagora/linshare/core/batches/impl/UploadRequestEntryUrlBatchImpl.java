package org.linagora.linshare.core.batches.impl;

import java.util.Set;

import org.linagora.linshare.core.batches.UploadRequestEntryUrlBatch;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;

public class UploadRequestEntryUrlBatchImpl implements
		UploadRequestEntryUrlBatch {

	protected UploadRequestEntryUrlService uploadRequestEntryUrlService;

	public UploadRequestEntryUrlBatchImpl(
			UploadRequestEntryUrlService uploadRequestEntryUrlService) {
		super();
		this.uploadRequestEntryUrlService = uploadRequestEntryUrlService;
	}

	@Override
	public Set<UploadRequestEntryUrl> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResultContext<UploadRequestEntryUrl> execute(
			UploadRequestEntryUrl resource) throws BatchBusinessException,
			BusinessException {
		BatchResultContext<UploadRequestEntryUrl> context = new BatchResultContext<UploadRequestEntryUrl>(
				resource);
		try {
			sample();
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "sample");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchResultContext<UploadRequestEntryUrl> context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void notifyError(BatchBusinessException exception) {
		// TODO Auto-generated method stub
	}

	private void sample() throws BusinessException {
		BusinessException exception = new BusinessException("coucou");
		throw exception;
	}

}
