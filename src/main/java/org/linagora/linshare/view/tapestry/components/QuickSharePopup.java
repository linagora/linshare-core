/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SupportsInformalParameters
@Import(library = {"QuickSharePopup.js", "SizeOfPopup.js"})
public class QuickSharePopup{
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
	

	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;

	@Property
	private String textAreaValue;
	
	@Property
	private String textAreaSubjectValue;

    @Persist
    @Property
    private List<DocumentVo> addedDocuments;
	
    @Property
    private DocumentVo documentVo;
	
//	@Persist
	@Property
	private String recipientsSearch;
	
	@Property
	private String listRecipientsSearch;
	
	@Persist("flash")
	@Property
	private boolean secureSharing;
	
	@Persist
	@Property
	private boolean showSecureSharingCheckBox;
	
	@Property
	private int index;
	
	@Property
	private int currentSize = 1;
	
	@Persist("flash")
	private List<String> recipientsEmail;
	
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
	private DocumentFacade documentFacade;
	
	@Inject
	private MailingListFacade mailingListFacade;
	
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
	
	@Inject @Symbol("linshare.default.maxUpload")
	@Property
	private int maxUpload;

    @InjectComponent
    private FileUploader fileUploader;

    @Component
    private Zone reloadingZone;

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects quickShareWindow;

    @InjectComponent
    @Property
    private Form quickShareForm;
    
    @Property
	private int autocompleteMin;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	
	private XSSFilter filter;

    @Inject
    private Messages messages;
	
	@Inject
	private Policy antiSamyPolicy;


	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		documentsVolist = new ArrayList<DocumentVo>();
		autocompleteMin = functionalityFacade.completionThreshold(userVo.getDomainIdentifier());
		showSecureSharingCheckBox = shareFacade.isVisibleSecuredAnonymousUrlCheckBox(userVo.getDomainIdentifier());
		if(showSecureSharingCheckBox) 
			secureSharing = shareFacade.getDefaultSecuredAnonymousUrlCheckBoxValue(userVo.getDomainIdentifier());
	}
	/**
	 * Initialize the JS value
	 */
    @AfterRender
    public void afterRender() {
        renderSupport.addScript(String.format("setQuickShareMaxElement('%s');",maxUpload));
        renderSupport.addScript(String.format("setQuickShareCurrentElement('%s');",currentSize-1));
        
        //resize the share popup
        renderSupport.addScript(String.format("quickShareWindow.setSize(650, getHeightForPopup())"));
        
        quickShareForm.clearErrors();
    }
	
	/**
	 * Prior to validating the form, we need to initialise all the arrays
	 */
	public void onPrepare() {
		successFiles = new ArrayList<String>();
		failFiles = new HashMap<String,BusinessException>();
	}
    
	

	public List<String> onProvideCompletionsFromRecipientsPatternQuickSharePopup(String input) {
		List<UserVo> searchResults = performSearch(input);

		List<String> elements = new ArrayList<String>();
		for (UserVo user : searchResults) {
			 String completeName = MailCompletionService.formatLabel(user);
            if (!elements.contains(completeName)) {
                elements.add(completeName);
            }
		}

		return elements;
	}	
	
	
	/** Perform a user search using the user search pattern.
	 * @param input user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {


		Set<UserVo> userSet = new HashSet<UserVo>();

		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		try {
	        if (input != null) {
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null, userVo));
	        }
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userVo));

			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_, userVo));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), userVo));
			
			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userVo);
		} catch (BusinessException e) {
			logger.error("Error while searching user in QuickSharePopup",e);
		}
		return new ArrayList<UserVo>();
	}

	public List<String> onProvideCompletionsFromListRecipientsPatternQuickSharePopup(String input) throws BusinessException {
		List<MailingListVo> searchResults = performSearchForMailingList(input);
		List<String> elements = new ArrayList<String>();
		for (MailingListVo current: searchResults) {
			if(current.getOwner().equals(userVo)){
				String completeName = "\""+current.getIdentifier()+"\" (Me)";
				elements.add(completeName);
			} else {
				String completeName = "\""+current.getIdentifier()+"\" ("+current.getOwner().getFullName()+")";
				elements.add(completeName);
			}
		}
		return elements;
	}
	
	/**
	 * Perform a list search.
	 * 
	 * @param input
	 *            list search pattern.
	 * @return list of lists.
	 * @throws BusinessException 
	 */
	private List<MailingListVo> performSearchForMailingList(String input) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		list = mailingListFacade.findAllMailingListByUser(userVo);
		for(MailingListVo current : list){
			if(current.getIdentifier().indexOf(input) != -1){
				finalList.add(current);
			}
		}
		return finalList;
	}
	
	
	
    public void onSuccessFromQuickShareForm() throws BusinessException {
    	
    	
    	filter = new XSSFilter(shareSessionObjects, quickShareForm, antiSamyPolicy, messages);
    	try {
    		textAreaSubjectValue = filter.clean(textAreaSubjectValue);
    		textAreaValue = filter.clean(textAreaValue);
    		if (filter.hasError()) {
    			logger.debug("XSSFilter found some tags and striped them.");
    			businessMessagesManagementService.notify(filter.getWarningMessage());
    		}
    	} catch (BusinessException e) {
    		businessMessagesManagementService.notify(e);
    	}
    	//VALIDATE

    	boolean sendErrors = false;
		  	
    	try{
    		List<String> recipients = new ArrayList<String>();
    		if(recipientsSearch != null){
        		recipients = MailCompletionService.parseEmails(recipientsSearch);
    		} else {
    			recipientsSearch = listRecipientsSearch;
    		}
    		
			List<MailingListVo> mailingListSelected = mailingListFacade.getMailingListFromQuickShare(userVo,listRecipientsSearch);
			if(!(mailingListSelected.isEmpty())){
			
				for(MailingListVo current : mailingListSelected){
					
					for(MailingListContactVo currentContact : current.getMails()){
						recipients.add(currentContact.getMail());
					}
				}
			}
			String badFormatEmail =  "";
			
			for (String recipient : recipients) {
				if (!MailCompletionService.MAILREGEXP.matcher(recipient.toUpperCase()).matches()){
					badFormatEmail = badFormatEmail + recipient + " ";
					sendErrors = true;
				}
			}
			
			if(sendErrors) {
				businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_BADMAIL,
	                MessageSeverity.ERROR, badFormatEmail));
				addedDocuments = new ArrayList<DocumentVo>();
				return;
			} else {
				this.recipientsEmail = recipients;
			}
	    	
	    	
	    	if (addedDocuments == null || addedDocuments.size() == 0) {
	    		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_NO_FILE_TO_SHARE, MessageSeverity.ERROR));
				return;
	    	}
	    	
	
	    	
			//PROCESS SHARE
			
	    	Boolean errorOnAddress = false;
	    	
			SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
			try {
				MailContainer mailContainer = new MailContainer(userVo.getLocale(), textAreaValue, textAreaSubjectValue);
				sharing = shareFacade.createSharingWithMailUsingRecipientsEmailAndExpiryDate(userVo, addedDocuments, recipientsEmail, secureSharing, mailContainer,null);
			
			} catch (BusinessException e1) {
				
				// IF RELAY IS DISABLE ON SMTP SERVER 
				if(e1.getErrorCode() == BusinessErrorCode.RELAY_HOST_NOT_ENABLE){
					logger.error("Could not create sharing, relay host is disable : ", e1);
					
					String buffer =  "";
					String sep = "";
					for (String extra : e1.getExtras()) {
						buffer = buffer + sep + extra;
						sep = ", ";
					}
					businessMessagesManagementService.notify(new BusinessUserMessage(
			                BusinessUserMessageType.UNREACHABLE_MAIL_ADDRESS, MessageSeverity.ERROR, buffer));
					errorOnAddress = true;
				} else {
					logger.error("Could not create sharing, caught a BusinessException.");
					logger.error(e1.getMessage());
					businessMessagesManagementService.notify(e1);
					
			        // reset list of documents
			        addedDocuments = new ArrayList<DocumentVo>();
			        return;
				}
			}
	
			
			if (sharing.getFailsItem().size() > 0) {
	    		businessMessagesManagementService.notify(new BusinessUserMessage(
	                BusinessUserMessageType.QUICKSHARE_FAILED, MessageSeverity.ERROR));
			} else if (errorOnAddress) {
				recipientFavouriteFacade.increment(userVo, recipientsEmail);
				businessMessagesManagementService.notify(new BusinessUserMessage(
	                BusinessUserMessageType.SHARE_WARNING_MAIL_ADDRESS, MessageSeverity.WARNING));				
			} else {
				if(recipientsEmail.size() > 0){
					recipientFavouriteFacade.increment(userVo, recipientsEmail);
					businessMessagesManagementService.notify(new BusinessUserMessage(
			                BusinessUserMessageType.QUICKSHARE_SUCCESS, MessageSeverity.INFO));
				} else {
					businessMessagesManagementService.notify(new BusinessUserMessage(
			                BusinessUserMessageType.QUICKSHARE_NOMAIL, MessageSeverity.ERROR));
				}
					
			}
			
    	}catch (NullPointerException e3) {
    		logger.error("No Email in textarea", e3);
    		businessMessagesManagementService.notify(new BusinessUserMessage(
                    BusinessUserMessageType.QUICKSHARE_NOMAIL, MessageSeverity.ERROR));
		}		
		
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
//            DocumentVo doc = documentFacade.insertFile(aFile.getStream(), aFile.getSize(), aFile.getFileName(), userVo);
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
		return quickShareWindow.getJSONId();
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
