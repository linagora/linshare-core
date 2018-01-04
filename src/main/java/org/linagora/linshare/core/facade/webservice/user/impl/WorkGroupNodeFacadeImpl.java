/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;

import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.Lists;

public class WorkGroupNodeFacadeImpl extends UserGenericFacadeImp implements WorkGroupNodeFacade {

	protected final ThreadService threadService;

	protected final WorkGroupNodeService service;

	protected final FunctionalityReadOnlyService functionalityService;

	protected final AuditLogEntryService auditLogEntryService;

	protected final DocumentEntryService documentEntryService;

	protected final ShareEntryService shareEntryService;

	public WorkGroupNodeFacadeImpl(AccountService accountService,
			WorkGroupNodeService service,
			ThreadService threadService,
			FunctionalityReadOnlyService functionalityService,
			DocumentEntryService documentEntryService,
			ShareEntryService shareEntryService,
			AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.service = service;
		this.threadService = threadService;
		this.documentEntryService = documentEntryService;
		this.shareEntryService = shareEntryService;
		this.functionalityService = functionalityService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	protected User checkAuthentication() throws BusinessException {
		User actor = super.checkAuthentication();
		Functionality functionality = functionalityService
				.getWorkGroupFunctionality(actor.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return actor;
	}

	@Override
	public List<WorkGroupNode> findAll(String ownerUuid, String workGroupUuid, String parentNodeUuid, Boolean flatDocumentMode, WorkGroupNodeType nodeType) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.findAll(actor, owner, workGroup, parentNodeUuid, flatDocumentMode, nodeType);
	}

	@Override
	public WorkGroupNode find(String ownerUuid, String workGroupUuid, String workGroupNodeUuid, Boolean withTree)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup folder uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.find(actor, owner, workGroup, workGroupNodeUuid, withTree);
	}

	@Override
	public WorkGroupNode create(String ownerUuid, String workGroupUuid, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.create(actor, owner, workGroup, workGroupNode, strict, dryRun);
	}

	@Override
	public WorkGroupNode create(String ownerUuid, String workGroupUuid, String parentNodeUuid, File tempFile,
			String fileName, Boolean strict) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required thread uuid");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(tempFile, "Missing required input temp file");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		WorkGroupNode node = service.create(actor, owner, workGroup, tempFile, fileName, parentNodeUuid, strict);
		return node;
	}

	@Override
	public List<WorkGroupNode> copy(String ownerUuid, String workGroupUuid, String toParentNodeUuid, CopyDto copy, boolean deleteShare) {
		Account actor = checkAuthentication();
		User owner = (User) getOwner(actor, ownerUuid);
		Validate.notNull(copy);
		TargetKind resourceKind = copy.getKind();
		Validate.notNull(resourceKind, "Missing resource kind.");
		String fromResourceUuid = copy.getUuid();
		Validate.notEmpty(fromResourceUuid, "Missing entry uuid to copy from");
		Validate.notEmpty(workGroupUuid, "Missing workGroup uuid to copy into");
		Thread toWorkGroup = threadService.find(actor, owner, workGroupUuid);
		if (TargetKind.RECEIVED_SHARE.equals(resourceKind)) {
			// if the current user do have enough space, there is side effect on audit.
			// Some audit traces will be created before quota checks ! :s
			ShareEntry share = shareEntryService.findForDownloadOrCopyRight(actor, owner, fromResourceUuid);
			CopyResource cr = new CopyResource(resourceKind, share);
			WorkGroupNode node = service.copy(actor, owner, toWorkGroup, toParentNodeUuid, cr);
			shareEntryService.markAsCopied(actor, owner, fromResourceUuid, new CopyMto(node, toWorkGroup));
			if (deleteShare) {
				shareEntryService.delete(actor, owner, share.getUuid(), LogActionCause.COPY);
			}
			return Lists.newArrayList(node);
		} else if (TargetKind.PERSONAL_SPACE.equals(resourceKind)) {
			DocumentEntry documentEntry = documentEntryService.findForDownloadOrCopyRight(actor, owner, fromResourceUuid);
			CopyResource cr = new CopyResource(resourceKind, documentEntry);
			WorkGroupNode node = service.copy(actor, owner, toWorkGroup, toParentNodeUuid, cr);
			documentEntryService.markAsCopied(actor, owner, documentEntry, new CopyMto(node, toWorkGroup));
			return Lists.newArrayList(node);
		} else if (TargetKind.SHARED_SPACE.equals(resourceKind)) {
			String fromWorkGroupUuid = service.findWorkGroupUuid(actor, owner, fromResourceUuid);
			Thread fromWorkGroup = threadService.find(actor, owner, fromWorkGroupUuid);
			WorkGroupNode node = service.copy(actor, owner, fromWorkGroup, fromResourceUuid, toWorkGroup, toParentNodeUuid);
			return Lists.newArrayList(node);
		}
		throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
				"This action is not supported.");
	}

	@Override
	public WorkGroupNode update(String ownerUuid, String workGroupUuid, WorkGroupNode workGroupNode)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupNode, "Missing required workGroupFolder");
		Validate.notEmpty(workGroupNode.getUuid(), "Missing required workGroupNode uuid");
		Validate.notEmpty(workGroupNode.getName(), "Missing required name");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.update(actor, owner, workGroup, workGroupNode);
	}

	@Override
	public WorkGroupNode delete(String ownerUuid, String workGroupUuid, String workGroupNodeUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.delete(actor, owner, workGroup, workGroupNodeUuid);
	}

	@Override
	public WorkGroupNode delete(String ownerUuid, String workGroupUuid, WorkGroupNode workGroupNode)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupNode, "Missing required workGroup folder");
		Validate.notEmpty(workGroupNode.getUuid(), "Missing required workGroup folder uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.delete(actor, owner, workGroup, workGroupNode.getUuid());
	}

	@Override
	public Response download(String ownerUuid, String workGroupUuid, String workGroupNodeUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup node uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		FileAndMetaData data = service.download(actor, owner, workGroup, workGroupNodeUuid);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return builder.build();
	}

	@Override
	public Response thumbnail(String ownerUuid, String workGroupUuid, String workGroupNodeUuid, boolean base64, ThumbnailType thumbnailType)
			throws BusinessException {
		if (thumbnailType == null) {
			thumbnailType = ThumbnailType.MEDIUM;
		}
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup node uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		FileAndMetaData data = service.thumbnail(actor, owner, workGroup, workGroupNodeUuid, thumbnailType);
		ResponseBuilder builder = DocumentStreamReponseBuilder
				.getThumbnailResponseBuilder(data, base64, thumbnailType);
		return builder.build();
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String ownerUuid, String workGroupUuid, String workGroupNodeUuid,
			List<String> actions, List<String> types, String beginDate, String endDate) {
		Account actor = checkAuthentication();
		User owner = (User) getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		WorkGroupNode workGroupNode = service.find(actor, owner, workGroup, workGroupNodeUuid, false);
		return auditLogEntryService.findAll(actor, owner, workGroup, workGroupNode, actions, types, beginDate, endDate);
	}

}
