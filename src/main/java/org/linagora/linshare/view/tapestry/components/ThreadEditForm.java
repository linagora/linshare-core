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
import org.linagora.linshare.core.Facade.ShareFacade;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;

public class ThreadEditForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    

    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	@Inject
	private ShareFacade shareFacade;

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
    private WindowWithEffectsComponent shareEditWindow;
    
    @InjectComponent
    private Zone shareEditTemplateZone;
    
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
    private String editShareWithUuid;

    @SessionState
    @Property
    private UserVo userLoggedIn;

    @SessionState
    private ShareSessionObjects shareSessionObjects;

    @Property
    private String fileName;

    @Property
    private String shareComment;
    
    
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
    
     public void onSelectedFromReset(){
    	 reset =  true;
     }

	public void onSuccessFromEditForm() {
		logger.debug("onSuccessFromEditForm");
		filter = new XSSFilter(shareSessionObjects, editForm, antiSamyPolicy, messages);
		try {
			shareComment = filter.clean(shareComment);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter.getWarningMessage());
			}
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
		}
		if(reset) return;

		try {
			shareFacade.updateShareComment(userLoggedIn, editShareWithUuid, shareComment);
			shareSessionObjects.addMessage(messages.get("component.shareEditForm.action.update.confirm"));
			componentResources.triggerEvent("resetListFiles", null, null);
		} catch (IllegalArgumentException e) {
			onFailure();
		} catch (BusinessException e) {
			onFailure();
		}
		
	}

    public void onFailure() {
    	 shareSessionObjects.addError(messages.get("component.shareEditForm.action.update.error"));
    }
    
    
    @CleanupRender
    public void cleanupRender(){
    	editForm.clearErrors();
    }
    
    public Zone getShowPopupWindow() {
        return shareEditTemplateZone;
    }
    
    public String getJSONId(){
    	return shareEditWindow.getJSONId();
    }
    
    /**
     * give the id of the window
     * use it to open the window
     * @return client id
     */
    public String getJavascriptOpenPopup(){
    	return shareEditWindow.getJavascriptOpenPopup();
    }
    
    public String getZoneClientId()
    {
    	if(_assignedZoneClientId==null)
    		_assignedZoneClientId = "zone"+_pageRenderSupport.allocateClientId( id ); 
    	
    	return _assignedZoneClientId;
    }

	public void setEditShareWithId(String editShareWithId) {
		this.editShareWithUuid = editShareWithId;
		initFormToEdit();
	}
	
    private void initFormToEdit(){
		if(editShareWithUuid!=null){
		    	ShareDocumentVo share;
				try {
					share = shareFacade.getShareDocumentVoByUuid(userLoggedIn, editShareWithUuid);
					shareComment = share.getFileComment();
				} catch (BusinessException e) {
					logger.error("share not found : " + editShareWithUuid);
				}
		}
    }
}
