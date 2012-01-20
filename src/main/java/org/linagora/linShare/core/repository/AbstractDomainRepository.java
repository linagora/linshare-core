package org.linagora.linShare.core.repository;

import java.util.List;

import org.linagora.linShare.core.domain.entities.AbstractDomain;

public interface AbstractDomainRepository extends AbstractRepository<AbstractDomain> {
	
	public AbstractDomain findById(String identifier);

	/**
	 * return all TopDomain and SubDomain identifiers
	 * @return
	 */
	public List<String> findAllDomainIdentifiers();

	/**
	 * return all TopDomain and SubDomain objects
	 * @return
	 */
	public List<AbstractDomain> findAllDomain();

	/**
	 * return all TopDomain objects
	 * @return
	 */
	public List<AbstractDomain> findAllTopDomain();
	
	/**
	 * return all SubDomain objects
	 * @return
	 */
	public List<AbstractDomain> findAllSubDomain();
	
}