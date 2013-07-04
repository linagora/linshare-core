package org.linagora.linshare.view.tapestry.pages.files;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.core.utils.FileUtils.Unit;
import org.linagora.linshare.core.utils.StringJoiner;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.QuickSharePopup;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.linagora.linshare.core.utils.FileUtils;
//import org.linagora.linshare.core.utils.FileUtils.Unit;

@Import(library = { "../../components/jquery/jquery-1.7.2.js",
		"../../components/fineuploader/fineuploader-3.6.4.js",
		"./fine-uploader/js/uploader-demo.js" }, stylesheet = {
		"../../components/fineuploader/fineuploader-3.6.4.css",
		"./fine-uploader/css/styles.css" })
public class Upload {

	private static final Logger logger = LoggerFactory
			.getLogger(QuickSharePopup.class);
	
	// illimited file size
	private static final long DEFAULT_MAX_FILE_SIZE = 0;

	/* ***********************************************************
	 * Parameters ***********************************************************
	 */

	/* ***********************************************************
	 * Properties & injected symbol, ASO, etc
	 * ***********************************************************
	 */
	@SessionState
	private UserVo userVo;

	@SessionState
	private ShareSessionObjects shareSessionObjects;

	@InjectComponent
	@Property
	private Form quickShareForm;

	// @InjectComponent
	// private Zone shareZone;

	@Persist("flash")
	@Property
	private String uuids;

	@Persist("flash")
	@Property
	private boolean is_submit;

	@Persist("flash")
	@Property
	private int progress;

	@Property
	private String recipientsSearch;

	@Persist("flash")
	@Property
	private boolean secureSharing;

	@Property
	private String textAreaValue;

	@Property
	private String textAreaSubjectValue;

	@Persist
	@Property
	private boolean showSecureSharingCheckBox;

	@Property
	private int autocompleteMin;

	@Property
	private String contextPath;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */
	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Messages messages;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private UserFacade userFacade;

	@Inject
	private ShareFacade shareFacade;

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private Policy antiSamyPolicy;

	private XSSFilter filter;

	void onActivate() {
		contextPath = requestGlobals.getHTTPServletRequest().getContextPath();
	}

	@SetupRender
	void init() {
		String domainId = userVo.getDomainIdentifier();
		autocompleteMin = functionalityFacade.completionThreshold(domainId);
		showSecureSharingCheckBox = shareFacade
				.isVisibleSecuredAnonymousUrlCheckBox(domainId);
		if (showSecureSharingCheckBox) {
			secureSharing = shareFacade
					.getDefaultSecuredAnonymousUrlCheckBoxValue(domainId);
		}
	}

	void onValidateFromQuickShareForm() {
		is_submit = true;
		if (progress > 0) {
			return; // some uploads are still in progress
		}
	}

	Object onSubmitFromQuickShareForm() throws BusinessException {
		/*
		 * XXX FIXME TODO HACK : same code as QuickSharePopup ... it's not just
		 * smelly
		 */
		logger.debug("uuids = " + uuids);
		filter = new XSSFilter(shareSessionObjects, quickShareForm, antiSamyPolicy,
				messages);
		try {
			textAreaSubjectValue = filter.clean(textAreaSubjectValue);
			textAreaValue = filter.clean(textAreaValue);
			if (filter.hasError()) {
				logger.debug("XSSFilter found some tags and striped them.");
				businessMessagesManagementService.notify(filter
						.getWarningMessage());
			}
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
		}
		// VALIDATE

		boolean sendErrors = false;

		try {
			List<DocumentVo> addedDocuments = new ArrayList<DocumentVo>();
			List<String> recipients = MailCompletionService
					.parseEmails(recipientsSearch);
			List<String> recipientsEmail;

			String badFormatEmail = "";

			for (String uuid : StringJoiner.split(uuids, ",")) {
				DocumentVo d = documentFacade.getDocument(userVo.getLogin(),
						uuid);

				if (d == null) {
					// shouldn't be there
					logger.error("Error document with uuid: " + uuid
							+ " not found.");
				}
				addedDocuments.add(d);
			}

			for (String recipient : recipients) {
				if (!MailCompletionService.MAILREGEXP.matcher(
						recipient.toUpperCase()).matches()) {
					badFormatEmail = badFormatEmail + recipient + " ";
					sendErrors = true;
				}
			}

			if (sendErrors) {
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_BADMAIL,
								MessageSeverity.ERROR, badFormatEmail));
				addedDocuments = new ArrayList<DocumentVo>();
				return Upload.class;
			} else {
				recipientsEmail = recipients;
			}

			if (addedDocuments == null || addedDocuments.size() == 0) {
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_NO_FILE_TO_SHARE,
								MessageSeverity.ERROR));
				return Upload.class;
			}

			// PROCESS SHARE

			Boolean errorOnAddress = false;

			SuccessesAndFailsItems<ShareDocumentVo> sharing = new SuccessesAndFailsItems<ShareDocumentVo>();
			try {
				MailContainer mailContainer = new MailContainer(
						userVo.getLocale(), textAreaValue, textAreaSubjectValue);
				sharing = shareFacade
						.createSharingWithMailUsingRecipientsEmailAndExpiryDate(
								userVo, addedDocuments, recipientsEmail,
								secureSharing, mailContainer, null);

			} catch (BusinessException e1) {

				// IF RELAY IS DISABLE ON SMTP SERVER
				if (e1.getErrorCode() == BusinessErrorCode.RELAY_HOST_NOT_ENABLE) {
					logger.error(
							"Could not create sharing, relay host is disable : ",
							e1);

					String buffer = "";
					String sep = "";
					for (String extra : e1.getExtras()) {
						buffer = buffer + sep + extra;
						sep = ", ";
					}
					businessMessagesManagementService
							.notify(new BusinessUserMessage(
									BusinessUserMessageType.UNREACHABLE_MAIL_ADDRESS,
									MessageSeverity.ERROR, buffer));
					errorOnAddress = true;
				} else {
					logger.error("Could not create sharing, caught a BusinessException.");
					logger.error(e1.getMessage());
					businessMessagesManagementService.notify(e1);

					// reset list of documents
					addedDocuments = new ArrayList<DocumentVo>();
					return Upload.class;
				}
			}

			if (sharing.getFailsItem().size() > 0) {
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_FAILED,
								MessageSeverity.ERROR));
			} else if (errorOnAddress) {
				recipientFavouriteFacade.increment(userVo, recipientsEmail);
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.SHARE_WARNING_MAIL_ADDRESS,
								MessageSeverity.WARNING));
			} else {
				recipientFavouriteFacade.increment(userVo, recipientsEmail);
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_SUCCESS,
								MessageSeverity.INFO));
			}

		} catch (NullPointerException e3) {
			logger.error("No Email in textarea", e3);
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.QUICKSHARE_NOMAIL,
					MessageSeverity.ERROR));
		}
		return Index.class;
	}

	List<String> onProvideCompletionsFromRecipientsPattern(String input) {
		List<String> elements = new ArrayList<String>();
		List<UserVo> searchResults = performSearch(input);

		for (UserVo user : searchResults) {
			String completeName = MailCompletionService.formatLabel(user);
			if (!elements.contains(completeName)) {
				elements.add(completeName);
			}
		}
		return elements;
	}
	
	public long getMaxFileSize() {
		long maxFileSize = DEFAULT_MAX_FILE_SIZE;
		try {
			long freeSpace = documentFacade.getUserAvailableQuota(userVo);
			maxFileSize = documentFacade.getUserMaxFileSize(userVo);
			if (freeSpace < maxFileSize) {
				maxFileSize = freeSpace;
			}
			logger.debug("max size file : " + FileUtils.getFriendlySize(maxFileSize, messages));
		} catch (BusinessException e) {
			logger.error("Can not set user maximum size for a file : " + e.getLocalizedMessage());
			// value has not been defined. We use the default value.
		}
		return maxFileSize;
	}

	public long getMaxCountFile() {
		long maxCountFile = 0 ;
		try {
			long freeSpace = documentFacade.getUserAvailableQuota(userVo);
			long maxFileSize = documentFacade.getUserMaxFileSize(userVo);
			maxCountFile = freeSpace / freeSpace;
			logger.debug("max size : " + FileUtils.getFriendlySize(maxFileSize, messages));
			logger.debug("free space : " + FileUtils.getFriendlySize(maxCountFile,messages));
			logger.debug("max count file : " + maxCountFile);
		} catch (BusinessException e) {
			logger.error("Can not set user maximum size for a file : " + e.getLocalizedMessage());
			// value has not been defined. We use the default value.
		}
		return maxCountFile;
		
	}
	/*
	 * Helpers
	 */

	/**
	 * Perform a user search using the user search pattern.
	 * 
	 * @param input
	 *            user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {
		Set<UserVo> userSet = new HashSet<UserVo>();
		List<UserVo> res = new ArrayList<UserVo>();

		String firstName_ = null;
		String lastName_ = null;
		String input_ = input != null ? input.trim() : null;

		if (input_.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}
		try {
			userSet.addAll(userFacade.searchUser(input_, null, null, userVo));
			userSet.addAll(userFacade.searchUser(null, firstName_, lastName_,
					userVo));
			userSet.addAll(userFacade.searchUser(null, lastName_, firstName_,
					userVo));
			userSet.addAll(recipientFavouriteFacade.findRecipientFavorite(
					input.trim(), userVo));
			res.addAll(recipientFavouriteFacade.recipientsOrderedByWeightDesc(
					new ArrayList<UserVo>(userSet), userVo));
		} catch (BusinessException e) {
			logger.error("Error while searching user in QuickSharePopup", e);
		}
		return res;
	}
}
