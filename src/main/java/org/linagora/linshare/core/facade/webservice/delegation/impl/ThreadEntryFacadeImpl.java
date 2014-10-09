/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.ThreadEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;

import com.google.common.collect.Lists;

public class ThreadEntryFacadeImpl extends DelegationGenericFacadeImpl
		implements ThreadEntryFacade {

	private final ThreadService threadService;

	private final ThreadEntryService threadEntryService;

	private final DocumentEntryService documentEntryService;

	public ThreadEntryFacadeImpl(final AccountService accountService,
			final UserService userService, final ThreadService threadService,
			final ThreadEntryService threadEntryService,
			final DocumentEntryService documentEntryService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public ThreadEntryDto create(String ownerUuid, String threadUuid,
			InputStream theFile, String comment, String fileName) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(theFile, "Missing required file");
		Validate.notNull(fileName, "Missing required fileName");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.findByLsUuid(actor, owner, threadUuid);
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor,
				owner, thread, theFile, fileName);
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public ThreadEntryDto copy(String ownerUuid, String threadUuid,
			String entryUuid) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.findByLsUuid(actor, owner, threadUuid);
		DocumentEntry doc = documentEntryService.find(actor, owner, entryUuid);
		InputStream stream = documentEntryService.getDocumentStream(actor,
				owner, entryUuid);
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor,
				owner, thread, stream, doc.getName());
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public ThreadEntryDto find(String ownerUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.findByLsUuid(actor, owner, threadUuid);
		DocumentEntry doc = documentEntryService.find(actor, owner, entryUuid);
		InputStream stream = documentEntryService.getDocumentStream(actor,
				owner, entryUuid);
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor,
				owner, thread, stream, doc.getName());
		return new ThreadEntryDto(threadEntry);
	}

	@Override
	public List<ThreadEntryDto> findAll(String ownerUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.findByLsUuid(actor, owner, threadUuid);
		List<ThreadEntryDto> ret = Lists.newArrayList();

		for (ThreadEntry t : threadEntryService.findAllThreadEntries(actor, owner, thread)) {
			ret.add(new ThreadEntryDto(t));
		}
		return ret;
	}

	@Override
	public ThreadEntryDto update(String ownerUuid, String threadUuid,
			ThreadEntryDto threadEntry) throws BusinessException {
		return null;
	}

	@Override
	public void delete(String ownerUuid, String threadUuid,
			ThreadEntryDto threadEntry) throws BusinessException {
		delete(ownerUuid, threadUuid, threadEntry.getUuid());
	}

	@Override
	public void delete(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException {
	}

	@Override
	public Response download(String ownerUuid, String threadUuid,
			String entryUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(entryUuid, "Missing required entry uuid");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);

		return Response.ok(
				threadEntryService.getDocumentStream(actor, owner, entryUuid))
				.build();
	}

	@Override
	public Response thumbnail(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException {
		return null;
	}
}
