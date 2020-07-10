/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
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
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.delegation.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.delegation.WorkGroupEntryFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.WorkGroupEntryRestService;
import org.linagora.linshare.webservice.userv1.task.WorkGroupEntryCopyAsyncTask;
import org.linagora.linshare.webservice.userv1.task.WorkGroupEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/{actorUuid}/workgroups/{workgroupUuid}/entries")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupEntryRestServiceImpl extends WebserviceBase implements
		WorkGroupEntryRestService {

	private final WorkGroupEntryFacade workGroupEntryFacade;

	private final WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade ;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private boolean sizeValidation;

	public WorkGroupEntryRestServiceImpl(WorkGroupEntryFacade workgroupEntryFacade,
			WorkGroupEntryAsyncFacade workgroupEntryAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor,
			boolean sizeValidation) {
		super();
		this.workGroupEntryFacade = workgroupEntryFacade;
		this.workGroupEntryAsyncFacade = workgroupEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.sizeValidation = sizeValidation;
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Create a workgroup entry which will contain the uploaded file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupEntryDto create(
			@Parameter(description = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) 
				@PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "File stream.", required = true) 
				@Multipart(value = "file", required = true) InputStream file,
			@Parameter(description = "An optional description of a document.") 
				@Multipart(value = "description", required = false) String description,
			@Parameter(description = "The given file name of the uploaded file.", required = false) 
				@Multipart(value = "filename", required = false) String givenFileName,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) 
				@DefaultValue("false") @QueryParam("async") Boolean async,
			@HeaderParam("Content-Length") Long contentLength,
			@Parameter(description = "file size (size validation purpose).", required = true) 
				@Multipart(value = "filesize", required = true)  Long fileSize,
			@Parameter(description = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false) 
				@DefaultValue("false") @QueryParam("strict") Boolean strict,
			MultipartBody body)
					throws BusinessException {
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		if (file == null) {
			String msg = "Missing file (check multipart parameter named 'file')";
			logger.error(msg);
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(msg).build());
		}
		String fileName = getFileName(givenFileName, body);
		// Default mode. No user input.
		if (async == null) {
			async = false;
		}
		File tempFile = WebServiceUtils.getTempFile(file, "rest-delegation-workgroup-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			checkSizeValidation(contentLength, fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createWorkGroupEntryDtoAsynchronously(actorUuid, workgroupUuid, tempFile, fileName, "", "",
					transfertDuration, strict);
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				return workGroupEntryFacade.create(actorUuid, workgroupUuid, tempFile, fileName, strict);
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
	}

	@Path("/copy")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Create a workgroup entry which will contain the uploaded file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupEntryDto copy(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The document entry uuid.", required = true) @PathParam("entryUuid")  String entryUuid,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @QueryParam("async") Boolean async)
					throws BusinessException {
		// Default mode. No user input.
		if (async == null) {
			async = false;
		}
		if (async) {
			logger.debug("Async mode is used");
			AccountDto authUserDto = workGroupEntryFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				asyncTask = asyncTaskFacade.create(actorUuid, entryUuid, AsyncTaskType.DOCUMENT_COPY);
				WorkGroupEntryTaskContext tetc = new WorkGroupEntryTaskContext(authUserDto, actorUuid, workgroupUuid, entryUuid, null);
				WorkGroupEntryCopyAsyncTask task = new WorkGroupEntryCopyAsyncTask(workGroupEntryAsyncFacade, tetc, asyncTask);
				taskExecutor.execute(task);
				return new WorkGroupEntryDto(asyncTask, tetc);
			} catch (Exception e) {
				logAsyncFailure(actorUuid, asyncTask, e);
				throw e;
			}
		} else {
			logger.debug("Async mode is not used");
			return workGroupEntryFacade.copy(actorUuid, workgroupUuid, entryUuid);
		}
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Get a workgroup entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupEntryDto find(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The workgroup entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return workGroupEntryFacade.find(actorUuid, workgroupUuid, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a workgroup entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void head(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The workgroup entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		workGroupEntryFacade.find(actorUuid, workgroupUuid, uuid);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all workgroup entries.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupEntryDto> findAll(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid)
					throws BusinessException {
		return workGroupEntryFacade.findAll(actorUuid, workgroupUuid);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a workgroup entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupEntryDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The workgroup entry to delete.", required = true) WorkGroupEntryDto workgroupEntry)
					throws BusinessException {
		return workGroupEntryFacade.delete(actorUuid, workgroupUuid, workgroupEntry);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a workgroup entry.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupEntryDto delete(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The workgroup entry uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return workGroupEntryFacade.delete(actorUuid, workgroupUuid, uuid);
	}

	@Path("/{actorUuid}/documents/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.")
	@Override
	public Response download(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The workgroup entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return workGroupEntryFacade.download(actorUuid, workgroupUuid, uuid);
	}

	@Path("/{actorUuid}/documents/{uuid}/thumbnail")
	@GET
	@Operation(summary = "Download the thumbnail of a file.")
	@Override
	public Response thumbnail(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
			return workGroupEntryFacade.thumbnail(actorUuid, workgroupUuid, uuid, ThumbnailType.MEDIUM);
	}

	@Path("/{actorUuid}/documents/{uuid}")
	@PUT
	@Operation(summary = "Update the workgroup entry properties.")
	@Override
	public WorkGroupEntryDto update(
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String workgroupEntryuuid,
			@Parameter(description = "The workgroup Entry.", required = true) WorkGroupEntryDto workgroupEntryDto)
			throws BusinessException {
		return workGroupEntryFacade.update(actorUuid, workgroupUuid, workgroupEntryuuid, workgroupEntryDto);
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

	@Path("/url")
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
	public WorkGroupEntryDto createFromURL(
			@Parameter(description = "The document URL object.", required = true) DocumentURLDto documentURLDto,
			@Parameter(description = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The workgroup uuid.", required = true) @PathParam("workgroupUuid") String workgroupUuid,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false") @QueryParam("async") Boolean async,
			@Parameter(description = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false) @QueryParam("strict") @DefaultValue("false") Boolean strict)
			throws BusinessException {
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		Validate.notNull(documentURLDto, "DocumentURLDto must be set.");
		String fileURL = documentURLDto.getURL();
		Validate.notEmpty(fileURL, "Missing url");
		Validate.notEmpty(actorUuid, "Missing Actor UUID");
		Validate.notEmpty(workgroupUuid, "Missing Work group UUID");
		String fileName = WebServiceUtils.getFileNameFromUrl(fileURL, documentURLDto.getFileName());
		File tempFile = WebServiceUtils.createFileFromURL(documentURLDto, "rest-delegation-workgroup-entries",
				sizeValidation);
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createWorkGroupEntryDtoAsynchronously(actorUuid, workgroupUuid, tempFile, fileName, "", "",
					transfertDuration, strict);
		} else {
			try {
				logger.debug("Async mode is not used");
				return workGroupEntryFacade.create(actorUuid, workgroupUuid, tempFile, fileName, strict);
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
	}

	protected void logAsyncFailure(String actorUuid, AsyncTaskDto asyncTask,
			Exception e) {
		logger.error(e.getMessage());
		logger.debug("Exception : ", e);
		if (asyncTask != null) {
			asyncTaskFacade.fail(actorUuid, asyncTask, e);
		}
	}

	private WorkGroupEntryDto createWorkGroupEntryDtoAsynchronously(String actorUuid, String workgroupUuid,
			File tempFile, String fileName, String string, String string2, Long transfertDuration, Boolean strict) {
		AccountDto authUserDto = workGroupEntryFacade.getAuthenticatedAccountDto();
		AsyncTaskDto asyncTask = null;
		try {
			asyncTask = asyncTaskFacade.create(actorUuid, tempFile.length(), transfertDuration, fileName, null,
					AsyncTaskType.THREAD_ENTRY_UPLOAD);
			WorkGroupEntryTaskContext workgroupEntryTaskContext = new WorkGroupEntryTaskContext(authUserDto, actorUuid,
					workgroupUuid, tempFile, fileName, null, strict);
			WorkGroupEntryUploadAsyncTask task = new WorkGroupEntryUploadAsyncTask(workGroupEntryAsyncFacade,
					workgroupEntryTaskContext, asyncTask);
			taskExecutor.execute(task);
			return new WorkGroupEntryDto(asyncTask, workgroupEntryTaskContext);
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

