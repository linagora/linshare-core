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
package org.linagora.linShare.view.tapestry.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.pages.user.Index;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.slf4j.Logger;

/** This component is used to create a new user.
 */
@SupportsInformalParameters
@IncludeJavaScriptLibrary(value = {"GuestEditForm.js"})
public class GuestEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @SuppressWarnings("unused")
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> users;

    @SuppressWarnings("unused")
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private String JSONid;
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private UserFacade userFacade;
    
    @Inject
    private DomainFacade domainFacade;

    @InjectComponent
    private Form guestCreateForm;
	
    @Inject
    private Messages messages;

    @Inject
    private Logger logger;

	@Inject
	private PropertiesSymbolProvider propertiesSymbolProvider;

	@Inject
	private Templating templating;


	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @ApplicationState
    @Property
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    
	@Environmental
	private RenderSupport renderSupport;

    @Property
    private String mail;

    @Property
    private String firstName;

    @Property
    private String lastName;

//    @Property
//    private boolean uploadGranted;
	
	@Property
	private boolean guestsAllowedToCreateGuest;
    
    @Property
    private boolean restrictedGuest;

    @Property
    private String customMessage;

    @Property
    private String comment;
	
	
	@Persist("flash")
	private List<String> recipientsEmail;
	
	@Persist("flash")
	@Property
	private String recipientsSearch;

    private boolean userAlreadyExists = false;

    @Inject
    private ComponentResources componentResources;
    
    @Inject
    private MailContainerBuilder mailBuilder;
	
	
	@SetupRender
	void init() throws BusinessException {
		recipientsSearch = MailCompletionService.formatLabel(userLoggedIn);
		
		if (userLoggedIn.isRestricted()) {
			restrictedGuest=true;
		}
		
		guestsAllowedToCreateGuest = false;
		if (userLoggedIn.getDomainIdentifier() != null) {
			DomainVo domain = domainFacade.retrieveDomain(userLoggedIn.getDomainIdentifier());
			guestsAllowedToCreateGuest = domain.getParameterVo().getGuestCanCreateOther();
		}
	}
	
	@AfterRender
	void afterRender() {
		renderSupport.addScript("initAllowedContacts("+Boolean.toString(restrictedGuest)+");");
	}

	public List<String> onProvideCompletionsFromRecipientsPatternGuestForm(String input) throws BusinessException {
		List<UserVo> searchResults = performSearch(input);

		List<String> elements = new ArrayList<String>();
		for (UserVo user : searchResults) {
			 String completeName = MailCompletionService.formatLabel(user);
            if (!elements.contains(completeName)) {
                elements.add(completeName);
            }
		}

		return elements;
	}
	
	/** Perform a user search using the user search pattern.
	 * @param input user search pattern.
	 * @return list of users.
	 * @throws BusinessException 
	 */
	private List<UserVo> performSearch(String input) throws BusinessException {


		Set<UserVo> userSet = new HashSet<UserVo>();

		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

        if (input != null) {
            userSet.addAll(userFacade.searchUser(input.trim(), null, null,userLoggedIn));
        }
		userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userLoggedIn));
		userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,  userLoggedIn));
		
		return new ArrayList<UserVo>(userSet);
	}
	

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
    public void  onValidateFormFromGuestCreateForm() throws BusinessException {

    	if (guestCreateForm.getHasErrors()) {
    		return ;
    	}
    	
    	if (mail==null) {
    		// the message will be handled by Tapestry
    		return ;
    	}
        if (userFacade.findUser(mail, userLoggedIn.getDomainIdentifier()) != null) {
            guestCreateForm.recordError(messages.get("pages.user.edit.error.alreadyExist"));
            userAlreadyExists = true;
            return ;
        }
        
		
    	if (restrictedGuest || userLoggedIn.isRestricted()) {
        	boolean sendErrors = false;
        	
	    	List<String> recipients = MailCompletionService.parseEmails(recipientsSearch);
	    	String badFormatEmail =  "";
			
	    	for (String recipient : recipients) {
	        	if (!MailCompletionService.MAILREGEXP.matcher(recipient.toUpperCase()).matches()){
	            	badFormatEmail = badFormatEmail + recipient + " ";
	            	sendErrors = true;
	            }
	    	}
			
	    	if(sendErrors) {
	        	guestCreateForm.recordError(String.format(messages.get("components.confirmSharePopup.validate.email"), badFormatEmail));
	        }
	    	else {
	        	this.recipientsEmail = recipients;
	        }
    	}
    }

    Object onSuccess() {
		
		MailContainer mailContainer = mailBuilder.buildMailContainer(userLoggedIn, customMessage);

        try {
            
        	//set uploadGranted always to true for guest
        	boolean uploadGranted = true;
        	
        	boolean allowedToCreateGuest = guestsAllowedToCreateGuest;
        	
        	userFacade.createGuest(mail, firstName, lastName, uploadGranted, allowedToCreateGuest,comment, 
        			mailContainer,userLoggedIn);
        	
        	if (userLoggedIn.isRestricted()) { //user restricted needs to see the guest he has created
        		userFacade.addGuestContactRestriction(userLoggedIn.getLogin(), mail);
        	}
        	
        	if (restrictedGuest || userLoggedIn.isRestricted()) { // a restricted guest can only create restricted guests
        		userFacade.setGuestContactRestriction(mail, recipientsEmail);
        	}
            shareSessionObjects.addMessage(messages.get("components.guestEditForm.action.add.confirm"));
        } catch (BusinessException e) { //bad contact for contacts list
        	shareSessionObjects.addError(messages.get("components.guestEditForm.action.add.guestRestriction.badUser"));
        	List<String> userLogin = new ArrayList<String>();
        	userLogin.add(userLoggedIn.getLogin());
        	try {
				userFacade.setGuestContactRestriction(mail, userLogin); //default: set guest contact restriction with the owner
			} catch (BusinessException e1) {
				e1.printStackTrace();
			}
        }
        componentResources.triggerEvent("resetListUsers", null, null);
        
        return Index.class;
    }

    Object onFailure() {
    	if (!userAlreadyExists)
    		shareSessionObjects.addError(messages.get("pages.user.edit.error.generic"));
    	return this;
    }
  
}
