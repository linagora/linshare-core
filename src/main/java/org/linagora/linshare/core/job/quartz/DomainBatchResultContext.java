package org.linagora.linshare.core.job.quartz;

import org.linagora.linshare.core.domain.entities.AbstractDomain;

public class DomainBatchResultContext extends BatchResultContext<AbstractDomain> {

	public DomainBatchResultContext(AbstractDomain resource) {
		super(resource);
		this.identifier = resource.getIdentifier();
	}

}
