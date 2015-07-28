/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityService extends
		AbstractFunctionalityService<Functionality> {

	Functionality find(Account actor, String domainId, String identifier, boolean tree)
			throws BusinessException;

	/**
	 * @param actor
	 *            : should an account with administration rights
	 * @param domainId
	 *            : the targeted domain
	 * @param parentId
	 *            : if different than null, it will return a list with children
	 *            of the functionality name 'parentId'.
	 * @param tree
	 *            : children will be add inside each {@link Functionality}
	 * @param withSubFunctionalities
	 *            : the result list will also contain children functionalities.
	 * @return : by default, all parent functionalities will be returned
	 * @throws BusinessException
	 */
	Iterable<Functionality> findAll(Account actor, String domainId,
			String parentId, boolean tree, boolean withSubFunctionalities)
			throws BusinessException;

	/**
	 * @param actor
	 *            : should an account with administration rights
	 * @param domainId
	 *            : the targeted domain
	 * @param parentId
	 *            : if different than null, it will return a list with children
	 *            of the functionality name 'parentId'.
	 * @return : by default, all parent functionalities will be returned
	 * @throws BusinessException
	 */
	Iterable<Functionality> findAll(Account actor, String domainId,
			String parentId) throws BusinessException;

	void delete(Account actor, String domainId, String functionalityId)
			throws BusinessException;

	/**
	 * @param actor : a simple user
	 * @return
	 * @throws BusinessException
	 */
	Set<Functionality> findAll(Account actor) throws BusinessException;

	/**
	 * @param actor : a simple user
	 * @param functionalityId
	 * @return
	 * @throws BusinessException
	 */
	Functionality find(Account actor, String functionalityId) throws BusinessException;

}