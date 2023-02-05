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

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface WorkGroupNodeRestService {

	WorkGroupNode create(String workGroupUuid, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun) throws BusinessException;

	WorkGroupNode create(String workGroupUuid, String parentNodeUuid, InputStream file, String description,
			String givenFileName, Boolean async, Long contentLength, Long fileSize, MultipartBody body, Boolean strict)
			throws BusinessException;

	List<WorkGroupNode> copy(String workGroupUuid, CopyDto copy, boolean deleteShare) throws BusinessException;

	List<WorkGroupNode> copy(String workGroupUuid, String parentNodeUuid, CopyDto copy, boolean deleteShare) throws BusinessException;

	List<WorkGroupNode> findAll(String workGroupUuid, String parentNodeUuid, Boolean flat, List<WorkGroupNodeType> nodeTypes) throws BusinessException;

	WorkGroupNode find(String workGroupUuid, String workGroupNodeUuid, Boolean withTree) throws BusinessException;

	WorkGroupNode update(String workGroupUuid, String workGroupNodeUuid, WorkGroupNode workGroupNode) throws BusinessException;

	WorkGroupNode delete(String workGroupUuid, String workGroupNodeUuid, WorkGroupNode workGroupNode) throws BusinessException;

	void head(String workGroupUuid, String uuid) throws BusinessException;

	Response download(String workGroupUuid, String uuid, Boolean withRevision) throws BusinessException;

	Response thumbnail(String workGroupUuid, String uuid, ThumbnailType thumbnailType, boolean base64) throws BusinessException;

	AsyncTaskDto findAsync(String uuid) throws BusinessException;

	Set<AuditLogEntryUser> findAll(String workGroupUuid, String workGroupNodeUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate);

	WorkGroupNode createFromURL(String workGroupUuid, String parentNodeUuid, DocumentURLDto documentURLDto,
			Boolean async, Boolean strict) throws BusinessException;

}