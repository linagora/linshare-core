package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.exception.BusinessException;

public interface SignatureService {

	public Signature findByUuid(String signatureUuid);
		
	public Signature createSignature(Account actor, Document document, InputStream stream, Long size, String fileName, X509Certificate signerCertificate) throws BusinessException ;
	
	public void deleteSignature(Signature signature) throws BusinessException;
	
	public InputStream getDocumentStream(Signature signature);
	
	
}
