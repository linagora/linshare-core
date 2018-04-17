/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 1'2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2020. Contribute to
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
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.Validate;
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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ric}/documents")
@Api(value = "/rest/safe/{ric}/documents", basePath = "/rest/safe/{ric}/documents",
		description = "Safe documents service.",
		produces = "application/json,application/xml", 
		consumes = "application/json,application/xml")
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
	@ApiOperation(value = "Create a document which will contain the uploaded file.", response = DocumentDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public WorkGroupEntryDto create(
			@ApiParam(value = "The safeUuid uuid.", required = true) 
				@PathParam("ric") String ric,
			@ApiParam(value = "File stream.", required = true) 
				@Multipart(value = "file", required = true) InputStream file,
			@ApiParam(value = "An optional description of a document.") 
				@Multipart(value = "description", required = false) String description,
			@ApiParam(value = "The given file name of the uploaded file.", required = false) 
				@Multipart(value = "filename", required = false) String givenFileName,
			@ApiParam(value = "True to enable asynchronous upload processing.", required = false) 
				@DefaultValue("false") @QueryParam("async") Boolean async,
			@HeaderParam("Content-Length") Long contentLength,
			@ApiParam(value = "file size (size validation purpose).", required = true) 
				@Multipart(value = "filesize", required = true) Long fileSize,
			MultipartBody body) throws BusinessException {

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
				return workGroupEntryFacade.create(user.getLsUuid(), workGroupUuid, tempFile, fileName, false);
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
