/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.delegation.ThreadEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.Lists;

public class ThreadEntryFacadeImpl extends DelegationGenericFacadeImpl
		implements ThreadEntryFacade {

	private final ThreadService threadService;

	private final WorkGroupNodeService workGroupNodeService;

	private final DocumentEntryService documentEntryService;

	public ThreadEntryFacadeImpl(final AccountService accountService,
			final UserService userService, final ThreadService threadService,
			final WorkGroupNodeService workGroupNodeService,
			final DocumentEntryService documentEntryService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.workGroupNodeService = workGroupNodeService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public WorkGroupEntryDto create(String ownerUuid, String threadUuid,
			File file, String fileName) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(file, "Missing required file");
		Validate.notNull(fileName, "Missing required fileName");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.find(actor, owner, threadUuid);
		WorkGroupNode node = workGroupNodeService.create(actor, owner, thread, file, fileName, null, false);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		// why ?
//		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}

	@Override
	public WorkGroupEntryDto copy(String ownerUuid, String threadUuid,
			String entryUuid) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		// Check if we have the right to access to the specified thread
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		// Check if we have the right to access to the specified document entry
		documentEntryService.find(actor, owner, entryUuid);
		// Check if we have the right to download the specified document entry
		documentEntryService.checkDownloadPermission(actor, owner, entryUuid);
		WorkGroupNode node = workGroupNodeService.copy(actor, owner, workGroup, entryUuid, null);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}

	@Override
	public WorkGroupEntryDto find(String ownerUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		// Check if we have the right to access to the specified thread
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		WorkGroupNode node = workGroupNodeService.find(actor, owner, workGroup, entryUuid, false);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}

	@Override
	public List<WorkGroupEntryDto> findAll(String ownerUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		// Check if we have the right to access to the specified thread
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		List<WorkGroupNode> all = workGroupNodeService.findAll(actor, owner, workGroup, null, true, null);
		List<WorkGroupEntryDto> ret = Lists.newArrayList();
		for (WorkGroupNode node : all) {
			if (node.getNodeType().equals(WorkGroupNodeType.DOCUMENT)) {
				ret.add(new WorkGroupEntryDto((WorkGroupDocument)node));
			}
		}
		return ret;
	}

	@Override
	public WorkGroupEntryDto delete(String ownerUuid, String threadUuid,
			WorkGroupEntryDto threadEntry) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required entry uuid");
		return delete(ownerUuid, threadUuid, threadEntry.getUuid());
	}

	@Override
	public WorkGroupEntryDto delete(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		// Check if we have the right to access to the specified thread
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		WorkGroupNode node = workGroupNodeService.delete(actor, owner, workGroup, uuid);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}

	@Override
	public Response download(String ownerUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		FileAndMetaData data = workGroupNodeService.download(actor, owner, workGroup, entryUuid);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return builder.build();
	}

	@Override
	public Response thumbnail(String ownerUuid, String threadUuid, String threadEntryUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required document uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required document uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		FileAndMetaData data = workGroupNodeService.thumbnail(actor, owner, workGroup, threadEntryUuid);
		ResponseBuilder builder = DocumentStreamReponseBuilder
				.getThumbnailResponseBuilder(data, false);
		return builder.build();
	}

	@Override
	public WorkGroupEntryDto update(String ownerUuid, String threadUuid,
			String threadEntryUuid, WorkGroupEntryDto threadEntryDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required document uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required document uuid");
		Validate.notNull(threadEntryDto, "Missing required threadEntryDto");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		WorkGroupDocument document = new WorkGroupDocument();
		document.setUuid(threadEntryDto.getUuid());
		document.setName(threadEntryDto.getName());
		document.setDescription(threadEntryDto.getDescription());
		document.setMetaData(threadEntryDto.getMetaData());
		WorkGroupNode node = workGroupNodeService.update(actor, owner, workGroup, document);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		return dto;
	}
}
