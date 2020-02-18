/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
import org.apache.commons.lang3.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.annotations.NoCache;
import org.linagora.linshare.webservice.userv1.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.userv2.DocumentRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/documents")
public class DocumentRestServiceImpl extends WebserviceBase implements DocumentRestService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentRestServiceImpl.class);

	private final DocumentFacade documentFacade;

	private final DocumentAsyncFacade documentAsyncFacade;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private final AccountQuotaFacade accountQuotaFacade;

	private boolean sizeValidation;

	public DocumentRestServiceImpl(DocumentFacade documentFacade, DocumentAsyncFacade documentAsyncFacade,
			ThreadPoolTaskExecutor taskExecutor, AsyncTaskFacade asyncTaskFacade, AccountQuotaFacade accountQuotaFacade,
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
	@Operation(summary = "Create a document which will contain the uploaded file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DocumentDto create(
			@Parameter(description = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@Parameter(description = "An optional description of a document.") @Multipart(value = "description", required = false) String description,
			@Parameter(description = "The given file name of the uploaded file.", required = false) @Multipart(value = "filename", required = false) String givenFileName,
			@Parameter(description = "Signature file stream.", required = false) @Multipart(value = "signaturefile", required = false) InputStream theSignatureFile,
			@Parameter(description = "The given file name of the signature uploaded file.", required = false) @Multipart(value = "signatureFileName", required = false) String signatureFileName,
			@Parameter(description = "X509 Certificate entity.", required = false) @Multipart(value = "x509cert", required = false) InputStream x509certificate,
			@Parameter(description = "The given metadata of the uploaded file.", required = false) @Multipart(value = "metadata", required = false) String metaData,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false") @QueryParam("async") boolean async,
			@Parameter(description = "file size (size validation purpose).", required = true) @Multipart(value = "filesize", required = true) Long fileSize,
			MultipartBody body) throws BusinessException {
		checkMaintenanceMode();
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check multipart parameter named 'file')");
		}
		String fileName = getFileName(givenFileName, body);
		File tempFile = WebServiceUtils.getTempFile(file, "rest-userv2-document-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			WebServiceUtils.checkSizeValidation(fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createDocumentDtoAsynchronously(tempFile, fileName, metaData, description, transfertDuration);
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
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
	}

	@Path("/copy")
	@POST
	@Operation(summary = "Create a document from an existing workgroup document or a received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DocumentDto> copy(CopyDto copy,
			@Parameter(description = "Delete the share at the end of the copy.", required = false) @QueryParam("deleteShare") @DefaultValue("false") boolean deleteShare)
			throws BusinessException {
		return documentFacade.copy(null, copy, deleteShare);
	}

	@Path("/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Get a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override

	public DocumentDto find(@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "If you want document shares too.", required = false) @QueryParam("withShares") @DefaultValue("false") boolean withShares)
			throws BusinessException {
		return documentFacade.find(uuid, withShares);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a document.")
	@Override
	public void head(@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		documentFacade.find(uuid, false);
	}

	@NoCache
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Get all documents.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DocumentDto> findAll() throws BusinessException {
		return documentFacade.findAll();
	}

	@DELETE
	@Path("/{uuid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Delete a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DocumentDto delete(@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.delete(uuid);
	}

	@DELETE
	@Path("/")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Delete a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DocumentDto delete(@Parameter(description = "The document.", required = true) DocumentDto documentDto)
			throws BusinessException {
		Validate.notNull(documentDto, "Document dto must be set.");
		return documentFacade.delete(documentDto.getUuid());
	}

	@Path("/{uuid}")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DocumentDto update(@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "The document dto.", required = true) DocumentDto documentDto) throws BusinessException {
		return documentFacade.update(uuid, documentDto);
	}

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response download(@PathParam("uuid") String uuid) throws BusinessException {
		DocumentDto documentDto = documentFacade.find(uuid, false);
		InputStream documentStream = documentFacade.getDocumentStream(uuid);
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream,
				documentDto.getName(), documentDto.getType(), documentDto.getSize());
		return response.build();
	}

	@Path("/{uuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Response.class))),
			responseCode = "200"
		)
	})
	@Override
	public Response thumbnail(@PathParam("uuid") String documentUuid,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false) @PathParam("kind") ThumbnailType thumbnailType,
			@Parameter(description = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64)
			throws BusinessException {
		DocumentDto documentDto = documentFacade.find(documentUuid, false);
		InputStream documentStream = documentFacade.getThumbnailStream(documentUuid, thumbnailType);
		ResponseBuilder response = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(documentStream,
				documentDto.getName() + ThumbnailType.getFileType(thumbnailType), base64, thumbnailType);
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
	@Operation(summary = "Get all traces for a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "Filter by type of actions..", required = false) @QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types.", required = false) @QueryParam("types") List<AuditLogEntryType> types,
			@QueryParam("beginDate") String beginDate, @QueryParam("endDate") String endDate) {
		return documentFacade.findAll(null, uuid, actions, types, beginDate, endDate);
	}

	@Path("/")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a document from an URL.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DocumentDto createFromURL(
			@Parameter(description = "The document URL object.", required = true) DocumentURLDto documentURLDto,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false") @QueryParam("async") boolean async)
			throws BusinessException {
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		Validate.notNull(documentURLDto, "DocumentURLDto must be set.");
		String fileURL = documentURLDto.getURL();
		Validate.notEmpty(fileURL, "Missing url");
		String fileName = WebServiceUtils.getFileNameFromUrl(fileURL, documentURLDto.getFileName());
		File tempFile = WebServiceUtils.createFileFromURL(documentURLDto, "rest-userv2-document-entries",
				sizeValidation);
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createDocumentDtoAsynchronously(tempFile, fileName, "", "", transfertDuration);
		} else {
			try {
				logger.debug("Async mode is not used");
				return documentFacade.create(tempFile, fileName, "", "");
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
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
			throw new BusinessException(BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable, uploads are disabled.");
		}
	}

	protected DocumentDto createDocumentDtoAsynchronously(File tempFile, String fileName, String metaData,
			String description, Long transferDuration) {
		AccountDto authUserDto = documentFacade.getAuthenticatedAccountDto();
		AsyncTaskDto asyncTask = null;
		try {
			DocumentTaskContext documentTaskContext = new DocumentTaskContext(authUserDto, authUserDto.getUuid(),
					tempFile, fileName, "", "");
			asyncTask = asyncTaskFacade.create(tempFile.length(), WebServiceUtils.getTransfertDuration(), fileName,
					null, AsyncTaskType.DOCUMENT_UPLOAD);
			DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext,
					asyncTask);
			taskExecutor.execute(task);
			return new DocumentDto(asyncTask, documentTaskContext);
		} catch (Exception e) {
			logAsyncFailure(asyncTask, e);
			WebServiceUtils.deleteTempFile(tempFile);
			if (asyncTask == null) {
				throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
						"Failure during asynchronous file upload : asyncTask null");
			}
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Failure during asynchronous file upload in the asyncTask with UUID " + asyncTask.getUuid());
		}
	}
}
