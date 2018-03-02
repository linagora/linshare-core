/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationtDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadRequestGroupService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestGroupFacadeImpl extends GenericFacadeImpl implements UploadRequestGroupFacade {

	private final UploadRequestGroupService uploadRequestGroupService;

	public UploadRequestGroupFacadeImpl(AccountService accountService,
			final UploadRequestGroupService uploadRequestGroupService) {
		super(accountService);
		this.uploadRequestGroupService = uploadRequestGroupService;
	}

	@Override
	public List<UploadRequestGroupDto> findAll(String actorUuid, List<UploadRequestStatus> status) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<UploadRequestGroup> list = uploadRequestGroupService.findAllGroupRequest(authUser, actor, status);
		return ImmutableList.copyOf(Lists.transform(list, UploadRequestGroupDto.toDto()));
	}

	@Override
	public UploadRequestGroupDto find(String actorUuid, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		UploadRequestGroup group = uploadRequestGroupService.findRequestGroupByUuid(authUser, actor, uuid);
		return new UploadRequestGroupDto(group);
	}

	@Override
	public List<UploadRequestDto> create(String actorUuid, UploadRequestCreationtDto uploadRequesCreationtDto,
			Boolean groupMode) throws BusinessException {
		Validate.notNull(uploadRequesCreationtDto, "Upload request must be set.");
		Validate.notNull(uploadRequesCreationtDto.getSubject(), "Upload request subject must be set.");
		Validate.notEmpty(uploadRequesCreationtDto.getContactList());
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest req = uploadRequesCreationtDto.toObject();
		List<Contact> contacts = Lists.newArrayList();
		for (String mail : uploadRequesCreationtDto.getContactList()) {
			contacts.add(new Contact(mail));
		}
		List<UploadRequest> e = uploadRequestGroupService.createRequest(authUser, actor, req, contacts,
				uploadRequesCreationtDto.getSubject(), uploadRequesCreationtDto.getBody(), groupMode);
		return ImmutableList.copyOf(Lists.transform(e, UploadRequestDto.toDto(true)));
	}

	@Override
	public UploadRequestGroupDto updateStatus(String actorUuid, String requestGroupUuid, UploadRequestStatus status, boolean copy) throws BusinessException {
		Validate.notEmpty(requestGroupUuid, "Upload request group uuid must be set.");
		Validate.notNull(status, "Status must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.updateStatus(authUser, actor, requestGroupUuid, status, copy);
		return new UploadRequestGroupDto(uploadRequestGroup);
	}

	public UploadRequestGroupDto update(String actorUuid, UploadRequestGroupDto uploadRequestGroupDto) {
		Validate.notNull(uploadRequestGroupDto, "Upload request group must be set.");
		Validate.notNull(uploadRequestGroupDto.getUuid(), "Upload request group uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupDto.toObject();
		return new UploadRequestGroupDto(uploadRequestGroupService.update(authUser, actor, uploadRequestGroup));
	}

	@Override
	public UploadRequestGroupDto addRecipients(String actorUuid, String groupUuid, List<ContactDto> recipientEmail) {
		Validate.notNull(groupUuid, "Upload request group must be set.");
		Validate.notEmpty(recipientEmail, "Upload request contact must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.findRequestGroupByUuid(authUser, actor, groupUuid);
		uploadRequestGroup = uploadRequestGroupService.addNewRecipients(authUser, actor, uploadRequestGroup, recipientEmail);
		return new UploadRequestGroupDto(uploadRequestGroup);
	}
}
