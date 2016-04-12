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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestFacadeImpl extends GenericFacadeImpl implements UploadRequestFacade {

	private final UploadRequestService uploadRequestService;

	public UploadRequestFacadeImpl(AccountService accountService, UploadRequestService uploadRequestService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
	}

	@Override
	public List<UploadRequestDto> findAll(String ownerUuid) {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		List<UploadRequest> eList = uploadRequestService.findAllRequest(actor, owner, null);
		return ImmutableList.copyOf(Lists.transform(eList, UploadRequestDto.toDto(false)));
	}

	@Override
	public UploadRequestDto find(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequest ur = uploadRequestService.findRequestByUuid(actor, owner, uuid);
		return new UploadRequestDto(ur, true);
	}

	@Override
	public List<UploadRequestDto> create(String ownerUuid, UploadRequestDto uploadRequestDto, Boolean groupMode) throws BusinessException {
		Validate.notNull(uploadRequestDto, "Upload request must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequest req = uploadRequestDto.toObject();
		Contact contact = new Contact(uploadRequestDto.getRecipient().getMail());
		List<UploadRequest> e = uploadRequestService.createRequest(actor, owner, req, contact,
				uploadRequestDto.getSubject(), uploadRequestDto.getBody(), groupMode);
		return ImmutableList.copyOf(Lists.transform(e, UploadRequestDto.toDto(true)));
	}

	@Override
	public UploadRequestDto update(String ownerUuid, String uuid, UploadRequestDto req) throws BusinessException {
		Validate.notEmpty(req.getUuid(), "Upload request uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequest e = req.toObject();
		e = uploadRequestService.update(actor, owner, req.getUuid(), e);
		return new UploadRequestDto(e, true);
	}

	@Override
	public UploadRequestDto updateStatus(String ownerUuid, String uuid, String status) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequestStatus stat = UploadRequestStatus.fromString(status);
		UploadRequest e = uploadRequestService.updateStatus(actor, owner, uuid, stat);
		return new UploadRequestDto(e, false);
	}

	@Override
	public UploadRequestDto delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequest e = uploadRequestService.deleteRequest(actor, owner, uuid);
		return new UploadRequestDto(e, false);
	}

	@Override
	public UploadRequestDto delete(String ownerUuid, UploadRequestDto uploadRequestDto) throws BusinessException {
		Validate.notNull(uploadRequestDto, "Upload Request must be set.");
		Validate.notEmpty(uploadRequestDto.getUuid(), "Upload Request uuid must be set.");
		return delete(ownerUuid, uploadRequestDto.getUuid());
	}
}
