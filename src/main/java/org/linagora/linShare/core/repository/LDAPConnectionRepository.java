package org.linagora.linShare.core.repository;

import org.linagora.linShare.core.domain.entities.LDAPConnection;

public interface LDAPConnectionRepository extends AbstractRepository<LDAPConnection> {
	
	public LDAPConnection findById(String identifier);

}
