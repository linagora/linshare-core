/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.delegation.WorkGroupEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.Lists;

public class WorkGroupEntryFacadeImpl extends DelegationGenericFacadeImpl
		implements WorkGroupEntryFacade {

	private final ThreadService threadService;

	private final WorkGroupNodeService workGroupNodeService;

	private final DocumentEntryService documentEntryService;

	public WorkGroupEntryFacadeImpl(final AccountService accountService,
			final UserService userService, final ThreadService threadService,
			final WorkGroupNodeService workGroupNodeService,
			final DocumentEntryService documentEntryService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.workGroupNodeService = workGroupNodeService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public WorkGroupEntryDto create(String actorUuid, String threadUuid,
			File file, String fileName, Boolean strict) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(file, "Missing required file");
		Validate.notNull(fileName, "Missing required fileName");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		WorkGroupNode node = workGroupNodeService.create(authUser, actor, workGroup, file, fileName, null, strict);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		// why ?
//		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}

	/**
	 * copy a document entry to a workgroup.
	 */
	@Override
	public WorkGroupEntryDto copy(String actorUuid, String threadUuid,
			String entryUuid) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		// Check if we have the right to access to the specified thread
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		// Check if we have the right to download the specified document entry
		DocumentEntry de = documentEntryService.findForDownloadOrCopyRight(authUser, actor, entryUuid);
		CopyResource cr = new CopyResource(TargetKind.PERSONAL_SPACE, de);
		WorkGroupNode node = workGroupNodeService.copy(authUser, actor, workGroup, null, cr);
		documentEntryService.markAsCopied(authUser, actor, de, new CopyMto(node, workGroup));
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument) node);
		return dto;
	}

	@Override
	public WorkGroupEntryDto find(String actorUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		// Check if we have the right to access to the specified thread
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		WorkGroupNode node = workGroupNodeService.find(authUser, actor, workGroup, entryUuid, false);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}

	@Override
	public List<WorkGroupEntryDto> findAll(String actorUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		// Check if we have the right to access to the specified thread
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		List<WorkGroupNode> all = workGroupNodeService.findAll(authUser, actor, workGroup, null, true, Lists.newArrayList(WorkGroupNodeType.DOCUMENT));
		List<WorkGroupEntryDto> ret = all.stream()
				.map(node -> new WorkGroupEntryDto((WorkGroupDocument) node))
				.collect(Collectors.toList());
		return ret;
	}

	@Override
	public WorkGroupEntryDto delete(String actorUuid, String threadUuid,
			WorkGroupEntryDto threadEntry) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required entry uuid");
		return delete(actorUuid, threadUuid, threadEntry.getUuid());
	}

	@Override
	public WorkGroupEntryDto delete(String actorUuid, String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		// Check if we have the right to access to the specified thread
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		WorkGroupNode node = workGroupNodeService.delete(authUser, actor, workGroup, uuid);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}

	@Override
	public Response download(String actorUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		FileAndMetaData data = workGroupNodeService.download(authUser, actor, workGroup, entryUuid, false);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return builder.build();
	}

	@Override
	public Response thumbnail(String actorUuid, String threadUuid, String threadEntryUuid, ThumbnailType kind)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required document uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required document uuid");
		if (kind == null) {
			kind = ThumbnailType.MEDIUM;
		}
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, threadEntryUuid);
		FileAndMetaData data = workGroupNodeService.thumbnail(authUser, actor, workGroup, threadEntryUuid, kind);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(data, false, kind);
		return builder.build();
	}

	@Override
	public WorkGroupEntryDto update(String actorUuid, String threadUuid,
			String threadEntryUuid, WorkGroupEntryDto threadEntryDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required document uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required document uuid");
		Validate.notNull(threadEntryDto, "Missing required threadEntryDto");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, threadUuid);
		WorkGroupDocument document = new WorkGroupDocument();
		document.setUuid(threadEntryDto.getUuid());
		document.setName(threadEntryDto.getName());
		document.setDescription(threadEntryDto.getDescription());
		document.setMetaData(threadEntryDto.getMetaData());
		WorkGroupNode node = workGroupNodeService.update(authUser, actor, workGroup, document);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}
}
