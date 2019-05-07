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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
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
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.NodeDetailsMto;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class WorkGroupNodeFacadeImpl extends UserGenericFacadeImp implements WorkGroupNodeFacade {

	protected final ThreadService threadService;

	protected final WorkGroupNodeService service;

	protected final FunctionalityReadOnlyService functionalityService;

	protected final AuditLogEntryService auditLogEntryService;

	protected final DocumentEntryService documentEntryService;

	protected final ShareEntryService shareEntryService;

	protected final SharedSpaceNodeService sharedSpaceNodeService;

	protected final WorkGroupDocumentRevisionService workGroupDocumentRevisionService;

	public WorkGroupNodeFacadeImpl(AccountService accountService,
			WorkGroupNodeService service,
			ThreadService threadService,
			FunctionalityReadOnlyService functionalityService,
			DocumentEntryService documentEntryService,
			ShareEntryService shareEntryService,
			AuditLogEntryService auditLogEntryService,
			SharedSpaceNodeService sharedSpaceNodeService,
			WorkGroupDocumentRevisionService workGroupDocumentRevisionService) {
		super(accountService);
		this.service = service;
		this.threadService = threadService;
		this.documentEntryService = documentEntryService;
		this.shareEntryService = shareEntryService;
		this.functionalityService = functionalityService;
		this.auditLogEntryService = auditLogEntryService;
		this.sharedSpaceNodeService = sharedSpaceNodeService;
		this.workGroupDocumentRevisionService = workGroupDocumentRevisionService;
	}

	@Override
	protected User checkAuthentication() throws BusinessException {
		User authUser = super.checkAuthentication();
		Functionality functionality = functionalityService.getWorkGroupFunctionality(authUser.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return authUser;
	}

	@Override
	public List<WorkGroupNode> findAll(String actorUuid, String workGroupUuid, String parentNodeUuid,
			Boolean flat, List<WorkGroupNodeType> nodeTypes) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.findAll(authUser, actor, workGroup, parentNodeUuid, flat, nodeTypes);
	}

	@Override
	public WorkGroupNode find(String actorUuid, String workGroupUuid, String workGroupNodeUuid, Boolean withTree)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup folder uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.find(authUser, actor, workGroup, workGroupNodeUuid, withTree);
	}

	@Override
	public WorkGroupNode create(String actorUuid, String workGroupUuid, WorkGroupNode workGroupNode, Boolean strict,
			Boolean dryRun) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupNode.getName(), "Missing default name for the folder");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.create(authUser, actor, workGroup, workGroupNode, strict, dryRun);
	}

	@Override
	public WorkGroupNode create(String actorUuid, String workGroupUuid, String parentNodeUuid, File tempFile,
			String fileName, Boolean strict) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required thread uuid");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(tempFile, "Missing required input temp file");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.create(authUser, actor, workGroup, tempFile, fileName, parentNodeUuid, strict);
	}

	@Override
	public List<WorkGroupNode> copy(String actorUuid, String workGroupUuid, String toParentNodeUuid, CopyDto copy,
			boolean deleteShare) {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, actorUuid);
		Validate.notNull(copy);
		TargetKind resourceKind = copy.getKind();
		Validate.notNull(resourceKind, "Missing resource kind.");
		String fromResourceUuid = copy.getUuid();
		Validate.notEmpty(fromResourceUuid, "Missing entry uuid to copy from");
		Validate.notEmpty(workGroupUuid, "Missing workGroup uuid to copy into");
		SharedSpaceNode toSharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		// The workgroup here is used for the OperationHistory
		WorkGroup toWorkGroup = threadService.find(authUser, actor, toSharedSpaceNode.getUuid());
		if (TargetKind.RECEIVED_SHARE.equals(resourceKind)) {
			// if the current user do have enough space, there is side effect on audit.
			// Some audit traces will be created before quota checks ! :s
			ShareEntry share = shareEntryService.findForDownloadOrCopyRight(authUser, actor, fromResourceUuid);
			CopyResource cr = new CopyResource(resourceKind, share);
			WorkGroupNode node = service.copy(authUser, actor, toWorkGroup, toParentNodeUuid, cr);
			shareEntryService.markAsCopied(authUser, actor, fromResourceUuid, new CopyMto(node, toWorkGroup));
			if (deleteShare) {
				shareEntryService.delete(authUser, actor, share.getUuid(), LogActionCause.COPY);
			}
			return Lists.newArrayList(node);
		} else if (TargetKind.PERSONAL_SPACE.equals(resourceKind)) {
			DocumentEntry documentEntry = documentEntryService.findForDownloadOrCopyRight(authUser, actor,
					fromResourceUuid);
			CopyResource cr = new CopyResource(resourceKind, documentEntry);
			WorkGroupNode node = service.copy(authUser, actor, toWorkGroup, toParentNodeUuid, cr);
			documentEntryService.markAsCopied(authUser, actor, documentEntry, new CopyMto(node, toWorkGroup));
			return Lists.newArrayList(node);
		} else if (TargetKind.SHARED_SPACE.equals(resourceKind)) {
			String fromWorkGroupUuid = service.findWorkGroupUuid(authUser, actor, fromResourceUuid);
			// The workgroup here is used for the OperationHistory
			WorkGroup fromWorkGroup = threadService.find(authUser, actor, fromWorkGroupUuid);
			WorkGroupNode node = service.copy(authUser, actor, fromWorkGroup, fromResourceUuid, toWorkGroup,
					toParentNodeUuid);
			return Lists.newArrayList(node);
		}
		throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN, "This action is not supported.");
	}

	@Override
	public WorkGroupNode update(String actorUuid, String workGroupUuid, WorkGroupNode workGroupNode)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupNode, "Missing required workGroupFolder");
		Validate.notEmpty(workGroupNode.getUuid(), "Missing required workGroupNode uuid");
		Validate.notEmpty(workGroupNode.getName(), "Missing required name");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.update(authUser, actor, workGroup, workGroupNode);
	}

	@Override
	public WorkGroupNode delete(String actorUuid, String workGroupUuid, String workGroupNodeUuid, WorkGroupNode workGroupNode)
			throws BusinessException {
		if (Strings.isNullOrEmpty(workGroupNodeUuid)) {
			Validate.notNull(workGroupNode, "Missing required workGroup folder");
			Validate.notEmpty(workGroupNode.getUuid(), "Missing required workGroup folder uuid");
			workGroupNodeUuid = workGroupNode.getUuid();
		}
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		// The workgroup here is used for the OperationHistory
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		return service.delete(authUser, actor, workGroup, workGroupNodeUuid);
	}

	@Override
	public Response download(String actorUuid, String workGroupUuid, String workGroupNodeUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup node uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		FileAndMetaData data = service.download(authUser, actor, workGroup, workGroupNodeUuid);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return builder.build();
	}

	@Override
	public Response thumbnail(String actorUuid, String workGroupUuid, String workGroupNodeUuid, boolean base64,
			ThumbnailType thumbnailType) throws BusinessException {
		if (thumbnailType == null) {
			thumbnailType = ThumbnailType.MEDIUM;
		}
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupNodeUuid, "Missing required workGroup node uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		FileAndMetaData data = service.thumbnail(authUser, actor, workGroup, workGroupNodeUuid, thumbnailType);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(data, base64, thumbnailType);
		return builder.build();
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String actorUuid, String workGroupUuid, String workGroupNodeUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, String beginDate, String endDate) {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, workGroupUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		WorkGroupNode workGroupNode = service.find(authUser, actor, workGroup, workGroupNodeUuid, false);
		return auditLogEntryService.findAll(authUser, actor, workGroup, workGroupNode, actions, types, beginDate,
				endDate);
	}

	@Override
	public String findByWorkGroupNodeUuid(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required workGroup node uuid");
		checkAuthentication();
		WorkGroupNode workGroupNode = service.findByWorkGroupNodeUuid(uuid);
		Validate.notNull(workGroupNode, "Node not found");
		return workGroupNode.getWorkGroup();
	}

	@Override
	public NodeDetailsMto findDetails(String actorUuid, String sharedSpaceUuid, String sharedSpaceNodeUuid) {
		Validate.notEmpty(sharedSpaceUuid, "Missing required workGroup uuid");
		Validate.notEmpty(sharedSpaceNodeUuid, "Missing required workGroup folder uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		SharedSpaceNode sharedSpaceNode = sharedSpaceNodeService.find(authUser, actor, sharedSpaceUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, sharedSpaceNode.getUuid());
		WorkGroupNode node = service.find(authUser, actor, workGroup, sharedSpaceNodeUuid, true);
		return service.findDetails(authUser, actor, workGroup, node);
	}

}
