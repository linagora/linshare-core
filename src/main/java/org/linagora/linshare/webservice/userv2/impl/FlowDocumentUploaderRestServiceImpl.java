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
import org.apache.commons.lang3.Validate;
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
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupNodeFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv1.task.DocumentUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.WorkGroupEntryUploadAsyncTask;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;
import org.linagora.linshare.webservice.userv2.FlowDocumentUploaderRestService;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.linagora.linshare.webservice.utils.FlowUploaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Parameter;


@Path("/flow")
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

	private final WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade ;

	private final AsyncTaskFacade asyncTaskFacade;
	
	private final WorkGroupNodeFacade workGroupNodeFacade;

	private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

	public FlowDocumentUploaderRestServiceImpl(
			DocumentFacade documentFacade,
			AccountQuotaFacade accountQuotaFacade,
			DocumentAsyncFacade documentAsyncFacade,
			WorkGroupEntryAsyncFacade workGroupEntryAsyncFacade,
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
		this.workGroupEntryAsyncFacade = workGroupEntryAsyncFacade;
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
		boolean isValid = FlowUploaderUtils.isValid(chunkNumber, chunkSize,	totalSize, currentChunkSize, identifier, filename, totalChunks);
		checkIfMaintenanceIsEnabled();
		FlowDto flow = new FlowDto(chunkNumber);
		if (!isValid) {
			String msg = String.format(
					"One parameter's value among multipart parameters is set to '0'. It should not: chunkNumber: %1$d | chunkSize: %2$d | totalSize: %3$d | identifier length: %4$d | filename length: %5$d | totalChunks: %6$d",
					chunkNumber, chunkSize, totalSize, identifier.length(), filename.length(), totalChunks);
			logger.error(msg);
			flow.setChunkUploadSuccess(false);
			flow.setErrorMessage(msg);
			return flow;
		}
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
			if (FlowUploaderUtils.isUploadFinished(identifier, chunkSize, totalSize, chunkedFiles, totalChunks)) {
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
					AccountDto authUserDto = documentFacade.getAuthenticatedAccountDto();
					AsyncTaskDto asyncTask = null;
					try {
						if(isWorkGroup) {
							WorkGroupEntryTaskContext workGroupEntryTaskContext = new WorkGroupEntryTaskContext(authUserDto, authUserDto.getUuid(), workGroupUuid, tempFile.toFile(), filename, workGroupParentNodeUuid, false);
							asyncTask = asyncTaskFacade.create(totalSize, getTransfertDuration(identifier), filename, null, AsyncTaskType.THREAD_ENTRY_UPLOAD);
							WorkGroupEntryUploadAsyncTask task = new WorkGroupEntryUploadAsyncTask(workGroupEntryAsyncFacade, workGroupEntryTaskContext, asyncTask);
							taskExecutor.execute(task);
							flow.completeAsyncTransfert(asyncTask);
						} else {
							DocumentTaskContext documentTaskContext = new DocumentTaskContext(authUserDto, authUserDto.getUuid(), tempFile.toFile(), filename, null, null);
							asyncTask = asyncTaskFacade.create(totalSize, getTransfertDuration(identifier), filename, null, AsyncTaskType.DOCUMENT_UPLOAD);
							DocumentUploadAsyncTask task = new DocumentUploadAsyncTask(documentAsyncFacade, documentTaskContext, asyncTask);
							taskExecutor.execute(task);
							flow.completeAsyncTransfert(asyncTask);
						}
					} catch (Exception e) {
						logAsyncFailure(asyncTask, e);
						WebServiceUtils.deleteTempFile(tempFile.toFile());
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
						WebServiceUtils.deleteTempFile(tempFile.toFile());
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
			@QueryParam(CURRENT_CHUNK_SIZE) long currentChunkSize,
			@QueryParam(TOTAL_SIZE) long totalSize,
			@QueryParam(IDENTIFIER) String identifier,
			@QueryParam(FILENAME) String filename,
			@QueryParam(RELATIVE_PATH) String relativePath) {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		Response testChunk = FlowUploaderUtils.testChunk(chunkNumber, totalChunks, chunkSize, currentChunkSize,
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
			@Parameter(description = "Get the async task created at the end of an upload.", required = true) @PathParam("uuid") String uuid) throws BusinessException {
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
			logger.warn("Maintenance mode is enabled for this user. Uploads are disabled.");
			 // Http error 501
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enabled for this user. Uploads are disabled.");
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
