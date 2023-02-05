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
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;

public interface DomainQuotaRepository extends GenericQuotaRepository<DomainQuota> {

	DomainQuota find(AbstractDomain domain);

	List<DomainQuota> findAllByParent(AbstractDomain domain);

	Long sumOfCurrentValueForSubdomains(AbstractDomain domain);

	Long cascadeMaintenanceMode(AbstractDomain domain, boolean maintenance);

	Long cascadeDefaultQuota(AbstractDomain domain, Long quota);

	Long cascadeDomainShared(DomainQuota quota, Boolean domainShared);

	List<DomainQuota> findAllByDomain(AbstractDomain domain);

}
