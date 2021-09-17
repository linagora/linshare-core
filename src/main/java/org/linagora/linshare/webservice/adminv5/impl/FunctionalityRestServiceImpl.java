/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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

package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.adminv5.FunctionalityRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("{domainUuid}/functionalities")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class FunctionalityRestServiceImpl implements
		FunctionalityRestService {

	protected final FunctionalityFacade functionalityFacade;

	public FunctionalityRestServiceImpl(
			final FunctionalityFacade functionalityFacade) {
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all functionalities of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema( implementation = FunctionalityDto.class))
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public List<FunctionalityDto> findAll(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality parent identifier, in order to list nested functionalities", required = false)
				@QueryParam(value = "parentIdentifier") String parentIdentifier,
			@Parameter(description = "Return all functionalities as a tree", required = false)
				@QueryParam("tree") @DefaultValue("false") boolean tree,
			@Parameter(description = "Return all functionalities (root and nested ones) in one list", required = false)
				@QueryParam("subs") @DefaultValue("false") boolean withSubFunctionalities)
			throws BusinessException {
		return functionalityFacade.findAll(domainUuid, parentIdentifier, tree, withSubFunctionalities);
	}

	@Path("/{identifier}")
	@GET
	@Operation(summary = "It will return one functionality of the current domain.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema( implementation = FunctionalityDto.class)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
		)
	})
	@Override
	public FunctionalityDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = true)
				@PathParam(value = "identifier") String identifier,
			@Parameter(description = "Return all functionalities as a tree", required = false)
				@QueryParam("tree") @DefaultValue("false") boolean tree)
			throws BusinessException {
		return functionalityFacade.find(domainUuid, identifier, tree);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows adminstrator to update a functionality.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
					schema = @Schema( implementation = FunctionalityDto.class)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
			)
	})
	@Override
	public FunctionalityDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = false)
				@PathParam(value = "identifier") String identifier,
			@Parameter(description = "functionality to update", required = true)
				FunctionalityDto func)
			throws BusinessException {
		return functionalityFacade.update(func);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows adminstrator to reset a functionality.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
					schema = @Schema( implementation = FunctionalityDto.class)
			)
		),
		@ApiResponse(
			responseCode = "40X",
			content = @Content(
				schema = @Schema(
					implementation = ErrorDto.class
				)
			)
			)
	})
	@Override
	public FunctionalityDto delete(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam(value = "domainUuid") String domainUuid,
			@Parameter(description = "Functionality identifier", required = false)
				@PathParam(value = "identifier") String identifier, 
			@Parameter(description = "functionality to reset (restore parent's values", required = false)
				FunctionalityDto func) throws BusinessException {
		return functionalityFacade.delete(func);
	}
}
