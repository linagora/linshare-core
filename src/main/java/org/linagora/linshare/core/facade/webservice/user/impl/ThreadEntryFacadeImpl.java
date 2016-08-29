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
import org.linagora.linshare.core.facade.webservice.common.dto.ThreadEntryDto;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.Lists;

public class ThreadEntryFacadeImpl extends UserGenericFacadeImp implements
		ThreadEntryFacade {

	private final ThreadEntryService threadEntryService;
	private final ThreadService threadService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final DocumentEntryService documentEntryService;

	public ThreadEntryFacadeImpl(AccountService accountService,
			ThreadService threadService, ThreadEntryService threadEntryService,
			FunctionalityReadOnlyService functionalityService,
			DocumentEntryService documentEntryService) {
		super(accountService);
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.functionalityReadOnlyService = functionalityService;
		this.documentEntryService = documentEntryService;
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
	public ThreadEntryDto create(String threadUuid, File tempFile,
			String fileName) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(tempFile, "Missing required input temp file");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, threadUuid);
		if (thread == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Current thread was not found : " + threadUuid);
		}
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor,
				actor, thread, tempFile, fileName);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public ThreadEntryDto copy(String threadUuid, String entryUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, actor, threadUuid);
		// Check if we have the right to access to the specified document entry
		DocumentEntry doc = documentEntryService.find(actor, actor, entryUuid);
		// Check if we have the right to download the specified document entry
		documentEntryService.checkDownloadPermission(actor, actor, entryUuid);
		ThreadEntry threadEntry = threadEntryService.copyFromDocumentEntry(actor, actor, thread, doc);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public DocumentDto copyFromThreadEntry(String threadUuid, String entryUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, actor, threadUuid);
		// Check if we have the right to access to the specified thread entry
		ThreadEntry threadEntry = threadEntryService.findById(actor, actor,
				entryUuid);

		DocumentEntry docEntry = threadEntryService.copyFromThreadEntry(actor,
				actor, thread, threadEntry);
		return new DocumentDto(docEntry);
	}

	@Override
	public ThreadEntryDto find(String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		@SuppressWarnings("unused")
		Thread thread = threadService.find(actor, actor, threadUuid);
		ThreadEntry threadEntry = threadEntryService.findById(actor, actor,
				uuid);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public List<ThreadEntryDto> findAll(String threadUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		List<ThreadEntryDto> res = Lists.newArrayList();
		Thread thread = threadService.find(actor, actor, threadUuid);
		for (ThreadEntry threadEntry : threadEntryService.findAllThreadEntries(
				actor, actor, thread)) {
			res.add(new ThreadEntryDto(threadEntry));
		}
		return res;
	}

	@Override
	public ThreadEntryDto delete(String threadUuid, ThreadEntryDto threadEntryDto)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadEntryDto, "Missing required thread entry");
		Validate.notEmpty(threadEntryDto.getUuid(),
				"Missing required thread entry");
		User actor = checkAuthentication();
		ThreadEntry threadEntry = threadEntryService.findById(actor, actor,
				threadEntryDto.getUuid());
		threadEntryService.deleteThreadEntry(actor, actor, threadEntry);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public ThreadEntryDto delete(String threadUuid, String threadEntryUuid)
			throws BusinessException {
		Validate.notNull(threadEntryUuid, "Missing required thread uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required thread entry uuid");
		User actor = checkAuthentication();
		ThreadEntry threadEntry = threadEntryService.findById(actor, actor,
				threadEntryUuid);
		threadEntryService.deleteThreadEntry(actor, actor, threadEntry);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public Response download(String threadUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		ThreadEntry threadentry = threadEntryService.findById(actor, actor,
				uuid);
		InputStream threadEntryStream = threadEntryService.getDocumentStream(
				actor, actor, uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(threadEntryStream,
						threadentry.getName(), threadentry.getType(),
						threadentry.getSize());
		return response.build();
	}

	@Override
	public Response thumbnail(String threadUuid, String uuid, boolean base64)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(uuid, "Missing required entry uuid");
		User actor = checkAuthentication();
		ThreadEntry doc = threadEntryService.findById(actor, actor, uuid);
		InputStream documentStream = threadEntryService
				.getDocumentThumbnailStream(actor, actor, uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getThumbnailResponseBuilder(documentStream, doc.getName()
						+ "_thumb.png", base64);
		return response.build();
	}

	@Override
	public ThreadEntryDto update(String threadUuid, String threadEntryUuid,
			ThreadEntryDto threadEntryDto) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(threadEntryUuid, "Missing required thread entry uuid");
		User actor = checkAuthentication();
		return new ThreadEntryDto(threadEntryService.updateFileProperties(
				actor, threadEntryUuid, threadEntryDto.getDescription(),
				threadEntryDto.getMetaData(), threadEntryDto.getName()));
	}
}
