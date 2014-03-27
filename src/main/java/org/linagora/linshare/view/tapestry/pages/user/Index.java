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
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.view.tapestry.pages.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.GuestEditForm;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library={"../../components/SizeOfPopup.js"})
public class Index {

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=570", "height=450"})
    private WindowWithEffects userSearchWindow;
/*
    @InjectComponent
    private Zone guestEditTemplateZone;
*/
    @Inject
    private Response response;

    @Inject
    private Templating templating;
    
    @Inject
    private PageRenderLinkSource pageRenderLinkSource;
  
    @Inject
    private Messages messages;
 
    @InjectComponent
	private GuestEditForm guestEditForm;
    
    @Inject
    private UserFacade userFacade;
    
    @Inject
    private AbstractDomainFacade domainFacade;
    
    @Inject
    private FunctionalityFacade functionalityFacade;


    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    @Property
    private UserVo userVo;
	
	@Property
	private boolean userVoExists;

    @Environmental
    private JavaScriptSupport renderSupport;

    @Persist
    private boolean flagFinishShare;
    
    @Persist
    @Property
    private List<UserVo> users;

    private boolean shareSessionObjectsExists;
    
    @Persist
    @Property
    private boolean inSearch;
    
	@Inject @Symbol("linshare.users.internal.defaultView.showAll")
	@Property
	private boolean showAll;
	
	@SuppressWarnings("unused")
	@Property
	private boolean superadmin;
	   
    @Property
    private boolean showUser;


	private static Logger logger = LoggerFactory.getLogger(Index.class);

    
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void initList(){
    	showUser = userVo.isSuperAdmin() | functionalityFacade.isEnableUserTab(userVo.getDomainIdentifier());
    	if (!shareSessionObjectsExists) {
    		shareSessionObjects = new ShareSessionObjects();
    	}
    	superadmin=(userVoExists && userVo.getRole().equals(Role.SUPERADMIN));
    }
    
    @OnEvent(value="sharePanel")
    public void onSharePanel(Object[] elements) {
        if (shareSessionObjects.getDocuments() == null || shareSessionObjects.getDocuments().size() == 0) {
            shareSessionObjects.addMessage(messages.get("pages.index.message.toFile"));
            Link linkUser = pageRenderLinkSource.createPageRenderLink("files/index");
            try {
                response.sendRedirect(linkUser);
            } catch (IOException ex) {
                throw new TechnicalException("Bad URL" + ex);
            }
        } else {
        	flagFinishShare=true;
        }
    }

    @OnEvent(value="deleteFromSharePanel")
    public void onDelete(Object[] elements) {
        if (elements.length == 1) {
            shareSessionObjects.removeUser((UserVo) elements[0]);
        }
    }

    @OnEvent(value="clearListObject")
    public void onReset() {
    	reinitASO();
    }
    
	private void reinitASO(){
		shareSessionObjects = new ShareSessionObjects();
	}
	/**
	 * show the popup if it is to be shown
	 */
    @AfterRender
    public void afterRender() {
    	if (flagFinishShare) {
            renderSupport.addScript(String.format("confirmWindow.showCenter(true)"));
            flagFinishShare=false;
            shareSessionObjects.setMessages(new ArrayList<String>());
            shareSessionObjects.setErrors(new ArrayList<String>());
    	}
    	//resize the share popup
        renderSupport.addScript(String.format("userSearchWindow.setSize(600, getHeightForPopup())"));
    }
    
    @OnEvent(value="resetListUsers")
    public void resetListUsers(Object[] o1) throws BusinessException {
		inSearch=false;
		if (showAll || userVo.isRestricted()) {
			users = userFacade.searchUser("", "", "", null, userVo);
		}
		else {
			users = userFacade.searchGuest(userVo);
		}
    }
    
    @OnEvent(value="inUserSearch")
    public void inSearch(Object[] o1) {
    	inSearch = true;
    }
    
    public String getUserSearchWindowId() {
    	return userSearchWindow.getJSONId();
    }
    
    public boolean getUserCanCreateGuest() throws BusinessException {
    	return domainFacade.userCanCreateGuest(userVo);
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
 
}
