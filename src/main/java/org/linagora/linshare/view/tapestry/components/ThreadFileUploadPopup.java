package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadFileUploadPopup {
	
	private static final Logger logger = LoggerFactory.getLogger(QuickSharePopup.class);

	public static final String RELOADZONE_EVENT = "reload";
	
	/* ***********************************************************
     *                         Parameters
     ************************************************************ */

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	@SessionState
	private UserVo userVo;

	@SessionState
	private ShareSessionObjects shareSessionObjects;

	@Property
	List<String> projectTags;
	
	@Property
	List<String> phaseTags;
	
	@Property
    private DocumentVo documentVo;

    @Persist
    @Property
    private List<DocumentVo> addedDocuments;


	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

	@Inject
    private JavaScriptSupport renderSupport;
	
	@Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
	
	@Inject
	private Policy antiSamyPolicy;

    @Inject
    private Messages messages;

    @InjectComponent
    private FileUploader fileUploader;
    
    @Inject
    private ComponentResources resources;

    @Component
    private Zone reloadingZone;

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects threadFileUploadWindow;
	
	@InjectComponent
    @Property
    private Form threadFileUploadForm;

	private XSSFilter filter;


	public String getJSONId() {
		return threadFileUploadWindow.getJSONId();
	}
	
	@OnEvent("reload")
    public Object onReloadFromUpdateZone() {
        return reloadingZone.getBody();
    }

    public String getReloadingZoneId() {
        return "reloadingZone";
    }

    public String getReloadingZoneUrl() {
        return resources.createEventLink(RELOADZONE_EVENT).toString();
    }

    public void onActionFromCancelQuickShare() {
        addedDocuments = new ArrayList<DocumentVo>();
    }
}
