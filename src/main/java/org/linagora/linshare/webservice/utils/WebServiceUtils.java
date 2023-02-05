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
package org.linagora.linshare.webservice.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentURLDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class WebServiceUtils {

	public static Logger logger = LoggerFactory.getLogger(WebServiceUtils.class);

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

	public static File createFileFromURL(DocumentURLDto documentURLDto, String discriminator, boolean sizeValidation) {
		return createFileFromUrl(documentURLDto.getURL(), documentURLDto.getFileName(), documentURLDto.getSize(), discriminator,
				sizeValidation);
	}

	public static File createFileFromUrl(String fileURL, String defaultFileName, Long fileSize, String discriminator,
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
			tempFile = WebServiceUtils.getTempFile(inputStream, discriminator, defaultFileName);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			WebServiceUtils.deleteTempFile(tempFile);
			throw new BusinessException(BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					"Can not generate temp file from input stream from URL : " + fileURL);
		}
		long currSize = tempFile.length();
		if (sizeValidation && (fileSize != null)) {
			WebServiceUtils.checkSizeValidation(fileSize, currSize);
		}
		return tempFile;
	}

	public static String getFileNameFromUrl(String fileURL, String defaultFileName) {
		boolean emptyFileName = Strings.isNullOrEmpty(defaultFileName);
		if (emptyFileName) {
			try {
				URL foundUrl = new URL(fileURL);
				String pathUrl = foundUrl.getPath();
				return pathUrl.substring(pathUrl.lastIndexOf('/') + 1, pathUrl.length());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage(), e);
				throw new BusinessException(BusinessErrorCode.WRONG_URL, "Malformed URL : " + fileURL);
			}
		}
		return defaultFileName;
	}

	public static Long getTransfertDuration() {
		Long uploadStartTime = null;
		Message currentMessage = PhaseInterceptorChain.getCurrentMessage();
		Exchange exchange = currentMessage.getExchange();
		if (exchange.containsKey("org.linagora.linshare.webservice.interceptor.start_time")) {
			uploadStartTime = (Long) exchange.get("org.linagora.linshare.webservice.interceptor.start_time");
			logger.debug("Upload start time : " + uploadStartTime);
		}
		Long transfertDuration = null;
		if (uploadStartTime != null) {
			Date endDate = new Date();
			transfertDuration = endDate.getTime() - uploadStartTime;
			if (logger.isDebugEnabled()) {
				Date beginDate = new Date(uploadStartTime);
				logger.debug("Upload was begining at : " + beginDate);
				logger.debug("Upload was ending at : " + endDate);
			}
			logger.info("statistics:upload time:" + transfertDuration + "ms.");
		}
		return transfertDuration;
	}
}
