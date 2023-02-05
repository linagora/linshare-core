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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.userv2.ReceivedShareRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;



@Path("/received_shares")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ReceivedShareRestServiceImpl extends org.linagora.linshare.webservice.userv2.impl.ReceivedShareRestServiceImpl implements ReceivedShareRestService {

	public ReceivedShareRestServiceImpl(ShareFacade shareFacade) {
		super(shareFacade);
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all connected user received shares.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		return shareFacade.getReceivedShares(Version.V5);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a received share entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareDto.class))),
			responseCode = "200"
		)
	})
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public ShareDto getReceivedShare(
			@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid)
					throws BusinessException {
		return shareFacade.getReceivedShare(Version.V5, receivedShareUuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a received share entry.")
	@Override
	public void head(@Parameter(description = "The received share uuid.", required = true) @PathParam("uuid") String receivedShareUuid) throws BusinessException {
		shareFacade.getReceivedShare(Version.V5, receivedShareUuid);
	}
}
