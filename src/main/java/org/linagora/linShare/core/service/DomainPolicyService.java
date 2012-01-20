package org.linagora.linShare.core.service;

import java.util.List;

import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.DomainPolicy;

public interface DomainPolicyService {

	public DomainPolicy findById(String identifier);
	public List<DomainPolicy> getAllDomainPolicy();
	public List<String> getAllDomainPolicyIdentifiers();
	
	/**
	 * This method returns true if we have the right to communicate with itself.
	 * @param domain
	 * @return
	 */
	public boolean isAuthorizedToCommunicateWithItSelf(AbstractDomain domain);
	
	/**
	 * This method returns true if we have the right to communicate with its parent.
	 * @param domain
	 * @return
	 */
	public boolean isAuthorizedToCommunicateWithItsParent(AbstractDomain domain);
	/**
	 * This method returns a list of authorized sub domain, just the sub domain of the domain parameter. 
	 * @param domain
	 * @return
	 */
	public List<AbstractDomain> getAuthorizedSubDomain(AbstractDomain domain);
	/**
	 * This method returns a list of all authorized sibling domain.
	 * @param domain
	 * @return
	 */
	public List<AbstractDomain> getAuthorizedSibblingDomain(AbstractDomain domain);
	/**
	 * This method returns a list of all authorized domain. useful ?
	 * @param domain
	 * @return
	 */
	public List<AbstractDomain> getAllAuthorizedDomain(AbstractDomain domain);
}
