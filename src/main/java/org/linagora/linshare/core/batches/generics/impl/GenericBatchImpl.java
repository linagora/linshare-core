package org.linagora.linshare.core.batches.generics.impl;

import org.linagora.linshare.core.batches.generics.GenericBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericBatchImpl<T> implements GenericBatch<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(GenericBatchImpl.class);

	protected String getStringPosition(long total, long position) {
		return total + "/" + position + ":";
	}

	protected void logDebug(long total, long position, String message) {
		logger.debug(getStringPosition(total, position) + message);
	}

	protected void logInfo(long total, long position, String message) {
		logger.info(getStringPosition(total, position) + message);
	}
	
	protected void logError(long total, long position, String message) {
		logger.error(getStringPosition(total, position) + message);
	}
}
