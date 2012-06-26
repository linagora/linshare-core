package org.linagora.linshare.core.service.impl;

import java.io.InputStream;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentEntryServiceImpl implements DocumentEntryService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryServiceImpl.class);
	
	private final DocumentEntryBusinessService documentEntryBusinessService;
	
	public DocumentEntryServiceImpl(DocumentEntryBusinessService documentEntryBusinessService) {
		this.documentEntryBusinessService = documentEntryBusinessService;
	}


	@Override
	public String getMimeType(InputStream theFileStream, String theFilePath) throws BusinessException {
		return documentEntryBusinessService.getMimeType(theFileStream, theFilePath);
	}

}
