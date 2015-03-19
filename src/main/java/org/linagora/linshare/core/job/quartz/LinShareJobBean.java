package org.linagora.linshare.core.job.quartz;

import java.util.Set;

import org.linagora.linshare.core.batches.generics.GenericBatch;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class LinShareJobBean<T> extends QuartzJobBean {

	private static final Logger logger = LoggerFactory
			.getLogger(LinShareJobBean.class);

	protected GenericBatch<T> batch;

	public void setBatch(GenericBatch<T> batch) {
		this.batch = batch;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		Set<T> all = batch.getAll();
		for (T ressource : all) {
			try {
				logger.info("LinshareJobBean Bath :");
				BatchResultContext<T> batchResult = batch.execute(ressource);
				batch.notify(batchResult);
			} catch (BatchBusinessException ex) {
				batch.notifyError(ex);
			} catch (BusinessException ex) {
				logger.error("Unhandled business exception in batches !");
				logger.error(ex.getMessage());
				logger.debug(ex.getStackTrace().toString());
			}
		}
	}

}
