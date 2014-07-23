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
import org.linagora.linshare.webservice.delegation.ThreadRestService;
import org.linagora.linshare.webservice.dto.ThreadDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/threads")
@Api(value = "/rest/delegation/{ownerUuid}/threads", basePath = "/rest/threads", description = "threads service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadRestServiceImpl extends WebserviceBase implements
		ThreadRestService {

	@Path("/")
	@POST
	@ApiOperation(value = "Create a thread.", response = ThreadDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Thread to create.", required = true) ThreadDto thread)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a thread.", response = ThreadDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all threads.", response = ThreadDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<ThreadDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a thread.", response = ThreadDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Thread to update.", required = true) ThreadDto thread)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Thread to delete.", required = true) ThreadDto thread)
					throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a thread.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub

	}

}
