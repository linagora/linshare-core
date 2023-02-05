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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.SignatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class SignatureBusinessServiceImpl implements SignatureBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(SignatureBusinessServiceImpl.class);

	private final FileDataStore fileDataStore;
	private final SignatureRepository signatureRepository;
	private final DocumentRepository documentRepository;
	private final AccountRepository<Account> accountRepository;
	
	
	public SignatureBusinessServiceImpl(FileDataStore fileSystemDao, SignatureRepository signatureRepository, AccountRepository<Account> accountRepository,
			DocumentRepository documentRepository) {
		super();
		this.fileDataStore = fileSystemDao;
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
		// Storing file
		FileMetaData metadataSign = new FileMetaData(FileMetaDataKind.SIGNATURE, mimeType, size, fileName);
		Signature entity = null;
		try {
			metadataSign = fileDataStore.add(Files.asByteSource(myFile), metadataSign);
			// create signature in db
			Calendar now = new GregorianCalendar();
			
			entity = new Signature(metadataSign.getUuid(), fileName, now, now, owner, document, size, signerCertificate);
			entity = signatureRepository.create(entity);
			
			document.getSignatures().add(entity);
			documentRepository.update(document);
			
			owner.getSignatures().add(entity);
			accountRepository.update(owner);

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			fileDataStore.remove(metadataSign);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_SIGNATURE, "couldn't register the signature in the database");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_SIGNATURE, "couldn't register the signature in the database");
		}
		return entity;
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
		logger.debug("suppression of signature, Uuid : " + signature.getUuid());
		FileMetaData metadata = new FileMetaData(signature);
		fileDataStore.remove(metadata);
		signatureRepository.delete(signature);
	}
}
