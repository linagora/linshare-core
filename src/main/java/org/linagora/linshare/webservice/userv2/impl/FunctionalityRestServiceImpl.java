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
package org.linagora.linshare.webservice.userv2.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.FunctionalityRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


//Class created to generate the swagger documentation of v1 RestServices
@Path("/functionalities")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class FunctionalityRestServiceImpl extends WebserviceBase implements FunctionalityRestService {

	protected FunctionalityFacade functionalityFacade;

	public FunctionalityRestServiceImpl(FunctionalityFacade functionalityFacade) {
		super();
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domain's functionalities.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<FunctionalityDto> findAll() throws BusinessException {
		return functionalityFacade.findAll(Version.V2);
	}

	@Path("/{funcId}")
	@GET
	@Operation(summary = "Find a functionality.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public FunctionalityDto find(@PathParam(value = "funcId") String funcId) throws BusinessException {
		return functionalityFacade.find(funcId, Version.V2);
	}

	@Path("/{funcId}")
	@HEAD
	@Operation(summary = "Find a functionality.")
	@Override
	public void head(@PathParam(value = "funcId") String identifier) throws BusinessException {
		functionalityFacade.find(identifier, Version.V2);
	}
}
