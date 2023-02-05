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

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.google.common.io.ByteSource;

public interface WorkGroupDocumentService extends WorkGroupNodeAbstractService {

	WorkGroupDocument createWithoutLogStorage(Account actor, Account owner, WorkGroup workGroup, Long size, String mimeType, String fileName,
			WorkGroupNode nodeParent) throws BusinessException;

	WorkGroupNode create(Account actor, Account owner, WorkGroup workGroup, Long size, String mimeType, String fileName,
			WorkGroupNode nodeParent) throws BusinessException;

	WorkGroupNode copy(Account actor, Account owner, WorkGroup toWorkGroup, String documentUuid, String fileName,
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom, WorkGroupNodeAuditLogEntry auditLogEntry) throws BusinessException;

	void markAsCopied(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode node, CopyMto copiedTo) throws BusinessException;

	WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode)
			throws BusinessException;

	ByteSource getDocumentStream(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision node,
			WorkGroupNodeType nodeType) throws BusinessException;

	ByteSource getThumbnailByteSource(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocument node, ThumbnailType thumbnailType)
			throws BusinessException;

	FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, WorkGroupDocument node,
			WorkGroupDocumentRevision revision);

	WorkGroupNode copy(Account actor, Account owner, WorkGroup toWorkGroup, String documentUuid, String fileName,
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom,
			WorkGroupNodeAuditLogEntry log, boolean moveDocument) throws BusinessException;

}
