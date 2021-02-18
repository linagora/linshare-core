/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import javax.ws.rs.POST;
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
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestGroupFacade;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.userv2.UploadRequestGroupRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


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
			@Parameter(description = "Group mode.", required = false)
				@QueryParam(value = "collective") Boolean collective) {
		return uploadRequestGroupFacade.create(null, uploadRequestCreationDto, collective);
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
				UploadRequestGroupDto uploadRequestGroupDto, 
			@Parameter(description = "If set to true, force update all upload requests (pristine or not) inside the given group", required = false) 
				@QueryParam("force") @DefaultValue("false") Boolean force) {
		return uploadRequestGroupFacade.update(null, uploadRequestGroupDto, uuid, force);
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
	@Operation(summary = "Get all traces of an upload request group, with no optional parameters it returns list of audit traces with"
			+ " UPLOAD_REQUEST_GROUP (only for individual URG) type, UPLOAD_REQUEST and UPLOAD_REQUEST_URL type.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAuditsOfGroup(
			@Parameter(description = "The upload request group uuid.", required = true)
				@PathParam("uuid") String uploadRequestGroupUuid,
			@Parameter(description =  "If true return traces add to the default list UPLOAD_REQUEST_ENTRY audit types ")
				@QueryParam("all") boolean all,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types (If the type is not empty)", required = false)
				@QueryParam("types") List<AuditLogEntryType> types) {
		return uploadRequestGroupFacade.findAllAuditsOfGroup(null, uploadRequestGroupUuid, all, actions, types);
	}
	
	@GET
	@Path("/{groupUuid}/upload_requests/{uploadRequestUuid}/audit")
	@Operation(summary = "Get all traces for a given Upload Request inside a given Upload Request Group.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAuditsForUploadRequest(
			@Parameter(description = "The upload request group uuid.", required = true)
				@PathParam("groupUuid") String groupUuid,
			@Parameter(description = "The upload request uuid.", required = true)
				@PathParam("uploadRequestUuid") String uploadRequestUuid,
			@Parameter(description = "Optional. If you want to filter the result by action", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Optional, If you want to filter the result by type of resource", required = false)
				@QueryParam("types") List<AuditLogEntryType> types) {
		return uploadRequestGroupFacade.findAllAuditsForUploadRequest(null, groupUuid ,uploadRequestUuid, actions, types);
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

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Archive download of the upload request entries.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response downloadEntries(
			@Parameter(description = "Upload request group uuid.", required = true)
				@PathParam(value = "uuid") String uuid,
			@Parameter(description = "UploadRequestUuid to download its entries if the uploadRequestGroup is not collective", required = false)
				@QueryParam("requestUuid") String requestUuid) throws BusinessException {
		return uploadRequestGroupFacade.downloadEntries(null, uuid, requestUuid);
	}

}