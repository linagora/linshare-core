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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.cxf.helpers.IOUtils;
import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.ThumbnailService;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.LinThumbnail.utils.ImageUtils;
import org.linagora.LinThumbnail.utils.LargeThumbnail;
import org.linagora.LinThumbnail.utils.MediumThumbnail;
import org.linagora.LinThumbnail.utils.SmallThumbnail;
import org.linagora.LinThumbnail.utils.Thumbnail;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailKind;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class ThumbnailGeneratorServiceImpl implements ThumbnailGeneratorService {

	private static final Logger logger = LoggerFactory.getLogger(ThumbnailGeneratorServiceImpl.class);

	protected ThumbnailService thumbnailService;

	protected FileDataStore fileDataStore;

	protected final boolean thumbEnabled;

	protected final boolean pdfThumbEnabled;

	protected DocumentEntryBusinessServiceImpl documentEntryBusiness;

	public ThumbnailGeneratorServiceImpl(FileDataStore fileDataStore, boolean thumbEnabled, boolean pdfThumbEnabled) {
		this.fileDataStore = fileDataStore;
		this.thumbEnabled = thumbEnabled;
		this.pdfThumbEnabled = pdfThumbEnabled;
		this.thumbnailService = new ThumbnailService();
	}

	@Override
	public Map<ThumbnailKind, FileMetaData> getThumbnails(Account owner, File myFile, FileMetaData metadata,
			FileResource fileResource) {
		if (!thumbEnabled || (!pdfThumbEnabled && metadata.getMimeType().contains("pdf"))) {
			logger.warn("Thumbnail generation is disabled.");
			return null;
		}
		Thumbnail smallThumb = new SmallThumbnail(myFile.getAbsolutePath());
		Thumbnail mediumThumb = new MediumThumbnail(myFile.getAbsolutePath());
		Thumbnail largeThumb = new LargeThumbnail(myFile.getAbsolutePath());
		Map<ThumbnailKind, FileMetaData> thumbnails = Maps.newHashMap();
		thumbnails.put(ThumbnailKind.SMALL, computeAndStoreThumbnail(smallThumb, owner, myFile, metadata, fileResource));
		thumbnails.put(ThumbnailKind.MEDIUM, computeAndStoreThumbnail(mediumThumb, owner, myFile, metadata, fileResource));
		thumbnails.put(ThumbnailKind.LARGE, computeAndStoreThumbnail(largeThumb, owner, myFile, metadata, fileResource));
		return thumbnails;
	}

	@Override
	public void thumbnailServiceStart() {
		thumbnailService.start();
	}

	@Override
	public void thumbnailServiceStop() {
		thumbnailService.stop();
	}

	@Override
	public FileResourceFactory getFileResourceFactory() {
		return this.thumbnailService.getFactory();
	}

	@Override
	public Map<ThumbnailKind, FileMetaData> copyThumbnail(Document srcDocument, Account owner, FileMetaData metadata) {
		Map<ThumbnailKind, FileMetaData> thumbnailMetaData = Maps.newHashMap();
		Thumbnail thumbnail = null;
		File tempThumbFile = null;
		String mimeType = "image/png";
		for (ThumbnailKind kind : ThumbnailKind.values()) {
			try {
				tempThumbFile = getFileCopyThumbnail(srcDocument, owner, metadata, kind);
				FileResource fileResource = getFileResourceFactory().getFileResource(tempThumbFile, mimeType);
				if (kind.equals(ThumbnailKind.SMALL)) {
					thumbnail = new SmallThumbnail(tempThumbFile.getAbsolutePath());
				} else if (kind.equals(ThumbnailKind.MEDIUM)) {
					thumbnail = new MediumThumbnail(tempThumbFile.getAbsolutePath());
				} else if (kind.equals(ThumbnailKind.LARGE)) {
					thumbnail = new LargeThumbnail(tempThumbFile.getAbsolutePath());
				}
				thumbnailMetaData.put(kind,
						computeAndStoreThumbnail(thumbnail, owner, tempThumbFile, metadata, fileResource));

			} catch (Exception e) {
				logger.error("Copy thumbnail failed");
				logger.error(e.getMessage(), e);
				if (tempThumbFile != null) {
					tempThumbFile.delete();
				}
			} 
		}
		return thumbnailMetaData;
	}

	private File getFileCopyThumbnail(Document srcDocument, Account owner, FileMetaData metadata, ThumbnailKind kind){
		FileMetaDataKind fileMetaDataKind = ThumbnailKind.toFileMetaDataKind(kind);
		try (InputStream inputStream = documentEntryBusiness.getDocumentThumbnailStream(srcDocument, fileMetaDataKind)) {
			if (inputStream != null) {
				File tempThumbFile = null;
				try {
					tempThumbFile = File.createTempFile("linthumbnail", owner + "_thumb.png");
					tempThumbFile.createNewFile();
					try (FileOutputStream fos = new FileOutputStream(tempThumbFile)) {
						IOUtils.copyAndCloseInput(inputStream, fos);
						return tempThumbFile;
					}
				} catch (Exception e) {
					logger.error("Can not create a copy thumbnail of existing document.");
					logger.error(e.getMessage(), e);
					if (tempThumbFile != null) {
						tempThumbFile.delete();
					}
				}
			}
		} catch (IOException e1) {
			logger.error("Can not create a copy thumbnail of existing document.");
			logger.error(e1.getMessage(), e1);
		}
		return null;
	}

	private FileMetaData computeAndStoreThumbnail(Thumbnail thumbnail, Account owner, File myFile,
			FileMetaData metadata, FileResource fileResource) {
		FileMetaData ret = null;
		InputStream fisThmb = null;
		BufferedImage bufferedImage = null;
		File tempThumbFile = null;
		if (fileResource != null) {
			try {
				try {
					bufferedImage = fileResource.generateThumbnailImage(thumbnail);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
					logger.debug(e.toString());
				}
				if (bufferedImage != null) {
					fisThmb = ImageUtils.getInputStreamFromImage(bufferedImage, "png");
					tempThumbFile = File.createTempFile("linthumbnail", owner + "_thumb.png");
					tempThumbFile.createNewFile();
					if (bufferedImage != null) {
						ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, tempThumbFile);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("5.1)start insert of thumbnail in jack rabbit:" + tempThumbFile.getName());
					}
					ret = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png", tempThumbFile.length(),
							metadata.getFileName());
					ret = fileDataStore.add(tempThumbFile, ret);
				}
			} catch (FileNotFoundException e) {
				logger.error(e.toString());
				// if the thumbnail generation fails, it's not big deal, it has
				// not to block
				// the entire process, we just don't have a thumbnail for this
				// document
			} catch (IOException e) {
				logger.error(e.toString());
			} finally {
				try {
					if (fisThmb != null)
						fisThmb.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
				if (tempThumbFile != null) {
					tempThumbFile.delete();
				}
			}
		}
		return ret;
	}
}
