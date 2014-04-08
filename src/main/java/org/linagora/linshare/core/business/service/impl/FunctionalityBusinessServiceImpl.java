package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;

public class FunctionalityBusinessServiceImpl extends
		AbstractFunctionalityBusinessServiceImpl<Functionality> implements FunctionalityBusinessService{

	public FunctionalityBusinessServiceImpl(
			FunctionalityRepository functionalityRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super(functionalityRepository, abstractDomainRepository);
	}
}
