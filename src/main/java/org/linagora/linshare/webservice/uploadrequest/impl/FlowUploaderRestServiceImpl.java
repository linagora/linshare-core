/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.webservice.uploadrequest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.uploadrequest.FlowUploaderRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;

@Path("/flow/upload")
@Api(value = "/rest/uploadrequest/flow/upload", description = "upload_requests API")
@Produces({ "application/json", "application/xml" })
public class FlowUploaderRestServiceImpl extends WebserviceBase implements
		FlowUploaderRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(FlowUploaderRestServiceImpl.class);

	private static final String CHUNK_NUMBER = "flowChunkNumber";
	private static final String TOTAL_CHUNKS = "flowTotalChunks";
	private static final String CHUNK_SIZE = "flowChunkSize";
	private static final String TOTAL_SIZE = "flowTotalSize";
	private static final String IDENTIFIER = "flowIdentifier";
	private static final String FILENAME = "flowFilename";
	private static final String RELATIVE_PATH = "flowRelativePath";
	private static final String FILE = "file";

	private final DocumentFacade documentFacade;
	
	private static final Map<String, ArrayList<Integer>> chunks = Maps.newHashMap();

	public FlowUploaderRestServiceImpl(DocumentFacade documentFacade) {
		super();
		this.documentFacade = documentFacade;
	}

	@Path("/")
	@GET
	@Override
	public Response testChumk(@QueryParam(CHUNK_NUMBER) long chunkNumber,
			@QueryParam(TOTAL_CHUNKS) long totalChunks,
			@QueryParam(CHUNK_SIZE) long chunkSize,
			@QueryParam(TOTAL_SIZE) long totalSize,
			@QueryParam(IDENTIFIER) String identifier,
			@QueryParam(FILENAME) String filename,
			@QueryParam(RELATIVE_PATH) String relativePath) {

		identifier = cleanIdentifier(identifier);
		Validate.isTrue(isValid(chunkNumber, chunkSize, totalSize, identifier,
				filename));

		if (chunks.get(identifier).contains(chunkNumber)) {
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@Path("/")
	@POST
	@Consumes("multipart/form-data")
	@Override
	public void uploadChunk(@Multipart(CHUNK_NUMBER) long chunkNumber,
			@Multipart(TOTAL_CHUNKS) long totalChunks,
			@Multipart(CHUNK_SIZE) long chunkSize,
			@Multipart(TOTAL_SIZE) long totalSize,
			@Multipart(IDENTIFIER) String identifier,
			@Multipart(FILENAME) String filename,
			@Multipart(RELATIVE_PATH) String relativePath,
			@Multipart(FILE) InputStream file, MultipartBody body)
			throws BusinessException {

		identifier = cleanIdentifier(identifier);
		Validate.isTrue(isValid(chunkNumber, chunkSize, totalSize, identifier,
				filename));

		
		try {
			java.nio.file.Path tempFile = getTempFile(identifier);
			FileChannel fc = FileChannel.open(tempFile, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
			byte[] byteArray = IOUtils.toByteArray(file);
			fc.write(ByteBuffer.wrap(byteArray), (chunkNumber - 1) * chunkSize);
			//Files.write(tempFile, byteArray, StandardOpenOption.CREATE,
			//		StandardOpenOption.APPEND);
			if (isUploadFinished(chunkSize, totalSize)) {
				documentFacade
						.uploadfile(Files.newInputStream(tempFile,
								StandardOpenOption.READ), filename, "");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private java.nio.file.Path getTempFile(String identifier)
			throws IOException {
		java.nio.file.Path tempFile = Files.createTempFile("ls-chunks-" + identifier, ".temp");
		return tempFile;
	}

	/**
	 * HELPERS
	 */

	private String cleanIdentifier(String identifier) {
		return identifier.replaceAll("[^0-9A-Za-z_-]", "");
	}

	private boolean isValid(long chunkNumber, long chunkSize, long totalSize,
			String identifier, String filename) {
		// Check if the request is sane
		if (chunkNumber == 0 || chunkSize == 0 || totalSize == 0
				|| identifier.length() == 0 || filename.length() == 0) {
			return false;
		}
		double numberOfChunks = computeNumberOfChunks(chunkSize, totalSize);
		if (chunkNumber > numberOfChunks) {
			return false;
		}
		return true;
	}

	private boolean isValid(int chunkNumber, int chunkSize, int totalSize,
			String identifier, String filename, int fileSize) {
		if (!isValid(chunkNumber, chunkSize, totalSize, identifier, filename)) {
			return false;
		}
		double numberOfChunks = computeNumberOfChunks(chunkSize, totalSize);
		if (chunkNumber > numberOfChunks) {
			return false;
		}
		if (chunkNumber < numberOfChunks && fileSize != chunkSize) {
			// The chunk in the POST request isn't the correct size
			return false;
		}
		if (numberOfChunks > 1 && chunkNumber == numberOfChunks
				&& fileSize != ((totalSize % chunkSize) + chunkSize)) {
			// The chunks in the POST is the last one, and the fil is not the
			// correct size
			return false;
		}
		if (numberOfChunks == 1 && fileSize != totalSize) {
			// The file is only a single chunk, and the data size does not fit
			return false;
		}
		return true;
	}

	private double computeNumberOfChunks(long chunkSize, long totalSize) {
		return Math.max(Math.floor(totalSize / chunkSize), 1);
	}

	private boolean isUploadFinished(long chunkSize, long totalSize) {
		double numberOfChunks = computeNumberOfChunks(chunkSize, totalSize);

		// check if all parts are present
		if (numberOfChunks * chunkSize > (totalSize - chunkSize + 1)) {
			return true;
		}
		return false;
	}
}
