/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.SignatureTransformer;
import org.linagora.linshare.core.domain.vo.DisplayableAccountOccupationEntryVo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.EntryService;
import org.linagora.linshare.core.service.SignatureService;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentFacadeImpl implements DocumentFacade {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFacadeImpl.class);
	
	private final UserRepository<User> userRepository;
	
	private final SignatureTransformer signatureTransformer;
	
	private final EnciphermentService enciphermentService;
	
	private final DocumentEntryService documentEntryService;
	
	private final EntryService entryService;
	
	private final AccountService accountService;
	
	private final DocumentEntryTransformer documentEntryTransformer;
	
	private final SignatureService signatureService;
	
	
	public DocumentFacadeImpl(UserRepository<User> userRepository, SignatureTransformer signatureTransformer,
			EnciphermentService enciphermentService, DocumentEntryService documentEntryService, AccountService accountService,
			DocumentEntryTransformer documentEntryTransformer, SignatureService signatureService, 
			EntryService entryService) {
		super();
		this.userRepository = userRepository;
		this.signatureTransformer = signatureTransformer;
		this.enciphermentService = enciphermentService;
		this.documentEntryService = documentEntryService;
		this.accountService = accountService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.signatureService = signatureService;
		this.entryService = entryService;
	}


	@Override
	public DocumentVo insertFile(InputStream in, String fileName, UserVo owner) throws BusinessException {
		logger.debug("insert files for document entries");
		Account actor = accountService.findByLsUuid(owner.getLsUuid());
		DocumentEntry createDocumentEntry = documentEntryService.createDocumentEntry(actor, in, fileName);
		return documentEntryTransformer.disassemble(createDocumentEntry);
	}


	@Override
	public void removeDocument(UserVo actorVo, DocumentVo document) throws BusinessException {
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		if(actor != null) {
			entryService.deleteAllShareEntriesWithDocumentEntry(actor, document.getIdentifier());
		} else {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user couldn't be found");
		}
	}

	
	@Override
	public DocumentVo getDocument(String login, String uuid) {
		Account actor = accountService.findByLsUuid(login);
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
	public InputStream retrieveFileStream(DocumentVo doc, String lsUid) throws BusinessException {
		Account actor = accountService.findByLsUuid(lsUid);
		return documentEntryService.getDocumentStream(actor, doc.getIdentifier());
	}
	
	
	@Override
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actorVo) throws BusinessException {
		return retrieveFileStream(doc, actorVo.getLsUuid());
	}

	
	@Override
	public void insertSignatureFile(InputStream file, long size, String fileName, UserVo ownerVo, DocumentVo documentVo, X509Certificate signerCertificate) throws BusinessException {
		Account actor = accountService.findByLsUuid(ownerVo.getLsUuid());
		DocumentEntry documentEntry = documentEntryService.findById(actor, documentVo.getIdentifier());
		signatureService.createSignature(actor, documentEntry.getDocument(), file, size, fileName, signerCertificate);
	}
	
	
	@Override
	public List<SignatureVo> getAllSignatures(UserVo userVo, DocumentVo documentVo){
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		try {
			DocumentEntry document = documentEntryService.findById(actor, documentVo.getIdentifier());
			return signatureTransformer.disassembleList(new ArrayList<Signature>(document.getDocument().getSignatures()));
		} catch (BusinessException e) {
			logger.error("Can't find document : " + documentVo.getIdentifier() + ": " + e.getMessage());
		}
		return null;
	}

	
	@Override
	public SignatureVo getSignature(UserVo currentSigner, DocumentVo documentVo) {
		Account actor = accountService.findByLsUuid(currentSigner.getLsUuid());
		try {
			DocumentEntry document = documentEntryService.findById(actor, documentVo.getIdentifier());
			SignatureVo res = null;
			for (Signature signature : document.getDocument().getSignatures()) {
				if (signature.getSigner().equals(actor)) {
					res = signatureTransformer.disassemble(signature);
					break;
				}
			}
			return res;
			
		} catch (BusinessException e) {
			logger.error("Can't find document : " + documentVo.getIdentifier() + ": " + e.getMessage());
		}
		return null;
	}
	
	
	
	@Override
	public boolean isSignedDocument(String userLsUuid, DocumentVo documentVo){
		// Fix me : just write a finder
		boolean res = false;
		Account actor = accountService.findByLsUuid(userLsUuid);
		try {
			DocumentEntry document = documentEntryService.findById(actor, documentVo.getIdentifier());
			Set<Signature> signatures = document.getDocument().getSignatures();
			if(signatures!=null && signatures.size()>0) res = true;
		} catch (BusinessException e) {
			logger.error("Can't find document : " + documentVo.getIdentifier() + ": " + e.getMessage());
		}
		return res;
	}
	

	@Override
	public boolean isSignedDocumentByCurrentUser(UserVo currentSigner, DocumentVo documentVo) throws BusinessException{
		
		Account actor = accountService.findByLsUuid(currentSigner.getLsUuid());
		DocumentEntry doc = documentEntryService.findById(actor, documentVo.getIdentifier());
		Set<Signature> signatures = doc.getDocument().getSignatures();
		
		boolean res = false;
		
		for (Signature signature : signatures) {
			if (signature.getSigner().equals(actor)) {
				res = true;
				break;
			}
		}
		
		return res;
	}
	
	
	@Override
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc){
		Signature signature = signatureService.findByUuid(signaturedoc.getIdentifier());
		return signatureService.getDocumentStream(signature);
	}
	

	@Override
	public Long getUserAvailableQuota(UserVo userVo) throws BusinessException {
		Account account = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.getAvailableSize(account);
	}
	
	@Override
	public Long getUserMaxFileSize(UserVo userVo) throws BusinessException {
		Account account = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.getUserMaxFileSize(account);
	}
	
	@Override
	public Long getUserAvailableSize(UserVo userVo) throws BusinessException {
		Account account = accountService.findByLsUuid(userVo.getLsUuid());

		return Math.min(documentEntryService.getAvailableSize(account),
				documentEntryService.getUserMaxFileSize(account));
	}
	
	// FIXME : ugly
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
		for (Entry entry : user.getEntries()) {
			if(entry.getEntryType().equals(EntryType.DOCUMENT)) {
				userUsedSize += ((DocumentEntry)entry).getSize();
			}
		}
		DisplayableAccountOccupationEntryVo accountOccupation = new DisplayableAccountOccupationEntryVo(user.getFirstName(), 
				user.getLastName(), user.getMail(), user.getAccountType(), 
				userAvailableQuota, userTotalQuota, userUsedSize);
		return accountOccupation;
	}

	
	@Override
	public DocumentVo encryptDocument(DocumentVo docVo, UserVo userVo, String password) throws BusinessException{
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		DocumentEntry documentEntry = enciphermentService.encryptDocument(actor, docVo.getIdentifier(),actor, password);
		return documentEntryTransformer.disassemble(documentEntry);
	}

	
	@Override
	public DocumentVo decryptDocument(DocumentVo docVo, UserVo userVo,String password) throws BusinessException {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		DocumentEntry documentEntry = enciphermentService.decryptDocument(actor, docVo.getIdentifier(),actor, password);
		return documentEntryTransformer.disassemble(documentEntry);
	}
	
	
	@Override
	public Long getUserTotalQuota(UserVo userVo) throws BusinessException {
		Account account = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.getTotalSize(account);
	}
	
	
	@Override
	public DocumentVo updateDocumentContent(String currentFileUUID, InputStream file, long size, String fileName, UserVo ownerVo, String friendlySize) throws BusinessException {
		Account actor = accountService.findByLsUuid(ownerVo.getLsUuid());
		
		DocumentEntry originalEntry = documentEntryService.findById(actor, currentFileUUID);
		String originalFileName = originalEntry.getName();
		
		DocumentEntry documentEntry = documentEntryService.updateDocumentEntry(actor, currentFileUUID, file, size, fileName);
		if(documentEntry.isShared()){
			//send email, file has been replaced ....
			entryService.sendSharedUpdateDocNotification(documentEntry, friendlySize, originalFileName);
		}
           
		return documentEntryTransformer.disassemble(documentEntry);
	}
	
	@Override
    public void renameFile(String userlogin, String docEntryUuid, String newName) {
		Account actor = accountService.findByLsUuid(userlogin);
        try {
			documentEntryService.renameDocumentEntry(actor, docEntryUuid, newName);
		} catch (BusinessException e) {
			logger.error("Can't rename document : " + docEntryUuid + " : " + e.getMessage());
		}
    }
	
	
	@Override
    public void  updateFileProperties(String userlogin, String docEntryUuid, String newName, String comment){
		Account actor = accountService.findByLsUuid(userlogin);
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
    public InputStream getDocumentThumbnail(String actorUuid, String docEntryUuid) {
		if(actorUuid == null) {
			logger.error("Can't find user with null parameter.");
			return null;
		}
		
		Account actor = accountService.findByLsUuid(actorUuid);
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
    public boolean documentHasThumbnail(String actorUuid, String docEntryUuid) {
		if(actorUuid == null) {
			logger.error("Can't find user with null parameter.");
			return false;
		}
		
		Account actor = accountService.findByLsUuid(actorUuid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return documentEntryService.documentHasThumbnail(actor, docEntryUuid);
    }

	
	@Override
	public boolean isSignatureActive(UserVo userVo) {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.isSignatureActive(actor);
	}
	
	
	@Override
	public boolean isEnciphermentActive(UserVo userVo) {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.isEnciphermentActive(actor);
	}
	

	@Override
	public boolean isGlobalQuotaActive(UserVo userVo) throws BusinessException {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.isGlobalQuotaActive(actor);
	}

	@Override
	public boolean isUserQuotaActive(UserVo userVo) throws BusinessException {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.isUserQuotaActive(actor);
	}

	
	@Override
	public Long getGlobalQuota(UserVo userVo) throws BusinessException {
		Account actor = accountService.findByLsUuid(userVo.getLsUuid());
		return documentEntryService.getGlobalQuota(actor);
	}
	
	@Override
	public MimeTypeStatus getMimeTypeStatus(String login, String uuid) {
		Account actor = accountService.findByLsUuid(login);
		if (actor != null) {
			DocumentEntry entry;
			try {
				entry = documentEntryService.findById(actor, uuid);
				return documentEntryService.getDocumentMimeTypeStatus(entry);
			} catch (BusinessException e) {
				logger.error("can't get document : " + e.getMessage());
			}
		}
		return null;
	}
}
