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
package org.linagora.linshare.core.facade.webservice.delegation;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;

public interface DocumentFacade extends DelegationGenericFacade {

	List<DocumentDto> findAll(String actorUuid) throws BusinessException;

	DocumentDto find(String actorUuid, String documentUuid)
			throws BusinessException;

	DocumentDto create(String actorUuid, File file,
			String description, String givenFileName) throws BusinessException;

	DocumentDto update(String actorUuid, String documentUuid,
			DocumentDto documentDto) throws BusinessException;

	DocumentDto updateFile(String actorUuid, String documentUuid,
			File file, String givenFileName)
			throws BusinessException;

	DocumentDto delete(String actorUuid, DocumentDto documentDto)
			throws BusinessException;

	DocumentDto delete(String actorUuid, String documentUuid) throws BusinessException;

	Response download(String actorUuid, String documentUuuid)
			throws BusinessException;

	Response thumbnail(String actorUuid, String documentUuuid, ThumbnailType kind)
			throws BusinessException;

}
