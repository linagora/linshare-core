package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;

import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.SignatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureBusinessServiceImpl implements SignatureBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(SignatureBusinessServiceImpl.class);

	private final FileSystemDao fileSystemDao;
	private final SignatureRepository signatureRepository;
	private final DocumentRepository documentRepository;
	private final AccountRepository<Account> accountRepository;
	
	
	public SignatureBusinessServiceImpl(FileSystemDao fileSystemDao, SignatureRepository signatureRepository, AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository) {
		super();
		this.fileSystemDao = fileSystemDao;
		this.signatureRepository = signatureRepository;
		this.accountRepository = accountRepository;
		this.documentRepository = documentRepository;
	}


	@Override
	public Signature findByUuid(String signatureUuid) {
		return signatureRepository.findByUuid(signatureUuid);
	}


	@Override
	public Signature createSignature(Account owner, Document document, File myFile, Long size, String fileName, String mimeType, X509Certificate signerCertificate) throws BusinessException {
		
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUuid(), myFile);
		
		Signature entity = null;
		try {
			// create signature in db
			entity = new Signature(uuid, fileName, new GregorianCalendar(), owner, document, size, signerCertificate);
			signatureRepository.create(entity);
			
			document.getSignatures().add(entity);
			documentRepository.update(document);
			
			owner.getSignatures().add(entity);
			accountRepository.update(owner);
			

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			fileSystemDao.removeFileByUUID(uuid);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_SIGNATURE, "couldn't register the signature in the database");
		}
		return entity;
	}

	
	private String insertIntoJCR(long size, String fileName, String mimeType, String path, File tempFile) {
		// insert the file into JCR
		FileInputStream fis = null;
		String uuid;
		try {
			fis = new FileInputStream(tempFile);

			if (logger.isDebugEnabled()) {
				logger.debug("insert of the document in jack rabbit:" + fileName + ", size:"+ size + ", path:" + path + " , type: " + mimeType);
			}
			uuid = fileSystemDao.insertFile(path, fis, size, fileName, mimeType);
		} catch (FileNotFoundException e1) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"couldn't open inputStream on the temporary file");
		} finally {
			try {
				logger.debug("closing FileInputStream ");
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				// Do nothing Happy java :)
				logger.error("IO exception : should not happen ! ");
				logger.error(e.toString());
			}
		}
		return uuid;
	}

	@Override
	public void deleteSignature(Signature signature) throws BusinessException {

		Account owner = signature.getSigner();
		owner.getSignatures().remove(signature);
		

		Document document = signature.getDocument(); 
		document.getSignatures().remove(signature);
		
		accountRepository.update(owner);
		documentRepository.update(document);
		
		// remove old document in JCR
		logger.debug("suppresion of signature, Uuid : " + signature.getUuid());
		fileSystemDao.removeFileByUUID(signature.getUuid());
		signatureRepository.delete(signature);
	}


	@Override
	public InputStream getDocumentStream(Signature signature) {
		String UUID = signature.getUuid();
		if (UUID!=null && UUID.length()>0) {
			logger.debug("retrieve from jackrabbity : " + UUID);
			InputStream stream = fileSystemDao.getFileContentByUUID(UUID);
			return stream;
		}
		return null;
	}
	
	
}
