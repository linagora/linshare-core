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

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;

public interface ContainerQuotaRepository extends GenericQuotaRepository<ContainerQuota> {

	ContainerQuota find(AbstractDomain domain, ContainerQuotaType containerQuotaType);

	Long sumOfCurrentValue(DomainQuota domainQuota);

	Long cascadeMaintenanceMode(ContainerQuota container, boolean maintenance);

	Long cascadeDefaultQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType);

	Long cascadeDefaultMaxFileSize(AbstractDomain domain, Long maxFileSize, ContainerQuotaType containerType);

	Long cascadeMaxFileSize(ContainerQuota container, Long maxFileSize);

	Long cascadeDefaultAccountQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType);

	Long cascadeAccountQuota(ContainerQuota container, Long accountQuota);
}
