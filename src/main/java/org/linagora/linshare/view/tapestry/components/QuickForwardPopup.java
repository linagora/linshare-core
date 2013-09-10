package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
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

public class QuickForwardPopup {
	
	private static final Logger logger = LoggerFactory.getLogger(QuickForwardPopup.class);
	
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<ShareDocumentVo> documentsVo;
	
	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    @Property
    private UserVo userLoggedIn;

    @SessionState
    private ShareSessionObjects shareSessionObjects;
	
	@Property
	private ShareDocumentVo documentSelected;

	@Persist("flash")
	@Property
	private boolean secureSharing;

	@Persist
	@Property
	private boolean showSecureSharingCheckBox;
	
	@Persist
	@Property
	private int cssClassNumberCpt;

	@Persist("flash")
	private List<String> recipientsEmail;

	@Property
	private String listRecipientsSearch;
	
	@Property
	private String recipientsSearch;
	
	@Property
	private int autocompleteMin;

	@Property
	private String textAreaValue;

	@Property
	private String textAreaSubjectValue;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	@Inject
	private Policy antiSamyPolicy;

    @Inject
    private ShareFacade shareFacade;

	@Inject
	private UserFacade userFacade;
	
	@Inject
	private MailingListFacade mailingListFacade;

	@Inject
	private Messages messages;
 
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects quickForwardWindow;

    @InjectComponent
    @Property
    private Form quickForwardForm;
    
	private XSSFilter filter;
	
	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */
    
    @SetupRender
    public void init() {
    	cssClassNumberCpt = 0;
		autocompleteMin = functionalityFacade.completionThreshold(userLoggedIn.getDomainIdentifier());
    	showSecureSharingCheckBox = shareFacade.isVisibleSecuredAnonymousUrlCheckBox(userLoggedIn.getDomainIdentifier());
		if (showSecureSharingCheckBox) 
			secureSharing = shareFacade.getDefaultSecuredAnonymousUrlCheckBoxValue(userLoggedIn.getDomainIdentifier());	
    }
    
    public void onValidateFromQuickForwardForm() {
		if (quickForwardForm.getHasErrors()) {
    		return;
    	}
    	
    	boolean sendErrors = false;
    	logger.debug("FOOBAR :" + recipientsSearch);
		List<String> recipients = new ArrayList<String>();
		if(recipientsSearch != null){
    		recipients = MailCompletionService.parseEmails(recipientsSearch);
		} else {
			recipientsSearch = listRecipientsSearch;
		}
		List<MailingListVo> mailingListSelected = mailingListFacade.getListFromQuickShare(userLoggedIn,listRecipientsSearch);
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
			quickForwardForm.recordError(String.format(messages.get("components.confirmSharePopup.validate.email"), badFormatEmail));
		}
		else {
			this.recipientsEmail = recipients;
		}
    }
    
    public void onSuccess() throws BusinessException {
		filter = new XSSFilter(shareSessionObjects, quickForwardForm, antiSamyPolicy, messages);
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
    	
    	List<DocumentVo> docs = new ArrayList<DocumentVo>();
    	
    	// Copy documents to user's space
    	for (ShareDocumentVo doc : documentsVo) {
	        try {
	        	DocumentVo copied = shareFacade.createLocalCopy(doc, userLoggedIn);
	        	docs.add(copied);
	            businessMessagesManagementService.notify(new BusinessUserMessage(
	            		BusinessUserMessageType.LOCAL_COPY_OK, MessageSeverity.INFO));
	        } catch (BusinessException e) {
	        	// no space left or wrong mime type
	        	businessMessagesManagementService.notify(e);
	        }
    	}

    	// Share the copied documents to recipients
    	SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
    	boolean errorOnAddress = false;
    	
    	try {
    		MailContainer mailContainer = new MailContainer(userLoggedIn.getLocale(), textAreaValue, textAreaSubjectValue);
    		sharing = shareFacade.createSharingWithMailUsingRecipientsEmail(userLoggedIn, docs, recipientsEmail, secureSharing, mailContainer);
    	} catch (BusinessException e1) {
    		// relay host disabled on smtp server
    		if (e1.getErrorCode() == BusinessErrorCode.RELAY_HOST_NOT_ENABLE) {
    			logger.error("Could not create share, relay host is disabled : ", e1);
    			// join with comma
    			String buffer = "";
    			String delim = "";
    			for (String extra : e1.getExtras()) {
    				buffer += delim + extra;
    				delim = ", ";
    			}
    			businessMessagesManagementService.notify(new BusinessUserMessage(
    					BusinessUserMessageType.UNREACHABLE_MAIL_ADDRESS, MessageSeverity.ERROR, buffer));
    			errorOnAddress = true;
    		} else {
    			logger.error(e1.getMessage());
    			businessMessagesManagementService.notify(e1);
    		}
    	}
    	
    	shareSessionObjects = new ShareSessionObjects();
		if (sharing.getFailsItem().size()>0) {
			shareSessionObjects.addError(messages.get("components.confirmSharePopup.fail"));
		} else if (errorOnAddress) {
			recipientFavouriteFacade.increment(userLoggedIn, recipientsEmail);
			businessMessagesManagementService.notify(new BusinessUserMessage(
                BusinessUserMessageType.SHARE_WARNING_MAIL_ADDRESS, MessageSeverity.WARNING));				
		} else {
			if(recipientsEmail.size() > 0){
			recipientFavouriteFacade.increment(userLoggedIn, recipientsEmail);
			shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.success"));
			componentResources.triggerEvent("resetListFiles", null, null);
			} else {
				businessMessagesManagementService.notify(new BusinessUserMessage(
		                BusinessUserMessageType.QUICKSHARE_NOMAIL, MessageSeverity.ERROR));
				componentResources.triggerEvent("resetListFiles", null, null);
			}
		}
    }
    
	public List<String> onProvideCompletionsFromRecipientsPatternForwardPopup(String input) {
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
	
	public List<String> onProvideCompletionsFromListRecipientsPatternSharePopup(String input) throws BusinessException {
		List<MailingListVo> searchResults = performSearchForMailingList(input);
		List<String> elements = new ArrayList<String>();
		for (MailingListVo current: searchResults) {
			if(current.getOwner().equals(userLoggedIn)){
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
		list = mailingListFacade.findAllListByUser(userLoggedIn);
		for(MailingListVo current : list){
			if(current.getIdentifier().indexOf(input) != -1){
				finalList.add(current);
			}
		}
		return finalList;
	}
	
	public String getCssClassNumber() {
		cssClassNumberCpt += 1;
		return "number" + Integer.toString(cssClassNumberCpt);
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
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null,userLoggedIn));
	        }
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userLoggedIn));
			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,  userLoggedIn));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), userLoggedIn));
			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userLoggedIn);
		} catch (BusinessException e) {
			logger.error("Failed to search user on ConfirmSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
	
}
