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

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linShare.core.Facade.RecipientFavouriteFacade;
import org.linagora.linShare.core.Facade.ShareExpiryDateFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfirmSharePopup{
	private static final Logger logger = LoggerFactory.getLogger(ConfirmSharePopup.class);
	
	@ApplicationState
	private UserVo userVo;

	@ApplicationState
	private ShareSessionObjects shareSessionObjects;
	

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<UserVo> usersVo;

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<DocumentVo> documentsVo;

	@SuppressWarnings("unused")
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

	@Inject
	private Templating templating;
	
	@Property
	private String textAreaValue;
	
	
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
	

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	@Inject
	@Path("context:templates/shared-message.html")
	private Asset sharedTemplate;
	
	@Inject
	@Path("context:templates/shared-message.txt")
	private Asset sharedTemplateTxt;
	
	@Inject
	@Path("context:templates/shared-message-withpassword.html")
	private Asset passwordSharedTemplate;
	
	@Inject
	@Path("context:templates/shared-message-withpassword.txt")
	private Asset passwordSharedTemplateTxt;
	
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
	
	@Inject
	private PropertiesSymbolProvider propertiesSymbolProvider;

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
			String emails="";
			for (UserVo recipient : usersVo) {
				emails = emails + formatLabel(recipient)+',';
			}
			recipientsSearch = emails.substring(0,emails.length()-1); //delete last comma.
		}

		computePickerDates();
		buildTooltipValue();
	}

	/**
	 * Compute the minDate, maxDate and defaultDate the user can select in
	 * the datePicker for the expiration date of the share
	 */
	private void computePickerDates() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_MONTH, 1);
		minDatePicker = today.getTime();
		Calendar expiryDateMin = shareExpiryDateFacade.computeMinShareExpiryDateOfList(documentsVo);
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
			value.append(dateFormat.format(shareExpiryDateFacade.computeShareExpiryDate(docVo).getTime()));
			value.append("</td></tr>");
		}
		value.append("</table>");
		
		tooltipValue = value.toString();
	}

	public List<String> onProvideCompletionsFromRecipientsPatternSharePopup(String input) {
		List<UserVo> searchResults = performSearch(input);

		List<String> elements = new ArrayList<String>();
		for (UserVo user : searchResults) {
			 String completeName = formatLabel(user);
            if (!elements.contains(completeName)) {
                elements.add(completeName);
            }
		}

		return elements;
	}
	
	private String formatLabel(UserVo user){
		StringBuffer buf = new StringBuffer();
		
		if(user.getLastName()!=null&&user.getFirstName()!=null){
			//uservo from USER table or ldap
			buf.append("\"").append(user.getLastName().trim()).append(" ").append(user.getFirstName().trim()).append("\"");
			buf.append(" <").append(user.getMail()).append(">,");
		} else {
			//uservo from favorite table
			buf.append(user.getMail()).append(",");
		}
		return buf.toString();
	}
	
	// "Michael georges" <michael@linagora.com>,"Laporte Robert" <robert@robert.com>,bruce.willis@orange.fr,...
	private static List<String> parseEmails(String recipientsList){
		
		String[] recipients = recipientsList.split(",");
		ArrayList<String> emails = new ArrayList<String> ();
		
		for (String oneUser : recipients) {
			
			String email = contentInsideToken(oneUser, "<",">");
			if(email==null) email = oneUser.trim();
			
			if(!email.equals("")) //ignore empty string
			emails.add(email); // add good and bad email
		}
		
		return emails;
	}
	
	
	public static String contentInsideToken(String str,String tokenright,String tokenleft) {
		int deb = str.indexOf(tokenright,0);
		int end = str.indexOf(tokenleft,1);
		if(deb==-1||end==-1) return null;
		else return str.substring(deb+1, end).trim();
	}
	
	private static final Pattern MAILREGEXP = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
	
	
	
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

        if (input != null) {
            userSet.addAll(userFacade.searchUser(input.trim(), null, null,userVo));
        }
		userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userVo));
		userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,  userVo));
		userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), userVo));
		
		return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userVo);
	}
	
	
	
	public void onValidateFormFromConfirmshare() throws BusinessException {
		
    	if (confirmshare.getHasErrors()) {
    		return ;
    	}
    	
    	boolean sendErrors = false;
		
		List<String> recipients = parseEmails(recipientsSearch);
		String badFormatEmail =  "";
		
		for (String recipient : recipients) {
			if (!MAILREGEXP.matcher(recipient.toUpperCase()).matches()){
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

		/** 
		 * retrieve the url from propertie file
		 * 
		 */
		String linShareUrl=propertiesSymbolProvider.valueForSymbol("linshare.info.urlShare");

		/**
		 * retrieve the subject of the mail.
		 */
		String subject=messages.get("mail.user.all.share.subject");

		
		// prevent NPE
		if (textAreaValue==null)
			textAreaValue = "";
		
		//html template
		String sharedTemplateContent = null;
		String passwordSharedTemplateContent = null;
		//TXT template
		String sharedTemplateContentTxt = null;
		String passwordSharedTemplateContentTxt = null;

		try {
			sharedTemplateContent = templating.readFullyTemplateContent(sharedTemplate.getResource().openStream());
			passwordSharedTemplateContent = templating.readFullyTemplateContent(passwordSharedTemplate.getResource().openStream());
			
			sharedTemplateContentTxt = templating.readFullyTemplateContent(sharedTemplateTxt.getResource().openStream());
			passwordSharedTemplateContentTxt = templating.readFullyTemplateContent(passwordSharedTemplateTxt.getResource().openStream());
			
		} catch (IOException e) {
			logger.error("Bad mail template", e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad template",e);
		}
		SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
		try {
		
			//OLD CALL with userVo !
			//sharing = shareFacade.createSharingWithMail(userVo, documentsVo, usersVo,textAreaValue, message,subject);
			
			//CALL new share function with all adress mails !
			sharing = shareFacade.createSharingWithMailUsingRecipientsEmailAndExpiryDate(userVo, documentsVo,recipientsEmail,textAreaValue,subject,linShareUrl,secureSharing,sharedTemplateContent,sharedTemplateContentTxt,passwordSharedTemplateContent,passwordSharedTemplateContentTxt, dateExpiry);
			

		
		} catch (BusinessException e1) {
			logger.error("Could not create sharing", e1);
			throw e1;
		}

		
		shareSessionObjects=new ShareSessionObjects();
		if (sharing.getFailsItem().size()>0) {
			shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.fail"));
		} else {
			recipientFavouriteFacade.increment(userVo, recipientsEmail);
			shareSessionObjects.addMessage(messages.get("components.confirmSharePopup.success"));
		}
		
		return onSuccess;
		
	}
    

	public String getJSONId() {
		return confirmWindow.getJSONId();
	}


}
