/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.exception.BusinessException;

public interface DomainPolicyService {

	public DomainPolicy create(DomainPolicy domainPolicy) throws BusinessException;

	public DomainPolicy find(String identifier);

	public DomainPolicy update(DomainPolicy domainPolicy) throws BusinessException;

	public List<DomainPolicy> findAll() throws BusinessException;

	public DomainPolicy delete(String policyToDelete) throws BusinessException;

	public boolean policyIsDeletable(String policyToDelete);

	/**
	 * This method returns true if we have the right to communicate with itself.
	 * @param domain
	 * @return boolean
	 */
	public boolean isAuthorizedToCommunicateWithItSelf(AbstractDomain domain);

	/**
	 * This method returns true if we have the right to communicate with its parent.
	 * @param domain
	 * @return boolean
	 */
	public boolean isAuthorizedToCommunicateWithItsParent(AbstractDomain domain);
	/**
	 * This method returns a list of authorized sub domain, just the sub domain of the domain parameter. 
	 * @param domain
	 * @return List<AbstractDomain>
	 */
	public List<AbstractDomain> getAuthorizedSubDomain(AbstractDomain domain);
	/**
	 * This method returns a list of all authorized sibling domain.
	 * @param domain
	 * @return List<AbstractDomain>
	 */
	public List<AbstractDomain> getAuthorizedSibblingDomain(AbstractDomain domain);
	/**
	 * This method returns a list of all authorized domain. useful ?
	 * @param domain
	 * @return List<AbstractDomain>
	 */
	public List<AbstractDomain> getAllAuthorizedDomain(AbstractDomain domain);
}
