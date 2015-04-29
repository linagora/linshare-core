package org.linagora.linshare.core.job.quartz;

import java.util.List;
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

	protected List<GenericBatch<T>> batchs;

	public void setBatch(List<GenericBatch<T>> batchs) {
		this.batchs = batchs;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		for (GenericBatch<T> batch : batchs) {
			Set<T> all = batch.getAll();
			long position = 1;
			long errors = 0;
			long unhandled_errors = 0;
			long total = all.size();
			for (T resource : all) {
				try {
					logDebug(total, position, "processing resource ...");
					Context c = new ResourceContext<T>(resource);
					BatchResultContext<T> batchResult = batch.execute(
							c, total, position);
					batch.notify(batchResult, total, position);
				} catch (BatchBusinessException ex) {
					errors++;
					batch.notifyError(ex, resource, total, position);
				} catch (BusinessException ex) {
					unhandled_errors++;
					logger.error("Unhandled business exception in batches !");
					logger.error(ex.getMessage());
					logger.debug(ex.getStackTrace().toString());
					logger.error("Cannot process resource '{}' ");
				}
				logDebug(total, position, "resource processed.");
				position++;
			}
			batch.terminate(all, errors, unhandled_errors, total);
		}
	}

	protected void logDebug(long total, long position, String message) {
		logger.debug(getStringPosition(total, position) + message);
	}

	protected String getStringPosition(long total, long position) {
		return position + "/" + total + ":";
	}

}
