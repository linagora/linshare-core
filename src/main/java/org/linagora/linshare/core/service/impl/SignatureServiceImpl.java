/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SignatureService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureServiceImpl implements SignatureService {
	
	private final SignatureBusinessService signatureBusinessService;
	@SuppressWarnings("unused")
	private final LogEntryService logEntryService;
	private static final Logger logger = LoggerFactory.getLogger(SignatureServiceImpl.class);
	
	
	public SignatureServiceImpl(SignatureBusinessService signatureBusinessService, LogEntryService logEntryService) {
		super();
		this.signatureBusinessService = signatureBusinessService;
		this.logEntryService = logEntryService;
	}
	
	
	@Override
	public Signature findByUuid(String signatureUuid) {
		return signatureBusinessService.findByUuid(signatureUuid);
	}


	@Override
	public Signature createSignature(Account actor, Document document, InputStream stream, String fileName, X509Certificate signerCertificate) throws BusinessException {
		DocumentUtils util = new DocumentUtils();
		File tempFile =  util.getTempFile(stream, fileName);
		Long size = tempFile.length();
		Signature signature = null;
		try {
			String mimeType = "text/xml";
			signature = signatureBusinessService.createSignature(actor, document, tempFile, size, fileName, mimeType, signerCertificate);
//			FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_SIGN, "signature of a file", fileName, document.getSize(), mimeType);
//			logEntryService.create(logEntry);
		} finally {
			try{
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return signature;
	}
	

	@Override
	public void deleteSignature(Signature signature) throws BusinessException {
		signatureBusinessService.deleteSignature(signature);
	}
}
