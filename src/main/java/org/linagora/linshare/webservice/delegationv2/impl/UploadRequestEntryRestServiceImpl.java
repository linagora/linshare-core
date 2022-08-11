/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.delegationv2.UploadRequestEntryRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/{actorUuid}/upload_request_entries")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestEntryRestServiceImpl implements UploadRequestEntryRestService {

	protected final Logger logger = LoggerFactory.getLogger(UploadRequestEntryRestServiceImpl.class);

	private final UploadRequestEntryFacade uploadRequestEntryFacade;
	
	public UploadRequestEntryRestServiceImpl(UploadRequestEntryFacade uploadRequestEntryFacade) {
		super();
		this.uploadRequestEntryFacade = uploadRequestEntryFacade;
	}

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response download(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		UploadRequestEntryDto uploadRequestEntryDto = uploadRequestEntryFacade.find(2, actorUuid, uuid);
		ByteSource documentStream = uploadRequestEntryFacade.download(actorUuid, uuid);
		FileAndMetaData data = new FileAndMetaData(documentStream, uploadRequestEntryDto.getSize(),
				uploadRequestEntryDto.getName(), uploadRequestEntryDto.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public UploadRequestEntryDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "Upload request entry uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadRequestEntryFacade.delete(actorUuid, uuid);
	}

	@GET
	@Path("/{uploadRequestEntryUuid}/audit")
	@Operation(summary = "Get all traces for a given Upload Request entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAllAudits(
			@Parameter(description = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The upload request entry uuid.", required = true)
				@PathParam("uploadRequestEntryUuid") String uploadRequestEntryUuid,
			@Parameter(description = "Optional. If you want to filter the result with action", required = false)
				@QueryParam("actions") List<LogAction> actions)
			throws BusinessException {
		return uploadRequestEntryFacade.findAllAudits(actorUuid, uploadRequestEntryUuid, actions);
	}
}
