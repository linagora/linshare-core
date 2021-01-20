/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestGroupFacadeImpl extends GenericFacadeImpl implements UploadRequestGroupFacade {

	private final UploadRequestGroupService uploadRequestGroupService;

	private final AuditLogEntryService auditLogEntryService;

	private final UploadRequestService uploadRequestService;

	public UploadRequestGroupFacadeImpl(AccountService accountService,
			final UploadRequestGroupService uploadRequestGroupService,
			final AuditLogEntryService auditLogEntryService,
			final UploadRequestService uploadRequestService) {
		super(accountService);
		this.uploadRequestGroupService = uploadRequestGroupService;
		this.auditLogEntryService = auditLogEntryService;
		this.uploadRequestService = uploadRequestService;
	}

	@Override
	public List<UploadRequestGroupDto> findAll(String actorUuid, List<UploadRequestStatus> status) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<UploadRequestGroup> list = uploadRequestGroupService.findAll(authUser, actor, status);
		return ImmutableList.copyOf(Lists.transform(list, UploadRequestGroupDto.toDto()));
	}

	@Override
	public UploadRequestGroupDto find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		UploadRequestGroup group = uploadRequestGroupService.find(authUser, actor, uuid);
		return new UploadRequestGroupDto(group);
	}

	@Override
	public UploadRequestGroupDto create(String actorUuid, UploadRequestCreationDto uploadRequesCreationtDto,
			Boolean collectiveMode) throws BusinessException {
		Validate.notNull(uploadRequesCreationtDto, "Upload request must be set.");
		Validate.notEmpty(uploadRequesCreationtDto.getLabel(), "Upload request label must be set.");
		Validate.notEmpty(uploadRequesCreationtDto.getContactList(), "ContactList must be set");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest req = uploadRequesCreationtDto.toObject();
		List<Contact> contacts = Lists.newArrayList();
		for (String mail : uploadRequesCreationtDto.getContactList()) {
			Validate.notEmpty(mail, "Mail of the contact must be set");
			contacts.add(new Contact(mail));
		}
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(authUser, actor, req, contacts,
				uploadRequesCreationtDto.getLabel(), uploadRequesCreationtDto.getBody(), collectiveMode);
		return new UploadRequestGroupDto(uploadRequestGroup);
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

	public UploadRequestGroupDto update(String actorUuid, UploadRequestGroupDto uploadRequestGroupDto, String uuid, boolean force) {
		Validate.notNull(uploadRequestGroupDto, "Upload request group must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			uploadRequestGroupDto.setUuid(uuid);
		}
		Validate.notEmpty(uploadRequestGroupDto.getUuid(), "Upload request group uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupDto.toObject();
		return new UploadRequestGroupDto(uploadRequestGroupService.update(authUser, actor, uploadRequestGroup, force));
	}

	@Override
	public UploadRequestGroupDto addRecipients(String actorUuid, String groupUuid, List<ContactDto> recipientEmail) {
		Validate.notEmpty(groupUuid, "Upload request group must be set.");
		Validate.notEmpty(recipientEmail, "Upload request contact must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.find(authUser, actor, groupUuid);
		uploadRequestGroup = uploadRequestGroupService.addNewRecipients(authUser, actor, uploadRequestGroup, recipientEmail);
		return new UploadRequestGroupDto(uploadRequestGroup);
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String actorUuid, String groupUuid, boolean detail, boolean entriesLogsOnly,
			List<LogAction> actions, List<AuditLogEntryType> types) {
		Validate.notEmpty(groupUuid, "Upload request group uuid must be set");
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, null);
		return auditLogEntryService.findAll(authUser, actor, groupUuid, detail, entriesLogsOnly, actions, types);
	}
	
	@Override
	public Set<AuditLogEntryUser> findAllAuditsForUploadRequest(String actorUuid, String groupUuid,
			String uploadRequestUuid, List<LogAction> actions, List<AuditLogEntryType> types) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(groupUuid, "Upload request group Uuid must be set");
		Validate.notEmpty(uploadRequestUuid, "Upload request Uuid must be set");
		Account actor = getActor(authUser, null);
		UploadRequestGroup group = uploadRequestGroupService.find(authUser, actor, groupUuid);
		UploadRequest ur = uploadRequestService.find(authUser, actor, uploadRequestUuid);
		if (!group.getUuid().equals(ur.getUploadRequestGroup().getUuid())) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_NOT_FOUND,
					"The upload request with uuid: " + ur.getUuid() + "does not belong to the group with uuid: " + groupUuid);
		}
		return auditLogEntryService.findAllUploadRequestAudits(authUser, actor, uploadRequestUuid, actions, types);
	}

	@Override
	public List<UploadRequestDto> findAllUploadRequests(String actorUuid, String groupUuid, List<UploadRequestStatus> status) {
		Validate.notEmpty(groupUuid, "Upload request group Uuid must be set");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.find(authUser, actor, groupUuid);
		List<UploadRequest> requests = uploadRequestService.findAll(authUser, actor, uploadRequestGroup, status);
		List<UploadRequestDto> requestDtos = new ArrayList<UploadRequestDto>();
		requests.forEach(ur -> {
			UploadRequestDto requestDto = UploadRequestDto.toDto(ur, false);
			requestDto.setNbrUploadedFiles(uploadRequestService.countNbrUploadedFiles(ur));
			requestDto.setUsedSpace(uploadRequestService.computeEntriesSize(ur));
			requestDtos.add(requestDto);
		});
		return ImmutableList.copyOf(requestDtos);
	}

	@Override
	public Response downloadEntries(String actorUuid, String groupUuid, String requestUuid) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(groupUuid, "Upload request group uuid must be set");
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.find(authUser, actor, groupUuid);
		FileAndMetaData data = uploadRequestGroupService.downloadEntries(authUser, actor, uploadRequestGroup, requestUuid);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return builder.build();
	}
}
