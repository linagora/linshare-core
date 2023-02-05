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
package org.linagora.linshare.webservice.userv5.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.userv2.impl.FunctionalityRestServiceImpl;
import org.linagora.linshare.webservice.userv5.FunctionalityRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/functionalities")
public class FuntionalityRestServiceImpl extends FunctionalityRestServiceImpl implements FunctionalityRestService {

	public FuntionalityRestServiceImpl(FunctionalityFacade functionalityFacade) {
		super(functionalityFacade);
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
		return functionalityFacade.findAll(Version.V5);
	}

	@Override
	public FunctionalityDto find(String funcId) throws BusinessException {
		return functionalityFacade.find(funcId, Version.V5);
	}
}
