package org.linagora.linshare.core.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentUtils {

	private static final Logger logger = LoggerFactory.getLogger(DocumentUtils.class);
	
	
	public File getTempFile(InputStream stream, String fileName) {
		// Copy the input stream to a temporary file for safe use
		File tempFile = null;
		BufferedOutputStream bof = null;
		
		//extract extension
		int splitIdx = fileName.lastIndexOf('.');
		String extension = "";
		if(splitIdx>-1){
			extension = fileName.substring(splitIdx, fileName.length());
		}
		logger.debug("Found extension :"+extension);

		try {
			tempFile = File.createTempFile("linshare", extension); //we need to keep the extension for the thumbnail generator
			tempFile.deleteOnExit();

			if (logger.isDebugEnabled()) {
				logger.debug("createTempFile:" + tempFile);
			}

			bof = new BufferedOutputStream(new FileOutputStream(tempFile));

			// Transfer bytes from in to out
			byte[] buf = new byte[64 * 4096]; // 256Kio
			int len;
			while ((len = stream.read(buf)) > 0) {
				bof.write(buf, 0, len);
				logger.debug("len buf : " + len);
			}
			bof.flush();

		} catch (IOException e) {
			if (tempFile != null && tempFile.exists())
				tempFile.delete();
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "couldn't create a temporary file");
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
}
