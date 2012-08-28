package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linshare.core.Facade.FunctionalityFacade;
import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.TagEnumVo;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportsInformalParameters
@Import(library = {"ThreadFileUploadPopup.js", "SizeOfPopup.js"})
public class ThreadFileUploadPopup {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadFileUploadPopup.class);

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
	

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;

    @Persist
    @Property
    private List<ThreadEntryVo> addedThreadEntries;
	
    @Property
    private ThreadEntryVo tempThreadEntryVo;
	
	@Property
	private int index;
	
	@Property
	private int currentSize = 1;
    
	@SuppressWarnings("unused") // used in TML
	@Property
	private UploadedFile file;
    
	private List<String> successFiles;
	private Map<String,BusinessException> failFiles;
    
	@Persist
	private List<DocumentVo> documentsVolist;	

	@Persist(PersistenceConstants.FLASH) @Property(write=false)
	private String zoneId;
	
	@Property(write=false)
	private float period;
	
	@Persist
	@Property
	private ThreadVo currentThread;
	
	
	
	@Persist
	@Property
	private TagEnumVo step;
	
	@Persist
	@Property
	private List<String> stepNames;
	
	@Property
	private String selectStepName;
	 
	 
	 
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	
	@Inject
	private ThreadEntryFacade threadEntryFacade;
	
	@Inject
    private JavaScriptSupport renderSupport;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

    @Inject
    private ComponentResources resources;
	
	@Inject @Symbol("linshare.default.maxUpload")
	@Property
	private int maxUpload;

    @InjectComponent
    private FileUploaderForThreadEntry fileUploader;

    @Component
    private Zone reloadingZone;

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects threadFileUploadWindow;

    @InjectComponent
    @Property
    private Form threadEntryForm;
    
	@Inject
	private FunctionalityFacade functionalityFacade;
	
    @Inject
    private Messages messages;
    
    
   
	

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 * @throws BusinessException 
	 */
	@SetupRender
	public void init() throws BusinessException {
		documentsVolist = new ArrayList<DocumentVo>();
//		currentThread = new ThreadVo("9806de10-ed0b-11e1-877a-5404a6202d2c", "RATP");
//		step = threadEntryFacade.getTagEnumVo(userVo, currentThread, "Phases");
//		stepNames = step.getEnumValues();
	}
	
	public void setMyCurrentThread(ThreadVo thread) {
		currentThread = thread;
		try {
			step = threadEntryFacade.getTagEnumVo(userVo, currentThread, "Phases");
			stepNames = step.getEnumValues();
		} catch (BusinessException e) {
			logger.error("Can't get phases tag !");
			logger.debug(e.toString());
		}
	}
	
	
	/**
	 * Initialize the JS value
	 */
    @AfterRender
    public void afterRender() {
        renderSupport.addScript(String.format("setQuickShareMaxElement('%s');",maxUpload));
        renderSupport.addScript(String.format("setQuickShareCurrentElement('%s');",currentSize-1));
        
        //resize the share popup
        renderSupport.addScript(String.format("threadFileUploadWindow.setSize(650, getHeightForPopup())"));
        
        threadEntryForm.clearErrors();
    }
	
	/**
	 * Prior to validating the form, we need to initialise all the arrays
	 */
	public void onPrepare() {
		successFiles = new ArrayList<String>();
		failFiles = new HashMap<String,BusinessException>();
	}
	
	
    public void onSuccessFromThreadEntryForm() {
    	
    	if(logger.isDebugEnabled()) 	logger.debug("current thread is : " + currentThread.getName() + "(" + currentThread.getLsUuid() + ")");

    	
    	List<TagVo> tags = new ArrayList<TagVo>();
    	
    	tags.add(new TagVo(step.getName() , selectStepName));
    	
    	try {
    		threadEntryFacade.setTagsToThreadEntries(userVo, currentThread, addedThreadEntries, tags);
    	} catch (BusinessException e1) {
    		logger.error(e1.getMessage());
    		logger.debug(e1.toString());
    		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.THREAD_UPLOAD_FAILED, MessageSeverity.ERROR));
    	}
        // reset list of documents
        addedThreadEntries.clear();
	}
    

    public Boolean onValidateFormFromThreadEntryForm()  {
    	logger.debug("selectStepName : " + selectStepName);
    	
    	if(selectStepName == null) return false;
    	
    	return true;
    }
    

	public String getJSONId() {
		return threadFileUploadWindow.getJSONId();
	}

	
    /* ***********************************************************
     *                   Helpers
     ************************************************************ */
	
	/*
	 * Used to generate the source for the loop
	 * There ought to be a better method
	 */
	public int[] getFilesArray() {
		int[] filesArrays = new int[maxUpload];
		for (int i=0; i<maxUpload; i++) {
			filesArrays[i] = i;
		}
		return filesArrays;
	}
	
	public String getDisplay() {
		if (index>=currentSize) {
			return "none";
		} else {
			return "block";
		}
		
	}
	
	public List<String> getSuccessFiles() {
		return successFiles;
	}

	public Map<String,BusinessException> getFailFiles() {
		return failFiles;
	}

    @OnEvent("fileAdded")
    public void processFileAdded(ThreadEntryVo document) {
        if (addedThreadEntries == null) {
            addedThreadEntries = new ArrayList<ThreadEntryVo>();
        }
        addedThreadEntries.add(document);
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

    public void onActionFromBtnCancelThreadEntryPopup() {
    	logger.debug("running BtnCancelThreadEntryPopup");
    	if (addedThreadEntries != null) {
    		addedThreadEntries.clear();
        }
    }
}
