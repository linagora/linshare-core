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
package org.linagora.linshare.view.tapestry.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.WelcomeMessageUtils;
import org.slf4j.Logger;

/**
 * Start page of application securedShare.
 */
public class Index {

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private ShareFacade shareFacade;
    @Inject
    private AbstractDomainFacade domainFacade;
	@Inject
	private PageRenderLinkSource linkFactory;
    @Inject
    private PersistentLocale persistentLocale;
    @Inject
    private Request request;
	@Inject
	private Messages messages;
    @Inject
    private Response response;
	@Inject
	private Logger logger;
	

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    @Persist
    private List<ShareDocumentVo> shares;

    @SessionState
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
	
	public Object onActivate() {
		if (userVoExists && userVo.isSuperAdmin()) {
			return org.linagora.linshare.view.tapestry.pages.user.Index.class;
		}
		return null;
	}
	
    @SuppressWarnings("unused")
    @SetupRender
    private void initList() throws BusinessException {
    	
        if (!userVoExists) {
        	Locale locale = WelcomeMessageUtils.getNormalisedLocale(persistentLocale.get(), request.getLocale(), null);
        	Language language = WelcomeMessageUtils.getLanguageFromLocale(locale);
            shares = new ArrayList<ShareDocumentVo>();
            welcomeText = "blabla";
            //welcomeText = WelcomeMessageUtils.getWelcomeText(parameterVo.getWelcomeTexts(), language,
            //    UserType.INTERNAL).getWelcomeText();

        } else {
            AbstractDomainVo domain= domainFacade.retrieveDomain(userVo.getDomainIdentifier());
            
        	Locale userLocale = null;
        	if (((userVo.getLocale())!= null) && (!userVo.getLocale().equals(""))) {
        		userLocale = new Locale(userVo.getLocale());
        	}
        	Locale locale = WelcomeMessageUtils.getNormalisedLocale(persistentLocale.get(), request.getLocale(), userLocale);
        	Language language = WelcomeMessageUtils.getLanguageFromLocale(locale);
            
        	if(!flag){
	        	shares = shareFacade.getAllSharingReceivedByUser(userVo);
        	}
        	
            welcomeText = WelcomeMessageUtils.getWelcomeText(domainFacade.getMessages(domain.getIdentifier()).getWelcomeTexts(), language,
                userVo.getUserType()).getWelcomeText();

        }
        
    }

    @OnEvent(value = "eventDeleteUniqueFromListDocument")
    private void deleteFromList(Object[] object) throws BusinessException {
        String uuid = (String) object[0];
        
        ShareDocumentVo shareddoc = searchShareVoByUUid(uuid);
        shareFacade.deleteSharing(shareddoc, userVo);
        resetListFiles(null);
    }
    
	@OnEvent(value="eventDeleteFromListDocument")
	public void deleteFromListDocument(Object[] object) throws BusinessException{
		 
		for(Object currentObject:object){
			ShareDocumentVo share = (ShareDocumentVo)currentObject;
	        shareFacade.deleteSharing(share, userVo);
	        shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"), share.getFileName()));
	        resetListFiles(null);
		}
	}
	
	/**
	 * sign the document 
	 * Invoked when a user clicks on "sign" button in the searched document list
	 * @param object a DocumentVo[]
	 */
	@OnEvent(value="eventSignatureFromListDocument")
	public void signatureFromListDocument(Object[] object){

		List<String> identifiers = new ArrayList<String>();
		
		//context is a list of document (tab files)
		identifiers.add(DocToSignContext.SHARED.toString());
		
		for(Object currentObject:object){
			DocumentVo doc =  (DocumentVo) currentObject;
			
			if(doc.getEncrypted()) {
				shareSessionObjects.addWarning(messages.get("pages.index.message.signature.encryptedFiles"));
				return; //quit
			} else {
				identifiers.add(doc.getIdentifier());
			}
		}
		
        Link mylink = linkFactory.createPageRenderLinkWithContext("signature/SelectPolicy", identifiers.toArray());
		
        try {
            response.sendRedirect(mylink);
        } catch (IOException ex) {
            throw new TechnicalException("Bad URL" + ex);
        }
		
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
		this.shares = (List<ShareDocumentVo>)Arrays.copyOf(object,1)[0];
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
    
    public String getPageTitle() {
    	return messages.get("components.myborderlayout.home.title");
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
   
}
