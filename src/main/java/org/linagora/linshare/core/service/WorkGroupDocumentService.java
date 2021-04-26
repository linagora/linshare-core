/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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
package org.linagora.linshare.core.service;

import java.io.InputStream;

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

	InputStream getDocumentStream(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision node,
			WorkGroupNodeType nodeType) throws BusinessException;

	InputStream getThumbnailStream(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocument node, ThumbnailType thumbnailType)
			throws BusinessException;

	FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, WorkGroupDocument node,
			WorkGroupDocumentRevision revision);
	
	WorkGroupNode copy(Account actor, Account owner, WorkGroup toWorkGroup, String documentUuid, String fileName,
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom,
			WorkGroupNodeAuditLogEntry log, boolean moveDocument) throws BusinessException;

}
