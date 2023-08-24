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

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ShareRecipientStatisticDto;
import org.linagora.linshare.webservice.utils.PageContainer;

/**
 * Interface for the Share service REST jaxRS interface Allows for creation of a
 * sharing
 */

@Path("/shares")
public interface ShareRestService {

    PageContainer<ShareRecipientStatisticDto> getTopSharesByFileSize(String domainUuid, String beginDate, String endDateInteger, Integer pageNumber, Integer pageSize) throws BusinessException;

    PageContainer<ShareRecipientStatisticDto> getTopSharesByFileCount(String domainUuid, String beginDate, String endDate, Integer pageNumber, Integer pageSize) throws BusinessException;
}
