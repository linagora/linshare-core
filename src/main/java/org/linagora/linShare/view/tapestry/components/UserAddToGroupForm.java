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
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.GroupMemberType;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;

/** This component is used to edit an user.
 */
public class UserAddToGroupForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<GroupVo> groups;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> newMemberList;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private GroupFacade groupFacade;
    @Inject
    private UserFacade userFacade;

    @InjectComponent
    private Form userAddToGroup;

    @Inject
    private Messages messages;
    
	@Inject
	private MailContainerBuilder mailContainerBuilder;


	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @ApplicationState
    @Property
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    
	@Property
	private GroupVo groupSelected;
	
    @Property
    @Persist
    private List<GroupVo> groupsAllowed;
	
    private List<GroupVo> selectedGroups;

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    
    @SetupRender
    public void init(){
		groupsAllowed = new ArrayList<GroupVo>();
		if (groups != null && newMemberList != null && newMemberList.size() > 0) {
			groupsAllowed.addAll(groupFacade.findByUser(userLoggedIn.getLogin()));
		}
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
        if (selectedGroups == null) {
        	selectedGroups = new ArrayList<GroupVo>();
        }
        if (selected) {
        	selectedGroups.add(groupSelected);
        }
        else {
        	selectedGroups.remove(groupSelected);
        }
    }

    public void onSuccess() throws BusinessException {
    	
    	if (selectedGroups != null && newMemberList != null) {
    		for (UserVo user : newMemberList) {
	        	List<GroupVo> groupsOfUserAdded = groupFacade.findByUser(user.getLogin());
	        	List<GroupVo> groupsWhereUserWillBeAdded = new ArrayList<GroupVo>();
	        	
		    	for (GroupVo groupVo : selectedGroups) {
					if (!groupsOfUserAdded.contains(groupVo)) {
						groupsWhereUserWillBeAdded.add(groupVo);
					}
					else {
						shareSessionObjects.addWarning(messages.format("components.userAddToGroup.alreadyIn", user.getFirstName(), user.getLastName(), groupVo.getName()));
					}
				}
		    	
		    	if (groupsWhereUserWillBeAdded.size()>0) {
		    		for (GroupVo groupVo : groupsWhereUserWillBeAdded) {
		    			MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userLoggedIn, null);
						groupFacade.addMember(groupVo, userLoggedIn, user, mailContainer);
						shareSessionObjects.setReloadGroupsNeeded(true);
						
						if((groupFacade.retreiveMemberType(groupVo, userLoggedIn) == GroupMemberType.MANAGER)|| (groupFacade.retreiveMemberType(groupVo, userLoggedIn) == GroupMemberType.OWNER)){
							shareSessionObjects.addMessage(messages.format("components.userAddToGroup.successManager", user.getFirstName(), user.getLastName(), groupVo.getName()));
						}else{
							shareSessionObjects.addMessage(messages.format("components.userAddToGroup.successMember", user.getFirstName(), user.getLastName(), groupVo.getName()));
						}
						
						
					}
		    	}
	    	}
    	}
		
	}

    public void onFailure() {
    	 shareSessionObjects.addError(messages.get("components.userEditForm.action.update.error"));
    	
    }
    
    @CleanupRender
    public void cleanupRender(){
    	userAddToGroup.clearErrors();
    }
    
    
}
