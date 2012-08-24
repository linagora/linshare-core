package org.linagora.linshare.view.tapestry.components;

import java.io.InputStream;
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
import org.linagora.linshare.core.Facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.Facade.ShareFacade;
import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.Facade.UserFacade;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.services.impl.MailContainerBuilder;
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
    private List<DocumentVo> addedDocuments;
	
    @Property
    private DocumentVo documentVo;
	
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
	
	
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	
	@Inject
	private ThreadEntryFacade threadEntryFacade;
	
	@Inject
    private JavaScriptSupport renderSupport;
	
	@Inject
	private ShareFacade shareFacade;
	
	@Inject
	private UserFacade userFacade;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

    @Inject
    private ComponentResources resources;
	
	@Inject
	private MailContainerBuilder mailContainerBuilder;

	
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
    private Form quickShareForm;
    
	@Inject
	private FunctionalityFacade functionalityFacade;
	
    @Inject
    private Messages messages;
	

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		documentsVolist = new ArrayList<DocumentVo>();
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
        
        quickShareForm.clearErrors();
    }
	
	/**
	 * Prior to validating the form, we need to initialise all the arrays
	 */
	public void onPrepare() {
		successFiles = new ArrayList<String>();
		failFiles = new HashMap<String,BusinessException>();
	}
	
	
    public void onSuccessFromQuickShareForm() throws BusinessException {
    	boolean sendErrors = false;
		  	
//    	try{
//	    	
//			List<String> recipients = MailCompletionService.parseEmails(recipientsSearch);
//			String badFormatEmail =  "";
//			
//			for (String recipient : recipients) {
//				if (!MailCompletionService.MAILREGEXP.matcher(recipient.toUpperCase()).matches()){
//					badFormatEmail = badFormatEmail + recipient + " ";
//					sendErrors = true;
//				}
//			}
//			
//			if(sendErrors) {
//				businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_BADMAIL,
//	                MessageSeverity.ERROR, badFormatEmail));
//				addedDocuments = new ArrayList<DocumentVo>();
//				return;
//			} else {
//				this.recipientsEmail = recipients;
//			}
//	    	
//	    	
//	    	if (addedDocuments == null || addedDocuments.size() == 0) {
//	    		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_NO_FILE_TO_SHARE, MessageSeverity.ERROR));
//				return;
//	    	}
	    	
	
	    	
//			//PROCESS SHARE
//			
//	    	Boolean errorOnAddress = false;
//	    	
//			SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
//			try {
//				MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, textAreaValue);
//				mailContainer.setSubject(textAreaSubjectValue); //retrieve the subject of the mail defined by the user
//				sharing = shareFacade.createSharingWithMailUsingRecipientsEmailAndExpiryDate(userVo, addedDocuments, recipientsEmail, secureSharing, mailContainer,null);
//			
//			} catch (BusinessException e1) {
//				
//				// IF RELAY IS DISABLE ON SMTP SERVER 
//				if(e1.getErrorCode() == BusinessErrorCode.RELAY_HOST_NOT_ENABLE){
//					logger.error("Could not create sharing, relay host is disable : ", e1);
//					
//					String buffer =  "";
//					String sep = "";
//					for (String extra : e1.getExtras()) {
//						buffer = buffer + sep + extra;
//						sep = ", ";
//					}
//					businessMessagesManagementService.notify(new BusinessUserMessage(
//			                BusinessUserMessageType.UNREACHABLE_MAIL_ADDRESS, MessageSeverity.ERROR, buffer));
//					errorOnAddress = true;
//				} else {
//					logger.error("Could not create sharing, caught a BusinessException.");
//					logger.error(e1.getMessage());
//					businessMessagesManagementService.notify(e1);
//					
//			        // reset list of documents
//			        addedDocuments = new ArrayList<DocumentVo>();
//			        return;
//				}
//			}
//	
//			
//			if (sharing.getFailsItem().size() > 0) {
//	    		businessMessagesManagementService.notify(new BusinessUserMessage(
//	                BusinessUserMessageType.QUICKSHARE_FAILED, MessageSeverity.ERROR));
//			} else if (errorOnAddress) {
//				recipientFavouriteFacade.increment(userVo, recipientsEmail);
//				businessMessagesManagementService.notify(new BusinessUserMessage(
//	                BusinessUserMessageType.SHARE_WARNING_MAIL_ADDRESS, MessageSeverity.WARNING));				
//			} else {
//				recipientFavouriteFacade.increment(userVo, recipientsEmail);
//				businessMessagesManagementService.notify(new BusinessUserMessage(
//	                BusinessUserMessageType.QUICKSHARE_SUCCESS, MessageSeverity.INFO));
//			}
//
//    	}catch (NullPointerException e3) {
//    		logger.error("No Email in textarea", e3);
//    		businessMessagesManagementService.notify(new BusinessUserMessage(
//                    BusinessUserMessageType.QUICKSHARE_NOMAIL, MessageSeverity.ERROR));
//		}		
//		
        // reset list of documents
        addedDocuments = new ArrayList<DocumentVo>();
	}
    
//	/**
//	 * here we add the file uploaded in the array. It is a good place to check it
//	 * @param aFile
//	 * @throws BusinessException 
//	 */
//    public void onValidateFromFile(UploadedFile aFile)  {  	
//        if (aFile == null) {
//        	// the message will be handled by Tapestry
//        	return;
//        }
//
//        
//        try {
//        	ThreadVo thread = new ThreadVo("9806de10-ed0b-11e1-877a-5404a6202d2c");
//            DocumentVo doc = threadEntryFacade.insertFile(userVo, thread, aFile.getStream(), aFile.getSize(), aFile.getFileName());
//            // public DocumentVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException ;
//            documentsVolist.add(doc);
//            successFiles.add(aFile.getFileName());
//        } catch (BusinessException e) {
//            failFiles.put(aFile.getFileName(),e);
//        }
//    }
	
	
    /**
	 * This is the onValidate for the QuickSharePopup Form
	 */
    public void onValidateFormFromQuickShareForm()  {
    	
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
    public void processFileAdded(DocumentVo document) {
        if (addedDocuments == null) {
            addedDocuments = new ArrayList<DocumentVo>();
        }
        addedDocuments.add(document);
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
