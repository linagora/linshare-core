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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.LinkFactory;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.utils.WelcomeMessageUtils;
import org.slf4j.Logger;

/**
 * Start page of application securedShare.
 */
public class Index {

    @ApplicationState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private ShareFacade shareFacade;
    @Inject
    private ParameterFacade parameterFacade;
    @Inject
    private LinkFactory linkFactory;
    @Inject
    private PersistentLocale persistentLocale;
    @Inject
    private Request request;
	@Inject
	private Messages messages;
	@Inject
	private Logger logger;

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    @Persist
    private List<ShareDocumentVo> shares;

    @SuppressWarnings("unused")
    @ApplicationState
    @Property
    private UserVo userVo;
    @Property
    private boolean userVoExists;

    @Property
    private String welcomeText;
    
    
	@Property
	@Persist
	private boolean advanced;

	
	@Persist
	@Property
	/** used to prevent the clearing of documentsVo with search*/
	private boolean flag;
	
	

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SuppressWarnings("unused")
    @SetupRender
    private void initList() throws BusinessException {
    	
        ParameterVo parameterVo = parameterFacade.loadConfig();
        if (userVoExists == false) {
        	Language language = WelcomeMessageUtils.getLanguageFromLocale(persistentLocale.get(), request.getLocale(), null);
            shares = new ArrayList<ShareDocumentVo>();
            welcomeText = WelcomeMessageUtils.getWelcomeText(parameterVo.getWelcomeTexts(), language,
                UserType.INTERNAL).getWelcomeText();

        } else {
        	Locale userLocale = null;
        	if (((userVo.getLocale())!= null) && (!userVo.getLocale().equals(""))) {
        		userLocale = new Locale(userVo.getLocale());
        	}
        	Language language = WelcomeMessageUtils.getLanguageFromLocale(persistentLocale.get(), request.getLocale(), userLocale);
            
        	if(!flag){
	        	shares = shareFacade.getAllSharingReceivedByUser(userVo);
        	}
            
            welcomeText = WelcomeMessageUtils.getWelcomeText(parameterVo.getWelcomeTexts(), language,
                userVo.getUserType()).getWelcomeText();

        }
        
    }

    @SuppressWarnings("unused")
    @OnEvent(value = "eventDeleteUniqueFromListDocument")
    private void deleteFromList(Object[] object) throws BusinessException {
        String uuid = (String) object[0];
        ShareDocumentVo shareddoc = searchShareVoByUUid(uuid);
        shareFacade.deleteSharing(shareddoc, userVo);
        resetListFiles(null);
    }

    private ShareDocumentVo searchShareVoByUUid(String uuid) {
        for (ShareDocumentVo shareDocumentVo : shares) {
            if (uuid.equals(shareDocumentVo.getIdentifier())) {
                return shareDocumentVo;
            }
        }
        return null;
    }

    
   Object onActivate(Object obj) {
	   return ErrorNotFound.class;
   } 
   
	@SuppressWarnings("unused")
	@CleanupRender
	private void initFlag(){
		//flag=false;
	}
   
   
	@OnEvent(value="eventToggleAdvancedSearchSorterComponent")
	public void toggleAdvancedSearch(Object[] object){
		flag=!flag;
		advanced = (Boolean) object[0];
	}
	/**
	 * The search component returns a document list, and we store it
	 * @param object : object[0] contains a List<DocumentVo>
	 */
	@SuppressWarnings("unchecked")
	@OnEvent(value="eventDocument")
	public void initListDoc(Object[] object){
		flag=true;
		this.shares = (List<ShareDocumentVo>)object[0];
	}
    
    @OnEvent(value="resetListFiles")
    public void resetListFiles(Object[] o1) {
    	flag=false;
		shares = shareFacade.getAllSharingReceivedByUser(userVo);
    }
    
    @OnEvent(value="inFileSearch")
    public void inSearch(Object[] o1) {
    	flag=true;
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addMessage(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
   
}
