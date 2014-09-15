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
package org.linagora.linshare.view.tapestry.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.pages.files.Share;
import org.linagora.linshare.view.tapestry.utils.WelcomeMessageUtils;
import org.slf4j.Logger;

/**
 * Start page of application securedShare.
 */
public class Index {

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */
	@Inject
	private ShareFacade shareFacade;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Inject
	private PageRenderLinkSource linkFactory;

	@Inject
	private PersistentLocale persistentLocale;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Environmental
	private JavaScriptSupport renderSupport;

	@Inject
	private Response response;

	@Inject
	private Logger logger;

	@InjectPage
	private Share share;

	/* ***********************************************************
	 * Properties & injected symbol, ASO, etc
	 * ***********************************************************
	 */
	@Property
	@Persist
	private List<ShareDocumentVo> shares;

	@SessionState
	@Property
	private UserVo userVo;
	@Property
	private boolean userVoExists;

	@Inject
	@Symbol("linshare.display.licenceTerm")
	@Property
	private boolean linshareLicenceTerm;

	@Property
	private String welcomeText;

	@Property
	@Persist
	private boolean advanced;

	@Persist
	@Property
	/** used to prevent the clearing of documentsVo with search*/
	private boolean flag;

	@Persist
	private boolean finishForwarding;

	/* ***********************************************************
	 * Event handlers&processing
	 * ***********************************************************
	 */

	public Object onActivate() {
		if (shareSessionObjects != null)
			shareSessionObjects
					.checkDocumentsTypeIntegrity(ShareDocumentVo.class);

		if (userVoExists) {
			if (userVo.isSuperAdmin()) {
				return org.linagora.linshare.view.tapestry.pages.administration.Index.class;
			}
			if (userVo.hasDelegationRole()
					|| userVo.hasUploadPropositionRole()
					) {
				return org.linagora.linshare.view.tapestry.pages.administration.UserConfig.class;
			}
		}
		if (finishForwarding) {
			share.setSelectedDocuments(shareSessionObjects.getDocuments());
			clearList();
			finishForwarding = false;
			return share;
		}
		return null;
	}

	@SetupRender
	private void initList() throws BusinessException {
		if (!userVoExists) {
			Language language = WelcomeMessageUtils.getLanguage(
					persistentLocale.get(), request.getLocale(), null);
			shares = new ArrayList<ShareDocumentVo>();
			welcomeText = "";
			// welcomeText =
			// WelcomeMessageUtils.getWelcomeText(parameterVo.getWelcomeTexts(),
			// language,
			// UserType.INTERNAL).getWelcomeText();

		} else {
			AbstractDomainVo domain = domainFacade.retrieveDomain(userVo
					.getDomainIdentifier());

			Locale userLocale = null;
			if (((userVo.getLocale()) != null)
					&& (!userVo.getLocale().equals(""))) {
				userLocale = new Locale(userVo.getLocale());
			}
			Language language = WelcomeMessageUtils.getLanguage(
					persistentLocale.get(), request.getLocale(), userLocale);

			if (!flag) {
				shares = shareFacade.getAllSharingReceivedByUser(userVo);
			}

			welcomeText = WelcomeMessageUtils.getWelcomeText(
					domainFacade.getMessages(domain.getIdentifier())
							.getWelcomeTexts(), language, userVo.getUserType())
					.getWelcomeText();

		}

	}

	/**
	 * Show the popup if it should be shown
	 */
	@AfterRender
	public void postInit() {
		if (finishForwarding) {
			// show the share window popup
			// renderSupport.addScript(String.format("quickForwardWindow.showCenter(true)"));
			finishForwarding = false;
		}
	}

	/**
	 * Call the forward popup Invoked when the user submit the basket form
	 * 
	 * @param elements
	 *            : a DocumentVo[] of files to forward
	 */
	@OnEvent(value = "sharePanel")
	public void onForward(Object[] elements) {
		finishForwarding = true;
	}

	/**
	 * Remove the document from the share list
	 * 
	 * @param object
	 *            : object[0] contains a the DocumentVo
	 */
	@OnEvent(value = "deleteFromSharePanel")
	public void deleteSharePanel(Object[] object) {
		shareSessionObjects.removeDocument((DocumentVo) object[0]);

	}

	/**
	 * Clear the shared document list
	 */
	@OnEvent(value = "clearListObject")
	public void clearList() {
		shareSessionObjects = new ShareSessionObjects();
	}

	/**
	 * Add the document list in the shared list Invoked when the user click on
	 * the multi share button
	 * 
	 * @param object
	 *            : a DocumentVo[] of files to forward
	 */
	@OnEvent(value = "eventForwardFromListDocument")
	private void forwardFromList(Object[] object) throws BusinessException {
		DocumentVo doc;

		if (shareSessionObjects.isComeFromSharePopup()) {
			shareSessionObjects.getDocuments().clear();
			shareSessionObjects.setComeFromSharePopup(false);
		}
		for (Object currentObject : object) {
			doc = (DocumentVo) currentObject;
			shareSessionObjects.addDocument(doc);
		}
		shareSessionObjects.setMultipleSharing(true); // enable to multiple file
														// sharing
	}

	@OnEvent(value = "eventDeleteUniqueFromListDocument")
	private void deleteFromList(Object[] object) throws BusinessException {
		String uuid = (String) object[0];

		ShareDocumentVo shareddoc = searchShareVoByUUid(uuid);
		shareFacade.deleteSharing(shareddoc, userVo);
		resetListFiles(null);
	}

	@OnEvent(value = "eventDeleteFromListDocument")
	public void deleteFromListDocument(Object[] object)
			throws BusinessException {

		for (Object currentObject : object) {
			ShareDocumentVo share = (ShareDocumentVo) currentObject;
			shareFacade.deleteSharing(share, userVo);
			shareSessionObjects.addMessage(String.format(
					messages.get("pages.index.message.fileRemoved"),
					share.getFileName()));
			resetListFiles(null);
		}
	}

	/**
	 * sign the document Invoked when a user clicks on "sign" button in the
	 * searched document list
	 * 
	 * @param object
	 *            a DocumentVo[]
	 */
	@OnEvent(value = "eventSignatureFromListDocument")
	public void signatureFromListDocument(Object[] object) {

		List<String> identifiers = new ArrayList<String>();

		// context is a list of document (tab files)
		identifiers.add(DocToSignContext.SHARED.toString());

		for (Object currentObject : object) {
			DocumentVo doc = (DocumentVo) currentObject;

			if (doc.getEncrypted()) {
				shareSessionObjects.addWarning(messages
						.get("pages.index.message.signature.encryptedFiles"));
				return; // quit
			} else {
				identifiers.add(doc.getIdentifier());
			}
		}

		Link mylink = linkFactory.createPageRenderLinkWithContext(
				"signature/SelectPolicy", identifiers.toArray());

		try {
			response.sendRedirect(mylink);
		} catch (IOException ex) {
			throw new TechnicalException("Bad URL" + ex);
		}

	}

	private ShareDocumentVo searchShareVoByUUid(String uuid) {
		for (ShareDocumentVo shareDocumentVo : shares) {
			if (uuid.equals(shareDocumentVo.getIdentifier())) {
				return shareDocumentVo;
			}
		}
		return null;
	}

	public Object onActivate(Object obj) {
		return ErrorNotFound.class;
	}

	@CleanupRender
	private void initFlag() {
		// flag=false;
	}

	@OnEvent(value = "eventToggleAdvancedSearchSorterComponent")
	public void toggleAdvancedSearch(Object[] object) {
		flag = !flag;
		advanced = (Boolean) object[0];
	}

	/**
	 * The search component returns a document list, and we store it
	 * 
	 * @param object
	 *            : object[0] contains a List<DocumentVo>
	 */
	@SuppressWarnings("unchecked")
	@OnEvent(value = "eventDocument")
	public void initListDoc(Object[] object) {
		flag = true;
		this.shares = (List<ShareDocumentVo>) Arrays.copyOf(object, 1)[0];
	}

	@OnEvent(value = "resetListFiles")
	public void resetListFiles(Object[] o1) throws BusinessException {
		flag = false;
		shares = shareFacade.getAllSharingReceivedByUser(userVo);
	}

	@OnEvent(value = "inFileSearch")
	public void inSearch(Object[] o1) {
		flag = true;
	}

	public String getPageTitle() {
		return messages.get("components.myborderlayout.home.title");
	}

	public Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
