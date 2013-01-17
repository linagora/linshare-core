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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



public class WebServiceShareFacadeImpl implements WebServiceShareFacade {

	
	private final DocumentEntryService documentEntryService;
	private final  AccountService accountService;
	private final  ShareFacade shareFacade;
	
	private static final Logger logger = LoggerFactory.getLogger(WebServiceShareFacadeImpl.class);

	public WebServiceShareFacadeImpl(final DocumentEntryService documentEntryService, final AccountService accountService, final ShareFacade shareFacade) {
		this.documentEntryService = documentEntryService;
		this.accountService = accountService;
		this.shareFacade=shareFacade;
	}
	
	
	@Override
	public User checkAuthentication() throws BusinessException {
		
		User actor = getAuthentication();
		
		if (actor== null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		
		return actor;
	}
	
	
	
	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare) throws BusinessException{

		User actor = getAuthentication();
		
		if ((actor instanceof Guest  && !actor.getCanUpload())) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
 
		// fetch the document
		DocumentEntry documentEntry;
		try {
			documentEntry = documentEntryService.findById(actor, uuid);
		} catch (BusinessException e) {
			throw e;
		}
		
		if (documentEntry == null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_NOT_FOUND, "Document not found");
		}
 
		DocumentVo docVo = new DocumentVo(documentEntry.getUuid(), documentEntry.getName(), documentEntry.getComment(), documentEntry.getCreationDate(), documentEntry.getExpirationDate(), documentEntry.getType(), documentEntry.getEntryOwner().getLsUuid(), documentEntry.getCiphered(), documentEntry.getShareEntries().size()>0, documentEntry.getSize());
		
		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		listDoc.add(docVo);
 
		List<String> listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);
 
		SuccessesAndFailsItems<ShareDocumentVo> successes;
 
		
		//give personal message and subject in WS in the future? null at this time 
		String message = null;
		String subject = null;
		MailContainer mailContainer = new MailContainer (actor.getExternalMailLocale(),message,subject);
		
		UserVo uo =  new UserVo(actor);
		
		try {
			successes = shareFacade.createSharingWithMailUsingRecipientsEmail(uo, listDoc, listRecipient, (securedShare==1) , mailContainer);
			
		} catch (BusinessException e) {
			throw e;
		}
 
		if ( (successes.getSuccessesItem()==null) || ((successes.getFailsItem()!=null) && (successes.getFailsItem().size()>0))) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT, "Could not share the document");
		}
 
	}
	
	
	@Override
	public void multiplesharedocuments(String targetMail, List<String> uuid, int securedShare, String messageOpt) throws BusinessException {
		
		User actor = getAuthentication();
		
		List<String>  listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);
		 
		 
		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		
		// fetch the document
		DocumentEntry documentEntry;
			
		for (String onefileid : uuid) {
			documentEntry = documentEntryService.findById(actor, onefileid);
			DocumentVo docVo = new DocumentVo(documentEntry.getUuid(), documentEntry.getName(), documentEntry.getComment(), documentEntry.getCreationDate(), documentEntry.getExpirationDate(), documentEntry.getType(), documentEntry.getEntryOwner().getLsUuid(), documentEntry.getCiphered(), documentEntry.getShareEntries().size()>0, documentEntry.getSize());
			listDoc.add(docVo);
		}
			
		
		//give personal message and subject in WS in the future? null at this time 
		String message = (messageOpt==null)? "": messageOpt;
		String subject = null;
		MailContainer mailContainer = new MailContainer (actor.getExternalMailLocale(),message,subject);
		
		UserVo uo =  new UserVo(actor);
		
		SuccessesAndFailsItems<ShareDocumentVo> successes;
 
		try {
			successes = shareFacade.createSharingWithMailUsingRecipientsEmail(uo, listDoc, listRecipient, (securedShare==1), mailContainer);
		} catch (BusinessException e) {
			throw e;
		}
	
		if ( (successes.getSuccessesItem()==null) || ((successes.getFailsItem()!=null) && (successes.getFailsItem().size()>0))) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT, "Could not share the document");
		}
 
	}
	
	
	//#############  utility methods
	
	
	private User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
	     String name =  (auth != null) ? auth.getName() : null; //get logged in username
	     if (name == null) return null;
	     User user = (User) accountService.findByLsUid(name);
	     return user;
	}

}
