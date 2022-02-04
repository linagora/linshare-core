/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.UploadRequestRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/upload_requests")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestRestServiceImpl implements UploadRequestRestService {

	protected final Logger logger = LoggerFactory.getLogger(UploadRequestRestServiceImpl.class);

	protected final UploadRequestFacade uploadRequestFacade;

	public UploadRequestRestServiceImpl(UploadRequestFacade uploadRequestFacade) {
		super();
		this.uploadRequestFacade = uploadRequestFacade;
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Find an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestDto find(
			@Parameter(description = "Upload request uuid.", required = true)
				@PathParam(value = "uuid") String uuid) {
		UploadRequestDto dto = uploadRequestFacade.find(null, uuid);
		return dto;
	}

	@PUT
	@Path("/{uuid}/status/{status}")
	@Operation(summary = "Update status of an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestDto updateStatus(
			@Parameter(description = "Upload request uuid.", required = true)
				@PathParam(value = "uuid") String uuid,
			@Parameter(description = "New status for the upload request", required = true)
				@PathParam("status") UploadRequestStatus status,
			@Parameter(description = "If the owner wants to copy all documents and the upload request is in archived status.", required = false)
				@QueryParam("copy") @DefaultValue("false") boolean copy) {
		return uploadRequestFacade.updateStatus(null, uuid, status, copy);
	}

	@PUT
	@Path("/{uuid : .*}")
	@Operation(summary = "Update an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestDto update(
			@Parameter(description = "Upload request uuid, if null uploadRequestDto.uuid is used.", required = false)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Upload request.", required = true) UploadRequestDto uploadRequestDto) {
		UploadRequestDto dto = uploadRequestFacade.update(null, uploadRequestDto, uuid);
		return dto;
	}

	@Path("/{uuid}/entries")
	@GET
	@Operation(summary = "Find documents of an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<UploadRequestEntryDto> findAllEntries(
			@Parameter(description = "Find all documents.", required = false)
				@PathParam("uuid") String uploadRequestuuid)
			throws BusinessException {
		return uploadRequestFacade.findAllEntries(null, uploadRequestuuid);
	}

	@GET
	@Path("/{uploadRequestUuid}/audit")
	@Operation(summary = "Get all traces for a given Upload Request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAudits(
			@Parameter(description = "The upload request uuid.", required = true)
				@PathParam("uploadRequestUuid") String uploadRequestUuid,
			@Parameter(description = "List of actions", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types", required = false)
				@QueryParam("types") List<AuditLogEntryType> types) {
		return uploadRequestFacade.findAllAudits(null, uploadRequestUuid, actions, types);
	}

}
