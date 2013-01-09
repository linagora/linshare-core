package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.exception.BusinessException;

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
	 * return all TopDomain and SubDomain objects, excluding Guest and Root domains
	 * @return
	 */
	public List<AbstractDomain> findAllTopAndSubDomain();

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

	/**
	 * return the unique root domain
	 * @return
	 */
	public RootDomain getUniqueRootDomain() throws BusinessException;
	
}