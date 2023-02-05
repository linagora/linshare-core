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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;

public interface WorkGroupEntryRestService {
	WorkGroupEntryDto create(String actorUuid, String workgroupUuid, InputStream file, String description,
			String givenFileName, Boolean async, Long contentLength, Long fileSize, Boolean strict, MultipartBody body)
			throws BusinessException;

	WorkGroupEntryDto copy(String actorUuid, String workgroupUuid, String entryUuid, Boolean async)
			throws BusinessException;

	public WorkGroupEntryDto find(String actorUuid, String workgroupUuid, String uuid) throws BusinessException;

	void head(String actorUuid, String workgroupUuid, String uuid) throws BusinessException;

	public List<WorkGroupEntryDto> findAll(String actorUuid, String workgroupUuid) throws BusinessException;

	public WorkGroupEntryDto delete(String actorUuid, String workgroupUuid, WorkGroupEntryDto workgroupEntry)
			throws BusinessException;

	public WorkGroupEntryDto delete(String actorUuid, String workgroupUuid, String uuid) throws BusinessException;

	Response download(String actorUuid, String workgroupUuid, String uuid) throws BusinessException;

	Response thumbnail(String actorUuid, String workgroupUuid, String uuid) throws BusinessException;

	WorkGroupEntryDto update(String actorUuid, String workgroupUuid, String workgroupEntryuuid,
			WorkGroupEntryDto workgroupEntryDto) throws BusinessException;

	AsyncTaskDto findAsync(String actorUuid, String uuid) throws BusinessException;

	WorkGroupEntryDto createFromURL(DocumentURLDto documentURLDto, String actorUuid, String workgroupUuid,
			Boolean async, Boolean strict) throws BusinessException;
}
