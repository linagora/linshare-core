package org.linagora.linshare.core.exception;

import org.linagora.linshare.core.job.quartz.Context;

public class BatchBusinessException extends RuntimeException {

	private static final long serialVersionUID = -2766223512640837573L;

	protected Context context;

	protected String message;

	protected BusinessException businessException;

	public BatchBusinessException(Context context, String message) {
		super();
		this.context = context;
		this.message = message;
	}

	public Context getContext() {
		return context;
	}

	public String getMessage() {
		return message;
	}

	public BusinessException getBusinessException() {
		return businessException;
	}

	public void setBusinessException(BusinessException businessException) {
		this.businessException = businessException;
	}

}
