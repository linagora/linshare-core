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

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;

public class CreateGroupPopup {
    @ApplicationState
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=600", "height=230"})
    private WindowWithEffects createGroupWindow;
    @InjectComponent
    private Zone createGroupTemplateZone;
    @Inject
    private GroupFacade groupFacade;
	@Inject
	private ComponentResources componentResources;  
	
    @Inject
    private Messages messages;

    @InjectComponent
    private Form createGroupForm;
    
    // The block that contains the action to be thrown on success 
	@Inject
	private Block onSuccessCreateGroup;
    
    // The block that contains the action to be thrown on failure
	@Inject
	private Block onFailureCreateGroup;
    
    @Property
    private String groupName;
    @Property
    private String groupDescription;

    private boolean groupAlreadyExists = false;
    
    void onValidateFromGroupName(String string) {
    	boolean exist = groupFacade.nameAlreadyExists(string);
    	if (exist)
    		createGroupForm.recordError(messages.get("pages.user.edit.error.alreadyExist"));
    }
    
    public void  onValidateFormFromGuestCreateForm() {

    	if (createGroupForm.getHasErrors()) {
    		return ;
    	}
    	
    	if (groupName==null) {
    		// the message will be handled by Tapestry
    		return ;
    	}
        if (groupFacade.nameAlreadyExists(groupName)) {
        	createGroupForm.recordError(messages.get("pages.user.edit.error.alreadyExist"));
            groupAlreadyExists = true;
            return ;
        }
    }

    void onFailure() {
    	if (!groupAlreadyExists)
    		shareSessionObjects.addError(messages.get("pages.user.edit.error.generic"));
//    	return onFailureCreateGroup;
    }
    
    void onSuccess() {
    	boolean exist = groupFacade.nameAlreadyExists(groupName);
    	if (exist) {
    		createGroupForm.recordError(messages.get("pages.user.edit.error.alreadyExist"));
    		return; //onFailureCreateGroup;
    	}
    	
    	try {
			groupFacade.create(userLoggedIn, groupName, groupDescription);
			shareSessionObjects.addMessage(messages.format("pages.groups.create.success", groupName));
		} catch (BusinessException e) {
			e.printStackTrace();
		}

		componentResources.getContainer().getComponentResources()
				.triggerEvent("eventUpdateListGroups", null, null);
    	
//		return onSuccessCreateGroup;
    }
    
    public Zone getShowPopup() {
        return createGroupTemplateZone;
    }
    
    public String getJSONid() {
    	return createGroupWindow.getJSONId();
    }
}
