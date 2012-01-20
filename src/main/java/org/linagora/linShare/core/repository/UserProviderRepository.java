package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.LdapUserProvider;

public interface UserProviderRepository extends
		AbstractRepository<LdapUserProvider> {
	
	public LdapUserProvider findById(long id);

}
