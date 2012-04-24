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
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
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
import org.linagora.linShare.core.Facade.FunctionalityFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.SelectableRole;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linShare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

/** This component is used to edit an user.
 */
@IncludeJavaScriptLibrary(value = {"UserEditForm.js"})
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

    @SessionState
    private ShareSessionObjects shareSessionObjects;

    @Environmental
    private RenderSupport renderSupport;

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
    private UserType usertype;

    @Persist
    private boolean userGuest;
    
    @Persist
    private String userDomain;
    
    @Property
    private boolean restrictedEditGuest;
    
    @Property
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
    		 
		UserVo currentUser=null;
		userGuest = true;
		
		if(editUserWithMail!=null){
		
			for (UserVo oneUser : users) {
				if(oneUser.getLogin().equals(editUserWithMail)) {
					currentUser = oneUser;
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
	    		userGuest = usertype.equals(UserType.GUEST); //to set friendly title on account
	    		userDomain = currentUser.getDomainIdentifier();
	    		restrictedEditGuest = currentUser.isRestricted();
	    		userRestrictedGuest = currentUser.isRestricted();
	    		if (currentUser.isGuest()&&currentUser.isRestricted()) {
	    			List<UserVo> contacts = null;
					try {
						contacts = userFacade.fetchGuestContacts(currentUser.getLogin());
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
		
		try {

	        if (input != null) {
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null,userLoggedIn));
	        }
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userLoggedIn));
			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,  userLoggedIn));
		} catch (BusinessException e) {
			logger.error("Error while trying to perform user search", e);
		}
		
		return new ArrayList<UserVo>(userSet);
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
        		userFacade.updateGuest(userDomain, mail, firstName, lastName, uploadGranted,createGuestGranted,userLoggedIn);
        	} else {
        		userFacade.updateUserRole(userDomain, mail, SelectableRole.fromSelectableRole(role), userLoggedIn);
        	}
        } catch (BusinessException e) {
            // should never occur.
            logger.error(e.toString());
        }
		
		if (userGuest) {
			try {
				UserVo guest = userFacade.findGuestWithMailAndUserLoggedIn(userLoggedIn, mail);
				
				if (restrictedEditGuest && !guest.isRestricted()) { //toogle restricted to true
					userFacade.setGuestContactRestriction(mail, recipientsEmail);
					
				} else if (!restrictedEditGuest && guest.isRestricted()) { //toogle restricted to false
					userFacade.removeGuestContactRestriction(mail);
					
				} else if (restrictedEditGuest && guest.isRestricted()) { //maybe user add new contact
					if (!intialContacts.equalsIgnoreCase(recipientsSearch)) {
						userFacade.setGuestContactRestriction(mail, recipientsEmail);
					}
				}				
		        shareSessionObjects.addMessage(messages.get("components.userEditForm.action.update.confirm"));
			} catch (BusinessException e) {
				shareSessionObjects.addError(messages.get("components.guestEditForm.action.update.guestRestriction.badUser"));
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
