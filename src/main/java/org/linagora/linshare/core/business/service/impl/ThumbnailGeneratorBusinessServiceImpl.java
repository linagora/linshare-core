/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.util.Map;

import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.ThumbnailService;
import org.linagora.LinThumbnail.utils.ThumbnailKind;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class ThumbnailGeneratorBusinessServiceImpl implements ThumbnailGeneratorBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(ThumbnailGeneratorBusinessServiceImpl.class);

	protected ThumbnailService thumbnailService;

	protected FileDataStore fileDataStore;

	protected final boolean thumbEnabled;

	protected final boolean pdfThumbEnabled;

	public ThumbnailGeneratorBusinessServiceImpl(FileDataStore fileDataStore,
			ThumbnailService thumbnailService,
			boolean thumbEnabled,
			boolean pdfThumbEnabled) {
		this.fileDataStore = fileDataStore;
		this.thumbEnabled = thumbEnabled;
		this.pdfThumbEnabled = pdfThumbEnabled;
		this.thumbnailService = thumbnailService;
	}

	@Override
	public Map<ThumbnailType, FileMetaData> getThumbnails(Account owner, File myFile, FileMetaData metadata,
			String mimeType) {
		if (!thumbEnabled || (!pdfThumbEnabled && metadata.getMimeType().contains("pdf"))) {
			logger.warn("Thumbnail generation is disabled.");
			return Maps.newHashMap();
		}
		FileResource fileResource = getFileResourceFactory().getFileResource(myFile, mimeType);
		return computeAndStoreThumbnail(owner, metadata, fileResource);
	}

	protected FileResourceFactory getFileResourceFactory() {
		return this.thumbnailService.getFactory();
	}

	@Override
	public boolean isSupportedMimetype(String mimeType) {
		return thumbnailService.getFactory().isSupportedMimeType(mimeType);
	}

	protected Map<ThumbnailType, FileMetaData> computeAndStoreThumbnail(Account owner, FileMetaData metadata,
			FileResource fileResource) {
		FileMetaData metadataThumb = null;
		Map<ThumbnailType, FileMetaData> thumbnailMap = Maps.newHashMap();
		if (fileResource != null) {
			Map<ThumbnailKind, File> imageMap = Maps.newHashMap();
			try {
				imageMap = fileResource.generateThumbnailMap();
				for (Map.Entry<ThumbnailKind, File> entry : imageMap.entrySet()) {
					metadataThumb = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png",
							entry.getValue().length(), metadata.getFileName());
					metadataThumb = fileDataStore.add(entry.getValue(), metadataThumb);
					thumbnailMap.put(ThumbnailType.toThumbnailType(entry.getKey()), metadataThumb);
				}
			} catch (Exception e) {
				// We catch all exception to avoid failure when uploading
				// a new document. thumbnails are optionals.
				logger.error("Failed to generate the thumbnail files ", e);
				logger.debug(e.getMessage(), e);
				cleanMap(thumbnailMap);
				return thumbnailMap;
			}
		}
		return thumbnailMap;
	}

	protected void cleanMap(Map<ThumbnailType, FileMetaData> thumbnailMap) {
		if (!thumbnailMap.isEmpty()) {
			thumbnailMap.forEach((thumbnailType, metadata) -> {
				if (metadata != null) {
					fileDataStore.remove(metadata);
				}
			});
			thumbnailMap.clear();
		}
	}

	@Override
	public void start() {
		if (thumbEnabled) {
			this.thumbnailService.start();
		}
	}

	@Override
	public void stop() {
		if (thumbEnabled) {
			this.thumbnailService.stop();
		}
	}
}
