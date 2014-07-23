package org.linagora.linshare.webservice.delegation.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.UserRestService;
import org.linagora.linshare.webservice.dto.GenericUserDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/users")
@Api(value = "/rest/delegation/users", basePath = "/rest/delegation/users", description = "Guests service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UserRestServiceImpl extends WebserviceBase implements
		UserRestService {

	@Path("/{mail}")
	@GET
	@ApiOperation(value = "Find a user.", response = GenericUserDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<GenericUserDto> findUsers(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("mail") String mail,
			@ApiParam(value = "The owner (user) uuid.", required = false) @QueryParam("domainId") String domainId)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
