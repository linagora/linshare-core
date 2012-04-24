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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

/** This component is used to edit properties of a file.
 */
public class FileEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    

    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	@Inject
	private DocumentFacade documentFacade;

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
    
	
	@SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=400", "height=200"})
    private WindowWithEffectsComponent fileEditWindow;
    
    @InjectComponent
    private Zone fileEditTemplateZone;
    
    @Retain
    private String _assignedZoneClientId;
    
    @Environmental
    private RenderSupport _pageRenderSupport;
    
    /**
     * set explicitly the id for the component
     */
    @Parameter(required=true,value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String id;
    
    
    
    @Persist
    private String editFileWithUuid;
    
    
    @ApplicationState
    @Property
    private UserVo userLoggedIn;

    @ApplicationState
    private ShareSessionObjects shareSessionObjects;

    @Property
    private String fileName;

    @Property
    private String fileComment;
    
    
    private boolean reset = false;
    
	private XSSFilter filter;

	@Inject
	private Policy antiSamyPolicy;



	/* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */

    
    public boolean onValidateFormFromEditForm() {
    	if (editForm.getHasErrors()) {
    		return false;
    	}

    	if (fileName == null) {
    		return false;
    	}
    	
        return true;
    }
    
     public void onSelectedFromReset(){
    	 reset =  true;
     }

	public void onSuccessFromEditForm() {
		filter = new XSSFilter(shareSessionObjects, editForm, antiSamyPolicy, messages);
		try {
			fileComment = filter.clean(fileComment);
			fileName = filter.clean(fileName);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter.getWarningMessage());
			}
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
		}
		if(reset) return;

        documentFacade.updateFileProperties(editFileWithUuid, fileName, fileComment);
        shareSessionObjects.addMessage(messages.get("components.fileEditForm.action.update.confirm"));
        componentResources.triggerEvent("resetListFiles", null, null);
	}

    public void onFailure() {
    	 shareSessionObjects.addError(messages.get("components.fileEditForm.action.update.error"));
    }
    
    @CleanupRender
    public void cleanupRender(){
    	editForm.clearErrors();
    }
    
    public Zone getShowPopupWindow() {
        return fileEditTemplateZone;
    }
    
    public String getJSONId(){
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
    
    public String getZoneClientId()
    {
    	if(_assignedZoneClientId==null)
    		_assignedZoneClientId = "zone"+_pageRenderSupport.allocateClientId( id ); 
    	
    	return _assignedZoneClientId;
    }


	public void setUuidDocToedit(String uuid) {
		this.editFileWithUuid = uuid;
		initFormToEdit();
	}
	
    private void initFormToEdit(){
		 
		if(editFileWithUuid!=null){
		    	DocumentVo doc =  documentFacade.getDocument(userLoggedIn.getLogin(), editFileWithUuid);
		    	fileName = doc.getFileName();
		    	fileComment = doc.getFileComment();
		}
    }
}
