package org.linagora.linshare.core.repository;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;

public interface AbstractFunctionalityRepository<T> extends AbstractRepository<T> {

	public T findById(long id);

	public T findById(AbstractDomain domain, String identifier);

	public Set<T> findAll(AbstractDomain domain);
}
