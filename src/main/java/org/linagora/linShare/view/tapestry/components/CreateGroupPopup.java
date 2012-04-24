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
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linShare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateGroupPopup {
	private static final Logger logger = LoggerFactory.getLogger(CreateGroupPopup.class);
	
    @ApplicationState
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=600", "height=260"})
    private WindowWithEffects createGroupWindow;
    @InjectComponent
    private Zone createGroupTemplateZone;
    @Inject
    private GroupFacade groupFacade;
	@Inject
	private ComponentResources componentResources;  
	
    @Inject
    private Messages messages;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

    @InjectComponent
    private Form createGroupForm;
    
    // The block that contains the action to be thrown on success 
	@Inject
	private Block onSuccessCreateGroup;
    
    // The block that contains the action to be thrown on failure
	@Inject
	private Block onFailureCreateGroup;

	
    @Property
    @Persist
    private String groupName;
    @Property
    @Persist
    private String groupDescription;
    @Property
    @Persist
    private String groupFunctionalEmail;
    @Persist
    private boolean groupAlreadyExists;
    
    private XSSFilter filter;

	@Inject
	private Policy antiSamyPolicy;

    
    @CleanupRender
    void cleanupRender() {
    	createGroupForm.clearErrors();
    }
    
    public void onValidateFormFromCreateGroupForm() {
    	if (groupName == null) {
    		return;
		}

		
        if (groupFacade.nameAlreadyExists(groupName)) {
            createGroupForm.recordError(messages.get("pages.group.create.error.alreadyExist"));
        	shareSessionObjects.addError(messages.get("pages.group.create.error.alreadyExist"));
            groupAlreadyExists = true;
            return ;
        }
        if (groupFunctionalEmail!=null && groupFunctionalEmail.trim().length()>0) {
	        if (!MailCompletionService.MAILREGEXP.matcher(groupFunctionalEmail.toUpperCase()).matches()){
	        	createGroupForm.recordError(messages.get("pages.group.create.error.alreadyExist"));
	        	shareSessionObjects.addError(String.format(messages.get("components.confirmSharePopup.validate.email"), groupFunctionalEmail));
	        	return;
	        }
        }
    }

    Zone onFailure() {
    	if (!groupAlreadyExists)
    		shareSessionObjects.addError(messages.get("pages.user.edit.error.generic"));
    	return createGroupTemplateZone;
    }
    
    void onSuccess() {
    	filter = new XSSFilter(shareSessionObjects, createGroupForm, antiSamyPolicy, messages);
		try {
			groupName = filter.clean(groupName);
			groupDescription = filter.clean(groupDescription);
			groupFunctionalEmail = filter.clean(groupFunctionalEmail);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter.getWarningMessage());
			}
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
		}
    	boolean exist = groupFacade.nameAlreadyExists(groupName);
    	if (exist) {
    		shareSessionObjects.addError(messages.get("pages.user.edit.error.alreadyExist"));
    		return; //onFailureCreateGroup;
    	}
    	
    	groupFunctionalEmail = (groupFunctionalEmail!=null && groupFunctionalEmail.trim().length()>0) ? groupFunctionalEmail : null;
    	
    	try {
			groupFacade.create(userLoggedIn, groupName, groupDescription, groupFunctionalEmail);
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
