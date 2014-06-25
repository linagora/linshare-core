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
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.SelectableRole;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

/** This component is used to edit an user.
 */
@Import(library = {"UserEditForm.js"})
public class UserEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> users;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private String editUserWithMail;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private UserFacade userFacade;

    @InjectComponent
    private Form userForm;

    @Inject
    private Messages messages;

    @Inject
    private Logger logger;

    @Inject
    private ComponentResources componentResources;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;



	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    @Property
    private UserVo userLoggedIn;

    @Property
    @Persist
    private UserVo currentUser;
    
    
    
    @SessionState
    private ShareSessionObjects shareSessionObjects;

    @Environmental
    private JavaScriptSupport renderSupport;

    @Property
    private String mail;

    @Property
    private String firstName;

    @Property
    private String lastName;

    @Property
    private boolean uploadGranted;
    
    @Property
    private boolean createGuestGranted;

    @Property
    private SelectableRole role;
    
    @Property
    private AccountType usertype;

    @Persist
    private boolean userGuest;
    
    @Persist
    private String userDomain;
    
    @Property
	@Persist
    private boolean restrictedEditGuest;
    
    @Property
    private boolean showRestricted;
    
    @Property
	@Persist
    private boolean userRestrictedGuest;
	
	@Persist("flash")
	private List<String> recipientsEmail;
	
	@Persist("flash")
	@Property
	private String recipientsSearch;
	
	@Persist
	private String intialContacts;

    @Inject
    private PropertyAccess access;
    
    @Property
	private int autocompleteMin;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;
	
	
	private XSSFilter filter;

	@Inject
	private Policy antiSamyPolicy;
	
    
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    
    @SetupRender
    public void init(){

    	autocompleteMin = functionalityFacade.completionThreshold(userLoggedIn.getDomainIdentifier());
		recipientsSearch = MailCompletionService.formatLabel(userLoggedIn);
    		 
		currentUser=null;
		userGuest = true;
		
		if(editUserWithMail!=null){
		
			for (UserVo oneUser : users) {
				if(oneUser.getLsUuid().equals(editUserWithMail)) {
					currentUser = userFacade.loadUserDetails(oneUser.getMail(), oneUser.getDomainIdentifier());
					break;
				}
			}
			
			if(currentUser!=null){    		
	    		mail = currentUser.getMail();
	    		firstName = currentUser.getFirstName();
	    		lastName = currentUser.getLastName();
	    		uploadGranted = currentUser.isUpload();
	    		createGuestGranted = currentUser.isCreateGuest();
	    		role = SelectableRole.fromRole(currentUser.getRole());
	    		usertype = currentUser.getUserType();
	    		userGuest = usertype.equals(AccountType.GUEST); //to set friendly title on account
	    		userDomain = currentUser.getDomainIdentifier();
	    		showRestricted = functionalityFacade.isRestrictedGuestEnabled(userLoggedIn.getDomainIdentifier());
	    		restrictedEditGuest = currentUser.isRestricted();
	    		userRestrictedGuest = currentUser.isGuest() && currentUser.isRestricted();
	    		if (userRestrictedGuest) {
	    			List<UserVo> contacts = null;
					try {
						contacts = userFacade.fetchGuestContacts(userLoggedIn, currentUser.getLsUuid());
					} catch (BusinessException e) {
						e.printStackTrace();
					}
					if (contacts!=null) {
						recipientsSearch = MailCompletionService.formatList(contacts);
						intialContacts = recipientsSearch;
					}
	    		}
			}
		}
    }

    @AfterRender
    void afterRender() {
    	if (userRestrictedGuest) {
    		renderSupport.addScript(String.format("$('allowedContactsBlock').style.display = 'block';"));
    	}
    	else {
    		renderSupport.addScript(String.format("$('allowedContactsBlock').style.display = 'none';"));
    	}
    }
    
    
	public List<String> onProvideCompletionsFromRecipientsPatternEditForm(String input) throws BusinessException {
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
	 */
	private List<UserVo> performSearch(String input) {
		try {
			return userAutoCompleteFacade.autoCompleteUserSortedByFavorites(userLoggedIn, input);
		} catch (BusinessException e) {
			logger.error("Failed to autocomplete user on ConfirmSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
    
    
    public boolean isUserGuest(){
    	return userGuest;
    }
    
    public boolean isAdmin(){
    	return userLoggedIn.isAdministrator();
    }
    
    public boolean isUserRestrictedGuest() {
    	return userLoggedIn.isRestricted();
    }
    
    public boolean onValidateFormFromUserForm() {
    	if (userForm.getHasErrors()) {
    		return false;
    	}
    	if (mail == null | firstName == null | lastName == null) {
    		// the message will be handled by Tapestry
    		return false;
    	}

    	if (restrictedEditGuest || userLoggedIn.isRestricted()) {
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
	    		userForm.recordError(String.format(messages.get("components.confirmSharePopup.validate.email"), badFormatEmail));
	        }
	    	else {
	        	this.recipientsEmail = recipients;
	        }
    	}
        return true;
    }

    public void onSuccessFromUserForm() {
    	filter = new XSSFilter(shareSessionObjects, userForm, antiSamyPolicy, messages);
    	try {
    		mail = filter.clean(mail);
    		firstName = filter.clean(firstName);
    		lastName = filter.clean(lastName);
    		if (filter.hasError()) {
    			logger.debug("XSSFilter found some tags and striped them.");
    			businessMessagesManagementService.notify(filter.getWarningMessage());
    		}
    	} catch (BusinessException e) {
    		businessMessagesManagementService.notify(e);
    		logger.error(e.toString());
    		return;
    	}

        try {
        	if(userGuest) {    	
        		userFacade.updateGuest(currentUser.getLsUuid(), userDomain, mail, firstName, lastName,uploadGranted,createGuestGranted, userLoggedIn);
        	} else {
        		userFacade.updateUserRole(currentUser.getLsUuid(), userDomain, mail, SelectableRole.fromSelectableRole(role), userLoggedIn);
        	}
        } catch (BusinessException e) {
            // should never occur.
            logger.error(e.toString());
        }
		
		if (userGuest) {
			try {
				
				UserVo guest = userFacade.findGuestByLsUuid(userLoggedIn, currentUser.getLsUuid());
				logger.debug("current guest : " + guest);
				
				
				if (restrictedEditGuest && !guest.isRestricted()) { //toogle restricted to true
					userFacade.setGuestContactRestriction(userLoggedIn, guest.getLsUuid(), recipientsEmail);
					
				} else if (!restrictedEditGuest && guest.isRestricted()) { //toogle restricted to false
					userFacade.removeGuestContactRestriction(userLoggedIn, guest.getLsUuid());
					
				} else if (restrictedEditGuest && guest.isRestricted()) { //maybe user add new contact
					if (intialContacts != null && !intialContacts.equalsIgnoreCase(recipientsSearch)) {
						userFacade.setGuestContactRestriction(userLoggedIn, guest.getLsUuid(), recipientsEmail);
					}
				}				
		        shareSessionObjects.addMessage(messages.get("components.userEditForm.action.update.confirm"));
			} catch (BusinessException e) {
				shareSessionObjects.addError(messages.get("components.guestEditForm.action.update.guestRestriction.badUser"));
				logger.debug(e.toString());
			}
		}

		componentResources.triggerEvent("resetListUsers", null, null);
    }

    public void onFailure() {
    	 shareSessionObjects.addError(messages.get("components.userEditForm.action.update.error"));
    	
    }
    
    @CleanupRender
    public void cleanupRender(){
    	userForm.clearErrors();
    }
    
    
}
