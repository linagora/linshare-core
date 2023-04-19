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
package org.linagora.linshare.core.business.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface DomainBusinessService {

	AbstractDomain find(String identifier) throws BusinessException;

	// TODO this method should never raise an exception.
	// This check should be in the service.
	AbstractDomain findById(String identifier) throws BusinessException;

	AbstractDomain update(AbstractDomain domain) throws BusinessException;

	AbstractDomain getUniqueRootDomain() throws BusinessException;

	/**
	 * 
	 * @param welcomeMessage : a welcome message
	 * @return a set of domains which use the welcome message.
	 * @throws BusinessException
	 */
	List<AbstractDomain> loadRelativeDomains(WelcomeMessages welcomeMessage) throws BusinessException;

	List<AbstractDomain> loadRelativeDomains(MailConfig mailConfig) throws BusinessException;

	AbstractDomain findGuestDomain(AbstractDomain domain) throws BusinessException;

	List<String> getAllMyDomainIdentifiers(AbstractDomain domain);

	List<String> getAllSubDomainIdentifiers(String domain);

	List<AbstractDomain> getSubDomainsByDomain(String uuid) throws BusinessException;

	Set<AbstractDomain> getSubDomainsByDomainAsASet(String uuid) throws BusinessException;

	List<String> getSubDomainsByDomainIdentifiers(String uuid) throws BusinessException;

	AbstractDomain create(AbstractDomain domain);

	PageContainer<AbstractDomain> findAll(
			Optional<DomainType> domainType,
			Optional<String> name, Optional<String> description,
			Optional<AbstractDomain> parent,
			Optional<AbstractDomain> from,
			SortOrder sortOrder,
			DomainField sortField,
			PageContainer<AbstractDomain> container);
}
