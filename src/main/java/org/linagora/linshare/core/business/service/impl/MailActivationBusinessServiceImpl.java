package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AbstractFunctionalityRepository;

public class MailActivationBusinessServiceImpl extends
		AbstractFunctionalityBusinessServiceImpl<MailActivation> implements
		MailActivationBusinessService {

	public MailActivationBusinessServiceImpl(
			AbstractFunctionalityRepository<MailActivation> functionalityRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super(functionalityRepository, abstractDomainRepository);
	}

}
