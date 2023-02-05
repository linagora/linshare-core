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
package org.linagora.linshare.webservice.uploadrequestv4;

import java.util.List;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;

public interface UploadRequestRestService {

	Response find(String uuid, String password) throws BusinessException;

	UploadRequestDto close(String uuid, String password)
			throws BusinessException;

	UploadRequestEntryDto delete(String uuid, String password, String entryUuid, EntryDto entry) throws BusinessException;

	List<UploadRequestEntryDto> findAllEntries(String uuid, String password) throws BusinessException;

	Response thumbnail(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid, ThumbnailType thumbnailType, boolean base64)
			throws BusinessException;

	Response download(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid) throws BusinessException;
}
