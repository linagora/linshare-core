/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.webservice.userv2.impl;

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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.DocumentRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/documents")
@Api(value = "/rest/user/documents", basePath = "/rest/delegation/", description = "Documents service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class DocumentRestServiceImpl extends WebserviceBase implements
		DocumentRestService {

	private final DocumentFacade documentFacade;

	public DocumentRestServiceImpl(DocumentFacade documentFacade) {
		super();
		this.documentFacade = documentFacade;
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Create a document which will contain the uploaded file.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto create(
			@ApiParam(value = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream theFile,
			@ApiParam(value = "An optional description of a document.") @Multipart(value = "description", required = false) String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = false) @Multipart(value = "filename", required = false) String givenFileName,
			MultipartBody body) throws BusinessException {
		String fileName;
		String comment = (description == null) ? "" : description;
		if (theFile == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		if (givenFileName == null || givenFileName.isEmpty()) {
			// parameter givenFileName is optional
			// so need to search this information in the header of the
			// attachement (with id file)
			fileName = body.getAttachment("file").getContentDisposition()
					.getParameter("filename");
		} else {
			fileName = givenFileName;
		}
		return documentFacade.uploadfile(theFile, comment, fileName);
	}

	@Path("/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get a document.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto find(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.getDocument(uuid);
	}

	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get all documents.", response = DocumentDto.class, responseContainer = "Set")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<DocumentDto> findAll() throws BusinessException {
		return documentFacade.getDocuments();
	}

	@Path("/{uuid}")
	@PUT()
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update a document.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto update(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "An optional description of the document.") String description,
			@ApiParam(value = "Document new file name.") String fileName)
			throws BusinessException {
		return documentFacade.update(description, fileName, uuid);
	}

	@DELETE
	@Path("/{uuid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Delete a document.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void delete(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		documentFacade.deleteFile(uuid);
	}

	@Path("/{uuid}/upload")
	@PUT()
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update the file inside the document.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto updateFile(String uuid, InputStream theFile,
			String description, String givenFileName, MultipartBody body)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response download(@PathParam("uuid") String uuid)
			throws BusinessException {
		DocumentDto documentDto = documentFacade.getDocument(uuid);
		InputStream documentStream = documentFacade.getDocumentStream(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(documentStream,
						documentDto.getName(), documentDto.getType(),
						documentDto.getSize());
		return response.build();
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Owner not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response thumbnail(String uuid) throws BusinessException {
		DocumentDto documentDto = documentFacade.getDocument(uuid);
		InputStream documentStream = documentFacade.getThumbnailStream(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder
				.getDocumentResponseBuilder(documentStream,
						documentDto.getName() + "_thumb.png", "image/png");
		return response.build();
	}

}
