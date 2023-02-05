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
package org.linagora.linshare.webservice.delegationv2;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;

/**
 * REST jaxRS interface
 */

public interface DocumentRestService {

	DocumentDto create(String actorUuid, InputStream file,
			String description, String givenFileName,
			InputStream theSignature,
			String signatureFileName, InputStream x509certificate,
			String metaData,
			Boolean async,
			Long fileSize,
			MultipartBody body)
			throws BusinessException;

	DocumentDto find(String actorUuid, String uuid) throws BusinessException;

	void head(String actorUuid, String uuid) throws BusinessException;

	List<DocumentDto> findAll(String actorUuid) throws BusinessException;

	DocumentDto delete(String actorUuid, String uuid) throws BusinessException;

	Response download(String actorUuid, String uuid) throws BusinessException;

	Response thumbnail(String actorUuid, String uuid, ThumbnailType thumbnailType) throws BusinessException;

	DocumentDto update(String actorUuid, String uuid, DocumentDto documentDto) throws BusinessException;

	DocumentDto delete(String actorUuid, DocumentDto documentDto)
			throws BusinessException;

	AsyncTaskDto findAsync(String actorUuid, String uuid) throws BusinessException;

	DocumentDto createFromURL(DocumentURLDto documentURLDto, String actorUUID, Boolean async) throws BusinessException;
}