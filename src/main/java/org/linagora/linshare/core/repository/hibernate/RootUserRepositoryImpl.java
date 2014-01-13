package org.linagora.linshare.core.repository.hibernate;

import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.Root;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class RootUserRepositoryImpl  extends GenericUserRepositoryImpl<Root> implements RootUserRepository {

	public RootUserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Root findByLogin(String login) {
		return super.findByMailAndDomain(LinShareConstants.rootDomainIdentifier, login);
	}

	@Override
	public Root findByLoginAndDomain(String domain, String login) {
		return super.findByMailAndDomain(LinShareConstants.rootDomainIdentifier, login);
	}
}
