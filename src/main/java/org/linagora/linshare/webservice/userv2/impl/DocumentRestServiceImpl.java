/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.annotations.NoCache;
import org.linagora.linshare.webservice.userv1.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.userv2.DocumentRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/documents")
@Api(value = "/rest/user/documents", basePath = "/rest/user/", description = "Documents service.", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class DocumentRestServiceImpl extends WebserviceBase implements DocumentRestService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentRestServiceImpl.class);

	private final DocumentFacade documentFacade;

	private final DocumentAsyncFacade documentAsyncFacade;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private final AccountQuotaFacade accountQuotaFacade;

	private boolean sizeValidation;

	public DocumentRestServiceImpl(
			DocumentFacade documentFacade,
			DocumentAsyncFacade documentAsyncFacade,
			ThreadPoolTaskExecutor taskExecutor,
			AsyncTaskFacade asyncTaskFacade,
			AccountQuotaFacade accountQuotaFacade,
			boolean sizeValidation) {
		super();
		this.documentFacade = documentFacade;
		this.documentAsyncFacade = documentAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.accountQuotaFacade = accountQuotaFacade;
		this.sizeValidation = sizeValidation;
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Create a document which will contain the uploaded file.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto create(
			@ApiParam(value = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@ApiParam(value = "An optional description of a document.") @Multipart(value = "description", required = false) String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = false) @Multipart(value = "filename", required = false) String givenFileName,
			@ApiParam(value = "Signature file stream.", required = false) @Multipart(value = "signaturefile", required = false) InputStream theSignatureFile,
			@ApiParam(value = "The given file name of the signature uploaded file.", required = false) @Multipart(value = "signatureFileName", required = false) String signatureFileName,
			@ApiParam(value = "X509 Certificate entity.", required = false) @Multipart(value = "x509cert", required = false) InputStream x509certificate,
			@ApiParam(value = "The given metadata of the uploaded file.", required = false) @Multipart(value = "metadata", required = false) String metaData,
			@ApiParam(value = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false") @QueryParam("async") boolean async,
			@ApiParam(value = "file size (size validation purpose).", required = true) @Multipart(value = "filesize", required = true)  Long fileSize,
			MultipartBody body) throws BusinessException {
		checkMaintenanceMode();
		Long transfertDuration = getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check multipart parameter named 'file')");
		}
		String fileName = getFileName(givenFileName, body);
		File tempFile = getTempFile(file, "rest-userv2-document-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			checkSizeValidation(fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			AccountDto actorDto = documentFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				DocumentTaskContext documentTaskContext = new DocumentTaskContext(actorDto, actorDto.getUuid(),
						tempFile, fileName, metaData, description);
				asyncTask = asyncTaskFacade.create(currSize, transfertDuration, fileName, null,
						AsyncTaskType.DOCUMENT_UPLOAD);
				DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext,
						asyncTask);
				taskExecutor.execute(task);
				return new DocumentDto(asyncTask, documentTaskContext);
			} catch (Exception e) {
				logAsyncFailure(asyncTask, e);
				deleteTempFile(tempFile);
				throw e;
			}
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				if (theSignatureFile != null) {
					return documentFacade.createWithSignature(tempFile, fileName, description, theSignatureFile,
							signatureFileName, x509certificate);
				}
				return documentFacade.create(tempFile, fileName, description, metaData);
			} finally {
				deleteTempFile(tempFile);
			}
		}
	}

	@Path("/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get a document.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override

	public DocumentDto find(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "If you want document shares too.", required = false) @QueryParam("withShares") @DefaultValue("false") boolean withShares)
			throws BusinessException {
		return documentFacade.find(uuid, withShares);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Get a document.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		documentFacade.find(uuid, false);
	}

	@NoCache
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get all documents.", response = DocumentDto.class, responseContainer = "Set")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<DocumentDto> findAll() throws BusinessException {
		return documentFacade.findAll();
	}

	@DELETE
	@Path("/{uuid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Delete a document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto delete(@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.delete(uuid);
	}

	@DELETE
	@Path("/")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Delete a document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto delete(@ApiParam(value = "The document.", required = true) DocumentDto documentDto)
			throws BusinessException {
		Validate.notNull(documentDto, "Document dto must be set.");
		return documentFacade.delete(documentDto.getUuid());
	}

	@Path("/{uuid}")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update a document.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public DocumentDto update(
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "The document dto.", required = true) DocumentDto documentDto) throws BusinessException {
		return documentFacade.update(uuid, documentDto);
	}

	@Path("/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response download(@PathParam("uuid") String uuid)
			throws BusinessException {
		DocumentDto documentDto = documentFacade.find(uuid, false);
		InputStream documentStream = documentFacade.getDocumentStream(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream,
				documentDto.getName(), documentDto.getType(), documentDto.getSize());
		return response.build();
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.", response = Response.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Response thumbnail(@PathParam("uuid") String documentUuid,
			@ApiParam(value = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		DocumentDto documentDto = documentFacade.find(documentUuid, false);
		InputStream documentStream = documentFacade.getThumbnailStream(documentUuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(documentStream,
				documentDto.getName() + "_thumb.png", base64);
		return response.build();
	}

	@Path("/{uuid}/async")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public AsyncTaskDto findAsync(@PathParam("uuid") String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing uuid");
		return asyncTaskFacade.find(uuid);
	}

	@Path("/{uuid}/audit")
	@GET
	@ApiOperation(value = "Get all traces for a document.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Document not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@ApiParam(value = "The document uuid.", required = true)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<String> actions,
			@ApiParam(value = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<String> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return documentFacade.findAll(null, uuid, actions, types, beginDate, endDate);
	}

	protected void logAsyncFailure(AsyncTaskDto asyncTask, Exception e) {
		logger.error(e.getMessage());
		logger.debug("Exception : ", e);
		if (asyncTask != null) {
			asyncTaskFacade.fail(asyncTask, e);
		}
	}

	private void checkMaintenanceMode() {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		if (maintenance) {
			 // HTTP error 501
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable, uploads are disabled.");
		}
	}
}
