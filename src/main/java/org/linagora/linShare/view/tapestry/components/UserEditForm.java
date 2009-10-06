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

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;

/** This component is used to edit an user.
 */
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


	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @ApplicationState
    @Property
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;

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
    private Role role;
    
    @Property
    private UserType usertype;

    @Persist
    private boolean userGuest;

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    
    @SetupRender
    public void init(){
    		 
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
	    		role = currentUser.getRole();
	    		usertype = currentUser.getUserType();
	    		userGuest = usertype.equals(UserType.GUEST); //to set friendly title on account
			}
		}
    }
    
    
    public boolean isUserGuest(){
    	return userGuest;
    }
    
    public boolean isAdmin(){
    	return userLoggedIn.isAdministrator();
    }
    
    
    public boolean onValidateFormFromUserForm() {
    	if (userForm.getHasErrors()) {
    		return false;
    	}
    	
    	if (mail==null) {
    		// the message will be handled by Tapestry
    		return false;
    	}
        return true;
    }

    public void onSuccessFromUserForm() {

        try {
        	if(userGuest)        	
            userFacade.updateGuest(mail, firstName, lastName, uploadGranted,createGuestGranted,userLoggedIn);
        	else
        	userFacade.updateUser(mail, role, userLoggedIn);
        	
        } catch (BusinessException e) {
            // should never occur.
            logger.error(e.toString());
        }
        shareSessionObjects.addMessage(messages.get("components.userEditForm.action.update.confirm"));
        users = userFacade.searchGuest(userLoggedIn.getMail()); //refresh list users ?
    }

    public void onFailure() {
    	 shareSessionObjects.addMessage(messages.get("components.userEditForm.action.update.error"));
    	
    }
    
    @CleanupRender
    public void cleanupRender(){
    	userForm.clearErrors();
    }
    
    
}
