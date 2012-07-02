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
package org.linagora.linshare.core.Facade.impl;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.Facade.DocumentFacade;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.DocumentTransformer;
import org.linagora.linshare.core.domain.transformers.impl.SignatureTransformer;
import org.linagora.linshare.core.domain.vo.DisplayableAccountOccupationEntryVo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.DocumentService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.impl.DocumentEntryServiceImpl;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentFacadeImpl implements DocumentFacade {

	private final DocumentService documentService;
	
	private final UserRepository<User> userRepository;
	
	private final DocumentTransformer documentTransformer;
	private final SignatureTransformer signatureTransformer;
	
	private final EnciphermentService enciphermentService;
	
	private final ShareService shareService;
	
	private final DocumentEntryService documentEntryService;
	
	private final AccountService accountService;
	
	private final DocumentEntryTransformer documentEntryTransformer;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentFacadeImpl.class);
	
	
	
	public DocumentFacadeImpl(DocumentService documentService,
			UserRepository<User> userRepository,
			final DocumentTransformer documentTransformer,
			final ShareService shareService,final SignatureTransformer signatureTransformer,EnciphermentService enciphermentService, DocumentEntryService documentEntryService, AccountService accountService, DocumentEntryTransformer documentEntryTransformer) {
		super();
		this.documentService = documentService;
		this.userRepository = userRepository;
		this.documentTransformer = documentTransformer;
		this.shareService = shareService;
		this.signatureTransformer=signatureTransformer;
		this.enciphermentService = enciphermentService;
		this.documentEntryService = documentEntryService;
		this.accountService = accountService;
		this.documentEntryTransformer = documentEntryTransformer;
	}

	
	
	@Override
	public String getMimeType(InputStream theFileStream, String theFilePath) throws BusinessException {
		// TODO To be removed 
		return null;
	}



	@Override
	public DocumentVo insertFile(InputStream file, long size, String fileName, String mimeType, UserVo owner) throws BusinessException {
		Account actor = accountService.findUserInDB(owner.getLsUid());
		DocumentEntry createDocumentEntry = documentEntryService.createDocumentEntry(actor, file, size, fileName);
		return documentEntryTransformer.disassemble(createDocumentEntry);
	}


	@Override
	public void removeDocument(UserVo actorVo, DocumentVo document, MailContainer mailContainer) throws BusinessException {
		Account actor = accountService.findUserInDB(actorVo.getLsUid());
		if(actor != null) {
			if (document instanceof ShareDocumentVo) {
				
			} else if (document instanceof DocumentVo) {
				// old method : 
//				documentService.deleteFileWithNotification(actor.getLsUid(),document.getIdentifier(), Reason.NONE, mailContainer);
				documentEntryService.deleteDocumentEntry(actor, document.getIdentifier());
			} else {
				logger.warn("Unsuccessful attempt to delete a weird document : " + document.getIdentifier());
			}
		} else {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user couldn't be found");
		}
		
		
//		TODO : Fix removeDocument
//		if (document instanceof ShareDocumentVo) {
//			// if this document is a sharedocumentVo, it means we received the document
//			// and we delete the sharing (for us)
//
//			User receiver = userRepository.findByLsUid(actor.getLogin());
//			
//			if (receiver == null) {
//				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user couldn't be found");
//			}
//			
//			// must find the share
//			Set<Share> listShare = receiver.getReceivedShares();
//			Share shareToRemove = null;
//			
//			for (Share share : listShare) {
//				if (share.getDocument().getUuid().equals(document.getIdentifier())) {
//					shareToRemove = share;
//					break;
//				}
//			}
//			
//			if (shareToRemove!=null) {
//				shareService.removeReceivedShareForUser(shareToRemove, receiver, receiver);				
//			} else {
//				throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "The sharing couldn't be found");
//			}
//		} else {
//			documentService.deleteFileWithNotification(actor.getLogin(),document.getIdentifier(), Reason.NONE, mailContainer);
//		}
	}

	
	@Override
	public DocumentVo getDocument(String login, String uuid) {
		Account actor = accountService.findUserInDB(login);
		if(actor != null) {
			DocumentEntry entry;
			try {
				entry = documentEntryService.findById(actor, uuid);
				return documentEntryTransformer.disassemble(entry);
			} catch (BusinessException e) {
				logger.error("can't get document : " + e.getMessage());
			}
		}
		return null;
	}
	
	
	@Override
	public InputStream downloadSharedDocument(ShareDocumentVo doc, UserVo actorVo) throws BusinessException {
//		Account actor = accountService.findUserInDB(actorVo.getLsUid());
//		
//		return documentEntryService.getDocumentStream(actor, doc.getIdentifier());
//		TODO : Fix downloadSharedDocument
		return documentService.downloadSharedDocument(doc, actorVo);
	}
	
	
	@Override
	public InputStream retrieveFileStream(DocumentVo doc, String lsUid) throws BusinessException {
		Account actor = accountService.findUserInDB(lsUid);
		
		return documentEntryService.getDocumentStream(actor, doc.getIdentifier());
	}
	
	
	@Override
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actorVo) throws BusinessException {
		return retrieveFileStream(doc, actorVo.getLsUid());
	}

	
	@Override
	public void insertSignatureFile(InputStream file, long size, String fileName, String mimeType, UserVo owner, DocumentVo document, X509Certificate signerCertificate) throws BusinessException {
//		TODO : Fix signature 
//		User docOwner =  userRepository.findByLsUid(owner.getLogin());
//		Document doc = documentService.getDocument(document.getIdentifier());
//		
//		documentService.insertSignatureFile(file, size, fileName, mimeType, docOwner, doc, signerCertificate);
	}
	
	
	@Override
	public boolean isSignedDocumentByCurrentUser(UserVo currentSigner, DocumentVo document){
//		TODO : Fix signature 
//		User currentSignerDoc =  userRepository.findByLsUid(currentSigner.getLogin());
//		
//		Document doc = documentService.getDocument(document.getIdentifier());
//		List<Signature> signatures = doc.getSignatures();
//		
//		boolean res = false;
//		
//		for (Signature signature : signatures) {
//			if (signature.getSigner().equals(currentSignerDoc)) {
//				res = true;
//				break;
//			}
//		}
//		
//		return res;
		return false;
	}
	
	
	@Override
	public boolean isSignedDocument(DocumentVo document){
//		TODO : Fix signature 
//		Document doc = documentService.getDocument(document.getIdentifier());
//		List<Signature> signatures = doc.getSignatures();
//		boolean res = false;
//		if(signatures!=null && signatures.size()>0) res = true;
//		return res;
		return false;
	}
	
	
	@Override
	public List<SignatureVo> getAllSignatures(DocumentVo document){
//		TODO : Fix signature 
//		Document doc = documentService.getDocument(document.getIdentifier());
//		return signatureTransformer.disassembleList(doc.getSignatures());
		return null;
	}

	
	@Override
	public SignatureVo getSignature(UserVo currentSigner,DocumentVo document) {
//		TODO : Fix signature 
		
//		User currentSignerDoc =  userRepository.findByLsUid(currentSigner.getLogin());
//		
//		Document doc = documentService.getDocument(document.getIdentifier());
//		List<Signature> signatures = doc.getSignatures();
//		
//		SignatureVo res = null;
//		
//		for (Signature signature : signatures) {
//			if (signature.getSigner().equals(currentSignerDoc)) {
//				res = signatureTransformer.disassemble(signature);
//				break;
//			}
//		}
//		return res;
		
		return null;
	}
	
	
	@Override
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc){
//		return documentService.retrieveSignatureFileStream(signaturedoc);
//		TODO : Fix signature 
		return null;
	}
	

	@Override
	public Long getUserAvailableQuota(UserVo userVo) throws BusinessException {
		Account account = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.getAvailableSize(account);
	}
	
	@Override
	public Long getUserMaxFileSize(UserVo userVo) throws BusinessException {
		Account account = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.getUserMaxFileSize(account);
	}
	
	
	@Override
	public List<DisplayableAccountOccupationEntryVo> getAccountOccupationStat(AccountOccupationCriteriaBean criteria) throws BusinessException {
		List<DisplayableAccountOccupationEntryVo> result = new ArrayList<DisplayableAccountOccupationEntryVo>();
		
		List<User> users = userRepository.findByCriteria(criteria);
		for (User user : users) {
			if (user.getDomain() !=null) {
				//user needs to be in a domain to compute quotas
				DisplayableAccountOccupationEntryVo accountOccupation = getAccountStats(user);
				result.add(accountOccupation);
			}
		}
		
		return result;
	}

	
	private DisplayableAccountOccupationEntryVo getAccountStats(User user) throws BusinessException {
		Long userAvailableQuota = documentEntryService.getAvailableSize(user);
		Long userTotalQuota = documentEntryService.getTotalSize(user);
		Long userUsedSize = 0L;
		for (Document doc : user.getDocuments()) {
			userUsedSize += doc.getSize();
		}
		DisplayableAccountOccupationEntryVo accountOccupation = new DisplayableAccountOccupationEntryVo(user.getFirstName(), 
				user.getLastName(), user.getMail(), user.getAccountType(), 
				userAvailableQuota, userTotalQuota, userUsedSize);
		return accountOccupation;
	}

	
	@Override
	public DocumentVo encryptDocument(DocumentVo doc,UserVo user, String password) throws BusinessException{
		// TODO : Fix enciphermentService
		return null;
//		Document docenc = enciphermentService.encryptDocument(doc, user, password);
//		return documentTransformer.disassemble(documentService.getDocument(docenc.getUuid()));
	}

	
	@Override
	public DocumentVo decryptDocument(DocumentVo doc, UserVo user,String password) throws BusinessException {
		// TODO : Fix enciphermentService
		return null;
//		Document docenc = enciphermentService.decryptDocument(doc, user, password);
//		return documentTransformer.disassemble(documentService.getDocument(docenc.getUuid()));
	}
	
	
	@Override
	public boolean isDocumentEncrypted(DocumentVo doc) {
		// TODO : Fix enciphermentService
		return false;
//		return enciphermentService.isDocumentEncrypted(doc);
	}

	
	@Override
	public Long getUserTotalQuota(UserVo userVo) throws BusinessException {
		Account account = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.getTotalSize(account);
	}
	
	@Override
	public DocumentVo updateDocumentContent(String currentFileUUID, InputStream file, long size, String fileName, String mimeType, UserVo ownerVo) throws BusinessException {
		Account actor = accountService.findUserInDB(ownerVo.getLsUid());
		return documentEntryTransformer.disassemble(documentEntryService.updateDocumentEntry(actor, currentFileUUID, file, size, fileName));
	}

	
	@Override
    public void renameFile(String userlogin, String docEntryUuid, String newName) {
		Account actor = accountService.findUserInDB(userlogin);
        try {
			documentEntryService.renameDocumentEntry(actor, docEntryUuid, newName);
		} catch (BusinessException e) {
			logger.error("Can't rename document : " + docEntryUuid + " : " + e.getMessage());
		}
    }
	
	
	@Override
    public void  updateFileProperties(String userlogin, String docEntryUuid, String newName, String comment){
		Account actor = accountService.findUserInDB(userlogin);
		if(comment == null) {
			comment = "";
		}
        try {
			documentEntryService.updateFileProperties(actor, docEntryUuid, newName, comment);
		} catch (BusinessException e) {
			logger.error("Can't update file properties document : " + docEntryUuid + " : " + e.getMessage());
		}
    }
    
	
	@Override
    public InputStream getDocumentThumbnail(String login, String docEntryUuid) {
		if(login == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		Account actor = accountService.findUserInDB(login);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		try {
			return documentEntryService.getDocumentThumbnailStream(actor, docEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + docEntryUuid + " : " + e.getMessage());
		}
    	return null;
    }

	
	@Override
    public boolean documentHasThumbnail(String login, String docEntryUuid) {
		if(login == null) {
			logger.error("Can't find user with null parametter.");
			return false;
		}
		
		Account actor = accountService.findUserInDB(login);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return documentEntryService.documentHasThumbnail(actor, docEntryUuid);
    }

	
	@Override
	public boolean isSignatureActive(UserVo user) {
//		User currentUser =  userRepository.findByLsUid(user.getLogin());
//		return documentService.isSignatureActive(currentUser);
		// TODO : Fix enciphermentService
		return false;
	}
	
	
	@Override
	public boolean isEnciphermentActive(UserVo user) {
//		User currentUser =  userRepository.findByLsUid(user.getLogin());
//		return documentService.isEnciphermentActive(currentUser);
		// TODO : Fix enciphermentService
		return false;
	}
	

	@Override
	public boolean isGlobalQuotaActive(UserVo userVo) throws BusinessException {
		Account actor = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.isGlobalQuotaActive(actor);
	}

	@Override
	public boolean isUserQuotaActive(UserVo userVo) throws BusinessException {
		Account actor = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.isUserQuotaActive(actor);
	}

	
	@Override
	public Long getGlobalQuota(UserVo userVo) throws BusinessException {
		Account actor = accountService.findUserInDB(userVo.getLsUid());
		return documentEntryService.getGlobalQuota(actor);
	}
	
}
