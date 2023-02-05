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
package org.linagora.linshare.webservice.userv2;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;

/**
 * Interface for the Share service REST jaxRS interface Allows for creation of a
 * sharing
 */

@Path("/shares")
public interface ShareRestService {

	List<ShareDto> getShares() throws BusinessException;

	ShareDto getShare(String shareUuid) throws BusinessException;

	void head(String shareUuid) throws BusinessException;

	Response getDocumentStream(String shareUuid) throws BusinessException;

	Response getThumbnailStream(String shareUuid, boolean base64) throws BusinessException;

	Set<ShareDto> create(ShareCreationDto createDto) throws BusinessException;

	ShareDto delete(String shareUuid) throws BusinessException;

	ShareDto delete(ShareDto shareDto) throws BusinessException;
}
