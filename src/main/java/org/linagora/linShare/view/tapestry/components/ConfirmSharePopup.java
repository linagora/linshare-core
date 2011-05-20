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
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
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
import org.linagora.linShare.core.Facade.RecipientFavouriteFacade;
import org.linagora.linShare.core.Facade.ShareExpiryDateFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IncludeJavaScriptLibrary("SizeOfPopup.js")
public class ConfirmSharePopup{
	private static final Logger logger = LoggerFactory.getLogger(ConfirmSharePopup.class);
	
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
	
	
	@Persist("flash")
	@Property
	private String recipientsSearch;
	
	@Persist("flash")
	@Property
	private boolean secureSharing;
	
	
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
	
	@Property
	@Persist
	private Date minDatePicker;
	@Property
	@Persist
	private Date maxDatePicker;
	@Property
	private Date defaultDatePicker;

	@Property
	private String tooltipValue;
	@Property
	private String tooltipTitle;
	
	@Property
	private boolean warningCryptedFiles;
	

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	@Inject
	private ShareFacade shareFacade;
	
	@Inject
	private UserFacade userFacade;

	@Inject
	private PersistentLocale persistentLocale;
	
	@Inject
	private ShareExpiryDateFacade shareExpiryDateFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	
    @Environmental
    private RenderSupport renderSupport;

	@Inject
	private ComponentResources componentResources;
	
	@Inject
	private MailContainerBuilder mailContainerBuilder;

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */

	
	/**
	 * Initialization of the form.
	 */
	@SetupRender
	public void init() {
		
		//init emails list for selected users
		if(usersVo!=null && usersVo.size()>0){
			String emails = MailCompletionService.formatList(usersVo);
			recipientsSearch = emails.substring(0,emails.length()-1); //delete last comma.
		}

		computePickerDates();
		buildTooltipValue();
		this.warningCryptedFiles = checkCryptedFiles();
		
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
	
	
	
	public void onValidateFormFromConfirmshare() throws BusinessException {
		
    	if (confirmshare.getHasErrors()) {
    		return ;
    	}
    	
    	boolean sendErrors = false;
		
		List<String> recipients = MailCompletionService.parseEmails(recipientsSearch);
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
	
	
    public Block onSuccess() throws BusinessException {
    	/** 
		 * verify the value of the expiration date selected
		 * 
		 */
		Calendar dateExpiry = Calendar.getInstance();
		Date dateSelected = datePicker.getDatePicked();
		if (dateSelected == null || dateSelected.after(maxDatePicker) || dateSelected.before(Calendar.getInstance().getTime())) {
			dateExpiry = null;
		}
		else dateExpiry.setTime(dateSelected);

		//PROCESS SHARE
		
		SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
		try {
			MailContainer mailContainer = mailContainerBuilder.buildMailContainer(userVo, textAreaValue);
			mailContainer.setSubject(textAreaSubjectValue); //retrieve the subject of the mail defined by the user
			sharing = shareFacade.createSharingWithMailUsingRecipientsEmailAndExpiryDate(userVo, documentsVo, recipientsEmail, secureSharing, mailContainer, dateExpiry);

		
		} catch (BusinessException e1) {
			logger.error("Could not create sharing", e1);
			throw e1;
		}

		
		shareSessionObjects=new ShareSessionObjects();
		if (sharing.getFailsItem().size()>0) {
			shareSessionObjects.addError(messages.get("components.confirmSharePopup.fail"));
		} else {
			recipientFavouriteFacade.increment(userVo, recipientsEmail);
			shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.success"));
			componentResources.triggerEvent("resetListFiles", null, null);
		}
		
		return onSuccess;
		
	}
    

	public String getJSONId() {
		return confirmWindow.getJSONId();
	}


}
