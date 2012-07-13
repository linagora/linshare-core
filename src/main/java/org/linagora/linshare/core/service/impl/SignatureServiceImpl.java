package org.linagora.linshare.core.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.business.service.impl.SignatureBusinessServiceImpl;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SignatureService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureServiceImpl implements SignatureService {
	
	private final SignatureBusinessService signatureBusinessService;
	private final LogEntryService logEntryService;
	
	private static final Logger logger = LoggerFactory.getLogger(SignatureServiceImpl.class);
	
	public SignatureServiceImpl(SignatureBusinessService signatureBusinessService, LogEntryService logEntryService) {
		super();
		this.signatureBusinessService = signatureBusinessService;
		this.logEntryService = logEntryService;
	}

	@Override
	public Signature createSignature(Account actor, Document document, InputStream stream, Long size, String fileName, X509Certificate signerCertificate) throws BusinessException {
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		DocumentUtils util = new DocumentUtils();
		File tempFile =  util.getFileFromBufferedInputStream(bufStream, fileName);
		String mimeType = "text/xml";
		Signature signature = signatureBusinessService.createSignature(actor, document, tempFile, size, fileName, mimeType, signerCertificate);
		FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_SIGN, "signature of a file", fileName, document.getSize(), mimeType);
		logEntryService.create(logEntry);
		tempFile.delete(); // remove the temporary file
		return signature;
	}

	@Override
	public void deleteSignature(Signature signature) throws BusinessException {
		signatureBusinessService.deleteSignature(signature);
	}

	@Override
	public InputStream getDocumentStream(Signature signature) {
		return signatureBusinessService.getDocumentStream(signature);
	}

	

}
