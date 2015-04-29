package org.linagora.linshare.core.batches.generics;

import java.util.Set;

import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;

public interface GenericBatch<T> {

	Set<T> getAll();

	BatchResultContext<T> execute(Context resource, long total, long position) throws BatchBusinessException, BusinessException;

	/**
	 * There is no transaction neither Hibernate session for this method because there some parameters with generic.
	 * @param context
	 * @param total
	 * @param position
	 */
	void notify(BatchResultContext<T> context, long total, long position);

	/**
	 * There is no transaction neither Hibernate session for this method because there some parameters with generic.
	 * @param exception
	 * @param resource
	 * @param total
	 * @param position
	 */
	void notifyError(BatchBusinessException exception, T resource, long total, long position);

	/**
	 * There is no transaction neither Hibernate session for this method because there some parameters with generic.
	 * @param all
	 * @param errors
	 * @param unhandled_errors
	 * @param total
	 */
	void terminate(Set<T> all, long errors, long unhandled_errors, long total);

	/*
	 * Helpers
	 */
	T getResource(Context c);
}
