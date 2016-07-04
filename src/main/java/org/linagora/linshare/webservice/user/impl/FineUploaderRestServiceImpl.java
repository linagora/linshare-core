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
package org.linagora.linshare.webservice.user.impl;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.FineUploaderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ThreadEntryDto;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.user.FineUploaderRestService;
import org.linagora.linshare.webservice.user.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.user.task.ThreadEntryUploadAsyncTask;
import org.linagora.linshare.webservice.user.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.user.task.context.ThreadEntryTaskContext;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/upload")
@Api(value = "/fineuploader/upload", description = "Upload service used by Fine Uploader.")
@Produces({"application/json", "application/xml"})
public class FineUploaderRestServiceImpl extends WebserviceBase implements
		FineUploaderRestService {

	private static final String FILE = "qqfile";
	private static final String FILE_NAME = "filename";
	private static final String FILE_SIZE = "qqtotalfilesize";
	private static final String FILE_START_UPLOAD_TIME = "startdate";

	private final DocumentFacade documentFacade;

	private final DocumentAsyncFacade documentAsyncFacade;

	private final ThreadEntryFacade threadEntryFacade;

	private final ThreadEntryAsyncFacade threadEntryAsyncFacade;

	private final AsyncTaskFacade asyncTaskFacade;

	private Boolean enableAsyncMode;

	private Integer defaultThreshold;

	private Integer defaultFrequency;

	private Integer maxThreshold;

	private Integer maxFrequency;

	private boolean sizeValidation;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	public FineUploaderRestServiceImpl(
			DocumentFacade documentFacade,
			DocumentAsyncFacade documentAsyncFacade,
			org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor,
			ThreadEntryFacade threadEntryFacade,
			ThreadEntryAsyncFacade threadEntryAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			Boolean enableAsyncMode,
			Integer defaultThreshold,
			Integer defaultFrequency,
			Integer maxThreshold,
			Integer maxFrequency,
			boolean sizeValidation
			) {
		super();
		this.documentFacade = documentFacade;
		this.documentAsyncFacade = documentAsyncFacade;
		this.threadEntryFacade = threadEntryFacade;
		this.threadEntryAsyncFacade = threadEntryAsyncFacade;
		this.taskExecutor = taskExecutor;
		this.enableAsyncMode = enableAsyncMode;
		this.asyncTaskFacade = asyncTaskFacade;
		this.defaultThreshold = defaultThreshold;
		this.defaultFrequency = defaultFrequency;
		this.maxThreshold = maxThreshold;
		this.maxFrequency = maxFrequency;
		this.sizeValidation = sizeValidation;
	}

	@GET
	@Path("/receiver/{uuid}")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Response status(@PathParam("uuid") String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing uuid");
		AsyncTaskDto dto = asyncTaskFacade.find(uuid);
		// Fixing IE cache issue.
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		ResponseBuilder builder = Response.ok(dto);
		builder.cacheControl(cc);
		return builder.build();
	}

	@Path("/receiver")
	@ApiOperation(value = "Upload a file in the user space.", response = FineUploaderDto.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public FineUploaderDto upload(
			@ApiParam(value = "File stream", required = true) @Multipart(value = FILE) InputStream file,
			@ApiParam(value = "File size ", required = true) @Multipart(value = FILE_SIZE, required = false) Long size,
			@ApiParam(value = "File name") @Multipart(value = FILE_NAME, required = false) String fileName,
			MultipartBody body) throws BusinessException {
		Long transfertDuration = getTransfertDuration();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		// Ensure fileName and description aren't null
		if (fileName == null || fileName.isEmpty()) {
			fileName = body.getAttachment(FILE).getContentDisposition()
					.getParameter(FILE_NAME);
		}
		if (fileName == null) {
			logger.error("There is no multi-part attachment named '"+ FILE_NAME + "'.");
			logger.error("There is no '"+ FILE_NAME + "' header in multi-Part attachment named '" + FILE + "'.");
			Validate.notNull(fileName, "File name for file attachment is required.");
		}
		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
		}
		File tempFile = null;
		try {
			tempFile = getTempFile(file, "fineuploader", fileName);
		} catch (BusinessException e) {
			return processException(e, tempFile, null);
		}
		if (sizeValidation) {
			if (size != null) {
				long length = tempFile.length();
				if (length != size) {
					String msg = String.format("File size does not match, found : %1$d, announced : %2$d", length, size);
					logger.error(msg);
					throw giveRestException(HttpStatus.SC_BAD_REQUEST, msg);
				}
			}
		}
		boolean async= false;
		int frequency = defaultFrequency;
		if (enableAsyncMode) {
			logger.debug("Async mode is available");
			if (size == null) {
				// Hook for ie 9 because size is not  available.
				async = true;
			} else {
				if (size > defaultThreshold) {
					async = true;
					if (size > maxThreshold) {
						frequency = maxFrequency;
					}
				}
			}
		}
		if (async) {
			logger.debug("Async mode is used");
			logger.debug("Async mode frequency is " + frequency);
			AccountDto actorDto = documentFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				DocumentTaskContext documentTaskContext = new DocumentTaskContext(actorDto, actorDto.getUuid(), tempFile, fileName);
				asyncTask = asyncTaskFacade.create(size, transfertDuration, fileName, frequency, AsyncTaskType.DOCUMENT_UPLOAD);
				DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext, asyncTask);
				taskExecutor.execute(task);
				return new FineUploaderDto(asyncTask);
			} catch (Exception e) {
				return processException(e, tempFile, asyncTask);
			}
		} else {
			logger.debug("Async mode is not used");
			try {
				DocumentDto doc = documentFacade.create(tempFile, fileName, "", null);
				FineUploaderDto dto = new FineUploaderDto(true, doc.getUuid(), doc.getName());
				return dto;
			} catch (Exception e) {
				return processException(e, tempFile, null);
			}
		}
	}

	@Path("/receiver/{uuid}")
	@ApiOperation(value = "Remove a previously uploaded file by uuid", response = FineUploaderDto.class)
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public FineUploaderDto delete(
			@ApiParam(value = "File uuid", required = true)
			@PathParam("uuid") String uuid)
			throws BusinessException {
		if (uuid == null || uuid.isEmpty()) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		try {
			DocumentDto dto = documentFacade.delete(uuid);
			return new FineUploaderDto(true,dto.getName());
		} catch (BusinessException e) {
			return new FineUploaderDto(e);
		}
	}

	@Path("/threadentry/{threadUuid}")
	@ApiOperation(value = "Upload a file in a thread by uuid.", response = FineUploaderDto.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public FineUploaderDto uploadThreadEntry(
			@ApiParam(value = "Thread uuid", required = true) @PathParam("threadUuid") String threadUuid,
			@ApiParam(value = FILE, required = true) @Multipart(value = FILE) InputStream file,
			@ApiParam(value = "File size ", required = false) @Multipart(value = FILE_SIZE, required = false) Long size,
			@ApiParam(value = FILE_NAME) @Multipart(value = FILE_NAME, required = false) String fileName,
			@ApiParam(value = "Just the upload start time in second, it is used for statistics purpose only.")
				@Multipart(value = FILE_START_UPLOAD_TIME, required = false) Long uploadStartTime,
			MultipartBody body) throws BusinessException {
		Long transfertDuration = getTransfertDuration();
		if (file == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		if (threadUuid == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing thread Uuid(check parameter threadUuid)");
		}
		// Ensure fileName and description aren't null
		if (fileName == null || fileName.isEmpty()) {
			fileName = body.getAttachment(FILE).getContentDisposition()
					.getParameter(FILE_NAME);
		}
		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
		}
		File tempFile = null;
		try {
			tempFile = getTempFile(file, "fineuploader", fileName);
		} catch (BusinessException e) {
			return processException(e, tempFile, null);
		}
		if (sizeValidation) {
			if (size != null) {
				long length = tempFile.length();
				if (length != size) {
					String msg = String.format("File size does not match, found : %1$d, announced : %2$d", length, size);
					logger.error(msg);
					throw giveRestException(HttpStatus.SC_BAD_REQUEST, msg);
				}
			}
		}
		boolean async= false;
		int frequency = defaultFrequency;
		if (enableAsyncMode) {
			logger.debug("Async mode is available");
			if (size == null) {
				// Hook for ie 9 because size is not  available.
				async = true;
			} else {
				if (size > defaultThreshold) {
					async = true;
					if (size > maxThreshold) {
						frequency = maxFrequency;
					}
				}
			}
		}
		if (async) {
			logger.debug("Async mode is used");
			logger.debug("Async mode frequency is " + frequency);
			AccountDto actorDto = documentFacade.getAuthenticatedAccountDto();
			AsyncTaskDto asyncTask = null;
			try {
				asyncTask = asyncTaskFacade.create(size, transfertDuration, fileName, frequency, AsyncTaskType.THREAD_ENTRY_UPLOAD);
				ThreadEntryTaskContext threadEntryTaskContext = new ThreadEntryTaskContext(actorDto, actorDto.getUuid(), threadUuid, tempFile, fileName);
				ThreadEntryUploadAsyncTask task = new ThreadEntryUploadAsyncTask(threadEntryAsyncFacade, threadEntryTaskContext, asyncTask);
				taskExecutor.execute(task);
				return new FineUploaderDto(asyncTask);
			} catch (Exception e) {
				return processException(e, tempFile, asyncTask);
			}
		} else {
			logger.debug("Async mode is not used");
			try {
				ThreadEntryDto doc = threadEntryFacade.create(threadUuid, tempFile, fileName);
				FineUploaderDto dto = new FineUploaderDto(true, doc.getUuid(), doc.getName());
				return dto;
			} catch (Exception e) {
				return processException(e, tempFile, null);
			}
		}
	}

	private FineUploaderDto processException(Exception e,
			File tempFile, AsyncTaskDto asyncTask) {
		deleteTempFile(tempFile);
		if (asyncTask != null) {
			asyncTaskFacade.fail(asyncTask, e);
		}
		logger.error(e.getMessage());
		logger.debug("Exception : ", e);
		return new FineUploaderDto(e);
	}

	@Override
	public void destroy() {
		logger.info("Destroying fineUploaderRestService");
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		// It seems spring shutdown taskExecutor itself.
		//		taskExecutor.shutdown();
	}
}
