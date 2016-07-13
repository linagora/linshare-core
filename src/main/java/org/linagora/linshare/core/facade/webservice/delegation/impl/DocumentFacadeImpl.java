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
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.Validate;
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
	public List<DocumentDto> findAll(String ownerUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		List<DocumentEntry> list = documentEntryService.findAll(actor, owner);
		return  ImmutableList.copyOf(Lists.transform(list, DocumentDto.toDelegationVo()));
	}

	@Override
	public DocumentDto create(String ownerUuid, File file,
			String description, String givenFileName) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(file, "Missing required file");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		DocumentEntry doc = documentEntryService.create(actor, owner, file,
				givenFileName, description, false, null);
		return new DocumentDto(doc);
	}

	@Override
	public DocumentDto find(String ownerUuid, String documentUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "");
		Validate.notEmpty(documentUuid, "");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		return new DocumentDto(documentEntryService.find(actor, owner,
				documentUuid));
	}

	@Override
	public DocumentDto delete(String ownerUuid, DocumentDto documentDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(documentDto, "Missing required documentDto");
		Validate.notEmpty(documentDto.getUuid(),
				"Missing required document uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		DocumentEntry doc = documentEntryService.delete(actor, owner, documentDto.getUuid());
		return new DocumentDto(doc);
	}

	@Override
	public DocumentDto delete(String ownerUuid, String documentUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		DocumentEntry doc = documentEntryService.delete(actor, owner, documentUuid);
		return new DocumentDto(doc);
	}

	@Override
	public Response download(String ownerUuid, String documentUuid)
			throws BusinessException {

		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		DocumentEntry doc = documentEntryService.find(actor, owner,
				documentUuid);

		InputStream file = documentEntryService.getDocumentStream(actor, owner,
				documentUuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(file, doc.getName(), doc.getType(),
						doc.getSize());
		return response.build();
	}

	@Override
	public Response thumbnail(String ownerUuid, String documentUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		DocumentEntry doc = documentEntryService.find(actor, owner,
				documentUuid);
		InputStream file = documentEntryService.getDocumentThumbnailStream(actor, owner, documentUuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(file, doc.getName() + "_thumb.png",
						"image/png");
		return response.build();
	}

	@Override
	public DocumentDto update(String ownerUuid, String documentUuid,
			DocumentDto documentDto) throws BusinessException {

		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(documentDto, "Missing required DocumentDto");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		return new DocumentDto(documentEntryService.updateFileProperties(actor, owner, documentUuid,
				documentDto.getName(), documentDto.getDescription(),
				documentDto.getMetaData()));
	}

	@Override
	public DocumentDto updateFile(String ownerUuid, String documentUuid,
			File file, String givenFileName) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(documentUuid, "Missing required document uuid");
		Validate.notNull(file, "Missing required File stream");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		return new DocumentDto(documentEntryService.update(actor, owner,
				documentUuid, file, givenFileName));
	}
}
