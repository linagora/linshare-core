/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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