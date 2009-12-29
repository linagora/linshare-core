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
package org.linagora.linShare.core.Facade.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linagora.linShare.core.Facade.SecuredUrlFacade;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.DocumentAdapter;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.SecuredUrlService;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredUrlFacadeImpl implements SecuredUrlFacade {

	private static final Logger logger = LoggerFactory.getLogger(SecuredUrlFacadeImpl.class);
	
	private final SecuredUrlService securedUrlService;
	
	private final DocumentAdapter documentAdapter;
	
	private final NotifierService notifierService;
	
	private final Templating templating;
	
	private final UserRepository<User> userRepository;
	
	public SecuredUrlFacadeImpl(SecuredUrlService securedUrlService,DocumentAdapter documentAdapter,
			final NotifierService notifierService, final Templating templating, 
			final UserRepository<User> userRepository) {
		this.securedUrlService=securedUrlService;
		this.documentAdapter=documentAdapter;
		this.notifierService = notifierService;
		this.templating = templating;
		this.userRepository = userRepository;
		
	}

	public DocumentVo getDocument(String alea, String urlPath,
			Integer documentId) throws BusinessException {
		return  documentAdapter.disassemble(securedUrlService.getDocument(alea, urlPath, documentId));
	}

	public DocumentVo getDocument(String alea, String urlPath, String password,
			Integer documentId) throws BusinessException {
		return documentAdapter.disassemble(securedUrlService.getDocument(alea, urlPath, password, documentId));
	}

	public List<DocumentVo> getDocuments(String alea, String urlPath)
			throws BusinessException {
		return documentAdapter.disassembleDocList(securedUrlService.getDocuments(alea, urlPath));
	}

	public List<DocumentVo> getDocuments(String alea, String urlPath,
			String password) throws BusinessException {
		return documentAdapter.disassembleDocList(securedUrlService.getDocuments(alea, urlPath, password));
	}

	public boolean isPasswordProtected(String alea, String urlPath) throws LinShareNotSuchElementException {
		return securedUrlService.isPasswordProtected(alea, urlPath);
	}

	public boolean isValid(String alea, String urlPath) {
		return securedUrlService.isValid(alea, urlPath);
	}

	public boolean isValid(String alea, String urlPath, String password) {
		return securedUrlService.isValid(alea, urlPath, password);
	}

	public boolean exists(String alea, String urlPath) {
		return securedUrlService.exists(alea,urlPath);
	}
	
	
	public void logDownloadedDocument(String alea, String urlPath, String password,
			Integer documentId, String email) {
		securedUrlService.logDownloadedDocument(alea, urlPath, password, documentId, email) ;
	}

	public void sendEmailNotification(String alea, String urlPath, String subject, String anonymousDownloadTemplateContent,String anonymousDownloadTemplateContentTxt, List<DocumentVo> docs, String email) {
		//Setting parameters and values for supply the template.
		Map<String,String> templateParams=new HashMap<String, String>();
		
		User owner = securedUrlService.getSecuredUrlOwner(alea, urlPath);
		
		//TEMPLATE sharedTemplateContent
		templateParams.put("${firstName}", owner.getFirstName());
		templateParams.put("${lastName}", owner.getLastName());
		
		templateParams.put("${email}", email); //email of the user (anonymous) who has has made the download of the file
		
		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		
		if (docs != null && docs.size()>0) {
			for (DocumentVo doc : docs) {
				names.append("<li>"+doc.getFileName()+"</li>");
				namesTxt.append(doc.getFileName()+"\n");
			}	
		}
		templateParams.put("${documentNames}", names.toString());
		templateParams.put("${documentNamesTxt}", namesTxt.toString());
		
		String messageForAnonymousDownloadTemplateContent = templating.getMessage(anonymousDownloadTemplateContent, templateParams);
		String messageForAnonymousDownloadTemplateContentTxt = templating.getMessage(anonymousDownloadTemplateContentTxt, templateParams);
		
		//send a notification by mail to the owner
		notifierService.sendNotification(null,owner.getMail(), subject, messageForAnonymousDownloadTemplateContent,messageForAnonymousDownloadTemplateContentTxt);
	}
	
	public Map<String, Calendar> getSharingsByMailAndFile(UserVo senderVo, DocumentVo document) {
		User sender = userRepository.findByLogin(senderVo.getLogin());

		List<SecuredUrl> secUrls = securedUrlService.getUrlsByMailAndFile(sender, document);

		Map<String, Calendar> res = new HashMap<String, Calendar>();
		for (SecuredUrl securedUrl : secUrls) {
			for (Contact recipient : securedUrl.getRecipients()) {
				res.put(recipient.getMail(), securedUrl.getExpirationTime());
			}
		}
		return res;
	}
}
