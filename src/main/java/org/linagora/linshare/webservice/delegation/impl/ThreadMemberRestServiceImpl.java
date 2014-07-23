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
import org.linagora.linshare.webservice.delegation.ThreadMemberRestService;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/threads/{threadUuid}/members")
@Api(value = "/rest/delegation/{ownerUuid}/threads/{threadUuid}/members", basePath = "/rest/threads/{threadUuid}/members",
	description = "thread members service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadMemberRestServiceImpl extends WebserviceBase implements
		ThreadMemberRestService {

	@Path("/")
	@POST
	@ApiOperation(value = "Create a thread member.", response = ThreadMemberDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadMemberDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The user domain identifier.", required = true) @PathParam("domainId") String domainId,
			@ApiParam(value = "The user mail.", required = true) @PathParam("threadUuid") String mail,
			@ApiParam(value = "To create a readonly member.", required = true) @PathParam("readonly") boolean readonly,
			@ApiParam(value = "To give admin rights to the new member.", required = true) @PathParam("admin") boolean admin)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a thread member.", response = ThreadMemberDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadMemberDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread member uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all thread members.", response = ThreadMemberDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<ThreadMemberDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid)
				throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a thread member.", response = ThreadMemberDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread member not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadMemberDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread member to update.", required = true) ThreadMemberDto threadMember)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread member.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread member not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread member to delete.", required = true) ThreadMemberDto threadMember)
					throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a thread member.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread member not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread member uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
