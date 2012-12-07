package org.linagora.linshare.core.business.service;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.exception.BusinessException;

public interface SignatureBusinessService {
	
	public Signature findByUuid(String signatureUuid);
	
	public Signature createSignature(Account owner, Document document, File myFile, Long size, String fileName, String mimeType, X509Certificate signerCertificate) throws BusinessException;
	
	public void deleteSignature(Signature signature) throws BusinessException;
	
	public InputStream getDocumentStream(Signature signature);
}
