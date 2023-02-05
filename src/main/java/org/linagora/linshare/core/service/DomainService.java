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

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface DomainService {

	AbstractDomain find(Account actor, String uuid) throws BusinessException;

	List<AbstractDomain> findAll(Account actor);

	PageContainer<AbstractDomain> findAll(
			Account authUser,
			Optional<String> domainType,
			Optional<String> name, Optional<String> description,
			Optional<String> parentUuid,
			SortOrder sortOrder, DomainField sortField,
			PageContainer<AbstractDomain> container);

	List<AbstractDomain> getSubDomainsByDomain(Account actor, String uuid) throws BusinessException;

	AbstractDomain create(Account actor, String name, String description, DomainType type, AbstractDomain parent) throws BusinessException;

	AbstractDomain update(Account actor, String domainUuid, AbstractDomain dto) throws BusinessException;

	AbstractDomain markToPurge(Account actor, String domainUuid);
}
