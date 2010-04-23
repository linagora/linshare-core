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
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IncludeJavaScriptLibrary(value = {"SizeOfPopup.js"})
public class ShareWithGroupsPopup{
	private static final Logger logger = LoggerFactory.getLogger(ShareWithGroupsPopup.class);
	
	@ApplicationState
	private UserVo userVo;

	@ApplicationState
	private ShareSessionObjects shareSessionObjects;
	
	@Persist
	@Property
	private List<GroupVo> groupsVo;

	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<DocumentVo> documentsVo;

	@Component(parameters = {"style=bluelighting", "show=false","width=700", "height=300"})
	private WindowWithEffects groupShareWindow;

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String messageLabel;

	@Inject
	private Messages messages;
	
	
    // The block that contains the action to be thrown on success 
	@Inject
	private Block onSuccess;
    
    // The block that contains the action to be thrown on failure
	@Inject
	private Block onFailure;
    
	@Property
	private GroupVo groupSelected;
	
	@Property
	@Persist
	private List<GroupVo> groups;
    private List<GroupVo> selectedGroups;
	
    @SuppressWarnings("unused")
	@Property
    private Boolean valueCheck;
	

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	@Inject
	private ShareFacade shareFacade;

	@Inject
	private GroupFacade groupFacade;
	
    @Environmental
    private RenderSupport renderSupport;

	@Inject
	private ComponentResources componentResources;
	
	@Inject
	private MailContainerBuilder mailContainerBuilder;

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		groupsVo = new ArrayList<GroupVo>();
		groups = groupFacade.findByUser(userVo.getLogin());

	}

	@AfterRender
    public void afterRender() {
    	//resize the share popup
        renderSupport.addScript(String.format("groupShareWindow.setSize(650, getHeightForPopup())"));

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
	
    public Block onFailure() {
    	return onFailure;
    }
	
	
    public Block onSuccess() throws BusinessException {
        for (GroupVo groupVo : selectedGroups) {
            if (!groupsVo.contains(groupVo)) {
            	groupsVo.add(groupVo);
            }
        }
		
		SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
		try {
			MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, null);
			sharing = shareFacade.createSharingWithGroups(userVo, documentsVo, groupsVo, mailContainer);
			

		
		} catch (BusinessException e1) {
			logger.error("Could not create sharing", e1);
			throw e1;
		}

		
		shareSessionObjects=new ShareSessionObjects();
		if (sharing.getFailsItem().size()>0) {
			shareSessionObjects.addError(messages.get("components.confirmSharePopup.fail"));
		} else {
			shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.success"));
			componentResources.triggerEvent("resetListFiles", null, null);
		}
		
		return onSuccess;
		
	}
    

	public String getJSONId() {
		return groupShareWindow.getJSONId();
	}


}
