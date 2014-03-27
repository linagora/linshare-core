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
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.pages.user.Index;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.Templating;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

/** This component is used to create a new user.
 */
@SupportsInformalParameters
@Import(library = {"GuestEditForm.js"})
public class GuestEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> users;

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private String JSONid;
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private UserFacade userFacade;
    
    @Inject
    private AbstractDomainFacade domainFacade;

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
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;


	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    @Property
    private UserVo userLoggedIn;

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

//    @Property
//    private boolean uploadGranted;
	
	@Property
	private boolean guestsAllowedToCreateGuest;
    
    @Property
    @Persist("flash")
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
    
    @Property
	private int autocompleteMin;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;
	
	@Property
	private boolean showRestricted;
	
	
	private XSSFilter filter;

	@Inject
	private Policy antiSamyPolicy;
	

	@SetupRender
	void init() throws BusinessException {
		recipientsSearch = MailCompletionService.formatLabel(userLoggedIn);
		
    	GuestDomainVo guestDomainVo = domainFacade.findGuestDomain(userLoggedIn.getDomainIdentifier());
    	if (guestDomainVo != null){
    		showRestricted = functionalityFacade.isRestrictedGuestEnabled(userLoggedIn.getDomainIdentifier());
			restrictedGuest = functionalityFacade.getDefaultRestrictedGuestValue(userLoggedIn.getDomainIdentifier()) || userLoggedIn.isRestricted();
    	}
    	
		guestsAllowedToCreateGuest = false;
		autocompleteMin = functionalityFacade.completionThreshold(userLoggedIn.getDomainIdentifier());
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
		try {
			return userAutoCompleteFacade.autoCompleteUserSortedByFavorites(userLoggedIn, input);
		} catch (BusinessException e) {
			logger.error("Failed to autocomplete user on ConfirmSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
	

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
    public void  onValidateFormFromGuestCreateForm() throws BusinessException {
    	
    	if (guestCreateForm.getHasErrors()) {
    		return ;
    	}
    	
    	if (mail == null | firstName == null | lastName == null) {
    		// the message will be handled by Tapestry
    		return;
    	}

    	GuestDomainVo guestDomainVo = domainFacade.findGuestDomain(userLoggedIn.getDomainIdentifier());
    	
        if (userFacade.findUserInDb(mail, userLoggedIn.getDomainIdentifier()) != null || userFacade.findUserInDb(mail, guestDomainVo.getIdentifier()) != null ) {
            guestCreateForm.recordError(messages.get("pages.user.edit.error.alreadyExist"));
            shareSessionObjects.addError(messages.get("pages.user.edit.error.alreadyExist"));
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
		filter = new XSSFilter(shareSessionObjects, guestCreateForm, antiSamyPolicy, messages);
		try {
			mail = filter.clean(mail);
			firstName = filter.clean(firstName);
			lastName = filter.clean(lastName);
			comment = filter.clean(comment);
			customMessage = filter.clean(customMessage);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter.getWarningMessage());
			}
		} catch (BusinessException e) {
			logger.debug(e.toString());
			businessMessagesManagementService.notify(e);
			businessMessagesManagementService.notify(e);
			return Index.class;
		}
		
		UserVo guestVo = null ;
		try {
			// set uploadGranted always to true for guest
			boolean uploadGranted = true;

			boolean allowedToCreateGuest = guestsAllowedToCreateGuest;
        	
        	guestVo = userFacade.createGuest(mail, firstName, lastName, uploadGranted, allowedToCreateGuest, comment, userLoggedIn);
		} catch (BusinessException e) { 
			logger.error("Can't create Guest : " + mail);
			logger.debug(e.toString());
			businessMessagesManagementService.notify(e);
			return Index.class;
		}
        	
        try {
        	// TODO : need some heavy refactoring
        	if (userLoggedIn.isRestricted()) { //user restricted needs to see the guest he has created
        		userFacade.addGuestContactRestriction(userLoggedIn.getLsUuid(), guestVo.getLsUuid());
        	}
        	
        	if (restrictedGuest || userLoggedIn.isRestricted()) { // a restricted guest can only create restricted guests
        		userFacade.setGuestContactRestriction(guestVo.getLsUuid(), recipientsEmail);
        	}
            shareSessionObjects.addMessage(messages.get("components.guestEditForm.action.add.confirm"));
        } catch (BusinessException e) { //bad contact for contacts list
        	logger.debug(e.toString());
        	shareSessionObjects.addError(messages.get("components.guestEditForm.action.add.guestRestriction.badUser"));
        	List<String> contactMailList = new ArrayList<String>();
        	contactMailList.add(userLoggedIn.getMail());
        	try {
				userFacade.setGuestContactRestriction(guestVo.getLsUuid(), contactMailList); //default: set guest contact restriction with the owner
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
