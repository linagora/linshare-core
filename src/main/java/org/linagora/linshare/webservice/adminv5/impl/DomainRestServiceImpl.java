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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.DomainRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class DomainRestServiceImpl extends WebserviceBase implements
		DomainRestService {

	private final DomainFacade domainFacade;

	private static PagingResponseBuilder<DomainDto> pageResponseBuilder = new PagingResponseBuilder<>();

	public DomainRestServiceImpl(final DomainFacade domainFacade) {
		this.domainFacade = domainFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all domains where you are allowed to perform administration tasks.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<DomainDto> findAll(
			@Parameter(description = "If enable, your own domain will be returned as a list of one entry. "
					+ "Parent domains and children domains will be also provided as a tree.")
				@QueryParam("tree") @DefaultValue("false") boolean tree) {
		return domainFacade.findAll(tree);
	}

	@Path("/r2/")
	@GET
	@Override
	public Response findAll(
			@Parameter(
					description = "Filter the type of domains to retrieve.",
					required = false,
					schema = @Schema(implementation = DomainType.class)
				)
				@QueryParam("type") String domainType,
			@Parameter(
					description = "It is an optional parameter to filter the domains by the given name.",
					required = false)
				@QueryParam("name") String name,
			@Parameter(
					description = "It is an optional parameter to filter the domains by the given description.",
					required = false)
				@QueryParam("description") String description,
			@Parameter(description = "parent domain's uuid")
				@QueryParam("parent") String parentUuid,
			@Parameter(
					description = "The admin can choose the order of sorting the domain's list to retrieve, if not set the ascending order will be applied by default.",
					required = false,
					schema = @Schema(implementation = SortOrder.class, defaultValue = "ASC")
				)
				@QueryParam("sortOrder") @DefaultValue("ASC") String sortOrder,
			@Parameter(
					description = "The admin can choose the field to sort with the domain's list to retrieve, if not set the name order will be choosen by default.",
					required = false,
					schema = @Schema(implementation = DomainField.class, defaultValue = "name")
				)
				@QueryParam("sortField") @DefaultValue("name") String sortField,
			@Parameter(
					description = "The admin can choose the page number to get.",
					required = false
				)
				@QueryParam("page") Integer pageNumber,
			@Parameter(
					description = "The admin can choose the number of elements to get.",
					required = false
				)
				@QueryParam("size") @DefaultValue("50") Integer pageSize) {
		PageContainer<DomainDto> container = domainFacade.findAll(
				Optional.ofNullable(domainType),
				Optional.ofNullable(name),
				Optional.ofNullable(description),
				Optional.ofNullable(parentUuid),
				SortOrder.valueOf(sortOrder),
				DomainField.valueOf(sortField),
				pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of a domain. You must be administrator of this domain to be able to get its details.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "If enable, the domain will be returned with its parent domains and  its children domains, as a tree. "
					+ "It will override 'detail' query param.")
				@QueryParam("tree") @DefaultValue("false") boolean tree,
			@Parameter(description = "If true, more sensitive information about the domain will be provided.")
				@QueryParam("detail") @DefaultValue("false") boolean detail) {
		return domainFacade.find(uuid, tree, detail);
	}

	@Path("/")
	@POST
	@Operation(summary = "It allows root adminstrator to create a new domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto create(
			@Parameter(description = "Create a dedicated domain policy, allowing the domain to only communicate with itself.")
				@QueryParam("dedicatedDomainPolicy") @DefaultValue("false") boolean dedicatedDomainPolicy,
			@Parameter(description = "Add this domain to an existing domain policy using ALLOW.")
				@QueryParam("addItToDomainPolicy") String addItToDomainPolicy,
			DomainDto dto) {
		if (dedicatedDomainPolicy && addItToDomainPolicy != null) {
			throw new IllegalArgumentException("You can't use dedicatedDomainPolicy and addItToDomainPolicy at the same time.");
		}
		return domainFacade.create(dto, dedicatedDomainPolicy, addItToDomainPolicy);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "It allows adminstrator to update a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto update(
			@Parameter(description = "Domain uuid to update, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Domain dto with properties to update", required = false) 
				DomainDto dto) {
		return domainFacade.update(uuid, dto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "It allows adminstrator to delete a domain.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto delete(
			@Parameter(description = "Domain uuid to delete, if null, object.uuid is used", required = false)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Domain dto to delete, only uuid is required if not set in the path", required = false) 
				DomainDto dto) {
		return domainFacade.delete(uuid, dto);
	}

}
