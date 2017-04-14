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
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import com.google.common.collect.Sets;

/**
 * This class is designed to detect mime type an extension from a file.
 * 
 * @author fma
 * 
 */
public class MimeTypeMagicNumberTikaImpl implements MimeTypeMagicNumberDao {

	private static final Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaImpl.class);

	@Override
	public String getMimeType(InputStream theFileInputStream) throws BusinessException {
		try {
			Metadata metadata = new Metadata();
			ContentHandler contenthandler = new BodyContentHandler();
			Parser parser = new AutoDetectParser();
			try {
				parser.parse(theFileInputStream, contenthandler, metadata, null);
            } catch (Exception e) {
                logger.debug("some exceptions could be raised in this case : " + e.getMessage());
                if (e.getCause() != null) logger.debug(e.getCause().toString());
            } catch (java.lang.NoSuchMethodError e) {
                logger.debug("some exceptions could be raised in this case : " + e.getMessage());
                if (e.getCause() != null) logger.debug(e.getCause().toString());
            }
			String stringMimeType = metadata.get(Metadata.CONTENT_TYPE);
			logger.debug("Mime type : " + stringMimeType);

//			MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(stringMimeType);
//			String extension = mimeType.getExtension();
//			logger.debug("extension : " + extension);
			return stringMimeType;
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
		}
		return "data";
	}

	@Override
	public String getMimeType(File file) throws BusinessException {
		BufferedInputStream bufStream = null;
		FileInputStream f = null;
		String mimeType = null;
		try {
			f = new FileInputStream(file);
			bufStream = new BufferedInputStream(f);
			mimeType = this.getMimeType(bufStream);
		} catch (FileNotFoundException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufStream != null) {
				try {
					bufStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(mimeType == null) {
			mimeType = "data";
		}
		logger.debug("Mime type found : " + mimeType);
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
		MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
		SortedSet<MediaType> types = defaultMimeTypes.getMediaTypeRegistry().getTypes();
		for (MediaType mediaType : types) {
			String strMimeType = mediaType.toString();
			try {
				org.apache.tika.mime.MimeType forName = defaultMimeTypes.forName(strMimeType);
				List<String> extensions = forName.getExtensions();
				for (String found : extensions) {
					if (extension.equals(found)) {
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
