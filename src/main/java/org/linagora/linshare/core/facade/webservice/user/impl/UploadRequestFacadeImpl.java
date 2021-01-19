/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestFacadeImpl extends GenericFacadeImpl implements UploadRequestFacade {

	private final UploadRequestService uploadRequestService;
	
	private final AuditLogEntryService auditLogEntryService;
	
	public UploadRequestFacadeImpl(final AccountService accountService,
			final UploadRequestService uploadRequestService,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
		this.auditLogEntryService = auditLogEntryService;
	}

	private UploadRequestDto toDto(UploadRequest ur, boolean full) {
		UploadRequestDto requestDto = UploadRequestDto.toDto(ur, true);
		requestDto.setNbrUploadedFiles(uploadRequestService.countNbrUploadedFiles(ur));
		requestDto.setUsedSpace(uploadRequestService.computeEntriesSize(ur));
		return requestDto;
	}

	@Override
	public UploadRequestDto find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest ur = uploadRequestService.find(authUser, actor, uuid);
		UploadRequestDto requestDto = toDto(ur, true);
		return requestDto;
	}

	@Override
	public UploadRequestDto update(String actorUuid, UploadRequestDto req, String uuid) throws BusinessException {
		Validate.notNull(req, "UploadRequestDto must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			req.setUuid(uuid);
		}
		Validate.notEmpty(req.getUuid(), "Upload request uuid is required");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest e = req.toObject();
		e = uploadRequestService.update(authUser, actor, req.getUuid(), e, false);
		UploadRequestDto requestDto = toDto(e, true);
		return requestDto;
	}

	@Override
	public UploadRequestDto updateStatus(String actorUuid, String requestUuid, UploadRequestStatus status, boolean copy) throws BusinessException {
		Validate.notEmpty(requestUuid, "Upload request uuid must be set.");
		Validate.notNull(status, "Status must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest uploadRequest = uploadRequestService.updateStatus(authUser, actor, requestUuid, status, copy);
		UploadRequestDto requestDto = toDto(uploadRequest, false);
		return requestDto;
	}

	@Override
	public UploadRequestDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequest e = uploadRequestService.deleteRequest(authUser, actor, uuid);
		UploadRequestDto requestDto = toDto(e, false);
		return requestDto;
	}

	@Override
	public UploadRequestDto delete(String actorUuid, UploadRequestDto uploadRequestDto) throws BusinessException {
		Validate.notNull(uploadRequestDto, "Upload Request must be set.");
		Validate.notEmpty(uploadRequestDto.getUuid(), "Upload Request uuid must be set.");
		return delete(actorUuid, uploadRequestDto.getUuid());
	}

	@Override
	public List<UploadRequestEntryDto> findAllEntries(String actorUuid, String uuid) {
		Validate.notEmpty(uuid, "Upload request uuid must be set.");
		Account authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		List<UploadRequestEntry> uploadRequestEntries = uploadRequestService.findAllEntries(authUser, actor, uuid);
		return ImmutableList.copyOf(Lists.transform(uploadRequestEntries, UploadRequestEntryDto.toDto()));
	}

	@Override
	public Set<AuditLogEntryUser> findAllAudits(String actorUuid, String uploadRequestUuid, List<LogAction> actions,
			List<AuditLogEntryType> types) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(uploadRequestUuid, "Upload request uuid must be set");
		Account actor = getActor(authUser, actorUuid);
		return auditLogEntryService.findAllUploadRequestAudits(authUser, actor, uploadRequestUuid, actions, types);
	}

}
