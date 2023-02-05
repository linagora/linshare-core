/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class ThumbnailGeneratorRemoteBusinessServiceImpl implements ThumbnailGeneratorBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(ThumbnailGeneratorRemoteBusinessServiceImpl.class);

	protected FileDataStore fileDataStore;

	protected final boolean thumbEnabled;

	protected final boolean pdfThumbEnabled;

	protected final String thumbnailWebServiceUrl;

	public ThumbnailGeneratorRemoteBusinessServiceImpl(FileDataStore fileDataStore,
			boolean thumbEnabled,
			boolean pdfThumbEnabled,
			String thumbnailWebServiceUrl) {
		this.fileDataStore = fileDataStore;
		this.thumbEnabled = thumbEnabled;
		this.pdfThumbEnabled = pdfThumbEnabled;
		this.thumbnailWebServiceUrl = thumbnailWebServiceUrl;
	}

	@Override
	public Map<ThumbnailType, FileMetaData> getThumbnails(Account owner, File myFile, FileMetaData metadata,
			String mimeType) {
		if (!isSupportedMimetype(mimeType)) {
			logger.warn("Thumbnail generation is disabled.");
			return Maps.newHashMap();
		}
		return computeAndStoreThumbnail(owner, metadata, mimeType, myFile);
	}

	@Override
	public boolean isSupportedMimetype(String mimeType) {
		if (!thumbEnabled) {
			return false;
		}
		if (!pdfThumbEnabled && mimeType.contains("pdf")) {
			return false;
		}
		try {
			final Response isSupported = ClientBuilder.newClient()
					.target(getLinthumbnailWebServiceLink(mimeType)).request().get(Response.class);
			if (isSupported.getStatusInfo().getStatusCode() == 204) {
				return true;
			}
		} catch (Exception ex) {
			logger.warn("Connexion failure to the server ! ", ex);
		}
		return false;
	}

	protected Map<ThumbnailType, FileMetaData> computeAndStoreThumbnail(Account owner, FileMetaData metadata,
			String mimeType, File myFile) {
		Map<ThumbnailType, FileMetaData> thumbnailMap = Maps.newHashMap();
		try {
			WebClient client = WebClient.create(getLinthumbnailWebServiceLink(mimeType));
			client.type(MediaType.MULTIPART_FORM_DATA);
			client.accept("multipart/mixed");
			try (InputStream stream = FileUtils.openInputStream(myFile);) {
				ContentDisposition cd = new ContentDisposition("form-data; name=file; filename=document");
				Attachment attFile = new Attachment("file", stream, cd);
				MultipartBody body = new MultipartBody(attFile);
				Response response = client.post(body);
				if (response.getStatusInfo().getStatusCode() == 200) {
					MultipartBody mb = response.readEntity(MultipartBody.class);
					List<Attachment> allAttachments = mb.getAllAttachments();
					thumbnailMap = getThumbnailFileMetadata(allAttachments, metadata);
				} else {
					logger.debug("Linthumbnail Web service error : " + response.getStatusInfo().getStatusCode());
				}
			} catch (IOException e) {
				logger.error("Failled to get the document ", e);
			}
		} catch (Exception e) {
			logger.error("Connexion failure to the server ! ", e);
			logger.debug(e.getMessage(), e);
		}
		return thumbnailMap;
	}

	protected Map<ThumbnailType, FileMetaData> getThumbnailFileMetadata(List<Attachment> allAttachments,
			FileMetaData metadata) {
		Map<ThumbnailType, FileMetaData> thumbnailMap = Maps.newHashMap();
		FileMetaData metadataThumb = null;
		for (Attachment attachment : allAttachments) {
			File tempThumbFile = null;
			try {
				String fileName = attachment.getContentDisposition().getFilename();
				tempThumbFile = File.createTempFile("linthumbnail", fileName);
				tempThumbFile.createNewFile();
				DataHandler dataHandler = attachment.getDataHandler();
				try (FileOutputStream fos = new FileOutputStream(tempThumbFile);) {
					IOUtils.copy(dataHandler.getInputStream(), fos);
				}
				metadataThumb = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png", tempThumbFile.length(),
						metadata.getFileName());
				metadataThumb = fileDataStore.add(Files.asByteSource(tempThumbFile), metadataThumb);
				ThumbnailType thumbnailType = ThumbnailType.getThumbnailType(fileName);
				if (thumbnailType != null && metadataThumb != null) {
					thumbnailMap.put(thumbnailType, metadataThumb);
				} else {
					// We abort all the process of generation of thumbnails, if
					// one thumbnail has failed
					cleanMap(thumbnailMap);
					logger.error("failed to generate the thumbnail, we aborted all thumbnails generation process ");
					return thumbnailMap;
				}
			} catch (IOException e) {
				logger.error("Failed to generate the thumbnail", e);
				logger.debug(e.getMessage(), e);
				cleanMap(thumbnailMap);
				return thumbnailMap;
			} finally {
				if (tempThumbFile != null) {
					tempThumbFile.delete();
				}
			}
		}
		return thumbnailMap;
	}

	protected String getLinthumbnailWebServiceLink(String token) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format(thumbnailWebServiceUrl, token);
		formatter.close();
		return sb.toString();
	}

	protected void cleanMap(Map<ThumbnailType, FileMetaData> thumbnailMap) {
		if (!thumbnailMap.isEmpty()) {
			thumbnailMap.forEach((thumbnailType, metadata) -> {
				if (metadata != null) {
					fileDataStore.remove(metadata);
				}
			});
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
