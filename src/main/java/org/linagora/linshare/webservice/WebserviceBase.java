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
package org.linagora.linshare.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * common utility methods for webservice implementation (rest)
 */
public class WebserviceBase {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 1mo
	 */
	public static final int ERROR_THRESHOLD_FOR_FILE_SIZE_DIFFERENCE = 1048576;

	// REST

	protected String getCoreVersion() {
		Properties prop = new Properties();
		try {
			if (this.getClass().getResourceAsStream("/version.properties") != null) {
				prop.load(this.getClass().getResourceAsStream(
						"/version.properties"));
			} else {
				logger.debug("Impossible to load version.properties, Is this a dev environnement?");
			}
		} catch (IOException e) {
			logger.debug("Impossible to load version.properties, Is this a dev environnement?");
			logger.debug(e.toString());
		}
		if (prop.getProperty("Implementation-Version") != null) {
			return prop.getProperty("Implementation-Version");
		} else {
			return "trunk";
		}
	}

	protected String getFileName(String givenFileName, MultipartBody body) {
		String fileName;
		if (givenFileName == null || givenFileName.isEmpty()) {
			// parameter givenFileName is optional
			// so need to search this information in the header of the
			// attachment (with id file)
			fileName = body.getAttachment("file").getContentDisposition()
					.getParameter("filename");
		} else {
			fileName = givenFileName;
		}
		if (fileName == null) {
			logger.error("There is no multi-part attachment named 'filename'.");
			logger.error("There is no 'filename' header in multi-Part attachment named 'file'.");
			Validate.notNull(fileName, "File name for file attachment is required.");
		}
		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
		}
		return fileName;
	}

	protected void checkSizeValidation(Long contentLength, Long fileSize,
			long currSize) {
		if (fileSize != null) {
			if (!fileSize.equals(currSize)) {
				String msg = String
						.format("Invalid file size (check multipart parameter named 'filesize'), size found %1$d, expected %2$d.(diff=%3$d)",
								currSize, fileSize, Math.abs(fileSize - currSize));
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST, msg);
			}
		} else {
			//	if file size is not supply, we could try an approximation ~1Mo
			long diff = Math.abs(contentLength - currSize);
			if (diff - ERROR_THRESHOLD_FOR_FILE_SIZE_DIFFERENCE > 0) {
				String msg = String
						.format("Weird file size, size found %1$d, request content length %2$d.(diff=%3$d)",
								currSize, contentLength, diff);
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST, msg);
			}
		}
	}
}
