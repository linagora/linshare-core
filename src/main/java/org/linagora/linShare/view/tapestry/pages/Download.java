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
package org.linagora.linShare.view.tapestry.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.SecuredUrlFacade;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.ArchiveZipStream;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.components.PasswordPopup;
import org.linagora.linShare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download {

	private static final Logger logger = LoggerFactory.getLogger(Download.class);
	
	/***************************************************************************
	 * Properties
	 **************************************************************************/
	@Persist
	private String email;

	@Persist
	private String password;

	@Property
	private boolean passwordProtected;

	@Property
	private List<DocumentVo> documents;

	@Property
	private DocumentVo document;

	@Property
	private Integer index;

	@Property
	@Persist
	private String alea;

	/***************************************************************************
	 * Service injection
	 **************************************************************************/
	@InjectComponent
	private PasswordPopup passwordPopup;

	@Inject
	private Messages messages;

	@Inject
	private SecuredUrlFacade securedUrlFacade;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private JavaScriptSupport renderSupport;

	@Inject
	private BusinessMessagesManagementService messagesManagementService;
	
    @Inject
    private RequestGlobals requestGlobals;
	
	@Inject
	private MailContainerBuilder mailContainerBuilder;

	@SuppressWarnings("unused")
	@Inject @Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;
	
	
	public String[] getContext() {
		return new String[] { alea, index.toString()};
	}
	
	public boolean getContainsDocuments() {
		if (documents == null) 
			return false;
		else {
			return documents.size() >  0;
		}
	}

	public StreamResponse onActivate(String alea, Integer documentId) {
		
		//check current one with the old one
		if(this.alea!=null && !this.alea.equals(alea)){
			this.password = null; //reset cached password
		}
		this.alea = alea;
		

		try {
			if (!securedUrlFacade.exists(alea, componentResources.getPageName()
					.toLowerCase())) {
				throw new BusinessException(BusinessErrorCode.WRONG_URL,
						"secure url does not exists");
			}
			this.passwordProtected = (password == null && securedUrlFacade
					.isPasswordProtected(alea, componentResources.getPageName()
							.toLowerCase()));
			if (!securedUrlFacade.isValid(alea, componentResources
					.getPageName().toLowerCase(), password)) {
				throw new BusinessException("the secured url is not valid");
			}

			if(!passwordProtected){
				final DocumentVo doc = securedUrlFacade.getDocument(alea,
						componentResources.getPageName().toLowerCase(), password,
						documentId);
				final InputStream file = documentFacade.retrieveFileStream(doc,email);
				
				securedUrlFacade.logDownloadedDocument(alea, componentResources.getPageName().toLowerCase(), password, documentId, email);
	
				List<DocumentVo> docList = new ArrayList<DocumentVo>();
				docList.add(doc);
				
				MailContainer mailContainer = mailContainerBuilder.buildMailContainer(null, null);
				securedUrlFacade.sendEmailNotification(alea, componentResources.getPageName().toLowerCase(), mailContainer, docList,email);
				
				return new FileStreamResponse(doc, file); 
			} else {
				return null;
			}
			
			
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}

	public void onActivate(String alea) {
		//check current one with the old one
		if(this.alea!=null && !this.alea.equals(alea)){
			this.password = null; //reset cached password
		}
		this.alea = alea;
		
		//find email parameter (this parameter can be altered by the user, it is just an information)
		this.email = requestGlobals.getHTTPServletRequest().getParameter("email");
		if(email==null) email = "";
		

		try {
			if (!securedUrlFacade.exists(alea, componentResources.getPageName()
					.toLowerCase())) {
				throw new BusinessException(BusinessErrorCode.WRONG_URL,
						"secure url does not exists");
			}
			this.passwordProtected = (password == null && securedUrlFacade
					.isPasswordProtected(alea, componentResources.getPageName()
							.toLowerCase()));
			if (!securedUrlFacade.isValid(alea, componentResources
					.getPageName().toLowerCase(), password)) {
				throw new BusinessException("the secured url is not valid");
			}

			documents = securedUrlFacade.getDocuments(alea, componentResources.getPageName().toLowerCase(), password);
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	public StreamResponse onActionFromDownloadThemAll(String alea) throws IOException, BusinessException{
		
		//check current one with the old one
		if(this.alea!=null && !this.alea.equals(alea)){
			this.password = null; //reset cached password
		}
		this.alea = alea;
		
		
		if (!securedUrlFacade.exists(alea, componentResources.getPageName()
				.toLowerCase())) {
			throw new BusinessException(BusinessErrorCode.WRONG_URL,
					"secure url does not exists");
		}
		this.passwordProtected = (password == null && securedUrlFacade
				.isPasswordProtected(alea, componentResources.getPageName()
						.toLowerCase()));
		if (!securedUrlFacade.isValid(alea, componentResources
				.getPageName().toLowerCase(), password)) {
			throw new BusinessException("the secured url is not valid");
		}
		
		if(!passwordProtected){
			documents = securedUrlFacade.getDocuments(alea, componentResources.getPageName().toLowerCase(), password);
			
			Map<String,InputStream> map = new HashMap<String, InputStream>();
			
			for (DocumentVo d : documents) {
				map.put(d.getFileName(), documentFacade.retrieveFileStream(d,email));
			}	
			
			//prepare an archive zip
			ArchiveZipStream ai = new ArchiveZipStream(map);
			
			securedUrlFacade.logDownloadedDocument(alea, componentResources.getPageName().toLowerCase(), password, null,email);

			MailContainer mailContainer = mailContainerBuilder.buildMailContainer(null, null);
			securedUrlFacade.sendEmailNotification(alea, componentResources.getPageName().toLowerCase(), mailContainer, documents, email);
			
			return (new FileStreamResponse(ai,null));
		} else {
			return null;
		}
	}
	
	
	public Zone onValidateFormFromPasswordPopup() {
		if (securedUrlFacade.isValid(alea, componentResources.getPageName()
				.toLowerCase(), passwordPopup.getPassword())) {
			password = passwordPopup.getPassword();
			return passwordPopup.formSuccess();
		} else {
			passwordPopup
					.getFormPassword()
					.recordError(
							messages
									.get("components.download.passwordPopup.error.message"));
			return passwordPopup.formFail();
		}
	}

	@AfterRender
	void afterRender() {
		if (passwordProtected)
			renderSupport.addScript("window_passwordPopup.showCenter(true)");
	}
	
	public String getFriendlySize() {
		return FileUtils.getFriendlySize(document.getSize(), messages);
	}

}
