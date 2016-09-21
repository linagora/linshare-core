/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.Lists;

public class WorkGroupEntryFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupEntryFacade {

	private final ThreadEntryService threadEntryService;

	private final ThreadService threadService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final DocumentEntryService documentEntryService;

	private final WorkGroupFolderService workGroupFolderService;

	public WorkGroupEntryFacadeImpl(AccountService accountService,
			ThreadService threadService, ThreadEntryService threadEntryService,
			FunctionalityReadOnlyService functionalityService,
			WorkGroupFolderService workGroupFolderService,
			DocumentEntryService documentEntryService) {
		super(accountService);
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.functionalityReadOnlyService = functionalityService;
		this.documentEntryService = documentEntryService;
		this.workGroupFolderService = workGroupFolderService;
	}

	@Override
	protected User checkAuthentication() throws BusinessException {
		User actor = super.checkAuthentication();
		Functionality functionality = functionalityReadOnlyService
				.getThreadTabFunctionality(actor.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return actor;
	}

	@Override
	public WorkGroupEntryDto create(String ownerUuid, String workGroupUuid,
			String workGroupFolderUuid, File tempFile, String fileName) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required thread uuid");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(tempFile, "Missing required input temp file");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread thread = threadService.find(actor, owner, workGroupUuid);
		if (thread == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Current thread was not found : " + workGroupUuid);
		}
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor,
				owner, thread, tempFile, fileName);
		// Business code outside service !
		workGroupFolderService.addEntry(actor, owner, thread, workGroupFolderUuid, threadEntry);
		return new WorkGroupEntryDto(threadEntry);
	}

	@Override
	public WorkGroupEntryDto copy(String ownerUuid, String threadUuid, String entryUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, owner, threadUuid);
		// Check if we have the right to access to the specified document entry
		DocumentEntry doc = documentEntryService.find(actor, owner, entryUuid);
		// Check if we have the right to download the specified document entry
		documentEntryService.checkDownloadPermission(actor, owner, entryUuid);
		ThreadEntry threadEntry = threadEntryService.copyFromDocumentEntry(actor, owner, thread, doc);
		return new WorkGroupEntryDto(threadEntry);
	}

	@Override
	public DocumentDto copyFromThreadEntry(String ownerUuid, String threadUuid, String entryUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, owner, threadUuid);
		// Check if we have the right to access to the specified thread entry
		ThreadEntry threadEntry = threadEntryService.find(actor, owner,
				entryUuid);

		DocumentEntry docEntry = threadEntryService.copyFromThreadEntry(actor,
				owner, thread, threadEntry);
		return new DocumentDto(docEntry);
	}

	@Override
	public WorkGroupEntryDto find(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Used to check if we have the right to read this entry.
		@SuppressWarnings("unused")
		Thread thread = threadService.find(actor, owner, threadUuid);
		ThreadEntry threadEntry = threadEntryService.find(actor, owner,
				uuid);
		return new WorkGroupEntryDto(threadEntry);
	}

	@Override
	public List<WorkGroupEntryDto> findAll(String ownerUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		List<WorkGroupEntryDto> res = Lists.newArrayList();
		Thread thread = threadService.find(actor, owner, threadUuid);
		for (ThreadEntry threadEntry : threadEntryService.findAllThreadEntries(
				actor, owner, thread)) {
			res.add(new WorkGroupEntryDto(threadEntry));
		}
		return res;
	}

	@Override
	public WorkGroupEntryDto delete(String ownerUuid, String workGroupUuid, WorkGroupEntryDto wgEntryDto)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required thread uuid");
		Validate.notNull(wgEntryDto, "Missing required thread entry");
		Validate.notEmpty(wgEntryDto.getUuid(),
				"Missing required thread entry");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		ThreadEntry wgEntry = threadEntryService.find(actor, owner,
				wgEntryDto.getUuid());
		threadEntryService.deleteThreadEntry(actor, owner, wgEntry);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		try {
			workGroupFolderService.delEntry(actor, owner, workGroup, wgEntry);
		} catch (BusinessException e) {
			if (!e.getErrorCode().equals(BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND)) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
		return new WorkGroupEntryDto(wgEntry);
	}

	@Override
	public WorkGroupEntryDto delete(String ownerUuid, String threadUuid, String threadEntryUuid)
			throws BusinessException {
		Validate.notNull(threadEntryUuid, "Missing required thread uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required thread entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, threadUuid);
		ThreadEntry threadEntry = threadEntryService.find(actor, owner,
				threadEntryUuid);
		threadEntryService.deleteThreadEntry(actor, owner, threadEntry);
		workGroupFolderService.delEntry(actor, owner, workGroup, threadEntry);
		return new WorkGroupEntryDto(threadEntry);
	}

	@Override
	public Response download(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		ThreadEntry threadentry = threadEntryService.find(actor, owner,
				uuid);
		InputStream threadEntryStream = threadEntryService.getDocumentStream(
				actor, owner, uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(threadEntryStream,
						threadentry.getName(), threadentry.getType(),
						threadentry.getSize());
		return response.build();
	}

	@Override
	public Response thumbnail(String ownerUuid, String threadUuid, String uuid, boolean base64)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		ThreadEntry doc = threadEntryService.find(actor, owner, uuid);
		InputStream documentStream = threadEntryService
				.getDocumentThumbnailStream(actor, owner, uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getThumbnailResponseBuilder(documentStream, doc.getName()
						+ "_thumb.png", base64);
		return response.build();
	}

	@Override
	public WorkGroupEntryDto update(String ownerUuid, String threadUuid, String threadEntryUuid,
			WorkGroupEntryDto threadEntryDto) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required thread entry uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		return new WorkGroupEntryDto(threadEntryService.updateFileProperties(
				actor, owner, threadEntryUuid, threadEntryDto.getDescription(),
				threadEntryDto.getMetaData(), threadEntryDto.getName()));
	}
}
