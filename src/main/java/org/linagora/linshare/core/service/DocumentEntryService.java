package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryService {
	
	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @param theFilePath
	 * @return
	 */
	public String getMimeType(InputStream theFileStream, String theFilePath)  throws BusinessException;

}
