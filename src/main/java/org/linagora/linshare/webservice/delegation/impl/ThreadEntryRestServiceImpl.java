package org.linagora.linshare.webservice.delegation.impl;

import java.io.InputStream;
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
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.ThreadEntryRestService;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/threads/{threadUuid}/entries")
@Api(value = "/rest/delegation/{ownerUuid}/threads/{threadUuid}/entries", basePath = "/rest/threads/{threadUuid}/entries",
	description = "thread entries service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadEntryRestServiceImpl extends WebserviceBase implements
		ThreadEntryRestService {

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create a thread entry which will contain the uploaded file.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "File stream.", required = true) InputStream theFile,
			@ApiParam(value = "An optional description of a thread entry.") String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) String givenFileName,
			MultipartBody body)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a thread entry.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all thread entries.", response = ThreadEntryDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<ThreadEntryDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a thread entry.", response = ThreadEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public ThreadEntryDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry to update.", required = true) ThreadEntryDto threadEntry)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry to delete.", required = true) ThreadEntryDto threadEntry)
					throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Owner or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Path("/{ownerUuid}/documents/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response download(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/documents/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response thumbnail(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
