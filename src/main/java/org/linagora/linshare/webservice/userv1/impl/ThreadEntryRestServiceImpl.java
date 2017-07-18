/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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

package org.linagora.linshare.webservice.userv1.impl;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv1.ThreadEntryRestService;
import org.linagora.linshare.webservice.userv1.task.ThreadEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.ThreadEntryTaskContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/threads/{threadUuid}/entries")
@Api(value = "/rest/user/threads/{threadUuid}/entries", basePath = "/rest/threads/{threadUuid}/entries",
	description = "thread entries service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadEntryRestServiceImpl extends WebserviceBase implements
		ThreadEntryRestService {

	private final WorkGroupNodeFacade facade;

	private final ThreadEntryAsyncFacade threadEntryAsyncFacade ;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private final AccountQuotaFacade accountQuotaFacade;

	private boolean sizeValidation;

	public ThreadEntryRestServiceImpl(WorkGroupNodeFacade workGroupNodeFacade,
			ThreadEntryAsyncFacade threadEntryAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor,
			AccountQuotaFacade accountQuotaFacade,
			boolean sizeValidation) {
		super();
		this.facade = workGroupNodeFacade;
		this.threadEntryAsyncFacade = threadEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.accountQuotaFacade = accountQuotaFacade;
		this.sizeValidation = sizeValidation;
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create a thread entry which will contain the uploaded file.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupEntryDto create(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@ApiParam(value = "An optional description of a thread entry.") @Multipart(value = "description", required = false) String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) @Multipart(value = "filename", required = false) String givenFileName,
			@ApiParam(value = "True to enable asynchronous upload processing.", required = false) @QueryParam("async") Boolean async,
			@HeaderParam("Content-Length") Long contentLength,
			@ApiParam(value = "file size (size validation purpose).", required = false) @Multipart(value = "filesize", required = false)  Long fileSize,
			MultipartBody body)
					throws BusinessException {
		checkMaintenanceMode();
		Long transfertDuration = getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
		}
		String fileName = getFileName(givenFileName, body);
		// Default mode. No user input.
		if (async == null) {
			async = false;
		}
		File tempFile = getTempFile(file, "rest-userv2-thread-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			checkSizeValidation(contentLength, fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			AccountDto actorDto = facade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				asyncTask = asyncTaskFacade.create(currSize, transfertDuration, fileName, null, AsyncTaskType.THREAD_ENTRY_UPLOAD);
				ThreadEntryTaskContext threadEntryTaskContext = new ThreadEntryTaskContext(actorDto, actorDto.getUuid(), threadUuid, tempFile, fileName, null);
				ThreadEntryUploadAsyncTask task = new ThreadEntryUploadAsyncTask(threadEntryAsyncFacade, threadEntryTaskContext, asyncTask);
				taskExecutor.execute(task);
				return new WorkGroupEntryDto(asyncTask, threadEntryTaskContext);
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
				WorkGroupNode node = facade.create(null, threadUuid, null, tempFile, fileName, false);
				WorkGroupEntryDto dto = toDto(node);
				// Compatibility code : Reset this field (avoid to display new attribute in old API) 
				dto.setWorkGroup(null);
				dto.setWorkGroupFolder(null);
				return dto;
			} finally {
				deleteTempFile(tempFile);
			}
		}
	}

	@Path("/copy/{entryUuid}")
	@POST
	@ApiOperation(value = "Create a thread entry which will contain the uploaded file.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public DocumentDto copy(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The document entry uuid.", required = true) @PathParam("entryUuid")  String entryUuid)
					throws BusinessException {
		WorkGroupNode node = facade.copy(null, threadUuid, entryUuid, null, null);
		return toDocumentDto(node);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a thread entry.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupEntryDto find(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		WorkGroupNode node = facade.find(null, threadUuid, uuid, false);
		return toDto(node);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Get a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void head(String threadUuid, String uuid) throws BusinessException {
		facade.find(null, threadUuid, uuid, false);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all thread entries.", response = WorkGroupEntryDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<WorkGroupEntryDto> findAll(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid)
					throws BusinessException {
		List<WorkGroupEntryDto> res = Lists.newArrayList();
		List<WorkGroupNode> all = facade.findAll(null, threadUuid, null, true, null);
		for (WorkGroupNode node : all) {
			if (node.getNodeType().equals(WorkGroupNodeType.DOCUMENT)) {
				res.add(new WorkGroupEntryDto((WorkGroupDocument) node));
			}
		}
		return res;
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupEntryDto delete(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry to delete.", required = true) WorkGroupEntryDto threadEntry)
					throws BusinessException {
		Validate.notNull(threadEntry, "WorkGroupEntry must be set");
		WorkGroupNode node = facade.delete(null, threadUuid, threadEntry.getUuid());
		return toDto(node);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a thread entry.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupEntryDto delete(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid to delete.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		WorkGroupNode node = facade.delete(null, threadUuid, uuid);
		return toDto(node);
	}

	@Path("/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response download(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread entry uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return facade.download(null, threadUuid, uuid);
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response thumbnail(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		return facade.thumbnail(null, threadUuid, uuid, base64);
	}

	@Path("/{uuid}")
	@PUT
	@ApiOperation(value = "Update a thread entry.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Thread entry or thread entry not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupEntryDto update(
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = "The thread uuid.", required = true) @PathParam("uuid") String threadEntryUuid,
			WorkGroupEntryDto threadEntryDto) throws BusinessException {
		Validate.notNull(threadEntryDto, "Missing threadEntry");
		WorkGroupDocument dto = new WorkGroupDocument();
		dto.setUuid(threadEntryDto.getUuid());
		dto.setDescription(threadEntryDto.getDescription());
		dto.setName(threadEntryDto.getName());
		dto.setMetaData(threadEntryDto.getMetaData());
		WorkGroupNode node = facade.update(null, threadUuid, dto);
		return toDto(node);
	}

	@Path("/{uuid}/async")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public AsyncTaskDto findAsync(@PathParam("uuid") String uuid)
			throws BusinessException {
		Validate.notEmpty(uuid, "Missing uuid");
		return asyncTaskFacade.find(uuid);
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

	private WorkGroupEntryDto toDto(WorkGroupNode node) {
		WorkGroupEntryDto dto = null;
		if (node != null) {
			if (node.getNodeType().equals(WorkGroupNodeType.DOCUMENT)) {
				dto = new WorkGroupEntryDto((WorkGroupDocument) node);
			}
		}
		return dto;
	}

	private DocumentDto toDocumentDto(WorkGroupNode node) {
		DocumentDto dto = null;
		if (node != null) {
			if (node.getNodeType().equals(WorkGroupNodeType.DOCUMENT)) {
				dto = new DocumentDto((WorkGroupDocument) node);
			}
		}
		return dto;
	}

}