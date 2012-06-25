package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.LDAPConnection;

public interface LDAPConnectionRepository extends AbstractRepository<LDAPConnection> {
	
	public LDAPConnection findById(String identifier);

}
