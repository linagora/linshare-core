/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import org.linagora.linshare.mongo.entities.WorkGroupAsyncTask;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv1.task.ThreadEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.ThreadEntryTaskContext;
import org.linagora.linshare.webservice.userv2.WorkGroupFolderRestService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/work_groups/{workGroupUuid}/nodes")
@Api(value = "/rest/user/work_groups/{workGroupUuid}/nodes", basePath = "/rest/work_groups/{workGroupUuid}/nodes",
	description = "work group nodes service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupNodeRestServiceImpl extends WebserviceBase implements
		WorkGroupFolderRestService {

	protected final WorkGroupNodeFacade workGroupNodeFacade;

	protected final ThreadEntryAsyncFacade threadEntryAsyncFacade ;

	protected final AsyncTaskFacade asyncTaskFacade;

	protected org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	protected final AccountQuotaFacade accountQuotaFacade;

	protected boolean sizeValidation;

	public WorkGroupNodeRestServiceImpl(WorkGroupNodeFacade workGroupNodeFacade,
			ThreadEntryAsyncFacade threadEntryAsyncFacade, AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor, AccountQuotaFacade accountQuotaFacade, boolean sizeValidation) {
		super();
		this.workGroupNodeFacade = workGroupNodeFacade;
		this.threadEntryAsyncFacade = threadEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.accountQuotaFacade = accountQuotaFacade;
		this.sizeValidation = sizeValidation;
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a workgroup node.", response = WorkGroupNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Workgroup not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode create(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "Only the name and the parent of the new folder are required.", required = true) WorkGroupNode workGroupFolder,
			@ApiParam(value = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false)
				@QueryParam("strict") @DefaultValue("false") Boolean strict,
			@ApiParam(value = "Dry run mode . (default=false).", required = false)
				@QueryParam("dryRun") @DefaultValue("false") Boolean dryRun)
				throws BusinessException {
		return workGroupNodeFacade.create(null, workGroupUuid, workGroupFolder, strict, dryRun);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Get all workgroup folders.", response = WorkGroupNode.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public List<WorkGroupNode> findAll(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The parent uuid.", required = false) @QueryParam("parent") String parent,
			@ApiParam(value = "Filter by node type.", required = false) @QueryParam("type") WorkGroupNodeType nodeType
			)
				throws BusinessException {
		return workGroupNodeFacade.findAll(null, workGroupUuid, parent, false, nodeType);
	}

	@Path("/{workGroupNodeUuid}")
	@GET
	@ApiOperation(value = "Get a workgroup folder.", response = WorkGroupNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode find(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The user uuid.", required = true) @PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@QueryParam("tree") @DefaultValue("false") Boolean withTree)
			throws BusinessException {
		return workGroupNodeFacade.find(null, workGroupUuid, workGroupNodeUuid, withTree);
	}

	@Path("/{workGroupNodeUuid}")
	@PUT
	@ApiOperation(value = "Update a workgroup folder (name or parent).", response = WorkGroupNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode update(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@ApiParam(value = "The workgroup folder to update. Only name or parent can be updated. Uuid is required, others fields are useless.", required = true) WorkGroupNode workGroupFolder)
					throws BusinessException {
		return workGroupNodeFacade.update(null, workGroupUuid, workGroupFolder);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a workgroup folder.", response = WorkGroupNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or workgroup folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode delete(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The workgroup folder to delete. Only uuid is required", required = true) WorkGroupNode workGroupFolder)
					throws BusinessException {
		return workGroupNodeFacade.delete(null, workGroupUuid, workGroupFolder.getUuid());
	}

	@Path("/{workGroupFolderUuid}")
	@DELETE
	@ApiOperation(value = "Delete a workgroup folder.", response = WorkGroupNode.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or workgroup folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode delete(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The workGroup folder uuid.", required = true) @PathParam("workGroupFolderUuid") String workGroupFolderUuid)
					throws BusinessException {
		return workGroupNodeFacade.delete(null, workGroupUuid, workGroupFolderUuid);
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create a workgroup document which will contain the uploaded file.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Workgroup document not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode create(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The parent workgroup node uuid.", required = false) @QueryParam("parent") String parentNodeUuid,
			@ApiParam(value = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@ApiParam(value = "An optional description of a workgroup document.") @Multipart(value = "description", required = false) String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = true) @Multipart(value = "filename", required = false) String givenFileName,
			@ApiParam(value = "True to enable asynchronous upload processing.", required = false) @QueryParam("async") Boolean async,
			@HeaderParam("Content-Length") Long contentLength,
			@ApiParam(value = "file size (size validation purpose).", required = false) @Multipart(value = "filesize", required = false)  Long fileSize,
			MultipartBody body,
			@ApiParam(value = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false)
				@QueryParam("strict") @DefaultValue("false") Boolean strict)
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
			AccountDto actorDto = workGroupNodeFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				asyncTask = asyncTaskFacade.create(currSize, transfertDuration, fileName, null, AsyncTaskType.THREAD_ENTRY_UPLOAD);
				ThreadEntryTaskContext threadEntryTaskContext = new ThreadEntryTaskContext(actorDto, actorDto.getUuid(), workGroupUuid, tempFile, fileName, parentNodeUuid);
				ThreadEntryUploadAsyncTask task = new ThreadEntryUploadAsyncTask(threadEntryAsyncFacade, threadEntryTaskContext, asyncTask);
				taskExecutor.execute(task);
				return new WorkGroupAsyncTask(asyncTask, threadEntryTaskContext);
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
				WorkGroupNode create = workGroupNodeFacade.create(null, workGroupUuid, parentNodeUuid, tempFile, fileName, strict);
				return create;
			} finally {
				deleteTempFile(tempFile);
			}
		}
	}

	@Path("/{uuid}/copy")
	@POST
	@ApiOperation(value = "Create a threworkgroupry which will contain the uploaded file.", response = WorkGroupEntryDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Workgroup node not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public WorkGroupNode copy(
			@ApiParam(value = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The document node uuid.", required = true)
				@PathParam("uuid")  String nodeUuid,
			@ApiParam(value = "The destination node uuid.", required = false)
				@QueryParam("destinationNodeUuid")  String destinationNodeUuid,
			@ApiParam(value = "The workgroup folder to delete. Only uuid is required", required = true)
				WorkGroupNode workGroupNode)
				throws BusinessException {
		return workGroupNodeFacade.copy(null, workGroupUuid, nodeUuid, destinationNodeUuid, workGroupNode);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Get a workgroup node.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup node not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public void head(String workGroupUuid, String uuid) throws BusinessException {
		workGroupNodeFacade.find(null, workGroupUuid, uuid, false);
	}


	@Path("/{uuid}/download")
	@GET
	@ApiOperation(value = "Download a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Workgroup node not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response download(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The workgroup node uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		return workGroupNodeFacade.download(null, workGroupUuid, uuid);
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@ApiOperation(value = "Download the thumbnail of a file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
					@ApiResponse(code = 404, message = "Workgroup node not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Response thumbnail(
			@ApiParam(value = "The workgroup uuid.", required = true) @PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "True to get an encoded base 64 response", required = false) @QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		return workGroupNodeFacade.thumbnail(null, workGroupUuid, uuid, base64);
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

	@Path("/{workGroupNodeUuid}/audit")
	@GET
	@ApiOperation(value = "Get all traces for a workgroup node.", response = AuditLogEntryUser.class, responseContainer="Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") ,
					@ApiResponse(code = 404, message = "Workgroup or folder not found."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
					})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@ApiParam(value = "The workGroupNodeUuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@ApiParam(value = "The workGroup node uuid.", required = true)
				@PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<String> actions,
			@ApiParam(value = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<String> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return workGroupNodeFacade.findAll(null, workGroupUuid, workGroupNodeUuid, actions, types, beginDate, endDate);
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
