package org.linagora.linshare.core.job.quartz;


public class ResourceContext <T> extends Context {

	protected T ressource;

	public ResourceContext(T ressource) {
		super();
		this.ressource = ressource;
	}

	public T getRessource() {
		return ressource;
	}

	public void setRessource(T ressource) {
		this.ressource = ressource;
	}

}
