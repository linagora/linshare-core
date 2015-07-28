/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

/** This component is used to edit properties of a file.
 */
public class ThreadEntryEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    

    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	@Inject
	private ThreadEntryFacade threadEntryFacade;

    @InjectComponent
    private Form editForm;

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
    
	
    @Component(parameters = {"style=bluelighting", "show=false", "width=400", "height=200"})
    private WindowWithEffectsComponent fileEditWindow;
    
    @InjectComponent
    private Zone fileEditTemplateZone;
    
    @Retain
    private String _assignedZoneClientId;
    
    @Environmental
    private JavaScriptSupport _pageRenderSupport;
    
    /**
     * set explicitly the id for the component
     */
    @Parameter(required=true,value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String id;
    
    @Persist
    private String threadEntryUuid;
    
    @SessionState
    @Property
    private UserVo userLoggedIn;

    @SessionState
    private ShareSessionObjects shareSessionObjects;

    @Property
    private String fileComment;

    @Property
    private String newName;

    private boolean reset = false;
    
	private XSSFilter filter;

	@Inject
	private Policy antiSamyPolicy;



	/* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */

    
    public boolean onValidateFormFromEditForm() {
    	logger.debug("onValidateFormFromEditForm");
    	if (editForm.getHasErrors()) {
    		return false;
    	}
        return true;
    }
    
     public void onSelectedFromReset() {
    	 reset =  true;
     }

	public void onSuccessFromEditForm() {
		filter = new XSSFilter(shareSessionObjects, editForm, antiSamyPolicy, messages);
		try {
			fileComment = filter.clean(fileComment);
			newName = filter.clean(newName);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter.getWarningMessage());
			}
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
		}
		if(reset) return;
        try {
			threadEntryFacade.updateFileProperties(userLoggedIn.getLsUuid(), threadEntryUuid, fileComment, newName);
		} catch (BusinessException e) {
			logger.error("Couldn't update thread entry.", e.getMessage());
		}
        shareSessionObjects.addMessage(messages.get("component.fileEditForm.action.update.confirm"));
        componentResources.triggerEvent("resetListFiles", null, null);
	}

    public void onFailure() {
    	 shareSessionObjects.addError(messages.get("component.fileEditForm.action.update.error"));
    }
    
    
    @CleanupRender
    public void cleanupRender() {
    	editForm.clearErrors();
    }
    
    public Zone getShowPopupWindow() {
        return fileEditTemplateZone;
    }
    
    public String getJSONId() {
    	return fileEditWindow.getJSONId();
    }
    
    /**
     * give the id of the window
     * use it to open the window
     * @return client id
     */
    public String getJavascriptOpenPopup(){
    	return fileEditWindow.getJavascriptOpenPopup();
    }
    
    public String getZoneClientId() {
    	if(_assignedZoneClientId == null) {
    		_assignedZoneClientId = "zone"+_pageRenderSupport.allocateClientId(id); 
    	}
    	return _assignedZoneClientId;
    }

	public void setUuidThreadEntryToedit(String uuid) {
		this.threadEntryUuid = uuid;
		initFormToEdit();
	}
	
    private void initFormToEdit() {
		if(threadEntryUuid != null) {
			ThreadEntryVo doc;
			try {
				doc = threadEntryFacade.getThreadEntry(userLoggedIn, threadEntryUuid);
				fileComment = doc.getFileComment();
				newName = doc.getFileName();
			} catch (BusinessException e) {
				logger.error("Could not get thread entry.", e.getMessage());
			}
		}
    }
}
