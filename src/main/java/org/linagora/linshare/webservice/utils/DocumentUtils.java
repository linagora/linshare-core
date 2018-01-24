/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
package org.linagora.linshare.webservice.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DocumentUtils {

	public static Logger logger = LoggerFactory.getLogger(DocumentUtils.class);

	public static File getTempFile(InputStream theFile, String discriminator, String fileName) {
		if (discriminator == null) {
			discriminator = "";
		}
		// Legacy code, we need to extract extension for the dirty unstable LinThumbnail
		// Module.
		// I hope some day we get rid of it !
		String extension = null;
		if (fileName != null) {
			int splitIdx = fileName.lastIndexOf('.');
			if (splitIdx > -1) {
				extension = fileName.substring(splitIdx, fileName.length());
			}
		}
		File tempFile = null;
		try {
			tempFile = File.createTempFile("linshare-" + discriminator + "-", extension);
			tempFile.deleteOnExit();
			IOUtils.transferTo(theFile, tempFile);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Can not generate temp file from input stream.");
		}
		return tempFile;
	}

	public static void deleteTempFile(File tempFile) {
		if (tempFile != null) {
			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception e) {
				logger.warn("Can not delete temp file : " + tempFile.getAbsolutePath());
				logger.debug(e.getMessage(), e);
			}
		}
	}

	public static void checkSizeValidation(Long fileSize, long currSize) {
		Validate.notNull(fileSize, "filesize must be set");
		if (!fileSize.equals(currSize)) {
			String msg = String.format(
					"Invalid file size (check multipart parameter named 'filesize'), size found %1$d, expected %2$d.(diff=%3$d)",
					currSize, fileSize, Math.abs(fileSize - currSize));
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST, msg);
		}
	}

	public static File createFileFromURL(DocumentURLDto documentURLDto, boolean sizeValidation) {
		return createFileFromUrl(documentURLDto.getURL(), documentURLDto.getFileName(), documentURLDto.getSize(),
				sizeValidation);
	}

	public static File createFileFromUrl(String fileURL, String defaultFileName, Long fileSize,
			boolean sizeValidation) {
		URL url = null;
		File tempFile = null;
		try {
			url = new URL(fileURL);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(BusinessErrorCode.WRONG_URL, "Malformed URL : " + fileURL);
		}
		try (InputStream inputStream = url.openStream()) {
			tempFile = DocumentUtils.getTempFile(inputStream, "rest-userv2-document-entries", defaultFileName);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			DocumentUtils.deleteTempFile(tempFile);
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Can not generate temp file from input stream from URL : " + fileURL);
		}
		long currSize = tempFile.length();
		if (sizeValidation && (fileSize != null)) {
			DocumentUtils.checkSizeValidation(fileSize, currSize);
		}
		return tempFile;
	}

	public static String getFileNameFromUrl(String fileURL, String defaultFileName) {
		boolean emptyFileName = Strings.isNullOrEmpty(defaultFileName);
		if (emptyFileName) {
			return fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
		}
		return defaultFileName;
	}
}
