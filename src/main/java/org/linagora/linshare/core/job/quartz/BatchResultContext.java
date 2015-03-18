package org.linagora.linshare.core.job.quartz;

public class BatchResultContext<T> extends Context {

	protected T resource;

	public BatchResultContext(T resource) {
		super();
		this.resource = resource;
	}

	public T getResource() {
		return resource;
	}

	public void setResource(T resource) {
		this.resource = resource;
	}
}
