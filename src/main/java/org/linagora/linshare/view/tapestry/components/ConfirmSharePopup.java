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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
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
import org.linagora.linshare.core.facade.ShareExpiryDateFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.utils.FileUtils;
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

@Import(library={"SizeOfPopup.js"})
public class ConfirmSharePopup{
	private static final Logger logger = LoggerFactory.getLogger(ConfirmSharePopup.class);
	
	public static final String RELOADZONE_EVENT = "reload";
	
	@SessionState
	private UserVo userVo;

	@SessionState
	private ShareSessionObjects shareSessionObjects;
	

	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<UserVo> usersVo;

	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<DocumentVo> documentsVo;

	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550"})
	private WindowWithEffects confirmWindow;

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String messageLabel;

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;


	@Inject
	private Messages messages;
	
	@Property
	private String textAreaValue;
	
	@Property
	private String textAreaSubjectValue;
	
	
//	@Persist("flash")
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
	
	@Persist
	@Property
	private int cssClassNumberCpt;
	
	
	
	@Persist("flash")
	private List<String> recipientsEmail;
	
	
    // The block that contains the action to be thrown on success 
	@Inject
	private Block onSuccess;
    
    // The block that contains the action to be thrown on failure
	@Inject
	private Block onFailure;
	
    @InjectComponent
    private Form confirmshare;
	
	@InjectComponent
	private DatePicker datePicker;
	
	@SuppressWarnings("unused")
	@Property
	@Persist
	private Date minDatePicker;
	
	@Property
	@Persist
	private Date maxDatePicker;
	
	@SuppressWarnings("unused")
	@Property
	private Date defaultDatePicker;

	@SuppressWarnings("unused")
	@Property
	private String tooltipValue;
	
	@SuppressWarnings("unused")
	@Property
	private String tooltipTitle;
	
	@SuppressWarnings("unused")
	@Property
	private boolean warningCryptedFiles;
	
	
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
	private PersistentLocale persistentLocale;
	
	@Inject
	private ShareExpiryDateFacade shareExpiryDateFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;		
	
    @Environmental
    private JavaScriptSupport renderSupport;

	@Inject
	private ComponentResources componentResources;
	
	@SuppressWarnings("unused")
	@Property
	private int autocompleteMin;
	
	@Inject
	private FunctionalityFacade functionalityFacade;
	
	   
	private XSSFilter filter;
	
	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		cssClassNumberCpt = 0;
		autocompleteMin = functionalityFacade.completionThreshold(userVo.getDomainIdentifier());
		
		//init emails list for selected users
		if(usersVo!=null && usersVo.size()>0){
			
			String emails = MailCompletionService.formatList(usersVo);
			recipientsSearch = emails.substring(0,emails.length()-1); //delete last comma.
		}

		computePickerDates();
		buildTooltipValue();
		this.warningCryptedFiles = checkCryptedFiles();
		
		showSecureSharingCheckBox = shareFacade.isVisibleSecuredAnonymousUrlCheckBox(userVo.getDomainIdentifier());
		if(showSecureSharingCheckBox) 
			secureSharing = shareFacade.getDefaultSecuredAnonymousUrlCheckBoxValue(userVo.getDomainIdentifier());
	}
	
	private boolean checkCryptedFiles() {
		
		boolean warning =false;
		for (DocumentVo onedoc : documentsVo) {
			if(onedoc.getEncrypted()) warning = true;
		}
		return warning;
	}

	@AfterRender
    public void afterRender() {
    	//resize the share popup
        renderSupport.addScript(String.format("confirmWindow.setSize(650, getHeightForPopup())"));
    }

	/**
	 * Compute the minDate, maxDate and defaultDate the user can select in
	 * the datePicker for the expiration date of the share
	 */
	private void computePickerDates() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH, 1);
		minDatePicker = today.getTime();
		Calendar expiryDateMin = shareExpiryDateFacade.computeMinShareExpiryDateOfList(documentsVo, userVo);
		defaultDatePicker = expiryDateMin.getTime();
		maxDatePicker = expiryDateMin.getTime();
	}

	/**
	 * Build the content (title and message) of the tooltip which explain
	 * how the expiration date of files is computed
	 */
	private void buildTooltipValue() {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, persistentLocale.get());
		tooltipTitle = messages.get("components.confirmSharePopup.tooltip.title");
		
		StringBuffer value = new StringBuffer();
		value.append("<p class='tooltipTableDescription'>");
		value.append(messages.get("components.confirmSharePopup.tooltip.description"));
		value.append("</p>");
		value.append("<table class='tooltipTable'><tr><th>");
		value.append(messages.get("components.confirmSharePopup.tooltip.table.file"));
		value.append("</th><th>");
		value.append(messages.get("components.confirmSharePopup.tooltip.table.size"));
		value.append("</th><th>");
		value.append(messages.get("components.confirmSharePopup.tooltip.table.date"));
		value.append("</th></tr>");
		for (DocumentVo docVo : documentsVo) {
			value.append("<tr><td>");
			String docName = docVo.getFileName();
			if (docName.length() > 28) {
				docName = docName.substring(0, 28).concat("...");
			}
			value.append(docName);
			value.append("</td><td class='nowrap'>");
			value.append(FileUtils.getFriendlySize(docVo.getSize(), messages));
			value.append("</td><td>");
			value.append(dateFormat.format(shareExpiryDateFacade.computeShareExpiryDate(docVo, userVo).getTime()));
			value.append("</td></tr>");
		}
		value.append("</table>");
		
		tooltipValue = value.toString();
	}

	public List<String> onProvideCompletionsFromRecipientsPatternSharePopup(String input) {
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
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null,userVo));
	        }
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userVo));

			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,  userVo));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), userVo));
			
			return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userVo);
		} catch (BusinessException e) {
			logger.error("Failed to search user on ConfirmSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
	
	public List<String> onProvideCompletionsFromListRecipientsPatternSharePopup(String input) throws BusinessException {
		return mailingListFacade.completionsForShare(userVo, input);
	}
	
	public void onValidateFormFromConfirmshare() throws BusinessException {
		if (confirmshare.getHasErrors()) {
    		return ;
    	}
    	
    	boolean sendErrors = false;
		
		List<String> recipients = new ArrayList<String>();
		if(recipientsSearch != null){
    		recipients = MailCompletionService.parseEmails(recipientsSearch);
		} else {
			recipientsSearch = listRecipientsSearch;
		}
		List<MailingListVo> mailingListSelected = mailingListFacade.getListsFromShare(listRecipientsSearch);
		if(!(mailingListSelected.isEmpty())){
			
			for(MailingListVo current : mailingListSelected){
				
				for(MailingListContactVo currentContact : current.getContacts()){
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
			confirmshare.recordError(String.format(messages.get("components.confirmSharePopup.validate.email"), badFormatEmail));
		}
		else {
			this.recipientsEmail = recipients;
		}
	}
	
    public Block onFailure() {
    	return onFailure;
    }
	

    public Block onSubmit() throws BusinessException {
    	filter = new XSSFilter(shareSessionObjects, confirmshare, antiSamyPolicy, messages);
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

		Calendar dateExpiry = Calendar.getInstance();
		Date dateSelected = datePicker.getDatePicked();
		if (dateSelected == null || dateSelected.after(maxDatePicker) || dateSelected.before(Calendar.getInstance().getTime())) {
			dateExpiry = null;
		}
		else dateExpiry.setTime(dateSelected);

		//PROCESS SHARE
        
        Boolean errorOnAddress = false;
        
		SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
		try {
			MailContainer mailContainer = new MailContainer(userVo.getLocale(), textAreaValue, textAreaSubjectValue);
			sharing = shareFacade.createSharingWithMailUsingRecipientsEmailAndExpiryDate(userVo, documentsVo, recipientsEmail, secureSharing, mailContainer, dateExpiry);
		
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
				
		        return onSuccess;
			}
		}

		shareSessionObjects=new ShareSessionObjects();
		if (sharing.getFailsItem().size()>0) {
			shareSessionObjects.addError(messages.get("components.confirmSharePopup.fail"));
		} else if (errorOnAddress) {
			recipientFavouriteFacade.increment(userVo, recipientsEmail);
			businessMessagesManagementService.notify(new BusinessUserMessage(
                BusinessUserMessageType.SHARE_WARNING_MAIL_ADDRESS, MessageSeverity.WARNING));				
		} else {
			if(recipientsEmail.size() > 0){
				recipientFavouriteFacade.increment(userVo, recipientsEmail);
				shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.success"));
				componentResources.triggerEvent("resetListFiles", null, null);
			} else {
				businessMessagesManagementService.notify(new BusinessUserMessage(
		                BusinessUserMessageType.QUICKSHARE_NOMAIL, MessageSeverity.ERROR));
				componentResources.triggerEvent("resetListFiles", null, null);
			}
		}
		return onSuccess;
		
	}
   
	public String getJSONId() {
		return confirmWindow.getJSONId();
	}
	
	public String getCssClassNumber() {
		cssClassNumberCpt+=1;
		return "number" + Integer.toString(cssClassNumberCpt);
	}


}
