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
package org.linagora.linshare.webservice.uploadrequestv4.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.objects.ChunkedFile;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.FlowDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.uploadrequestv4.FlowUploaderRestService;
import org.linagora.linshare.webservice.utils.FlowUploaderUtils;
import org.linagora.linshare.webservice.utils.WebServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;


@Path("/flow/upload")
@Produces({ "application/json", "application/xml" })
public class FlowUploaderRestServiceImpl extends WebserviceBase implements
		FlowUploaderRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(FlowUploaderRestServiceImpl.class);

	private static final String CHUNK_NUMBER = "flowChunkNumber";
	private static final String TOTAL_CHUNKS = "flowTotalChunks";
	private static final String CHUNK_SIZE = "flowChunkSize";
	private static final String CURRENT_CHUNK_SIZE = "flowCurrentChunkSize";
	private static final String TOTAL_SIZE = "flowTotalSize";
	private static final String IDENTIFIER = "flowIdentifier";
	private static final String FILENAME = "flowFilename";
	private static final String RELATIVE_PATH = "flowRelativePath";
	private static final String FILE = "file";
	private static final String PASSWORD = "password";
	private static final String REQUEST_URL_UUID = "requestUrlUuid";
	
	private boolean sizeValidation;

	private final UploadRequestUrlFacade uploadRequestUrlFacade;

	private static final ConcurrentMap<String, ChunkedFile> chunkedFiles = Maps
			.newConcurrentMap();

	public FlowUploaderRestServiceImpl(
			UploadRequestUrlFacade uploadRequestUrlFacade,
			boolean sizeValidation) {
		super();
		this.uploadRequestUrlFacade = uploadRequestUrlFacade;
		this.sizeValidation = sizeValidation;
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

		return FlowUploaderUtils.testChunk(chunkNumber, totalChunks, chunkSize, currentChunkSize,
				totalSize, identifier, filename, relativePath, chunkedFiles, false);
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
			@Multipart(REQUEST_URL_UUID) String uploadRequestUrlUuid,
			@Multipart(PASSWORD) String password) throws BusinessException {

		logger.debug("upload chunk number : " + chunkNumber);
		identifier = FlowUploaderUtils.cleanIdentifier(identifier);
		boolean isValid = FlowUploaderUtils.isValid(chunkNumber, chunkSize, totalSize, currentChunkSize, identifier, filename, totalChunks);
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
			java.nio.file.Path tempFile = getTempFile(identifier);
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
				logger.error("currentChunkedFile.hasChunk(chunkNumber) !!! {} ", currentChunkedFile);
				logger.error("chunkedNumber skipped : {} ", chunkNumber);
			}
			logger.debug("number of uploading files : {}", chunkedFiles.size());
			logger.debug("current chuckedfile uuid : {}", identifier);
			logger.debug("current chuckedfiles {}", chunkedFiles.toString());
			if (FlowUploaderUtils.isUploadFinished(identifier, chunkSize, totalSize, chunkedFiles, totalChunks)) {
				logger.debug("upload finished ");
				flow.setLastChunk(true);
				try {
					uploadRequestUrlFacade.addUploadRequestEntry(uploadRequestUrlUuid, password, tempFile.toFile(), filename);
				} finally {
					WebServiceUtils.deleteTempFile(tempFile.toFile());
				}
				ChunkedFile remove = chunkedFiles.remove(identifier);
				if (remove != null) {
					Files.deleteIfExists(remove.getPath());
				} else {
					logger.error("Should not happen !!!");
					logger.error("chunk number: {}", chunkNumber);
					logger.error("chunk identifier: {}", identifier);
					logger.error("chunk filename: {}", filename);
				}
				flow.setChunkUploadSuccess(true);
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

	private java.nio.file.Path getTempFile(String identifier)
			throws IOException {
		ChunkedFile chunkedFile = chunkedFiles.get(identifier);
		if (chunkedFile == null) {
			java.nio.file.Path path = Files.createTempFile("linshare-chunks-"
					+ identifier, ".temp");
			chunkedFiles.putIfAbsent(identifier, new ChunkedFile(path));
			chunkedFile = chunkedFiles.get(identifier);
		}
		return chunkedFile.getPath();
	}

}
