/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
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

package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestGroupFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.UploadRequestGroupRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/upload_request_groups")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestGroupRestServiceImpl implements UploadRequestGroupRestService {

	private final UploadRequestGroupFacade uploadRequestGroupFacade;

	public UploadRequestGroupRestServiceImpl(final UploadRequestGroupFacade uploadRequestGroupFacade) {
		super();
		this.uploadRequestGroupFacade = uploadRequestGroupFacade;
	}

	@GET
	@Path("/")
	@Operation(summary = "Find a list of upload request group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<UploadRequestGroupDto> findAll(
			@Parameter(description = "Values t filter upload resquets by status", required = false)
				@QueryParam("status") List<UploadRequestStatus> status) throws BusinessException {
		return uploadRequestGroupFacade.findAll(null, status);
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Find an upload request group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestGroupDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestGroupDto find(
			@Parameter(description = "Upload request group uuid.", required = true)
				@PathParam(value = "uuid") String uuid)
			throws BusinessException {
		return uploadRequestGroupFacade.find(null, uuid);
	}

	@POST
	@Path("/")
	@Operation(summary = "Create an upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestGroupDto create(
			@Parameter(description = "Upload request.", required = true)
				UploadRequestCreationDto uploadRequestCreationDto,
			@Parameter(description = "Group mode.", required = true)
				@QueryParam(value = "groupMode") Boolean groupMode) {
		return uploadRequestGroupFacade.create(null, uploadRequestCreationDto, groupMode);
	}

	@PUT
	@Path("/{uuid}/status/{status}")
	@Operation(summary = "Update status of upload request group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestGroupDto updateStatus(
			@Parameter(description = "Upload request group uuid.", required = true)
				@PathParam(value = "uuid") String uuid,
			@Parameter(description = "New status for the upload request group", required = true)
				@PathParam("status") UploadRequestStatus status,
			@Parameter(description = "If the owner wants to copy all documents and the upload request group is in archived status", required = false) 
				@QueryParam("copy") @DefaultValue("false") boolean copy) throws BusinessException {
		return uploadRequestGroupFacade.updateStatus(null, uuid, status, copy);
	}

	@PUT
	@Path("/{uuid : .*}")
	@Operation(summary = "update an upload request group", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestGroupDto update(
			@Parameter(description = "Upload request group uuid, if null uploadRequestGroupDto.uuid is used.", required = false) 
				@PathParam("uuid") String uuid,
			@Parameter(description = "Upload request group", required = true)
				UploadRequestGroupDto uploadRequestGroupDto) {
		return uploadRequestGroupFacade.update(null, uploadRequestGroupDto, uuid);
	}

	@POST
	@Path("/{uuid}/recipients")
	@Operation(summary = "Add new recipient to upload request group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestGroupDto addRecipient(
			@Parameter(description = "Upload request group uuid", required = true)
				@PathParam(value = "uuid") String uuid,
			@Parameter(description = "List of new recipients", required = true)
				List<ContactDto> recipientEmail) {
		return uploadRequestGroupFacade.addRecipients(null, uuid, recipientEmail);
	}

	@GET
	@Path("/{uuid}/audit")
	@Operation(summary = "Get all traces for a n upload request group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@Parameter(description = "The request uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description =  "This parameter allow you to find all logs relative to upload request, upload request group and recipients (can be activated if types is empty and entriesLogsOnly has false value)")
				@QueryParam("detail") boolean detail,
			@Parameter(description = "This parameter allow you to find all logs relative to upload request entry only (can be activated if types is empty)", required = false)
				@QueryParam("entriesLogsOnly") boolean entriesLogsOnly,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types (If the type is not empty, the entriesLogsType and detail parameter are ignored)", required = false)
				@QueryParam("types") List<AuditLogEntryType> types) {
		return uploadRequestGroupFacade.findAll(null, uuid, detail, entriesLogsOnly, actions, types);
	}

	@GET
	@Path("/{uuid}/upload_requests")
	@Operation(summary = "Find a list of upload request.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadRequestDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<UploadRequestDto> findAllUploadRequests(
			@Parameter(description = "Upload request group uuid.", required = true)
				@PathParam(value = "uuid") String uuid,
			@Parameter(description = "Values to filter upload requests by status", required = false)
				@QueryParam("status") List<UploadRequestStatus> status) {
		return uploadRequestGroupFacade.findAllUploadRequests(null, uuid, status);
	}
}