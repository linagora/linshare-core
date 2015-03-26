package org.linagora.linshare.core.batches.generics;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;

public interface GenericBatch<T> {

	Set<T> getAll(SystemAccount systemAccount);

	BatchResultContext<T> execute(SystemAccount systemAccount, T resource, long total, long position) throws BatchBusinessException, BusinessException;

	void notify(SystemAccount systemAccount, BatchResultContext<T> context, long total, long position);

	void notifyError(SystemAccount systemAccount, BatchBusinessException exception, T resource, long total, long position);

	void terminate(SystemAccount systemAccount, Set<T> all, long errors, long unhandled_errors, long total);

}
