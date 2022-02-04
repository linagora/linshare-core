/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.webservice.delegationv2.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.delegation.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.delegation.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.DocumentRestService;
import org.linagora.linshare.webservice.userv1.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/{actorUuid}/documents")
public class DocumentRestServiceImpl extends WebserviceBase implements
		DocumentRestService {

	private final DocumentFacade documentFacade;

	private final DocumentAsyncFacade documentAsyncFacade;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private boolean sizeValidation;

	public DocumentRestServiceImpl(DocumentFacade documentFacade,
			DocumentAsyncFacade documentAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor,
			boolean sizeValidation) {
		super();
		this.documentFacade = documentFacade;
		this.documentAsyncFacade = documentAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
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
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@Parameter(description = "An optional description of a document.") @Multipart(value = "description", required = false) String description,
			@Parameter(description = "The given file name of the uploaded file.", required = false) @Multipart(value = "filename", required = false) String givenFileName,
			@Parameter(description = "Signature file stream.", required = false) @Multipart(value = "signaturefile", required = false) InputStream theSignatureFile,
			@Parameter(description = "The given file name of the signature uploaded file.", required = false) @Multipart(value = "signatureFileName", required = false) String signatureFileName,
			@Parameter(description = "X509 Certificate entity.", required = false) @Multipart(value = "x509cert", required = false) InputStream x509certificate,
			@Parameter(description = "The given metadata of the uploaded file.", required = false) @Multipart(value = "metadata", required = false) String metaData,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @QueryParam("async") Boolean async,
			@Parameter(description = "file size (size validation purpose).", required = true) @Multipart(value = "filesize", required = true)  Long fileSize,
			MultipartBody body)
			throws BusinessException {
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Missing file (check parameter file)").build());
		}
		String fileName = getFileName(givenFileName, body);
		// Default mode. No user input.
		if (async == null) {
			async = false;
		}
		File tempFile = WebServiceUtils.getTempFile(file, "rest-delegation-document-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			WebServiceUtils.checkSizeValidation(fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createDocumentDtoAsynchronously(actorUuid, tempFile, fileName, "", "", transfertDuration);
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				if (theSignatureFile != null) {
					// TODO : Manage signature and meta data
					return documentFacade.create(actorUuid, tempFile, description, fileName);
				}
				return documentFacade.create(actorUuid, tempFile, description, fileName);
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
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
	public DocumentDto find(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.find(actorUuid, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		documentFacade.find(actorUuid, uuid);
	}

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
	public List<DocumentDto> findAll(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid)
			throws BusinessException {
		return documentFacade.findAll(actorUuid);
	}


	@Path("/{uuid}")
	@PUT()
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Update a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public DocumentDto update(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "The documentDto with updated values.") DocumentDto documentDto)
			throws BusinessException {
		return documentFacade.update(actorUuid, uuid, documentDto);
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
	public DocumentDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.delete(actorUuid, uuid);
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
	public DocumentDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document to delete.", required = true) DocumentDto documentDto)
			throws BusinessException {
		return documentFacade.delete(actorUuid, documentDto);
	}

	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.")
	@Override
	public Response download(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return documentFacade.download(actorUuid, uuid);
	}

	@Path("/{uuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a file.")
	@Override
	public Response thumbnail(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false) @PathParam("kind") ThumbnailType thumbnailType
			) throws BusinessException {
		return documentFacade.thumbnail(actorUuid, uuid, thumbnailType);
	}

	@Path("/{uuid}/async")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public AsyncTaskDto findAsync(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The async task uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing uuid");
		return asyncTaskFacade.find(actorUuid, uuid);
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
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false") @QueryParam("async") Boolean async)
			throws BusinessException {
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		Validate.notNull(documentURLDto, "DocumentURLDto must be set.");
		String fileURL = documentURLDto.getURL();
		Validate.notEmpty(fileURL, "Missing url");
		Validate.notEmpty(actorUuid, "Missing Actor UUID");
		String fileName = WebServiceUtils.getFileNameFromUrl(fileURL, documentURLDto.getFileName());
		File tempFile = WebServiceUtils.createFileFromURL(documentURLDto, "rest-delegation-document-entries",
				sizeValidation);
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createDocumentDtoAsynchronously(actorUuid, tempFile, fileName, "", "", transfertDuration);
		} else {
			try {
				logger.debug("Async mode is not used");
				return documentFacade.create(actorUuid, tempFile, "", fileName);
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
	}

	protected void logAsyncFailure(String actorUuid, AsyncTaskDto asyncTask, Exception e) {
		logger.error(e.getMessage());
		logger.debug("Exception : ", e);
		if (asyncTask != null) {
			asyncTaskFacade.fail(actorUuid, asyncTask, e);
		}
	}

	protected DocumentDto createDocumentDtoAsynchronously(String actorUuid, File tempFile, String fileName,
			String metaData, String description, Long transfertDuration) {
		AccountDto authUserDto = documentFacade.getAuthenticatedAccountDto();
		AsyncTaskDto asyncTask = null;
		try {
			DocumentTaskContext documentTaskContext = new DocumentTaskContext(authUserDto, actorUuid, tempFile,
					fileName, metaData, description);
			asyncTask = asyncTaskFacade.create(actorUuid, tempFile.length(), transfertDuration, fileName, null,
					AsyncTaskType.DOCUMENT_UPLOAD);
			DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext,
					asyncTask);
			taskExecutor.execute(task);
			return new DocumentDto(asyncTask, documentTaskContext);
		} catch (Exception e) {
			logAsyncFailure(actorUuid, asyncTask, e);
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
