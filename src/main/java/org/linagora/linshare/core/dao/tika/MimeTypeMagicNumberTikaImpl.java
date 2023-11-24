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
package org.linagora.linshare.core.dao.tika;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * This class is designed to detect mime type an extension from a file.
 * 
 * @author fma
 * 
 */
public class MimeTypeMagicNumberTikaImpl implements MimeTypeMagicNumberDao {

	private static final Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaImpl.class);
	public static final String NOT_ANALYZED_MIME_TYPE = "not_analyzed";
	final private boolean skipMimeChecks;

	public MimeTypeMagicNumberTikaImpl(boolean skipMimeChecks) {
		this.skipMimeChecks = skipMimeChecks;
	}

	@Override
	public String getMimeType(InputStream theFileInputStream) throws BusinessException {
		if (skipMimeChecks) {
			return NOT_ANALYZED_MIME_TYPE;
		}
		try {
			Metadata metadata = new Metadata();
			AutoDetectParser parser = new AutoDetectParser();
			MediaType detect = parser.getDetector().detect(theFileInputStream, metadata);
			logger.debug("Mime type : {}", detect.toString());
			return detect.toString();
		} catch (java.lang.NoSuchMethodError e) {
			logger.debug("some exceptions could be raised in this case : {}", e.getMessage());
			if (e.getCause() != null) logger.debug(e.getCause().toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getMessage(), e);
		}
		return "data";
	}

	@Override
	public String getMimeType(File file) throws BusinessException {
		if (skipMimeChecks) {
			return NOT_ANALYZED_MIME_TYPE;
		}
		String mimeType = null;
		try(FileInputStream f = new FileInputStream(file);
			BufferedInputStream bufStream = new BufferedInputStream(f)) {
			mimeType = this.getMimeType(bufStream);
		} catch (FileNotFoundException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		} catch (IOException e1) {
			logger.error(e1.getMessage());
			logger.debug(e1.getMessage(), e1);
		}
		if(mimeType == null) {
			mimeType = "data";
		}
		logger.debug("Mime type found : {}", mimeType);
		return mimeType;
	}

	@Override
	public Set<MimeType> getAllMimeType() {
		Set<MimeType> mimeTypes = Sets.newHashSet();
		MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
		SortedSet<MediaType> types = defaultMimeTypes.getMediaTypeRegistry().getTypes();
		for (MediaType mediaType : types) {
			String strMimeType = mediaType.toString();
			try {
				String extension = defaultMimeTypes.forName(strMimeType).getExtension();
				mimeTypes.add(new MimeType(strMimeType, extension, true, false));
			} catch (MimeTypeException e) {
				logger.error("Can not find extension(s) for mime type : " + strMimeType);
				logger.debug(e.getMessage());
			}
		}
		return mimeTypes;
	}

	@Override
	public boolean isKnownExtension(String extension) {
		if (skipMimeChecks) {
			return true;
		}
		MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
		SortedSet<MediaType> types = defaultMimeTypes.getMediaTypeRegistry().getTypes();
		for (MediaType mediaType : types) {
			String strMimeType = mediaType.toString();
			try {
				org.apache.tika.mime.MimeType forName = defaultMimeTypes.forName(strMimeType);
				List<String> extensions = forName.getExtensions();
				for (String found : extensions) {
					if (found.equals(extension)) {
						return true;
					}
				}
			} catch (MimeTypeException e) {
				logger.error("Can not find extension(s) for mime type : " + strMimeType);
				logger.debug(e.getMessage());
			}
		}
		return false;
	}

}
