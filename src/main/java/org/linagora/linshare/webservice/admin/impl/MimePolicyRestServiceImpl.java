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
package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimePolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MimePolicyRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mime_policies")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MimePolicyRestServiceImpl extends WebserviceBase implements
		MimePolicyRestService {

	private final MimePolicyFacade mimePolicyFacade;

	public MimePolicyRestServiceImpl(final MimePolicyFacade mimePolicyFacade) {
		this.mimePolicyFacade = mimePolicyFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all the mime policies by domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MimePolicyDto> findAll(
			@Parameter(description = "Identifier of the domain which you are looking into.", required = true) @QueryParam("domainId") String domainId,
			@Parameter(description = "Return current and parent domain's mime policies,"
					+ " or only current domain's if onlyCurrentDomain is true.") @QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mimePolicyFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto find(
			@Parameter(description = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Return mime policy with mime types.") @QueryParam("full") @DefaultValue("false") boolean full)
			throws BusinessException {
		MimePolicyDto find = mimePolicyFacade.find(uuid, full);
		return find;
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mime policy.")
	@Override
	public void head(
			@Parameter(description = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		mimePolicyFacade.find(uuid, false);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto create(
			@Parameter(description = "Policy to create.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto update(
			@Parameter(description = "Policy to update.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.update(policy);
	}

	@Path("/{uuid}/enable_all")
	@PUT
	@Operation(summary = "Set all mime types to enable for the current mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto enableAllMimeTypes(
			@Parameter(description = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.enableAllMimeTypes(uuid);
	}

	@Path("/{uuid}/disable_all")
	@PUT
	@Operation(summary = "Set all mime types to disable for the current mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto disableAllMimeTypes(
			@Parameter(description = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.disableAllMimeTypes(uuid);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto delete(
			@Parameter(description = "Identifier of the mime policy to delete.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a mime policy.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MimePolicyDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MimePolicyDto delete(
			@Parameter(description = "Policy to delete.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.delete(policy.getUuid());
	}
}
