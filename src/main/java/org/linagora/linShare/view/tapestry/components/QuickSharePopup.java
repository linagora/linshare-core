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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.RecipientFavouriteFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SupportsInformalParameters
@IncludeJavaScriptLibrary(value = {"QuickSharePopup.js", "SizeOfPopup.js"})
public class QuickSharePopup{
	private static final Logger logger = LoggerFactory.getLogger(QuickSharePopup.class);

	public static final String RELOADZONE_EVENT = "reload";
	
	/* ***********************************************************
     *                         Parameters
     ************************************************************ */

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
	@ApplicationState
	private UserVo userVo;

	@ApplicationState
	private ShareSessionObjects shareSessionObjects;
	

	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;

	@Property
	private String textAreaValue;

    @Persist
    @Property
    private List<DocumentVo> addedDocuments;
	
    @Property
    private DocumentVo documentVo;
	
	@Persist
	@Property
	private String recipientsSearch;
	
	@Persist("flash")
	@Property
	private boolean secureSharing;
	
	@Property
	private int index;
	
	@Property
	private int currentSize = 1;
	
	@Persist("flash")
	private List<String> recipientsEmail;
	
	
    
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
	private DocumentFacade documentFacade;
	
	@Inject
    private RenderSupport renderSupport;
	
	@Inject
	private ShareFacade shareFacade;
	
	@Inject
	private UserFacade userFacade;
	
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private PropertiesSymbolProvider propertiesSymbolProvider;
	
	@Inject
	private Messages messages;

	@Inject
	private Templating templating;

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

	@SuppressWarnings("unused")
	@Component(parameters = {"style=bluelighting", "show=false","width=650", "height=550", "closable=true"})
	private WindowWithEffects quickShareWindow;

    @InjectComponent
    @Property
    private Form quickShareForm;

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
            userSet.addAll(userFacade.searchUser(input.trim(), null, null, userVo));
        }
		userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, userVo));
		userSet.addAll(userFacade.searchUser(null, lastName_, firstName_, userVo));
		userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(input.trim(), userVo));
		
		return recipientFavouriteFacade.recipientsOrderedByWeightDesc(new ArrayList<UserVo>(userSet), userVo);
	}
	
	
    public void onSuccessFromQuickShareForm() throws BusinessException {

    	//VALIDATE
    	
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
			businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_BADMAIL,
                MessageSeverity.ERROR, badFormatEmail));
			return;
		} else {
			this.recipientsEmail = recipients;
		}
    	
    	
    	if (addedDocuments == null || addedDocuments.size() == 0){
    		businessMessagesManagementService.notify(
                new BusinessUserMessage(BusinessUserMessageType.QUICKSHARE_NO_FILE_TO_SHARE, MessageSeverity.ERROR));
			return;
    	}
    	
		//PROCESS SHARE
    	
    	
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
		//txt template
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
		
			//CALL new share function with all adress mails !
			sharing = shareFacade.createSharingWithMailUsingRecipientsEmail(userVo, addedDocuments,recipientsEmail,textAreaValue,subject,linShareUrl,secureSharing,sharedTemplateContent,sharedTemplateContentTxt,passwordSharedTemplateContent,passwordSharedTemplateContentTxt);
		
		
		} catch (BusinessException e1) {
			logger.error("Could not create sharing", e1);
			businessMessagesManagementService.notify(e1);
		}

		
		if (sharing.getFailsItem().size()>0) {
    		businessMessagesManagementService.notify(new BusinessUserMessage(
                BusinessUserMessageType.QUICKSHARE_FAILED, MessageSeverity.ERROR));
		} else {
			
			recipientFavouriteFacade.increment(userVo, recipientsEmail);
			businessMessagesManagementService.notify(new BusinessUserMessage(
                BusinessUserMessageType.QUICKSHARE_SUCCESS, MessageSeverity.INFO));
		}

        // reset list of documents
        addedDocuments = new ArrayList<DocumentVo>();
	}
    
	/**
	 * here we add the file uploaded in the array. It is a good place to check it
	 * @param aFile
	 * @throws BusinessException 
	 */
    public void onValidateFromFile(UploadedFile aFile)  {
        if (aFile==null)
            return;
        String mimeType;
        try {
            mimeType = documentFacade.getMimeType(aFile.getStream(), aFile.getFilePath()); //get mime type with apperture
            if(null==mimeType){ //unknown mime type so take Uploaded File declaration
                mimeType = aFile.getContentType();
            }
        } catch (BusinessException e) {
            mimeType = aFile.getContentType();
        }

        try {
            DocumentVo doc = documentFacade.insertFile(aFile.getStream(), aFile.getSize(), aFile.getFileName(), mimeType, userVo);
            documentsVolist.add(doc);
            successFiles.add(aFile.getFileName());
        } catch (BusinessException e) {
            failFiles.put(aFile.getFileName(),e);
        }
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
