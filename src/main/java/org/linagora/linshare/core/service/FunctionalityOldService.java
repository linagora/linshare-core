/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityOldService {

	/**
	 * This method returns a functionality. 
	 * @param persistence id
	 * @return
	 */
	public Functionality findById(long id);
	
	public void update(AbstractDomain domain, Functionality functionality) throws BusinessException;
	public void update(String domainIdentifier, Functionality functionality) throws BusinessException;
	
	/**
	 * This method is designed to return a list of all existing functionalities from a domain.
	 * @param domain entity
	 * @return
	 */
	public List<Functionality> getAllFunctionalities(AbstractDomain domain);
	
	/**
	 * This method returns an updatable functionality. Do not try to update a functionality whit other ways. 
	 * @param domain identifier
	 * @param functionality identifier
	 * @return
	 */
	public Functionality getFunctionalityByIdentifiers(String domainIdentifier,String functionalityIdentifier);
	
	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the activation policy of these functionalities can be modified. 
	 * @param domain entity
	 * @return
	 */
	
	public List<Functionality> getAllAvailableFunctionalities(AbstractDomain domain);
	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the activation policy of these functionalities can be modified.  
	 * @param domain identifier
	 * @return
	 */
	public List<Functionality> getAllAvailableFunctionalities(String domainIdentifier);
			
	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the configuration policy of these functionalities can be modified. 
	 * @param domain entity
	 * @return
	 */
	public List<Functionality> getAllAlterableFunctionalities(AbstractDomain domain);
	
	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the configuration policy of these functionalities can be modified.
	 * @param domain identifier
	 * @return
	 */
	public List<Functionality> getAllAlterableFunctionalities(String domainIdentifier);

	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the parameters of these functionalities can be modified. 
	 * @param domain entity
	 * @return
	 */
	public List<Functionality> getAllEditableFunctionalities(AbstractDomain domain);
	
	/**
	 * This method is designed to return a list of all functionalities.
	 * Only the parameters of these functionalities can be modified. 
	 * @param domain identifier
	 * @return
	 */
	public List<Functionality> getAllEditableFunctionalities(String domainIdentifier);
}