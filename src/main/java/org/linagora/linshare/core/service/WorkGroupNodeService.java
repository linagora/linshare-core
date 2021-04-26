/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
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

import java.io.File;
import java.util.List;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;

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

}
