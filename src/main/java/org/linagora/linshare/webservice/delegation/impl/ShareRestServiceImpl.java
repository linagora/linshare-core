package org.linagora.linshare.webservice.delegation.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.ShareRestService;
import org.linagora.linshare.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.webservice.delegation.dto.ShareDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/shares")
@Api(value = "/rest/delegation/{ownerUuid}/shares", basePath = "/rest/shares", description = "shares service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService {

	@Path("/")
	@POST
	@ApiOperation(value = "Create a share.", response = ShareDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ShareDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			ShareCreationDto createDto)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
