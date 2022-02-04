/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainWorkSpaceFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractWorkSpaceFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkSpaceFilterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.adminv5.DomainWorkSpaceFilterRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/workspace_filters")
@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
public class DomainWorkSpaceFilterRestServiceImpl implements DomainWorkSpaceFilterRestService {

	private final DomainWorkSpaceFilterFacade domainWorkSpaceFilterFacade;

	public DomainWorkSpaceFilterRestServiceImpl(
			final DomainWorkSpaceFilterFacade domainWorkSpaceFilterFacade) {
		this.domainWorkSpaceFilterFacade = domainWorkSpaceFilterFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all domain workSpace filters.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(array = @ArraySchema(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceFilterDto.class},
					type = "object"
				))
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
	public List<AbstractWorkSpaceFilterDto> findAll(
			@Parameter(description = "It is an optional parameter, if true default domain workSpace filters' models will be returned, else the admins' created ones will be returned.", required = false)
				@QueryParam("model") boolean model) throws BusinessException {
		return domainWorkSpaceFilterFacade.findAll(model);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a chosen domain workSpace filter.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceFilterDto.class},
					type = "object"
				)
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
	public AbstractWorkSpaceFilterDto find(
			@Parameter(description = "The admin can find a domain workSpace filter by the entered uuid.", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return domainWorkSpaceFilterFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows root adminstrator to create a new domain workSpace filter.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceFilterDto.class},
					type = "object"
				)
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
	public AbstractWorkSpaceFilterDto create(
			@RequestBody(description = "The domain workSpace filter to create", required = true,
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceFilterDto.class},
					type = "object"
				)
			)
		)
		AbstractWorkSpaceFilterDto dto) throws BusinessException {
		return domainWorkSpaceFilterFacade.create(dto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows adminstrator to update a domain workSpace filter.", responses = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(
				schema = @Schema(
					oneOf = {LDAPWorkSpaceFilterDto.class},
					type = "object"
				)
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
	public AbstractWorkSpaceFilterDto update(
			@Parameter(description = "Domain workSpace filter's uuid to update, if null object is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "The domain workSpace filter to update", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceFilterDto.class},
						type = "object"
					)
				)
			)
			AbstractWorkSpaceFilterDto dto) throws BusinessException {
		return domainWorkSpaceFilterFacade.update(uuid, dto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows adminstrator to delete a domain workSpace filter.", responses = {
			@ApiResponse(
				responseCode = "200",
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceFilterDto.class},
						type = "object"
					)
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
	public AbstractWorkSpaceFilterDto delete(
			@Parameter(description = "Domain workSpace filter's uuid to delete, if null object is used", required = false)
				@PathParam("uuid") String uuid,
			@RequestBody(description = "The domain workSpace filter to delete", required = true,
				content = @Content(
					schema = @Schema(
						oneOf = {LDAPWorkSpaceFilterDto.class},
						type = "object"
					)
				)
			)
			AbstractWorkSpaceFilterDto dto) throws BusinessException {
		return domainWorkSpaceFilterFacade.delete(uuid, dto);
	}

	@Override
	@Path("/{uuid}/domains")
	@GET
	@Operation(summary = "Find all domains using a chosen workSpace filter.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	public List<DomainDto> findAllDomainsByWorkSpaceFilter(
			@Parameter(description = "Domain workSpace filter's uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return domainWorkSpaceFilterFacade.findAllDomainsByWorkSpaceFilter(uuid);
	}
}
