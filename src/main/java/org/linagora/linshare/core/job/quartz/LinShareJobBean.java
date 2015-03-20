package org.linagora.linshare.core.job.quartz;

import java.util.Set;

import org.linagora.linshare.core.batches.generics.GenericBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class LinShareJobBean<T> extends QuartzJobBean {

	private static final Logger logger = LoggerFactory
			.getLogger(LinShareJobBean.class);

	protected GenericBatch<T> batch;

	private AccountRepository<Account> accountRepository;

	public void setBatch(GenericBatch<T> batch) {
		this.batch = batch;
	}

	public void setAccountRepository(AccountRepository<Account> accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		Set<T> all = batch.getAll(systemAccount);
		for (T ressource : all) {
			try {
				logger.info("LinshareJobBean Bath :");
				BatchResultContext<T> batchResult = batch.execute(systemAccount, ressource);
				batch.notify(systemAccount, batchResult);
			} catch (BatchBusinessException ex) {
				batch.notifyError(systemAccount, ex);
			} catch (BusinessException ex) {
				logger.error("Unhandled business exception in batches !");
				logger.error(ex.getMessage());
				logger.debug(ex.getStackTrace().toString());
			}
		}
		batch.terminate(systemAccount, all);
	}

}
