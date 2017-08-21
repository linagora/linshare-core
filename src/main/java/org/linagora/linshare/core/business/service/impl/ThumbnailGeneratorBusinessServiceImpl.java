/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.ThumbnailService;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.LinThumbnail.utils.ImageUtils;
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
			FileResource fileResource) {
		if (!thumbEnabled || (!pdfThumbEnabled && metadata.getMimeType().contains("pdf"))) {
			logger.warn("Thumbnail generation is disabled.");
			return null;
		}
		return computeAndStoreThumbnail(owner, metadata, fileResource);
	}

	@Override
	public FileResourceFactory getFileResourceFactory() {
		return this.thumbnailService.getFactory();
	}

	private Map<ThumbnailType, FileMetaData> computeAndStoreThumbnail(Account owner, FileMetaData metadata,
			FileResource fileResource) {
		FileMetaData metadataThumb = null;
		Map<ThumbnailType, FileMetaData> thumbnailMap = Maps.newHashMap();
		if (fileResource != null) {
			Map<ThumbnailKind, BufferedImage> imageMap = null;
			try {
				imageMap = fileResource.generateThumbnailImageMap();
			} catch (Exception e) {
				// We catch all exception to avoid failure when uploading
				// a new document. thumbnails are optionals.
				logger.error(e.getMessage());
				logger.debug(e.getMessage(), e);
			}
			for (Map.Entry<ThumbnailKind, BufferedImage> entry : imageMap.entrySet()) {
				if (entry.getValue() != null) {
					File tempThumbFile = null;
					try (InputStream fisThmb = ImageUtils.getInputStreamFromImage(entry.getValue(), "png")) {
						tempThumbFile = File.createTempFile("linthumbnail", owner + "_thumb.png");
						tempThumbFile.createNewFile();
						tempThumbFile.deleteOnExit();
						ImageIO.write(entry.getValue(), Constants.THMB_DEFAULT_FORMAT, tempThumbFile);
						metadataThumb = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png",
								tempThumbFile.length(), metadata.getFileName());
						metadataThumb = fileDataStore.add(tempThumbFile, metadataThumb);
						thumbnailMap.put(ThumbnailType.toThumbnailKind(entry.getKey()), metadataThumb);
					} catch (IOException e) {
						logger.error("Failled to generate the thumbnail files ", e);
						logger.debug(e.getMessage(), e);
					} finally {
						if (tempThumbFile != null) {
							tempThumbFile.delete();
						}
					}
				}
			}
		}
		return thumbnailMap;
	}
}
