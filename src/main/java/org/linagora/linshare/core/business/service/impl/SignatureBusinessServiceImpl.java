/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.InputStream;
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
		metadataSign = fileDataStore.add(myFile, metadataSign);
				
		Signature entity = null;
		try {
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


	@Override
	public InputStream getDocumentStream(Signature signature) {
		String UUID = signature.getUuid();
		if (UUID!=null && UUID.length()>0) {
			logger.debug("retrieve from jackrabbity : " + UUID);
			FileMetaData metadata = new FileMetaData(signature);
			InputStream stream = fileDataStore.get(metadata);
			return stream;
		}
		return null;
	}
	
	
}
