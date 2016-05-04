/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.SearchDocumentFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.linagora.linshare.view.tapestry.services.MyMultipartDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page to upload files, search for documents, and handle the sharing Rely
 * heavily on uploader, sharePanelComponent and searchComponent This page
 * handles all the mechanics behind these component, all the communication is
 * done through event : - clearListObject : clear the current document share
 * list - eventDocument : launched by search component, contains in object[0]
 * the resulting List<DocumentVo> - eventDeleteFromListDocument : delete all the
 * documents contained DocumentVo[] from the repository - eventShare : add all
 * the the documents contained DocumentVo[] in the sharing -
 * eventDeleteUniqueFromListDocument : delete the unique document whom UUID is
 * object[0] from the repository - eventShareUniqueFromListDocument : add the
 * unique document whom UUID is object[0] in the current sharing -
 * eventReorderList : reorder the list of Document. - deleteFromSharePanel :
 * delete from the sharing the DocumentVo contained in object[0]
 * 
 * - sharePopupEvent : create the sharing, using the session objects
 * 
 * This page handles as well the Upload exception for the multipart decoder
 * 
 * @author ncharles
 * 
 */
@Import(library = { "Index.js" })
public class Index {

	private static final Logger logger = LoggerFactory.getLogger(Index.class);

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	private boolean shareSessionObjectsExists;

	@SessionState
	@Property
	private UserVo userVo;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */
	@Inject
	private ComponentResources componentResources;
	
	@InjectPage
	private Share share;

	@Inject
	private SearchDocumentFacade searchDocumentFacade;

	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private Messages messages;

	@Inject
	private Response response;

	@Environmental
	private JavaScriptSupport renderSupport;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Inject
	private MyMultipartDecoder myMultipartDecoder;

	@Inject
	private PageRenderLinkSource linkFactory;

	/* ***********************************************************
	 * Properties & injected symbol, ASO, etc
	 * ***********************************************************
	 */

	@Property
	@Persist
	/** the document list passed to the listDocument component, containing ShareDocumentVo or DocumentVo */
	private List<DocumentVo> listDocumentsVo;

	@Persist
	/** used to prevent the clearing of documentsVo */
	private boolean flag;

	@Persist
	private boolean flagFinishShare;

	@Persist
	private boolean flagGroupShare;

	@Persist("flash")
	private String fileMessage;

	@Property
	@Persist
	private boolean advanced;

	@Persist
	@Property
	private boolean inSearch;

	/* ***********************************************************
	 * Phase processing
	 * ***********************************************************
	 */
	
	public Object onActivate() {
		if (shareSessionObjects != null)
			shareSessionObjects.checkDocumentsTypeIntegrity(DocumentVo.class);
		if (flagFinishShare) {
			flagFinishShare = false;
			share.setSelectedDocuments(shareSessionObjects.getDocuments());
			clearList();
			return share;
		}
		return null;
	}
	
	@SetupRender
	private void initList() {
		if (!shareSessionObjectsExists) {
			shareSessionObjects = new ShareSessionObjects();
		}
		
		if (!flag) {
			listDocumentsVo = searchDocumentFacade.retrieveDocument(userVo);

		}

		if (fileMessage == null) {
			fileMessage = "";
		}
	}

	@CleanupRender
	private void initFlag() {
		shareSessionObjects.setMessages(new ArrayList<String>());
		// flag=false;
	}

	/* ***********************************************************
	 * Event handlers&processing
	 * ***********************************************************
	 */

	/**
	 * This is when the upload fails. It must be in the page, and not in the
	 * component
	 * 
	 * @throws BusinessException
	 */
	public Object onUploadException(Throwable cause) throws BusinessException {
		if (cause instanceof FileUploadBase.FileSizeLimitExceededException) {
			shareSessionObjects.addError(String.format(messages.get("pages.upload.FileSizeLimitExceededException"), FileUtils.getFriendlySize(documentFacade.getUserMaxFileSize(userVo), messages)));

		}
		myMultipartDecoder.cleanException();
		return this;
	}

	/**
	 * Sharing process between this page and user page.
	 */
	@OnEvent(value = "sharePanel")
	public void onShare(Object[] elements) {
		flagFinishShare = true;
	}

	/**
	 * Clear the shared document list
	 */
	@OnEvent(value = "clearListObject")
	public void clearList() {
		reinitASO();
	}

	@OnEvent(value = "resetListFiles")
	public void resetListFiles(Object[] o1) {
		inSearch = false;
		listDocumentsVo = searchDocumentFacade.retrieveDocument(userVo);
	}

	@OnEvent(value = "inFileSearch")
	public void inSearch(Object[] o1) {
		inSearch = true;
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
		this.listDocumentsVo = (List<DocumentVo>) Arrays.copyOf(object, 1)[0];
	}

	/**
	 * Delete the document from the repository/facade Invoked when a user clicks
	 * on "delete" button in the searched document list It also removes the
	 * documents from the shared list
	 * 
	 * @param object
	 *            a DocumentVo[]
	 */
	@OnEvent(value = "eventDeleteFromListDocument")
	public void deleteFromListDocument(Object[] object) {
		boolean flagError = false;

		for (Object currentObject : object) {
			try {
				documentFacade.removeDocument(userVo, ((DocumentVo) currentObject));

			} catch (BusinessException e) {
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failRemovingFile"), ((DocumentVo) currentObject).getFileName()));
				logger.debug(e.toString());
			}
			shareSessionObjects.removeDocument((DocumentVo) currentObject);
		}

		if (null != object && object.length > 0 && !flagError) {
			shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"), object.length));

			resetListFiles(null);
		}
	}

	/**
	 * crypt the list of documents Invoked when a user clicks on
	 * "encrypt/decrypt" button in the searched document list
	 * 
	 * @param object
	 *            a DocumentVo[]
	 */
	@SuppressWarnings("unchecked")
	@OnEvent(value = "eventCryptListDocFromListDocument")
	public void cryptListDoc(Object[] object) {

		String pass = (String) object[0];
		List<DocumentVo> listDocToEncrypt = (List<DocumentVo>) object[1];

		boolean ko = false; // if one problem exist in encrypt/decrypt many
							// files
		int numberIgnore = 0;

		for (DocumentVo currentObject : listDocToEncrypt) {
			try {
				if (!currentObject.getEncrypted() && !currentObject.getShared()) {
					documentFacade.encryptDocument(currentObject, userVo, pass);
				} else {
					numberIgnore++; // if it exists an entry which is already
									// encrypted ignore it
				}

			} catch (BusinessException e) {
				ko = true;
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failed.crypt"), (currentObject).getFileName()));
				logger.debug(e.toString());
			}

		}

		if (!ko && numberIgnore < listDocToEncrypt.size())
			shareSessionObjects.addMessage(messages.get("pages.index.message.success.crypt"));
		if (numberIgnore > 0)
			shareSessionObjects.addWarning(messages.get("pages.index.message.crypt.ignoreFile"));

	}

	@SuppressWarnings("unchecked")
	@OnEvent(value = "eventDecryptListDocFromListDocument")
	public void decryptListDoc(Object[] object) {

		String pass = (String) object[0];
		List<DocumentVo> listDocToDecrypt = (List<DocumentVo>) object[1];

		boolean ko = false; // if one problem exist in encrypt/decrypt many
							// files
		int numberIgnore = 0;

		for (DocumentVo currentObject : listDocToDecrypt) {
			try {
				if (currentObject.getEncrypted() && !currentObject.getShared()) {
					documentFacade.decryptDocument(currentObject, userVo, pass);
				} else {
					numberIgnore++; // if it exists an entry which is already
									// encrypted ignore it
				}
			} catch (BusinessException e) {
				ko = true;
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failed.decrypt"), (currentObject).getFileName()));
				logger.debug(e.toString());
			}

		}

		if (!ko && numberIgnore < listDocToDecrypt.size())
			shareSessionObjects.addMessage(messages.get("pages.index.message.success.decrypt"));
		if (numberIgnore > 0)
			shareSessionObjects.addWarning(messages.get("pages.index.message.decrypt.ignoreFile"));
	}

	/**
	 * crypt one doc
	 * 
	 * @param object
	 */
	@OnEvent(value = "eventCryptOneDocFromListDocument")
	public void cryptOneDoc(Object[] object) {
		String pass = (String) object[0];
		DocumentVo currentDocumentVo = (DocumentVo) object[1];

		if (currentDocumentVo.getShared()) {
			// do nothing on shared document
			shareSessionObjects.addWarning(String.format(messages.get("pages.index.message.failed.crypt.sharedFile"), (currentDocumentVo).getFileName()));
		} else {
			// ignore already encrypted file
			if (!currentDocumentVo.getEncrypted()) {
				try {
					documentFacade.encryptDocument(currentDocumentVo, userVo, pass);
					shareSessionObjects.addMessage(messages.get("pages.index.message.success.crypt"));
				} catch (BusinessException e) {
					shareSessionObjects.addError(String.format(messages.get("pages.index.message.failed.crypt"), (currentDocumentVo).getFileName()));
					logger.debug(e.toString());
				}
			}
		}
	}

	/**
	 * decrypt one doc
	 * 
	 * @param object
	 */
	@OnEvent(value = "eventDecryptOneDocFromListDocument")
	public void decryptOneDoc(Object[] object) {

		String pass = (String) object[0];
		DocumentVo currentDocumentVo = (DocumentVo) object[1];

		if (currentDocumentVo.getShared()) {
			// do nothing on shared document
			shareSessionObjects.addWarning(String.format(messages.get("pages.index.message.failed.decrypt.sharedFile"), (currentDocumentVo).getFileName()));
		} else {
			// ignore decrypted file !
			if (currentDocumentVo.getEncrypted()) {
				try {
					documentFacade.decryptDocument(currentDocumentVo, userVo, pass);
					shareSessionObjects.addMessage(messages.get("pages.index.message.success.decrypt"));
				} catch (BusinessException e) {
					shareSessionObjects.addError(String.format(messages.get("pages.index.message.failed.decrypt"), (currentDocumentVo).getFileName()));
					logger.debug(e.toString());
				}
			}
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
		identifiers.add(DocToSignContext.DOCUMENT.toString());

		for (Object currentObject : object) {
			DocumentVo doc = (DocumentVo) currentObject;

			if (doc.getEncrypted()) {
				shareSessionObjects.addWarning(String.format(messages.get("pages.index.message.signature.encryptedFiles")));
				return; // quit
			} else {
				identifiers.add(doc.getIdentifier());
			}
		}

		Link mylink = linkFactory.createPageRenderLinkWithContext("signature/SelectPolicy", identifiers.toArray());

		try {
			response.sendRedirect(mylink);
		} catch (IOException ex) {
			throw new TechnicalException("Bad URL" + ex);
		}

	}

	/**
	 * Add the document list in the shared list Invoked when the user click on
	 * the multi share button
	 * 
	 * @param object
	 *            : a DocumentVo[]
	 */
	@OnEvent(value = "eventShare")
	public void initShareList(Object[] object) {

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

	/**
	 * Delete a unique document from the repository It also removes the document
	 * from the shared list Invoked when a user click on the action delete
	 * button
	 * 
	 * @param object
	 *            : object[0] is the UUID of the document to be deleted
	 * @throws BusinessException
	 */
	@OnEvent(value = "eventDeleteUniqueFromListDocument")
	public void deleteUniqueFromListDocument(Object[] object) throws BusinessException {
		try {

			DocumentVo documentVo = getDocumentByUUIDInList((String) object[0]);
			if (null != documentVo) {
				documentFacade.removeDocument(userVo, documentVo);
				shareSessionObjects.removeDocument(documentVo);
				shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"), documentVo.getFileName()));
			} else {
				throw new BusinessException(BusinessErrorCode.INVALID_UUID, "invalid uuid");
			}
		} catch (BusinessException e) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID, "invalid uuid", e);
		}
	}

	/**
	 * Add a unique document in the share list Invoked when the user click on
	 * the unique share button
	 * 
	 * @param object
	 *            : object[0] contains the document UUID
	 * @throws BusinessException
	 */
	@OnEvent(value = "eventShareUniqueFromListDocument")
	public void shareUniqueFromListDocument(Object[] object) throws BusinessException {
		DocumentVo documentVoTemp = null;

		for (DocumentVo currentDocumentVo : this.listDocumentsVo) {
			if (currentDocumentVo.getIdentifier().equals((String) object[0])) {
				documentVoTemp = currentDocumentVo;
				break;
			}
		}
		if (null != documentVoTemp) {
			// enable direct sharing on this document
			flagFinishShare = true;
			shareSessionObjects.setComeFromSharePopup(true);
			shareSessionObjects.getDocuments().clear(); // delete all other doc
			shareSessionObjects.addDocument(documentVoTemp);
			shareSessionObjects.setMultipleSharing(false);
		}

	}

	@OnEvent(value = "eventShareWithGroupUniqueFromListDocument")
	public void shareUniqueWithGroupFromListDocument(Object[] object) throws BusinessException {

		// if(groupFacade.findByUser(user.getLogin()) == null){
		//
		// }
		DocumentVo documentVoTemp = null;

		for (DocumentVo currentDocumentVo : this.listDocumentsVo) {
			if (currentDocumentVo.getIdentifier().equals((String) object[0])) {
				documentVoTemp = currentDocumentVo;
				break;
			}
		}
		if (null != documentVoTemp) {
			// enable direct sharing on this document
			flagGroupShare = true;
			shareSessionObjects.setComeFromSharePopup(true);
			shareSessionObjects.getDocuments().clear(); // delete all other doc
			shareSessionObjects.addDocument(documentVoTemp);
			shareSessionObjects.setMultipleSharing(false);
		}

	}

	@OnEvent(value = "eventGroupShare")
	public void shareWithGroupFromListDocument(Object[] object) throws BusinessException {
		shareSessionObjects.getDocuments().clear(); // delete all other doc
		flagGroupShare = true;
		shareSessionObjects.setMultipleSharing(false);

		for (Object docObj : object) {
			DocumentVo documentVoTemp = (DocumentVo) docObj;
			if (null != documentVoTemp) {
				shareSessionObjects.addDocument(documentVoTemp);
			}
		}
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

	@OnEvent(value = "eventToggleAdvancedSearchSorterComponent")
	public void toggleAdvancedSearch(Object[] object) {
		advanced = (Boolean) object[0];
		flag = !flag;
	}

	private void reinitASO() {
		shareSessionObjects = new ShareSessionObjects();
	}

	/**
	 * show the popup if it is to be shown
	 */
	@AfterRender
	public void afterRender() {

		if (flagFinishShare) {

			// show the share window popup
			//renderSupport.addScript(String.format("confirmWindow.showCenter(true)"));
			flagFinishShare = false;
		}

		if (flagGroupShare) {
			renderSupport.addScript(String.format("groupShareWindow.showCenter(true)"));
			flagGroupShare = false;
		}

	}
	
	private DocumentVo getDocumentByUUIDInList(String UUId) {
		for (DocumentVo doc : listDocumentsVo) {
			if ((doc.getIdentifier()).equals(UUId)) {
				return doc;
			}
		}
		throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not find the document");
	}

	public boolean isDisplayUploadButton() {
		return (userVo.isUpload());
	}

	public boolean isDisplaySignButton() {
		return documentFacade.isSignatureActive(userVo);
	}

	public String getPageTitle() {
		return messages.get("components.myborderlayout.file.title");
	}

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
