/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	 * @return Set<Functionality>
	 * @throws BusinessException
	 */
	Set<Functionality> findAll(Account actor) throws BusinessException;

	/**
	 * @param actor : a simple user
	 * @param functionalityId
	 * @return Functionality
	 * @throws BusinessException
	 */
	Functionality find(Account actor, String functionalityId) throws BusinessException;

}