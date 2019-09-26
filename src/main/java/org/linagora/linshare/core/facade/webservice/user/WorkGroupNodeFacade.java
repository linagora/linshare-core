/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.user;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;

public interface WorkGroupNodeFacade extends GenericFacade {

	List<WorkGroupNode> findAll(String actorUuid, String workGroupUuid, String parentNodeUuid, Boolean flatDocumentMode, List<WorkGroupNodeType> nodeTypes) throws BusinessException;

	WorkGroupNode find(String actorUuid, String workGroupUuid, String workGroupNodeUuid, Boolean withTree) throws BusinessException;

	WorkGroupNode create(String actorUuid, String workGroupUuid, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun) throws BusinessException;

	WorkGroupNode create(String actorUuid, String workGroupUuid,
			String parentNodeUuid, File tempFile, String fileName, Boolean strict) throws BusinessException;

	List<WorkGroupNode> copy(String actorUuid, String workGroupUuid, String toParentNodeUuid, CopyDto copy, boolean deleteShare);

	WorkGroupNode update(String actorUuid, String workGroupUuid, WorkGroupNode workGroupNode, String workGroupNodeUuid) throws BusinessException;

	WorkGroupNode delete(String actorUuid, String workGroupUuid, String workGroupNodeUuid, WorkGroupNode workGroupNode) throws BusinessException;

	Response download(String actorUuid, String workGroupUuid, String workGroupNodeUuid, Boolean withRevision) throws BusinessException;

	Response thumbnail(String actorUuid, String workGroupUuid, String workGroupNodeUuid, boolean base64, ThumbnailType thumbnailType) throws BusinessException;

	Set<AuditLogEntryUser> findAll(String actorUuid, String workGroupUuid, String workGroupNodeUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, String beginDate, String endDate);

	String findByWorkGroupNodeUuid(String workGroupNodeUuid) throws BusinessException;

	NodeMetadataMto findMetaData(String actorUuid, String sharedSpaceUuid, String sharedSpaceNodeUuid, boolean storage);

}