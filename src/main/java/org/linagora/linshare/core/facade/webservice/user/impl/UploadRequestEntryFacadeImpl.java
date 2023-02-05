/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.io.ByteSource;

public class UploadRequestEntryFacadeImpl extends GenericFacadeImpl implements UploadRequestEntryFacade {

	private final UploadRequestEntryService uploadRequestEntryService;
	private final AuditLogEntryService auditLogEntryService;

	public UploadRequestEntryFacadeImpl(final AccountService accountService,
			final UploadRequestEntryService uploadRequestEntryService,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.auditLogEntryService = auditLogEntryService;
		this.uploadRequestEntryService = uploadRequestEntryService;
	}

	@Override
	public UploadRequestEntryDto find(Integer version, String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request entry uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestEntry uploadRequestEntry =  uploadRequestEntryService.find(authUser, actor, uuid);
		return new UploadRequestEntryDto(uploadRequestEntry, version);
	}

	@Override
	public ByteSource download(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required document uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		return uploadRequestEntryService.download(authUser, actor, uuid);
	}

	@Override
	public UploadRequestEntryDto delete(String actorUuid, String uuid) {
		Validate.notEmpty(uuid, "Upload request entry uuid must be set.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		UploadRequestEntry uploadRequestEntry =  uploadRequestEntryService.delete(authUser, actor, uuid);
		return new UploadRequestEntryDto(uploadRequestEntry);
	}

	@Override
	public Set<AuditLogEntryUser> findAllAudits(String actorUuid, String uploadRequestEntryUuid,
			List<LogAction> actions) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(uploadRequestEntryUuid, "Upload request entry uuid must be set");
		Account actor = getActor(authUser, actorUuid);
		return auditLogEntryService.findAllUploadRequestEntryAudits(authUser, actor, uploadRequestEntryUuid, actions);
	}

	@Override
	public Response thumbnail(String actorUuid, String uploadRequestEntryUuid, boolean base64,
			ThumbnailType thumbnailType) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntry uuid");
		FileAndMetaData data = uploadRequestEntryService.thumbnail(authUser, actor, uploadRequestEntryUuid,
				thumbnailType);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(data, base64, thumbnailType);
		return builder.build();
	}
}
