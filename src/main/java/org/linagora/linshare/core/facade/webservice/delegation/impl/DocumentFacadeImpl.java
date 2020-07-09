/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

public class DocumentFacadeImpl extends DelegationGenericFacadeImpl implements
		DocumentFacade {

	private final DocumentEntryService documentEntryService;

	public DocumentFacadeImpl(final AccountService accountService,
			final DocumentEntryService documentEntryService,
			final UserService userService) {
		super(accountService, userService);
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<DocumentDto> findAll(String actorUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		List<DocumentEntry> list = documentEntryService.findAll(authUser, actor);
		return  ImmutableList.copyOf(Lists.transform(list, DocumentDto.toDelegationVo()));
	}

	@Override
	public DocumentDto create(String actorUuid, File file,
			String description, String givenFileName) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(file, "Missing required file");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		DocumentEntry doc = documentEntryService.create(authUser, actor, file,
				givenFileName, description, false, null);
		return new DocumentDto(doc);
	}

	@Override
	public DocumentDto find(String actorUuid, String documentUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "");
		Validate.notEmpty(documentUuid, "");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		return new DocumentDto(documentEntryService.find(authUser, actor,
				documentUuid));
	}

	@Override
	public DocumentDto delete(String actorUuid, DocumentDto documentDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(documentDto, "Missing required documentDto");
		Validate.notEmpty(documentDto.getUuid(),
				"Missing required document uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		DocumentEntry doc = documentEntryService.delete(authUser, actor, documentDto.getUuid());
		return new DocumentDto(doc);
	}

	@Override
	public DocumentDto delete(String actorUuid, String documentUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		DocumentEntry doc = documentEntryService.delete(authUser, actor, documentUuid);
		return new DocumentDto(doc);
	}

	@Override
	public Response download(String actorUuid, String documentUuid)
			throws BusinessException {

		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		DocumentEntry doc = documentEntryService.find(authUser, actor,
				documentUuid);

		ByteSource file = documentEntryService.getByteSource(authUser, actor,
				documentUuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(file, doc.getName(), doc.getType(),
						doc.getSize());
		return response.build();
	}

	@Override
	public Response thumbnail(String actorUuid, String documentUuid, ThumbnailType kind)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		if (kind == null) {
			kind = ThumbnailType.MEDIUM;
		}

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		DocumentEntry doc = documentEntryService.find(authUser, actor,
				documentUuid);
		ByteSource byteSource = documentEntryService.getThumbnailByteSource(authUser, actor, documentUuid, kind);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(byteSource, doc.getName() + "_thumb.png",
						"image/png");
		return response.build();
	}

	@Override
	public DocumentDto update(String actorUuid, String documentUuid,
			DocumentDto documentDto) throws BusinessException {

		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(documentDto, "Missing required DocumentDto");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		return new DocumentDto(documentEntryService.updateFileProperties(authUser, actor, documentUuid,
				documentDto.getName(), documentDto.getDescription(),
				documentDto.getMetaData()));
	}

	@Override
	public DocumentDto updateFile(String actorUuid, String documentUuid,
			File file, String givenFileName) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(file, "Missing required File stream");

		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);

		return new DocumentDto(documentEntryService.update(authUser, actor,
				documentUuid, file, givenFileName));
	}
}
