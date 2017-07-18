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
package org.linagora.linshare.webservice.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.objects.ChunkedFile;

public class FlowUploaderUtils {

	public static Response testChunk(long chunkNumber, long totalChunks,
			long chunkSize, long totalSize, String identifier, String filename,
			String relativePath,
			ConcurrentMap<String, ChunkedFile> chunkedFiles, boolean maintenance) {
		if (maintenance) {
			return buildReponse(Status.NO_CONTENT); // 204
			// https://github.com/flowjs/flow.js
			// If this request returns a 200, 201 or 202 HTTP code, the chunks is assumed to have been completed.
			// If request returns a permanent error status, upload is stopped.
			// If request returns anything else, the chunk will be uploaded in the standard fashion.
		}
		identifier = cleanIdentifier(identifier);
		boolean isValid = isValid(chunkNumber, chunkSize, totalSize, identifier,
				filename);
		// Throw HTTP 400 error code.
		Validate.isTrue(isValid);
		if (chunkedFiles.containsKey(identifier)
				&& chunkedFiles.get(identifier).hasChunk(chunkNumber)) {
			// HTTP 200 : ok, we already get this chunk.
			return buildReponse(Status.ACCEPTED);
		}
		// HTTP 204 We did not have this chunk
		return buildReponse(Status.NO_CONTENT);
	}

	private static Response buildReponse(Status status) {
		ResponseBuilder builder = Response.status(status);
		// Fixing IE cache issue.
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		builder.cacheControl(cc);
		return builder.build();
	}

	private static String cleanIdentifier(String identifier) {
		return identifier.replaceAll("[^0-9A-Za-z_-]", "");
	}

	public static boolean isValid(long chunkNumber, long chunkSize,
			long totalSize, String identifier, String filename) {
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

	private static double computeNumberOfChunks(long chunkSize,
			long totalSize) {
		return Math.max(Math.floor(totalSize / chunkSize), 1);
	}

	public static boolean isUploadFinished(String identifier, long chunkSize,
			long totalSize, ConcurrentMap<String, ChunkedFile> chunkedFiles) {
		double numberOfChunks = computeNumberOfChunks(chunkSize, totalSize);
		return chunkedFiles.get(identifier).getChunks()
				.size() == numberOfChunks;
	}

	public static java.nio.file.Path getTempFile(String identifier,
			ConcurrentMap<String, ChunkedFile> chunkedFiles)
					throws IOException {
		ChunkedFile chunkedFile = chunkedFiles.get(identifier);
		if (chunkedFile == null) {
			java.nio.file.Path path = Files
					.createTempFile("linshare-chunks-" + identifier, ".temp");
			chunkedFiles.putIfAbsent(identifier, new ChunkedFile(path));
			chunkedFile = chunkedFiles.get(identifier);
		}
		return chunkedFile.getPath();
	}
}
