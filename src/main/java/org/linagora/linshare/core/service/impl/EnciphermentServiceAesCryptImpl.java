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
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.UUID;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO : TO be refactored, cleaned, improved !
public class EnciphermentServiceAesCryptImpl implements EnciphermentService {

	private static Logger logger = LoggerFactory.getLogger(EnciphermentServiceAesCryptImpl.class);

	private final static String EXTENSION_CRYPT=".aes";

	private final DocumentEntryService documentEntryService;

    private final LogEntryService logEntryService;

	private final String workingDir;


	public EnciphermentServiceAesCryptImpl(DocumentEntryService documentEntryService, LogEntryService logEntryService, String workingDir) {
		super();
		this.documentEntryService = documentEntryService;
		this.logEntryService = logEntryService;
		this.workingDir = workingDir; //linshare.encipherment.tmp.dir

		//test directory
		File workingDirtest = new File(workingDir);
		if(!workingDirtest.exists()) workingDirtest.mkdirs();
	}

	@Override
	public DocumentEntry encryptDocument(Account actor, Account owner, String documentEntryUuid, String password) throws BusinessException {
		DocumentEntry documentEntry = documentEntryService.find(actor, owner, documentEntryUuid);
		return encryptDocument(actor, owner, documentEntry, password);
	}

	@Override
	public DocumentEntry decryptDocument(Account actor, Account owner, String documentEntryUuid, String password) throws BusinessException {
		DocumentEntry documentEntry = documentEntryService.find(actor, owner, documentEntryUuid);
		return decryptDocument(actor, owner, documentEntry, password);
	}

	@Override
	public DocumentEntry decryptDocument(Account actor, Account owner, DocumentEntry documentEntry, String password) throws BusinessException {

		InputStream in =  null;
		OutputStream out = null; 

		File f = null;
		DocumentEntry resdoc = null;

		try {

			in = documentEntryService.getDocumentStream(actor, owner, documentEntry.getUuid());

			f = new File(workingDir + "/" + UUID.randomUUID());
			out = new FileOutputStream(f);

			AESCrypt aes = new AESCrypt(false, password);
			aes.decrypt(in, out);

			out.flush();
			out.close();

			String finalFileName = changeDocumentExtension(documentEntry.getName());

			resdoc = documentEntryService.update(actor, owner, documentEntry.getUuid(), f, finalFileName);

//			FileLogEntry logEntry = new FileLogEntry(owner, LogAction.FILE_DECRYPT, "Decrypt file Content", documentEntry.getName(), documentEntry.getSize(), documentEntry.getType());
//			logEntryService.create(logEntry);

		} catch (IOException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ documentEntry.getUuid());
		} catch (GeneralSecurityException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ documentEntry.getUuid());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
			if (f != null && f.exists()) {
				f.delete();
			}
		}

		return resdoc;
	}

	@Override
	public DocumentEntry encryptDocument(Account actor, Account owner, DocumentEntry documentEntry, String password) throws BusinessException {
		InputStream in =  null;
		OutputStream out = null; 
		DocumentEntry resdoc = null;

		File f = null;
		try {

			in = documentEntryService.getDocumentStream(actor, owner, documentEntry.getUuid());
			f = new File(workingDir+"/"+UUID.randomUUID());
			out = new FileOutputStream(f);

			AESCrypt aes = new AESCrypt(false, password);
			aes.encrypt(2,in, out);

			out.flush();
			out.close();

			String finalFileName =  changeDocumentExtension(documentEntry.getName());	

			resdoc = documentEntryService.update(actor, owner, documentEntry.getUuid(), f, finalFileName);

//			FileLogEntry logEntry = new FileLogEntry(owner, LogAction.FILE_ENCRYPT, "Encrypt file Content", documentEntry.getName(), documentEntry.getSize(), documentEntry.getType());
//			logEntryService.create(logEntry);

		} catch (IOException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt documentEntry "+ documentEntry.getUuid());
		} catch (GeneralSecurityException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt documentEntry "+ documentEntry.getUuid());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (f != null && f.exists()) {
				f.delete();
			}
		}

		return resdoc;
	}


	private String changeDocumentExtension(String docname) {
		if(docname.toLowerCase().endsWith(EXTENSION_CRYPT))
			return getDecryptedExtension(docname.toLowerCase());
		else
			return getEncryptedExtension(docname.toLowerCase());
	}


	private static String getEncryptedExtension(String originalFileName) {
		return originalFileName + EXTENSION_CRYPT;
	}


	private static String getDecryptedExtension(String originalCryptedFileName) {
		int pos = originalCryptedFileName.lastIndexOf(EXTENSION_CRYPT);

		if (pos==-1)
			return  originalCryptedFileName;
		else {
			return originalCryptedFileName.substring(0,pos);
		}
	}
}
