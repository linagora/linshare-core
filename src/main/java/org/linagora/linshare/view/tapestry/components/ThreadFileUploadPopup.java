package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linshare.core.Facade.DocumentFacade;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadFileUploadPopup {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadFileUploadPopup.class);

	
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
	private List<String> projectTags;
	
	@Property
	private List<String> phaseTags;
	
	@Property
    private DocumentVo documentVo;

    @Persist
    @Property
    private List<DocumentVo> addedDocuments;


	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

    @Inject
	private DocumentFacade documentFacade;
    
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

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects threadFileUploadWindow;

	@InjectComponent
    @Property
    private Form threadFileUploadForm;
	
	private List<String> successFiles;
	
	private Map<String,BusinessException> failFiles;

	private XSSFilter filter;

	
	
	
	/**
	 * here we add the file uploaded in the array. It is a good place to check it
	 * @param aFile
	 * @throws BusinessException 
	 */
    public void onValidateFromFile(UploadedFile aFile)  {  	
        if (aFile == null) {
        	// the message will be handled by Tapestry
        	return;
        }

//        try {
//        	 TODO : utiliser un ThreadEntry et donc la ThreadFacade
//            DocumentVo doc = documentFacade.insertFile(aFile.getStream(), aFile.getSize(), aFile.getFileName(), userVo);

            successFiles.add(aFile.getFileName());
            logger.info(aFile.getFileName() + " has been uploaded successfuly.");
//        } catch (BusinessException e) {
//            failFiles.put(aFile.getFileName(), e);
//        }
    }

	public String getJSONId() {
		return threadFileUploadWindow.getJSONId();
	}

    public void onActionFromCancelQuickShare() {
        addedDocuments = new ArrayList<DocumentVo>();
    }
}
