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
package org.linagora.linshare.webservice.adminv5;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AccountQuotaDto;

public interface AccountQuotaRestService {

	AccountQuotaDto find(String domainUuid, String uuid, boolean realTime) throws BusinessException;

	Response findAll(String domainUuid, boolean includeNestedDomains, String sortOrder, String sortField,
			Long greaterThanOrEqualTo, Long lessThanOrEqualTo,
			String containerQuotaType,
			String beginDate, String endDate, Integer pageNumber, Integer pageSize);

}