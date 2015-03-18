package org.linagora.linshare.core.batches.generics;

import java.util.Set;

import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;

public interface GenericBatch<T> {

	Set<T> getAll();

	BatchResultContext<T> execute(T resource) throws BatchBusinessException, BusinessException;

	void notify(BatchResultContext<T> context);

	void notifyError(BatchBusinessException exception);

}
