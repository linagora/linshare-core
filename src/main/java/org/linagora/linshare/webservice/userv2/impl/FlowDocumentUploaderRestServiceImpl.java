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
package org.linagora.linshare.webservice.userv2.impl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;
import org.linagora.linshare.core.domain.objects.ChunkedFile;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.common.dto.FlowDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv1.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.ThreadEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.userv1.task.context.ThreadEntryTaskContext;
import org.linagora.linshare.webservice.userv2.FlowDocumentUploaderRestService;
import org.linagora.linshare.webservice.utils.FlowUploaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/flow")
@Api(value = "/rest/user/v2/flow", basePath = "/rest/user/v2/", description = "Flow Upload Documents service", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class FlowDocumentUploaderRestServiceImpl extends WebserviceBase
		implements FlowDocumentUploaderRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(FlowDocumentUploaderRestService.class);

	private static final String CHUNK_NUMBER = "flowChunkNumber";
	private static final String TOTAL_CHUNKS = "flowTotalChunks";
	private static final String CHUNK_SIZE = "flowChunkSize";
	private static final String CURRENT_CHUNK_SIZE = "flowCurrentChunkSize";
	private static final String TOTAL_SIZE = "flowTotalSize";
	private static final String IDENTIFIER = "flowIdentifier";
	private static final String FILENAME = "flowFilename";
	private static final String RELATIVE_PATH = "flowRelativePath";
	private static final String FILE = "file";
	private static final String WORK_GROUP_UUID = "workGroupUuid";
	private static final String WORK_GROUP_PARENT_NODE_UUID = "workGroupParentNodeUuid";
	private static final String ASYNC_TASK = "asyncTask";

	private boolean sizeValidation;

	private final DocumentFacade documentFacade;

	private final AccountQuotaFacade accountQuotaFacade;

	private static final ConcurrentMap<String, ChunkedFile> chunkedFiles = Maps
			.newConcurrentMap();

	private final DocumentAsyncFacade documentAsyncFacade;

	private final ThreadEntryAsyncFacade threadEntryAsyncFacade ;

	private final AsyncTaskFacade asyncTaskFacade;
	
	private final WorkGroupNodeFacade workGroupNodeFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	public FlowDocumentUploaderRestServiceImpl(
			DocumentFacade documentFacade,
			AccountQuotaFacade accountQuotaFacade,
			DocumentAsyncFacade documentAsyncFacade,
			ThreadEntryAsyncFacade threadEntryAsyncFacade,
			AsyncTaskFacade asyncTaskFacade,
			WorkGroupNodeFacade workGroupNodeFacade,
			org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor,
			boolean sizeValidation) {
		super();
		this.documentFacade = documentFacade;
		this.sizeValidation = sizeValidation;
		this.accountQuotaFacade = accountQuotaFacade;
		this.documentAsyncFacade = documentAsyncFacade;
		this.workGroupNodeFacade = workGroupNodeFacade;
		this.threadEntryAsyncFacade = threadEntryAsyncFacade;
		this.asyncTaskFacade = asyncTaskFacade;
		this.taskExecutor = taskExecutor;
	}

	@Path("/")
	@POST
	@Consumes("multipart/form-data")
	@Override
	public FlowDto uploadChunk(@Multipart(CHUNK_NUMBER) long chunkNumber,
			@Multipart(TOTAL_CHUNKS) long totalChunks,
			@Multipart(CHUNK_SIZE) long chunkSize,
			@Multipart(CURRENT_CHUNK_SIZE) long currentChunkSize,
			@Multipart(TOTAL_SIZE) long totalSize,
			@Multipart(IDENTIFIER) String identifier,
			@Multipart(FILENAME) String filename,
			@Multipart(RELATIVE_PATH) String relativePath,
			@Multipart(FILE) InputStream inputStreamCxf, MultipartBody body,
			@Multipart(value=WORK_GROUP_UUID, required=false) String workGroupUuid,
			@Multipart(value=WORK_GROUP_PARENT_NODE_UUID, required=false) String workGroupParentNodeUuid,
			@Multipart(value=ASYNC_TASK, required=false) boolean async)
					throws BusinessException {
		logger.debug("upload chunk number : " + chunkNumber);
		identifier = cleanIdentifier(identifier);
		boolean isValid = FlowUploaderUtils.isValid(chunkNumber, chunkSize,
				totalSize, identifier, filename);
		Validate.isTrue(isValid);
		checkIfMaintenanceIsEnabled();
		FlowDto flow = new FlowDto(chunkNumber);
		try {
			logger.debug("writing chunk number : " + chunkNumber);
			java.nio.file.Path tempFile = FlowUploaderUtils.getTempFile(identifier, chunkedFiles);
			ChunkedFile currentChunkedFile = chunkedFiles.get(identifier);
			if (!currentChunkedFile.hasChunk(chunkNumber)) {
				FileChannel fc = FileChannel.open(tempFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				IOUtils.copy(inputStreamCxf, output);
				fc.write(ByteBuffer.wrap(output.toByteArray()), (chunkNumber - 1) * chunkSize);
				fc.close();
				inputStreamCxf.close();
				if (sizeValidation) {
					if (output.size() != currentChunkSize) {
						String msg = String.format("File size does not match, found : %1$d, announced : %2$d", output.size(), currentChunkSize);
						logger.error(msg);
						flow.setChunkUploadSuccess(false);
						flow.setErrorMessage(msg);
						return flow;
					}
				}
				currentChunkedFile.addChunk(chunkNumber);
			} else {
				logger.error("currentChunkedFile.hasChunk(chunkNumber) !!! " + currentChunkedFile);
				logger.error("chunkedNumber skipped : " + chunkNumber);
			}

			logger.debug("nb uploading files : " + chunkedFiles.size());
			logger.debug("current chuckedfile uuid : " + identifier);
			logger.debug("current chuckedfiles" + chunkedFiles.toString());
			if (FlowUploaderUtils.isUploadFinished(identifier, chunkSize, totalSize, chunkedFiles)) {
				flow.setLastChunk(true);
				logger.debug("upload finished : " + chunkNumber + " : " + identifier);
				if (sizeValidation) {
					long currSize = tempFile.toFile().length();
					if (currSize != totalSize) {
						String msg = String.format("File size does not match, found : %1$d, announced : %2$d", currSize, totalSize);
						logger.error(msg);
						flow.setChunkUploadSuccess(false);
						flow.setErrorMessage(msg);
						return flow;
					}
				}
				EntryDto uploadedDocument = new EntryDto();
				flow.setIsAsync(async);
				boolean isWorkGroup = !Strings.isNullOrEmpty(workGroupUuid);
				if (async) {
					logger.debug("Async mode is used");
					// Asynchronous mode
					AccountDto actorDto = documentFacade.getAuthenticatedAccountDto();
					AsyncTaskDto asyncTask = null;
					try {
						if(isWorkGroup) {
							ThreadEntryTaskContext threadEntryTaskContext = new ThreadEntryTaskContext(actorDto, actorDto.getUuid(), workGroupUuid, tempFile.toFile(), filename, workGroupParentNodeUuid);
							asyncTask = asyncTaskFacade.create(totalSize, getTransfertDuration(identifier), filename, null, AsyncTaskType.THREAD_ENTRY_UPLOAD);
							ThreadEntryUploadAsyncTask task = new ThreadEntryUploadAsyncTask(threadEntryAsyncFacade, threadEntryTaskContext, asyncTask);
							taskExecutor.execute(task);
							flow.completeAsyncTransfert(asyncTask);
						} else {
							DocumentTaskContext documentTaskContext = new DocumentTaskContext(actorDto, actorDto.getUuid(), tempFile.toFile(), filename, null, null);
							asyncTask = asyncTaskFacade.create(totalSize, getTransfertDuration(identifier), filename, null, AsyncTaskType.DOCUMENT_UPLOAD);
							DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext, asyncTask);
							taskExecutor.execute(task);
							flow.completeAsyncTransfert(asyncTask);
						}
					} catch (Exception e) {
						logAsyncFailure(asyncTask, e);
						deleteTempFile(tempFile.toFile());
						ChunkedFile remove = chunkedFiles.remove(identifier);
						Files.deleteIfExists(remove.getPath());
						throw e;
					}
				} else {
					try {
						if(isWorkGroup) {
							workGroupNodeFacade.create(null, workGroupUuid, workGroupParentNodeUuid, tempFile.toFile(), filename, false);
						} else {
							uploadedDocument = documentFacade.create(tempFile.toFile(), filename, "", null);
						}
						flow.completeTransfert(uploadedDocument);
					} finally {
						deleteTempFile(tempFile.toFile());
						ChunkedFile remove = chunkedFiles.remove(identifier);
						if (remove != null) {
							Files.deleteIfExists(remove.getPath());
						} else {
							logger.error("Should not happen !!!");
							logger.error("chunk number: " + chunkNumber);
							logger.error("chunk identifier: " + identifier);
							logger.error("chunk filename: " + filename);
							logger.error("chunks : " + chunkedFiles.toString());
						}
					}
				}
				return flow;
			} else {
				logger.debug("upload pending ");
				flow.setChunkUploadSuccess(true);
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug("Exception : ", e);
			flow.setChunkUploadSuccess(false);
			flow.setErrorMessage(e.getMessage());
			flow.setErrCode(e.getErrorCode().getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug("Exception : ", e);
			flow.setChunkUploadSuccess(false);
			flow.setErrorMessage(e.getMessage());
		}
		return flow;
	}

	@Path("/")
	@GET
	@Override
	public Response testChunk(@QueryParam(CHUNK_NUMBER) long chunkNumber,
			@QueryParam(TOTAL_CHUNKS) long totalChunks,
			@QueryParam(CHUNK_SIZE) long chunkSize,
			@QueryParam(TOTAL_SIZE) long totalSize,
			@QueryParam(IDENTIFIER) String identifier,
			@QueryParam(FILENAME) String filename,
			@QueryParam(RELATIVE_PATH) String relativePath) {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		Response testChunk = FlowUploaderUtils.testChunk(chunkNumber, totalChunks, chunkSize,
				totalSize, identifier, filename, relativePath, chunkedFiles, maintenance);
		if (chunkNumber == 1 || (chunkNumber % 20) == 0 || chunkNumber == totalChunks) {
			logger.info(String.format("GET: .../webservice/rest/user/v2/flow.json:%s: chunkNumber:%s/%s", identifier, chunkNumber, totalChunks));
		}
		return testChunk;
	}

	@Path("/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public AsyncTaskDto findAsync(
			@ApiParam(value = "Get the async task created at the end of an upload.", required = true) @PathParam("uuid") String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing uuid");
		return asyncTaskFacade.find(uuid);
	}

	/**
	 * HELPERS
	 */

	private String cleanIdentifier(String identifier) {
		return identifier.replaceAll("[^0-9A-Za-z_-]", "");
	}

	private long getTransfertDuration(String identifier) {
		Date endDate = new Date();
		long uploadStartTime = chunkedFiles.get(identifier).getStartTime();
		long transfertDuration = endDate.getTime() - uploadStartTime;
		if (logger.isDebugEnabled()) {
			Date beginDate = new Date(uploadStartTime);
			logger.debug("Upload was begining at : " + beginDate);
			logger.debug("Upload was ending at : " + endDate);
		}
		logger.info("statistics:upload time:" + transfertDuration + "ms.");
		return transfertDuration;
	}

	private void checkIfMaintenanceIsEnabled() {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		if (maintenance) {
			 // Http error 501
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable for this user. Uploads are disabled.");
		}
	}

	protected void logAsyncFailure(AsyncTaskDto asyncTask, Exception e) {
		logger.error(e.getMessage());
		logger.debug("Exception : ", e);
		if (asyncTask != null) {
			asyncTaskFacade.fail(asyncTask, e);
		}
	}
}
