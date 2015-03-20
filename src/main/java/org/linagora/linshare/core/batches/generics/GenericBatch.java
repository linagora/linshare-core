package org.linagora.linshare.core.batches.generics;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;

public interface GenericBatch<T> {

	Set<T> getAll(SystemAccount systemAccount);

	BatchResultContext<T> execute(SystemAccount systemAccount, T resource) throws BatchBusinessException, BusinessException;

	void notify(SystemAccount systemAccount, BatchResultContext<T> context);

	void notifyError(SystemAccount systemAccount, BatchBusinessException exception);

	void terminate(SystemAccount systemAccount, Set<T> all);

}
