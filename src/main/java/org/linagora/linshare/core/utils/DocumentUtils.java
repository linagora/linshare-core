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
package org.linagora.linshare.core.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(DocumentUtils.class);

    public File getTempFile(InputStream stream, String fileName) {
        // Copy the input stream to a temporary file for safe use
        File tempFile = null;
        BufferedOutputStream bof = null;

        //extract extension
        int splitIdx = fileName.lastIndexOf('.');
        String extension = "";
        if (splitIdx > -1) {
            extension = fileName.substring(splitIdx, fileName.length());
        }
        logger.debug("Found extension :" + extension);
        try {
            //we need to keep the extension for the thumbnail generator
            tempFile = File.createTempFile("linshare", extension);

            tempFile.deleteOnExit();
            if (logger.isDebugEnabled()) {
                logger.debug("createTempFile:" + tempFile);
            }
            bof = new BufferedOutputStream(new FileOutputStream(tempFile));
            // Transfer bytes from in to out
            byte[] buf = new byte[64 * 4096]; // 256Kio
            int len;
            long total_len = 0;

            while ((len = stream.read(buf)) > 0) {
                bof.write(buf, 0, len);
                total_len += len;
                logger.debug("data read : " + total_len);
            }
            bof.flush();

		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error("exception : ", e);
			if (tempFile != null && tempFile.exists())
				tempFile.delete();
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"couldn't create a temporary file or read input stream");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bof != null) {
				try {
					bof.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return tempFile;
	}

	/**
	 *
	 * @param bytes Long size of file to format
	 * @param si International unit system (if true 1Kb 1000b, if not 1Kb = 1024b)
	 * @param locale The locale in which the size will be displayed
	 * @return String in human readable format
	 */
	public static String humanReadableByteCount(long bytes, boolean si, Language locale) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) {
			if (locale == Language.FRENCH) {
				return bytes + " octets";
			}
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		if (locale == Language.FRENCH) {
			return String.format("%.2f %so", bytes / Math.pow(unit, exp),
					("KMGTPE").charAt(exp - 1));
		}
		return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getHumanMimeType(String mimeType) {
		if (mimeType.matches("^audio.*")
				|| mimeType.equals("application/ogg")
				) {
			return "audio";
		}
		if (mimeType.matches("^video.*")
				|| mimeType.matches(".*mp4.*")
				|| mimeType.matches(".*mpeg.*")
				|| mimeType.equals("application/x-matroska")
				|| mimeType.equals("application/quicktime")
				) {
			return "video";
		}
		if (mimeType.matches("^text.*")) {
			return "text";
		}
		if (mimeType.matches("^image.*")) {
			return "image";
		}
		if (mimeType.equals("application/pdf")
				|| mimeType.equals("application/vnd.cups-pdf")
				|| mimeType.equals("application/vnd.sealedmedia.softseal.pdf")
				) {
			return "pdf";
		}
		if (mimeType.equals("application/zip")
				|| mimeType.equals("application/gzip")
				|| mimeType.equals("application/bzip")
				|| mimeType.equals("application/bzip2")
				|| mimeType.equals("application/x-7z-compressed")
				|| mimeType.equals("application/x-gtar")
				|| mimeType.equals("application/x-lz4")
				|| mimeType.equals("application/x-lzip")
				|| mimeType.equals("application/x-lzma")
				|| mimeType.equals("application/x-archive")
				|| mimeType.equals("application/x-tar")
				|| mimeType.equals("application/x-xz")
				|| mimeType.matches("^application/.*zip.*")
				|| mimeType.matches("^application/x-rar-compressed.*")
				) {
			return "archive";
		}
		if (mimeType.equals("application/encrypted")
				|| mimeType.equals("application/pgp-encrypted")
				|| mimeType.equals("application/x-axcrypt")
				) {
			return "encrypted";
		}
		if (mimeType.matches("^application/msword.*")
				|| mimeType.matches("application/vnd.ms-.*")
				|| mimeType.matches("application/vnd.openxmlformats-officedocument.*")
				|| mimeType.matches("application/vnd.oasis.opendocument.*")
				) {
			return "document";
		}
		return "others";
	}
}
