/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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
package org.linagora.linshare.view.tapestry.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
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
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.SecuredUrlFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.components.PasswordPopup;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
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
	private String uuid;

	/***************************************************************************
	 * Service injection
	 **************************************************************************/
	@InjectComponent
	private PasswordPopup passwordPopup;

	@Inject
	private Messages messages;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport renderSupport;

	@Inject
	private BusinessMessagesManagementService messagesManagementService;
	
    @Inject
    private RequestGlobals requestGlobals;
	
	@Inject @Symbol("linshare.logo.webapp.visible")
	@Property
	private boolean linshareLogoVisible;
	
	@Inject @Symbol("linshare.display.licenceTerm")
	@Property
	private boolean linshareLicenceTerm;
	
	
	@Inject
	private DocumentFacade documentFacade;
	
	@Inject
	private SecuredUrlFacade securedUrlFacade;
	
	
	
	public String[] getContext() {
		return new String[] { uuid, index.toString()};
	}
	
	public boolean getContainsDocuments() {
		if (documents == null) 
			return false;
		else {
			return documents.size() >  0;
		}
	}

	public StreamResponse onActivate(String anonymousUrlUuid, Integer documentId) {
		logger.debug("documenbtId : " + String.valueOf(documentId));
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);

		try {
			checkUrl(anonymousUrlUuid);
			if(!passwordProtected){
				documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
				DocumentVo doc = documents.get(documentId);
				final InputStream file = securedUrlFacade.retrieveFileStream(anonymousUrlUuid, doc.getIdentifier(), password);
				return new FileStreamResponse(doc, file); 
			} else {
				return null;
			}
			
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}

	/**
	 * this is the first method called in this page.
	 * @param anonymousUrlUuid
	 */
	public void onActivate(String anonymousUrlUuid) {
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);
		try {
			checkUrl(anonymousUrlUuid);
			documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	private void setCurrentAnonymousUrlUuid(String uuid) {
		logger.debug("uuid : " + uuid);
		//check current one with the old one
		if(this.uuid!=null && !this.uuid.equals(uuid)){
			this.password = null; //reset cached password
		}
		this.uuid = uuid;
	}
	
	
	private void checkUrl(String uuid) {
		try {
			if (!securedUrlFacade.exists(uuid, componentResources.getPageName().toLowerCase())) {
				String msg = "secure url does not exists";
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WRONG_URL, msg);
			}
			this.passwordProtected = (securedUrlFacade.isPasswordProtected(uuid) && password == null);
			if (!securedUrlFacade.isValid(uuid, password)) {
				String msg = "the secured url is not valid";
				logger.error(msg);
				throw new BusinessException(msg);
			}
	
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
	}

	public StreamResponse onActionFromDownloadThemAll(String anonymousUrlUuid) throws IOException, BusinessException{
		
		setCurrentAnonymousUrlUuid(anonymousUrlUuid);
		try {
			checkUrl(anonymousUrlUuid);
			documents = securedUrlFacade.getDocuments(anonymousUrlUuid, password);
		
			if(!passwordProtected){
				return securedUrlFacade.retrieveArchiveZipStream(anonymousUrlUuid, password);
			}
		} catch (BusinessException e) {
			messagesManagementService.notify(e);
		}
		return null;
	}
	
	
	public Zone onValidateFormFromPasswordPopup() {
		if (securedUrlFacade.isValid(uuid, passwordPopup.getPassword())) {
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

	public boolean getDisplayLogo() {
		if(linshareLicenceTerm || linshareLogoVisible) {
			return true;
		}
		return false;
	}
}
