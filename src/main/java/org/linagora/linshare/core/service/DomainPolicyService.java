/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessException;

public interface DomainPolicyService {

	public DomainPolicy createDomainPolicy(DomainPolicy domainPolicy) throws BusinessException ;
    public DomainPolicy retrieveDomainPolicy(String identifier) throws BusinessException;
    public void updateDomainPolicy(DomainPolicy domainPolicy) throws BusinessException;
	public List<DomainPolicy> findAllDomainPolicy() throws BusinessException;
	public List<String> getAllDomainPolicyIdentifiers();
	public void deletePolicy(String policyToDelete) throws BusinessException;
	public boolean policyIsDeletable(String policyToDelete);
	
	
	public DomainAccessPolicy retrieveDomainAccessPolicy(long id);
	public List<DomainAccessPolicy> findAllDomainAccessPolicy();
    public DomainAccessPolicy createDomainAccessPolicy(DomainAccessPolicy domainPolicy) throws BusinessException ;
	public void updateDomainAccessPolicy(DomainAccessPolicy domainAccessPolicy) throws BusinessException;
	public void deleteDomainAccesspolicy(long id) throws BusinessException;
	
	public List<DomainAccessRule> findAllDomainAccessRule();
	public DomainAccessRule retrieveDomainAccessRule(long id);
	public void deleteDomainAccessRule(DomainPolicy policy,long persistenceID) throws BusinessException;
	
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
