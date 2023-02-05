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

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;

public interface DomainQuotaBusinessService {

	AbstractDomain getUniqueRootDomain();

	DomainQuota findRootQuota() throws BusinessException;

	DomainQuota find(AbstractDomain domain) throws BusinessException;

	DomainQuota find(String uuid) throws BusinessException;

	List<DomainQuota> findAll() throws BusinessException;

	List<DomainQuota> findAll(AbstractDomain parentDomain) throws BusinessException;

	DomainQuota create(DomainQuota entity) throws BusinessException;

	DomainQuota update(DomainQuota entity, DomainQuota dto) throws BusinessException;

	DomainQuota sumOfCurrentValue(DomainQuota entity);

	List<DomainQuota> findAllByDomain(AbstractDomain domain);

}
