/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

package org.linagora.linshare.webservice.userv2.impl.only;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.linagora.linshare.mongo.entities.WorkGroupAsyncTask;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv1.task.WorkGroupEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;
import org.linagora.linshare.webservice.userv2.WorkGroupNodeRestService;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Deprecated
@Path("/work_groups/{workGroupUuid}/nodes")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class WorkGroupNodeRestServiceImpl extends WebserviceBase implements
		WorkGroupNodeRestService {

	protected final WorkGroupNodeFacade workGroupNodeFacade;

	protected final WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade ;

	protected final AsyncTaskFacade asyncTaskFacade;

	protected org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	protected final AccountQuotaFacade accountQuotaFacade;

	protected boolean sizeValidation;

	public WorkGroupNodeRestServiceImpl(WorkGroupNodeFacade workGroupNodeFacade,
			WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade, AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor, AccountQuotaFacade accountQuotaFacade, boolean sizeValidation) {
		super();
		this.workGroupNodeFacade = workGroupNodeFacade;
		this.workGroupEntryAsyncFacade = workGroupEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.accountQuotaFacade = accountQuotaFacade;
		this.sizeValidation = sizeValidation;
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a folder into a workGroup.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode create(
			@Parameter(description = "The workgroup uuid.", required = true) 
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "Only the name and the parent of the new folder are required.", required = true) WorkGroupNode workGroupFolder,
			@Parameter(description = "Strict mode: Raise error if a folder with same name already exists (default=false).", required = false)
				@QueryParam("strict") @DefaultValue("false") Boolean strict,
			@Parameter(description = "Dry run mode . (default=false).", required = false)
				@QueryParam("dryRun") @DefaultValue("false") Boolean dryRun)
				throws BusinessException {
		return workGroupNodeFacade.create(null, workGroupUuid, workGroupFolder, strict, dryRun);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all workgroup folders.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupNode> findAll(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The parent uuid.", required = false)
				@QueryParam("parent") String parent,
			@Parameter(description = "True to enable flat document mode (DOCUMENT AND FOLDER only)", required = false)
				@QueryParam("flat") @DefaultValue("false") Boolean flat,
			@Parameter(description = "Filter by node type.", required = false)
				@QueryParam("type") List<WorkGroupNodeType> nodeTypes
			)
				throws BusinessException {
		return workGroupNodeFacade.findAll(null, workGroupUuid, parent, flat, nodeTypes);
	}

	@Path("/{workGroupNodeUuid}")
	@GET
	@Operation(summary = "Get a workgroup folder.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode find(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup node uuid.", required = true)
				@PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@QueryParam("tree") @DefaultValue("false") Boolean withTree)
			throws BusinessException {
		return workGroupNodeFacade.find(null, workGroupUuid, workGroupNodeUuid, withTree);
	}

	@Path("/{workGroupNodeUuid: .*}")
	@PUT
	@Operation(summary = "Update a workgroup folder (name or parent).", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode update(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup node uuid.", required = true)
				@PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@Parameter(description = "The workgroup folder to update. Only name or parent can be updated, Uuid is required, others fields are useless.", required = true) WorkGroupNode workGroupFolder)
					throws BusinessException {
		return workGroupNodeFacade.update(null, workGroupUuid, workGroupFolder, workGroupNodeUuid);
	}

	@Path("/{workGroupNodeUuid: .*}")
	@DELETE
	@Operation(summary = "Delete a workgroup node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode delete(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workGroup node uuid.", required = false)
				@PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@Parameter(description = "The workgroup node to delete. Only uuid is required", required = false) WorkGroupNode workGroupNode)
					throws BusinessException {
		return workGroupNodeFacade.delete(null, workGroupUuid, workGroupNodeUuid, workGroupNode);
	}

	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Create a workgroup document which will contain the uploaded file, if versionning is enabled and another workgroup document already exists with same name, it will be automatically a revision of the old one.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode create(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The parent node uuid.", required = false)
				@QueryParam("parent") String parentNodeUuid,
			@Parameter(description = "File stream.", required = true)
				@Multipart(value = "file", required = true) InputStream file,
			@Parameter(description = "An optional description of a workgroup document.")
				@Multipart(value = "description", required = false) String description,
			@Parameter(description = "The given file name of the file to upload.", required = true)
				@Multipart(value = "filename", required = false) String givenFileName,
			@Parameter(description = "True to enable asynchronous upload process.", required = false)
				@QueryParam("async") Boolean async,
			@HeaderParam("Content-Length") Long contentLength,
			@Parameter(description = "file size (size validation purpose).", required = false)
				@Multipart(value = "filesize", required = false)  Long fileSize,
			MultipartBody body,
			@Parameter(description = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false)
				@QueryParam("strict") @DefaultValue("false") Boolean strict)
					throws BusinessException {
		checkMaintenanceMode();
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
		File tempFile = WebServiceUtils.getTempFile(file, "rest-userv2-thread-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			checkSizeValidation(contentLength, fileSize, currSize);
		}
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createWorkGroupAsycTask(fileName, tempFile, parentNodeUuid, workGroupUuid, transfertDuration, strict);
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				WorkGroupNode create = workGroupNodeFacade.create(null, workGroupUuid, parentNodeUuid, tempFile, fileName, strict);
				return create;
			} finally {
				WebServiceUtils.deleteTempFile(tempFile);
			}
		}
	}

	@Path("/copy")
	@POST
	@Operation(summary = "Create a workgroup document from an existing document or received share.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupNode> copy(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			CopyDto  copy,
			@Parameter(description = "Delete the share at the end of the copy.", required = false)
				@QueryParam("deleteShare") @DefaultValue("false") boolean deleteShare
			) throws BusinessException {
		return workGroupNodeFacade.copy(null, workGroupUuid, null, copy, deleteShare);
	}

	@Path("/{uuid}/copy")
	@POST
	@Operation(summary = "Copy the workGroup document to another folder or to another workgroup, duplicate a workgroup document, or to restore a revision ", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupEntryDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<WorkGroupNode> copy(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The parent node uuid, which is the uuid of the node destination", required = true)
				@PathParam("uuid")  String parentNodeUuid,
			@Parameter(description = "The object which contains the target kind and the uuid of the node to copy.", required = true) CopyDto copy,
			@Parameter(description = "Delete the share at the end of the copy.", required = false)
				@QueryParam("deleteShare") @DefaultValue("false") boolean deleteShare)
			throws BusinessException {
		return workGroupNodeFacade.copy(null, workGroupUuid, parentNodeUuid, copy, deleteShare);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Get a workgroup node.")
	@Override
	public void head(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup node uuid.", required = true)
				@PathParam("uuid") String uuid)
					throws BusinessException {
		workGroupNodeFacade.find(null, workGroupUuid, uuid, false);
	}


	@Path("/{uuid}/download")
	@GET
	@Operation(summary = "Download a file.")
	@Override
	public Response download(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workgroup node uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "If withRevision is TRUE you will download the workGroupDocument with all its revisions (Available just for workGroupDocument).", required = false)
				@QueryParam("withRevision") @DefaultValue("false") Boolean withRevision)
						throws BusinessException {
		return workGroupNodeFacade.download(null, workGroupUuid, uuid, withRevision);
	}

	@Path("/{uuid}/thumbnail{kind:(small)?|(medium)?|(large)?|(pdf)?}")
	@GET
	@Operation(summary = "Download the thumbnail of a file.")
	@Override
	public Response thumbnail(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The document uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "This parameter allows you to choose which thumbnail you want : Small, Medium or Large. Default value is Medium", required = false)
				@PathParam("kind") ThumbnailType thumbnailType,
			@Parameter(description = "True to get an encoded base 64 response", required = false)
				@QueryParam("base64") @DefaultValue("false") boolean base64)
					throws BusinessException {
		return workGroupNodeFacade.thumbnail(null, workGroupUuid, uuid, base64, thumbnailType);
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
	@Operation(summary = "Get all traces for a workgroup node.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogEntryUser.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<AuditLogEntryUser> findAll(
			@Parameter(description = "The workGroupNodeUuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The workGroup node uuid.", required = true)
				@PathParam("workGroupNodeUuid") String workGroupNodeUuid,
			@Parameter(description = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions,
			@Parameter(description = "Filter by type of resource's types.", required = false)
				@QueryParam("types") List<AuditLogEntryType> types,
				@QueryParam("beginDate") String beginDate,
				@QueryParam("endDate") String endDate) {
		return workGroupNodeFacade.findAll(null, workGroupUuid, workGroupNodeUuid, actions, types, beginDate, endDate);
	}

	@Path("/url")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a workgroup document which will contain the uploaded file.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkGroupNode.class))),
			responseCode = "200"
		)
	})
	@Override
	public WorkGroupNode createFromURL(
			@Parameter(description = "The workgroup uuid.", required = true)
				@PathParam("workGroupUuid") String workGroupUuid,
			@Parameter(description = "The parent workgroup node uuid.", required = false)
				@QueryParam("parent") String parentNodeUuid,
			@Parameter(description = "The document URL object.", required = true) DocumentURLDto documentURLDto,
			@Parameter(description = "True to enable asynchronous upload processing.", required = false) @DefaultValue("false")
				@QueryParam("async") Boolean async,
			@Parameter(description = "Strict mode: Raise error if a node with same name already exists (default=false).", required = false)
				@QueryParam("strict") @DefaultValue("false") Boolean strict)
			throws BusinessException {
		checkMaintenanceMode();
		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		Validate.notNull(documentURLDto);
		String fileURL = documentURLDto.getURL();
		Validate.notEmpty(fileURL);
		String fileName = WebServiceUtils.getFileNameFromUrl(fileURL, documentURLDto.getFileName());
		File tempFile = WebServiceUtils.createFileFromURL(documentURLDto, "rest-userv2-thread-entries", sizeValidation);
		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			return createWorkGroupAsycTask(fileName, tempFile, parentNodeUuid, workGroupUuid, transfertDuration, strict);
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				WorkGroupNode create = workGroupNodeFacade.create(null, workGroupUuid, parentNodeUuid, tempFile,
						fileName, strict);
				return create;
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
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable, uploads are disabled.");
		}
	}

	protected WorkGroupAsyncTask createWorkGroupAsycTask(String fileName, File tempFile, String parentNodeUuid,
			String workGroupUuid, Long transfertDuration, Boolean strict) {
		AccountDto authUserDto = workGroupNodeFacade.getAuthenticatedAccountDto();
		AsyncTaskDto asyncTask = null;
		try {
			asyncTask = asyncTaskFacade.create(tempFile.length(), transfertDuration, fileName, null,
					AsyncTaskType.THREAD_ENTRY_UPLOAD);
			WorkGroupEntryTaskContext workGroupEntryTaskContext = new WorkGroupEntryTaskContext(authUserDto,
					authUserDto.getUuid(), workGroupUuid, tempFile, fileName, parentNodeUuid, strict);
			WorkGroupEntryUploadAsyncTask task = new WorkGroupEntryUploadAsyncTask(workGroupEntryAsyncFacade,
					workGroupEntryTaskContext, asyncTask);
			taskExecutor.execute(task);
			return new WorkGroupAsyncTask(asyncTask, workGroupEntryTaskContext);
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
