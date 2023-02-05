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
package org.linagora.linshare.webservice.userv5;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface DocumentRestService {

	DocumentDto create(InputStream file, String description,
			String givenFileName, InputStream theSignature,
			String signatureFileName, InputStream x509certificate,
			String metaData, boolean async,
			Long fileSize,
			MultipartBody body)
			throws BusinessException;

	List<DocumentDto> copy(CopyDto copy, boolean deleteShare) throws BusinessException;

	DocumentDto find(String uuid, boolean withShares) throws BusinessException;

	void head(String uuid) throws BusinessException;

	List<DocumentDto> findAll() throws BusinessException;

	DocumentDto delete(String uuid) throws BusinessException;

	DocumentDto delete(DocumentDto documentDto) throws BusinessException;

	Response download(String uuid) throws BusinessException;

	Response thumbnail(String uuid, ThumbnailType thumbnailType, boolean base64) throws BusinessException;

	DocumentDto update(String uuid, DocumentDto documentDto) throws BusinessException;

	AsyncTaskDto findAsync(String uuid) throws BusinessException;

	Set<AuditLogEntryUser> findAll(String uuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate);

	DocumentDto createFromURL(DocumentURLDto documentURLDto, boolean async) throws BusinessException;

}
