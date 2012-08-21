package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;

public interface TagRepository extends AbstractRepository<Tag> {

	public Tag findById(Long id);
	
	public Tag findByOwnerAndName(Account owner, String name);
	
}
