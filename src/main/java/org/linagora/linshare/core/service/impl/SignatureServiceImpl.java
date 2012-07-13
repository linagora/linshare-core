package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.business.service.impl.SignatureBusinessServiceImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureServiceImpl implements SignatureService {
	
	private final SignatureBusinessService signatureBusinessService;
	
	private static final Logger logger = LoggerFactory.getLogger(SignatureServiceImpl.class);
	
	public SignatureServiceImpl(SignatureBusinessService signatureBusinessService) {
		super();
		this.signatureBusinessService = signatureBusinessService;
	}

	@Override
	public Signature createSignature(Account owner, Document document, File myFile, Long size, String fileName, String mimeType, X509Certificate signerCertificate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
