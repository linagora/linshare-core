/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.view.tapestry.pages.files;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.CleanupRender;
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
import org.apache.tapestry5.services.SelectModelFactory;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
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

import se.unbound.tapestry.tagselect.LabelAwareValueEncoder;

import com.google.common.collect.Lists;

@Import(library = { "../../components/jquery/jquery-1.7.2.js",
		"../../components/bootstrap/js/bootstrap.js" }, stylesheet = {
		"context:css/spinner.css" })
public class Share {

	private static final Logger logger = LoggerFactory.getLogger(Upload.class);

	@SessionState
	private UserVo userVo;

	@SessionState
	private ShareSessionObjects shareSessionObjects;

	@InjectComponent
	@Property
	private Form shareForm;

	@Property
	private String recipientsSearch;

	@Property
	private List<MailingListVo> mailingLists;

	@Persist(PersistenceConstants.FLASH)
	@Property
	private boolean secureSharing;

	@Property
	private String textAreaValue;

	@Property
	private String textAreaSubjectValue;

	@Persist
	@Property
	private boolean showSecureSharingCheckBox;

	@Persist
	@Property
	private List<DocumentVo> documents;

	@Property
	private int autocompleteMin;

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
	private MailingListFacade mailingListFacade;

	@Inject
	private ShareFacade shareFacade;

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private SelectModelFactory selectModelFactory;

	@Inject
	private Policy antiSamyPolicy;

	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;

	private XSSFilter filter;

	public Object onActivate() {
		if (documents == null || documents.isEmpty())
			return Index.class;
		return null;
	}

	@SetupRender
	public void init() {
		String domainId = userVo.getDomainIdentifier();
		autocompleteMin = functionalityFacade.completionThreshold(domainId);
		showSecureSharingCheckBox = shareFacade
				.isVisibleSecuredAnonymousUrlCheckBox(domainId);
		if (showSecureSharingCheckBox) {
			secureSharing = shareFacade
					.getDefaultSecuredAnonymousUrlCheckBoxValue(domainId);
		}
	}

	/*
	 * Hack: force bootstrap css to be the last stylesheet loaded
	 */
	@Import(stylesheet = { "../../components/bootstrap/css/bootstrap.css" })
	@CleanupRender
	void cleanupRender() {
	}

	public void onPrepare() {
		if (this.mailingLists == null) {
			this.mailingLists = new ArrayList<MailingListVo>();
		}
	}

	public Object onSubmitFromShareForm() throws BusinessException {
		/*
		 * XXX FIXME TODO HACK : same code as QuickSharePopup ... it's not just
		 * smelly
		 */
		filter = new XSSFilter(shareSessionObjects, shareForm,
				antiSamyPolicy, messages);
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
			/*
			 * Handle forward
			 */
			if (documents.get(0) instanceof ShareDocumentVo) {
				List tmp = documents;
				documents = Lists.newArrayList();
				for (ShareDocumentVo doc : (List<ShareDocumentVo>) tmp) {
					try {
						DocumentVo copied = shareFacade.createLocalCopy(doc, userVo);
						documents.add(copied);
						businessMessagesManagementService.notify(new BusinessUserMessage(
								BusinessUserMessageType.LOCAL_COPY_OK, MessageSeverity.INFO));
					} catch (BusinessException e) {
						// no space left or wrong mime type
						businessMessagesManagementService.notify(e);
					}
				}
			}

			List<String> recipients = new ArrayList<String>();

			if (recipientsSearch != null) {
				recipients = MailCompletionService
						.parseEmails(recipientsSearch);
			}

			List<String> recipientsEmail;

			String badFormatEmail = "";

			for (MailingListVo ml : mailingLists) {
				recipients.addAll(mailingListFacade.getAllContactMails(userVo, ml));
			}

			for (String recipient : recipients) {
				if (!MailCompletionService.MAILREGEXP.matcher(
						recipient.toUpperCase().trim()).matches()) {
					logger.error("Bad format email: \"" + recipient + "\"");
					badFormatEmail = badFormatEmail + recipient + " ";
					sendErrors = true;
				}
			}

			if (sendErrors) {
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_BADMAIL,
								MessageSeverity.ERROR, badFormatEmail));
				return Index.class;
			} else {
				recipientsEmail = recipients;
			}

			if (documents == null || documents.size() == 0) {
				businessMessagesManagementService
						.notify(new BusinessUserMessage(
								BusinessUserMessageType.QUICKSHARE_NO_FILE_TO_SHARE,
								MessageSeverity.ERROR));
				return Index.class;
			}

			// PROCESS SHARE
			try {
				MailContainer mailContainer = new MailContainer(
						userVo.getExternalMailLocale(), textAreaValue, textAreaSubjectValue);
				shareFacade.share(userVo, documents, recipientsEmail, secureSharing, mailContainer);
			} catch (BusinessException ex) {

				// IF RELAY IS DISABLE ON SMTP SERVER
				if (ex.equalErrCode(BusinessErrorCode.RELAY_HOST_NOT_ENABLE)) {
					logger.error("Could not create sharing, relay host is disable : ", ex);

					String buffer = "";
					String sep = "";
					for (String extra : ex.getExtras()) {
						buffer = buffer + sep + extra;
						sep = ", ";
					}
					businessMessagesManagementService
							.notify(new BusinessUserMessage(
									BusinessUserMessageType.UNREACHABLE_MAIL_ADDRESS,
									MessageSeverity.ERROR, buffer));
				} else if (ex.equalErrCode(BusinessErrorCode.SHARE_MISSING_RECIPIENTS)) {
					businessMessagesManagementService.notify(new BusinessUserMessage(
							BusinessUserMessageType.QUICKSHARE_NOMAIL,
							MessageSeverity.ERROR));
				} else {
					// TODO : Translate businessErrorCode into BusinessUserMessage.
					logger.error("Could not create sharing, caught a BusinessException.");
					logger.error(ex.getMessage());
					businessMessagesManagementService.notify(ex);
					return Index.class;
				}
			}
		} catch (NullPointerException e3) {
			logger.error("NPE :" , e3);
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.QUICKSHARE_FAILED,
					MessageSeverity.ERROR));
		}
		return Index.class;
	}

	public List<String> onProvideCompletionsFromRecipientsPattern(String input) {
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

	public SelectModel onProvideCompletionsFromMailingLists(String input)
			throws BusinessException {
		List<MailingListVo> lists = mailingListFacade.completionForUploadForm(
				userVo, input);

		return selectModelFactory.create(lists, "representation");
	}

	public boolean isEnableListTab() {
		return functionalityFacade.isEnableListTab(userVo.getDomainIdentifier());
	}

	public LabelAwareValueEncoder<MailingListVo> getEncoder() {
		return new LabelAwareValueEncoder<MailingListVo>() {
			@Override
			public String toClient(MailingListVo value) {
				return value.getUuid();
			}

			@Override
			public MailingListVo toValue(String clientValue) {
				return mailingListFacade.findByUuid(userVo, clientValue);
			}

			@Override
			public String getLabel(MailingListVo arg0) {
				return arg0.toString();
			}
		};
	}

	public void setSelectedDocuments(List<DocumentVo> documents) {
		this.documents = documents;
	}


	/** Perform a user search using the user search pattern.
	 * @param input user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {
		try {
			return userAutoCompleteFacade.autoCompleteUserSortedByFavorites(userVo, input);
		} catch (BusinessException e) {
			logger.error("Failed to autocomplete user on ConfirmSharePopup", e);
		}
		return new ArrayList<UserVo>();
	}
}

