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
package org.linagora.linShare.view.tapestry.pages.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.LinkFactory;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.components.GuestEditForm;
import org.linagora.linShare.view.tapestry.components.WindowWithEffects;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IncludeJavaScriptLibrary("../../components/SizeOfPopup.js")
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
    private LinkFactory linkFactory;
  
    @Inject
    private Messages messages;
 
    @InjectComponent
	private GuestEditForm guestEditForm;
    
    @Inject
    private UserFacade userFacade;

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @ApplicationState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @ApplicationState
    @Property
    private UserVo userVo;

    @Environmental
    private RenderSupport renderSupport;

    @Persist
    private boolean flagFinishShare;

	@Property
	private String currentMessage;
    
    @Persist
    @Property
    private List<UserVo> users;

    private boolean shareSessionObjectsExists;
    
    @Persist
    @Property
    private boolean inSearch;


	private static Logger logger = LoggerFactory.getLogger(Index.class);

    
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void initList(){
    	if (!shareSessionObjectsExists) {
    		shareSessionObjects = new ShareSessionObjects();
    	}
    }
    
    @OnEvent(value="sharePanel")
    public void onSharePanel(Object[] elements) {
        if (shareSessionObjects.getDocuments() == null || shareSessionObjects.getDocuments().size() == 0) {
            shareSessionObjects.addMessage(messages.get("pages.index.message.toFile"));
            Link linkUser = linkFactory.createPageRenderLink("files/index", true);
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
    	}
    	//resize the share popup
        renderSupport.addScript(String.format("userSearchWindow.setSize(600, getHeightForPopup())"));
    }
    
    @OnEvent(value="resetListUsers")
    public void resetListUsers(Object[] o1) {
		inSearch=false;
		users = userFacade.searchUser("", "", "", userVo);
    }
    
    @OnEvent(value="inUserSearch")
    public void inSearch(Object[] o1) {
    	inSearch = true;
    }

    public List<String> getNotificationMessage() {
        List<String> notificationMessage = shareSessionObjects.getMessages();
        shareSessionObjects.setMessages(new ArrayList<String>());
        return notificationMessage;
    }

    public String getUserSearchWindowId() {
    	return userSearchWindow.getJSONId();
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addMessage(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
 
}
