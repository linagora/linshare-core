package org.linagora.linshare.core.business.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentEntryBusinessServiceImpl implements DocumentEntryBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	private final MimeTypeIdentifier mimeTypeIdentifier;
	
	public DocumentEntryBusinessServiceImpl(MimeTypeIdentifier identifier) {
		super();
		this.mimeTypeIdentifier = identifier;
	}

	@Override
	public String getMimeType(InputStream theFileStream, String theFilePath)
			throws BusinessException {
		byte[] bytes;
		try {
			bytes = IOUtil.readBytes(theFileStream, mimeTypeIdentifier
					.getMinArrayLength());
		} catch (IOException e) {
			logger.error("Could not read the uploaded file " + theFilePath
					+ " to fetch its mime : ", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
					"Could not read the uploaded file to fetch its mime");
		}

		// let the MimeTypeIdentifier determine the MIME type of this file
		return mimeTypeIdentifier.identify(bytes, theFilePath, null);

	}
}
