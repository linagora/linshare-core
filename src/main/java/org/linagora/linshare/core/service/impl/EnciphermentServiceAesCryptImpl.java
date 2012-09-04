/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.UUID;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnciphermentServiceAesCryptImpl implements EnciphermentService {
	

	private final UserService userService;
	// TODO to be removed 
	private DocumentService documentService;
	
	private final DocumentEntryBusinessService documentEntryBusinessService;
	
    private final LogEntryService logEntryService;  
	private final String workingDir;
	
	private static Logger logger = LoggerFactory.getLogger(EnciphermentServiceAesCryptImpl.class);
	
	
	public EnciphermentServiceAesCryptImpl(DocumentEntryBusinessService documentEntryBusinessService, UserService userService, LogEntryService logEntryService,String workingDir) {
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.userService =  userService;
		this.logEntryService = logEntryService;
		this.workingDir = workingDir; //linshare.encipherment.tmp.dir
		
		//test directory
		File workingDirtest = new File(workingDir);
		if(!workingDirtest.exists()) workingDirtest.mkdirs();
	}
	
	public Document decryptDocument(DocumentVo doc,UserVo userVo,String password) throws BusinessException {
		
		InputStream in =  null;
		OutputStream out = null; 
		
		FileInputStream res = null;
		File f = null;
		Document resdoc = null;
		
		try {
			
			in = documentService.retrieveFileStream(doc, userVo);
			
			f = new File(workingDir+"/"+UUID.randomUUID());
			out = new FileOutputStream(f);
			
			AESCrypt aes = new AESCrypt(false, password);
			aes.decrypt(in, out);
			
			out.flush();
			out.close();
			
			res = new FileInputStream(f);
			
			User owner = userService.findUserInDB(userVo.getDomainIdentifier(), userVo.getLogin());
			
			String finalFileName = changeDocumentExtension(doc.getFileName());
			
			resdoc = documentService.updateFileContent(doc.getIdentifier(), res, res.available(), finalFileName, doc.getType(), false, owner);

//			FileLogEntry logEntry = new FileLogEntry(userVo.getMail(), userVo.getFirstName(), userVo.getLastName(), userVo.getDomainIdentifier(),
//	        		LogAction.FILE_DECRYPT, "Decrypt file Content", doc.getFileName(), doc.getSize(), doc.getType() );
//	        
//	        logEntryService.create(logEntry);
		
		} catch (IOException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (GeneralSecurityException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} finally {
			
			if(in!=null) try {in.close();} catch (IOException e) {}
			if(out!=null) try {out.close();} catch (IOException e) {}
			if(res!=null) try {res.close();} catch (IOException e) {}
			if(f!=null&&f.exists()) f.delete();
		}
		
		return resdoc;
	}

	public Document encryptDocument(DocumentVo doc,UserVo userVo,String password) throws BusinessException{
		
		InputStream in =  null;
		OutputStream out = null; 
		Document resdoc = null;
		
		FileInputStream res = null;
		File f = null;
		
		try {
			
			in = documentService.retrieveFileStream(doc, userVo);
			f = new File(workingDir+"/"+UUID.randomUUID());
			out = new FileOutputStream(f);
			
			
			AESCrypt aes = new AESCrypt(false, password);
			aes.encrypt(2,in, out);
			
			out.flush();
			out.close();
			
			res = new FileInputStream(f);
			
			User owner = userService.findUserInDB(userVo.getDomainIdentifier(), userVo.getLogin());
			
			String finalFileName =  changeDocumentExtension(doc.getFileName());	
			
			resdoc = documentService.updateFileContent(doc.getIdentifier(), res, res.available(), finalFileName, doc.getType(), true,owner);
			
//			FileLogEntry logEntry = new FileLogEntry(userVo.getMail(), userVo.getFirstName(), userVo.getLastName(), userVo.getDomainIdentifier(),
//	        		LogAction.FILE_ENCRYPT, "Encrypt file Content", doc.getFileName(), doc.getSize(), doc.getType() );
//			
//	        logEntryService.create(logEntry);
			
		} catch (IOException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (GeneralSecurityException e) {
			logger.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} finally {
			
			if(in!=null) try {in.close();} catch (IOException e) {}
			if(out!=null) try {out.close();} catch (IOException e) {}
			if(res!=null) try {res.close();} catch (IOException e) {}
			if(f!=null&&f.exists()) f.delete();
		}
		
		return resdoc;
	}


	public boolean isDocumentEncrypted(DocumentVo doc) {
		DocumentEntry docdb = documentEntryBusinessService.findById(doc.getIdentifier());
		if (docdb==null) return false;
		if(docdb.getCiphered()==null) return false; else return docdb.getCiphered();
	}
	
	
	public String changeDocumentExtension(String docname) {
		if(docname.toLowerCase().endsWith(EXTENSION_CRYPT))
			return getDecryptedExtension(docname.toLowerCase());
		else
			return getEncryptedExtension(docname.toLowerCase());
	}

	private final static String EXTENSION_CRYPT=".aes";
	
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
