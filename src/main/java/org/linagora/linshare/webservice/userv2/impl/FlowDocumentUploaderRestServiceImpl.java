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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.objects.ChunkedFile;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.userv2.FlowDocumentUploaderRestService;
import org.linagora.linshare.webservice.utils.FlowUploaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;

@Path("/flow")
@Api(value = "/rest/user/flow", basePath = "/rest/user/", description = "Flow Upload Documents service", produces = "application/json,application/xml", consumes = "application/json,application/xml")
public class FlowDocumentUploaderRestServiceImpl extends WebserviceBase
		implements FlowDocumentUploaderRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(FlowDocumentUploaderRestService.class);

	private static final String CHUNK_NUMBER = "flowChunkNumber";
	private static final String TOTAL_CHUNKS = "flowTotalChunks";
	private static final String CHUNK_SIZE = "flowChunkSize";
	private static final String TOTAL_SIZE = "flowTotalSize";
	private static final String IDENTIFIER = "flowIdentifier";
	private static final String FILENAME = "flowFilename";
	private static final String RELATIVE_PATH = "flowRelativePath";
	private static final String FILE = "file";

	private final DocumentFacade documentFacade;

	private static final ConcurrentMap<String, ChunkedFile> chunkedFiles = Maps
			.newConcurrentMap();

	public FlowDocumentUploaderRestServiceImpl(DocumentFacade documentFacade) {
		super();
		this.documentFacade = documentFacade;
	}

	@Path("/")
	@POST
	@Consumes("multipart/form-data")
	@Override
	public Response uploadChunk(@Multipart(CHUNK_NUMBER) long chunkNumber,
			@Multipart(TOTAL_CHUNKS) long totalChunks,
			@Multipart(CHUNK_SIZE) long chunkSize,
			@Multipart(TOTAL_SIZE) long totalSize,
			@Multipart(IDENTIFIER) String identifier,
			@Multipart(FILENAME) String filename,
			@Multipart(RELATIVE_PATH) String relativePath,
			@Multipart(FILE) InputStream file, MultipartBody body)
					throws BusinessException {
		logger.debug("upload chunk number : " + chunkNumber);
		identifier = cleanIdentifier(identifier);
		boolean isValid = FlowUploaderUtils.isValid(chunkNumber, chunkSize,
				totalSize, identifier, filename);
		Validate.isTrue(isValid);
		try {
			logger.debug("writing chunk number : " + chunkNumber);
			java.nio.file.Path tempFile = FlowUploaderUtils
					.getTempFile(identifier, chunkedFiles);
			FileChannel fc = FileChannel.open(tempFile,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			byte[] byteArray = IOUtils.toByteArray(file);
			fc.write(ByteBuffer.wrap(byteArray), (chunkNumber - 1) * chunkSize);
			fc.close();

			chunkedFiles.get(identifier).addChunk(chunkNumber);
			if (FlowUploaderUtils.isUploadFinished(identifier, chunkSize,
					totalSize, chunkedFiles)) {
				logger.debug("upload finished ");
				InputStream inputStream = Files.newInputStream(tempFile,
						StandardOpenOption.READ);

				documentFacade.create(inputStream, filename, "", null);

				ChunkedFile remove = chunkedFiles.remove(identifier);
				Files.deleteIfExists(remove.getPath());
				return Response.ok("upload success").build();
			} else {
				logger.debug("upload pending ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.ok("upload success").build();
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
		return FlowUploaderUtils.testChunk(chunkNumber, totalChunks, chunkSize,
				totalSize, identifier, filename, relativePath, chunkedFiles);
	}

	/**
	 * HELPERS
	 */

	private String cleanIdentifier(String identifier) {
		return identifier.replaceAll("[^0-9A-Za-z_-]", "");
	}

}
