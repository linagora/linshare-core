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
package org.linagora.linShare.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.EnciphermentService;
import org.linagora.linShare.core.service.UserService;
import org.linagora.linShare.core.utils.SymmetricEnciphermentPBEwithAES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnciphermentServiceImpl implements EnciphermentService {
	
	private final static String CHALLENGE = "LINSHARE_IS_THE_BEST!!!";
	

	private final UserService userService;
	private final DocumentService documentService;
	private final DocumentRepository documentRepository;
    private final LogEntryRepository logEntryRepository;  
	private final String workingDir;
	
	private static Logger log = LoggerFactory.getLogger(EnciphermentServiceImpl.class);
	
	
	
	public EnciphermentServiceImpl(UserService userService, DocumentService documentService, DocumentRepository documentRepository, LogEntryRepository logEntryRepository,String workingDir) {
		this.userService =  userService;
		this.documentService = documentService;
		this.documentRepository = documentRepository;
		this.logEntryRepository = logEntryRepository;
		this.workingDir = workingDir; //linshare.encipherment.tmp.dir
		
		File workingDirtest = new File(workingDir);
		if(!workingDirtest.exists()) workingDirtest.mkdirs();
	}
	
	
	public void generateEnciphermentKey(UserVo user, String password) throws BusinessException{
		try {
			SymmetricEnciphermentPBEwithAES enc = new SymmetricEnciphermentPBEwithAES(password,CHALLENGE.getBytes(),Cipher.ENCRYPT_MODE);
			byte[] res = enc.encryptString();
			userService.updateUserEnciphermentKey(user.getMail(),res);
			
	        UserLogEntry logEntry = new UserLogEntry(user.getMail(), user.getFirstName(), user.getLastName(),
	        		LogAction.USER_UPDATE, "Generate symetric key (challenge) of a user", user.getMail(), user.getFirstName(), user.getLastName(), null);
	        
	        logEntryRepository.create(logEntry);
			
			
		} catch (InvalidKeyException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		} catch (InvalidKeySpecException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		} catch (NoSuchPaddingException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		} catch (IOException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_GENERATE_KEY,"can not generate symetric key for user "+user.getMail());
		}

	}
	
	
	public boolean checkEnciphermentKey(User user, String password) throws BusinessException {
		
		boolean res = false;
		SymmetricEnciphermentPBEwithAES enc;
		
		try {
			
			
			User userDb = userService.findUser(user.getMail(), user.getDomain().getIdentifier());
			if(userDb==null) return false;
			byte[] encryptedChallenge = userDb.getEnciphermentKeyPass();
			if (encryptedChallenge==null) return false;
			
			enc = new SymmetricEnciphermentPBEwithAES(password,encryptedChallenge,Cipher.DECRYPT_MODE);
			String challengeResult = new String(enc.decryptString());
			
			if(challengeResult.equals(CHALLENGE)) res = true; else res = false;
			
		} catch (InvalidKeyException e) {
			res = false;
			log.error(e.toString(),e);
		} catch (NoSuchAlgorithmException e) {
			res = false;
			log.error(e.toString(),e);
		} catch (InvalidKeySpecException e) {
			log.error(e.toString(),e);
			res = false;
		} catch (NoSuchPaddingException e) {
			log.error(e.toString(),e);
			res = false;
		} catch (InvalidAlgorithmParameterException e) {
			res = false;
			log.error(e.toString(),e);
		} catch (IOException e) {
			res = false;
			log.error(e.toString(),e);
		}
		return res;
	}

	
	public Document decryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException {
		
		InputStream in =  null;
		OutputStream out = null; 
		
		FileInputStream res = null;
		File f = null;
		Document resdoc = null;
		
		SymmetricEnciphermentPBEwithAES enc;
		try {
			
			in = documentService.retrieveFileStream(doc, user);
			
			f = new File(workingDir+"/"+UUID.randomUUID());
			out = new FileOutputStream(f);
			
			enc = new SymmetricEnciphermentPBEwithAES(password,in,out,Cipher.DECRYPT_MODE);
			enc.decryptStream();
			
			res = new FileInputStream(f);
			
			User owner = userService.findUser(user.getLogin(), user.getDomainIdentifier());
			
			resdoc = documentService.updateFileContent(doc.getIdentifier(), res, res.available(), changeDocumentExtension(doc.getFileName()), doc.getType(), false,owner);
			
			FileLogEntry logEntry = new FileLogEntry(user.getMail(), user.getFirstName(), user.getLastName(),
	        		LogAction.FILE_DECRYPT, "Decrypt file Content", doc.getFileName(), doc.getSize(), doc.getType() );
	        
	        logEntryRepository.create(logEntry);
			
		
		} catch (InvalidKeyException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (InvalidKeySpecException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (NoSuchPaddingException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} catch (IOException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not decrypt document "+ doc.getIdentifier());
		} finally {
			
			if(in!=null) try {in.close();} catch (IOException e) {}
			if(out!=null) try {out.close();} catch (IOException e) {}
			if(res!=null) try {res.close();} catch (IOException e) {}
			if(f!=null&&f.exists()) f.delete();
		}
		
		return resdoc;
	}

	public Document encryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException{
		
		InputStream in =  null;
		OutputStream out = null; 
		Document resdoc = null;
		
		FileInputStream res = null;
		File f = null;
		
		
		SymmetricEnciphermentPBEwithAES enc;
		try {
			
			in = documentService.retrieveFileStream(doc, user);
			f = new File(workingDir+"/"+UUID.randomUUID());
			out = new FileOutputStream(f);
			
			enc = new SymmetricEnciphermentPBEwithAES(password,in,out,Cipher.ENCRYPT_MODE);
			enc.encryptStream();
			
			res = new FileInputStream(f);
			
			User owner = userService.findUser(user.getLogin(), user.getDomainIdentifier());
			
			resdoc = documentService.updateFileContent(doc.getIdentifier(), res, res.available(), changeDocumentExtension(doc.getFileName()), doc.getType(), true, owner);
			
			FileLogEntry logEntry = new FileLogEntry(user.getMail(), user.getFirstName(), user.getLastName(),
	        		LogAction.FILE_ENCRYPT, "Encrypt file Content", doc.getFileName(), doc.getSize(), doc.getType() );
			
	        logEntryRepository.create(logEntry);
			
		} catch (InvalidKeyException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (InvalidKeySpecException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (NoSuchPaddingException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} catch (IOException e) {
			log.error(e.toString(),e);
			throw new BusinessException(BusinessErrorCode.CANNOT_ENCRYPT_DOCUMENT,"can not encrypt document "+ doc.getIdentifier());
		} finally {
			
			if(in!=null) try {in.close();} catch (IOException e) {}
			if(out!=null) try {out.close();} catch (IOException e) {}
			if(res!=null) try {res.close();} catch (IOException e) {}
			if(f!=null&&f.exists()) f.delete();
		}
		
		return resdoc;
	}


	public boolean isUserEnciphermentKeyGenerated(User user) {
		if(user==null) return false;
		if(user.getEnciphermentKeyPass()!=null) return true;
		else return false;
	}


	public boolean isDocumentEncrypted(DocumentVo doc) {
		Document docdb = documentService.getDocument(doc.getIdentifier());
		if (docdb==null) return false;
		if(docdb.getEncrypted()==null) return false; else return docdb.getEncrypted();
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
