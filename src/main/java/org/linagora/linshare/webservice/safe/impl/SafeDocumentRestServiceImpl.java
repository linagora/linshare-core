/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
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

package org.linagora.linshare.webservice.safe.impl;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.delegation.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.delegation.WorkGroupEntryFacade;
import org.linagora.linshare.core.facade.webservice.safe.SafeDocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.safe.SafeDocumentRestService;
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


@Path("/{ric}/documents")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SafeDocumentRestServiceImpl extends WebserviceBase implements 
	SafeDocumentRestService {

	private final WorkGroupEntryFacade workGroupEntryFacade;

	private final WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade ;

	private final AsyncTaskFacade asyncTaskFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	private SafeDocumentFacade safeDocumentFacade;

	private boolean sizeValidation;

	public SafeDocumentRestServiceImpl(WorkGroupEntryFacade workGroupEntryFacade,
			WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade, 
			AsyncTaskFacade asyncTaskFacade,
			ThreadPoolTaskExecutor taskExecutor,
			SafeDocumentFacade safeDocumentFacade,
			boolean sizeValidation) {
		super();
		this.workGroupEntryFacade = workGroupEntryFacade;
		this.workGroupEntryAsyncFacade = workGroupEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
		this.safeDocumentFacade = safeDocumentFacade;
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
	public WorkGroupEntryDto create(
			@Parameter(description = "The safeUuid uuid.", required = true) 
				@PathParam("ric") String ric,
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
				@Multipart(value = "filesize", required = true) Long fileSize,
			MultipartBody body) throws BusinessException {

		Long transfertDuration = WebServiceUtils.getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Missing file (check multipart parameter named 'file')").build());
		}
		String fileName = getFileName(givenFileName, body);
		File tempFile = WebServiceUtils.getTempFile(file, "rest-userv2-document-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			WebServiceUtils.checkSizeValidation(fileSize, currSize);
		}
		Validate.notNull(ric);
		String safeUuid = ric.substring(24);
		SafeDetail safeDetail = safeDocumentFacade.findSafeDetail(null, safeUuid);
		User user = safeDocumentFacade.findUser(safeUuid);
		String workGroupUuid = safeDetail.getContainerUuid();

		if (async) {
			logger.debug("Async mode is used");
			// Asynchronous mode
			AccountDto authUserDto = workGroupEntryFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				asyncTask = asyncTaskFacade.create(user.getLsUuid(), currSize, transfertDuration, fileName, null,
						AsyncTaskType.THREAD_ENTRY_UPLOAD);
				WorkGroupEntryTaskContext workGroupEntryTaskContext = new WorkGroupEntryTaskContext(authUserDto, user.getLsUuid(),
						workGroupUuid, tempFile, fileName, null, false);
				// TODO use something like safeDocumentFacade  with async support to replace workGroupEntryAsyncFacade
				WorkGroupEntryUploadAsyncTask task = new WorkGroupEntryUploadAsyncTask(workGroupEntryAsyncFacade,
						workGroupEntryTaskContext, asyncTask);
				taskExecutor.execute(task);
				return new WorkGroupEntryDto(asyncTask, workGroupEntryTaskContext);
			} catch (Exception e) {
				logAsyncFailure(user.getLsUuid(), asyncTask, e);
				WebServiceUtils.deleteTempFile(tempFile);
				throw e;
			}
		} else {
			// TODO : manage transfertDuration
			// Synchronous mode
			try {
				logger.debug("Async mode is not used");
				return safeDocumentFacade.create(user.getLsUuid(), workGroupUuid, tempFile, fileName, false);
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
}
