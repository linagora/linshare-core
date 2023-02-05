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
package org.linagora.linshare.core.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.fields.DocumentKind;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceNodeField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface WorkGroupNodeService {

	List<WorkGroupNode> findAll(Account actor, User owner, WorkGroup workGroup) throws BusinessException;

	List<WorkGroupNode> findAll(Account actor, Account owner, WorkGroup workGroup, String parentUuid, Boolean flat,
			List<WorkGroupNodeType> nodeTypes) throws BusinessException;

	WorkGroupNode find(Account actor, Account owner, WorkGroup workGroup, String workGroupNodeUuid, boolean withTree)
			throws BusinessException;

	WorkGroupNode findForDownloadOrCopyRight(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException;

	void markAsCopied(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode wgNode, CopyMto copiedTo)
			throws BusinessException;

	String findWorkGroupUuid(Account actor, User owner, String workGroupNodeUuid) throws BusinessException;

	WorkGroupNode create(Account actor, User owner, WorkGroup workGroup, WorkGroupNode workGroupNode, Boolean strict,
			Boolean dryRun) throws BusinessException;

	WorkGroupNode copy(Account actor, User owner, WorkGroup toWorkGroup, String toNodeUuid, CopyResource cr)
			throws BusinessException;

	WorkGroupNode copy(Account actor, User owner, WorkGroup fromWorkGroup, String fromNodeUuid, WorkGroup toWorkGroup,
			String toNodeUuid) throws BusinessException;

	WorkGroupNode create(Account actor, User owner, WorkGroup workGroup, File tempFile, String fileName,
			String parentNodeUuid, Boolean strict) throws BusinessException;

	WorkGroupNode update(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode)
			throws BusinessException;

	WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException;

	FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid,
			Boolean withRevision) throws BusinessException;

	FileAndMetaData thumbnail(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid,
			ThumbnailType kind) throws BusinessException;

	WorkGroupNode getRootFolder(Account actor, Account owner, WorkGroup workGroup);

	WorkGroupNode findByWorkGroupNodeUuid(String uuid) throws BusinessException;

	NodeMetadataMto findMetadata(User authUser, User actor, WorkGroup workGroup, WorkGroupNode node, boolean storage);

	WorkGroupNode copy(Account actor, User owner, WorkGroup fromWorkGroup, String fromNodeUuid, WorkGroup toWorkGroup,
			String toNodeUuid, boolean moveDocument) throws BusinessException;

	WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, String workGroupNodeUuid, boolean moveDocument)
			throws BusinessException;

	PageContainer<WorkGroupNode> findAll(Account authUser, Account actor, WorkGroup workGroup, String parentUuid,
			String pattern, boolean withTree, boolean caseSensitive, PageContainer<WorkGroupNode> pageContainer,
			Date creationDateAfter, Date creationDateBefore, Date modificationDateAfter,
			Date modificationDateBefore, List<WorkGroupNodeType> types, List<String> lastAuthors, Long minSize, Long maxSize,
			SortOrder sortOrder, SharedSpaceNodeField sortField, List<DocumentKind> documentKinds);
}
