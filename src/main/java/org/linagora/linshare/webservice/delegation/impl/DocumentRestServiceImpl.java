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
import org.linagora.linshare.core.facade.webservice.delegation.DocumentFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.DocumentRestService;
import org.linagora.linshare.webservice.delegation.dto.DocumentDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/")
@Api(value = "/rest/delegation/{ownerUuid}/documents", basePath = "/rest/delegation/", description = "Documents service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class DocumentRestServiceImpl extends WebserviceBase implements
		DocumentRestService {

	private final DocumentFacade documentFacade;

	public DocumentRestServiceImpl(DocumentFacade documentFacade) {
		super();
		this.documentFacade = documentFacade;
	}

	@Path("/{ownerUuid}/documents")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Create a document which will contain the uploaded file.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public DocumentDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "File stream.", required = true) InputStream theFile,
			@ApiParam(value = "An optional description of a document.") String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) String givenFileName,
			MultipartBody body)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/documents/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get a document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public DocumentDto get(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/documents")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get all documents.", response = DocumentDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<DocumentDto> getAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid)
			throws BusinessException {
		return documentFacade.getAll(ownerUuid);
	}

	@Path("/{ownerUuid}/documents/{uuid}")
	@PUT()
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update a document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public DocumentDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "An optional description of the document.") String description,
			@ApiParam(value = "Document new file name.") String fileName)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{ownerUuid}/documents/{uuid}/upload")
	@PUT()
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update the file inside the document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public DocumentDto updateFile(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "File stream.", required = true) InputStream theFile,
			@ApiParam(value = "An optional description of a document.") String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) String givenFileName,
			MultipartBody body)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/{ownerUuid}/documents/{uuid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Delete a document.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Owner not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
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
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
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
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
