package org.linagora.linshare.webservice.delegation.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.GuestRestService;
import org.linagora.linshare.webservice.dto.GuestDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;



@Path("/")
@Api(value = "/rest/delegation/{ownerUuid}/guests", basePath = "/rest/delegation/", description = "Guests service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl extends WebserviceBase implements GuestRestService {

	@Path("/{ownerUuid}/guests")
	@POST
	@ApiOperation(value = "Create a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public GuestDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Guest to create.", required = true) GuestDto guest)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/guests/{uuid}")
	@GET
	@ApiOperation(value = "Get a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public GuestDto get(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The guest uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/guests")
	@GET
	@ApiOperation(value = "Get all guests.", response = GuestDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<GuestDto> getAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/guests")
	@PUT
	@ApiOperation(value = "Update a guest.", response = GuestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or guest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public GuestDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Guest to update.", required = true) GuestDto guest)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/guests")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or guest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Guest to delete.", required = true) GuestDto guest)
					throws BusinessException {
		// TODO Auto-generated method stub

	}

	@Path("/{ownerUuid}/guests/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or guest not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid, 
			@ApiParam(value = "The guest uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub

	}

}
