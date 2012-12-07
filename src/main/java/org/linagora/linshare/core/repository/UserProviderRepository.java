package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.LdapUserProvider;

public interface UserProviderRepository extends
		AbstractRepository<LdapUserProvider> {
	
	public LdapUserProvider findById(long id);

}
