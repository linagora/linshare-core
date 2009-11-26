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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.services.LinkFactory;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.CacheUserPinVo;
import org.linagora.linShare.core.domain.vo.DocToSignContext;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.enums.ActionFromBarDocument;
import org.linagora.linShare.view.tapestry.models.SorterModel;
import org.linagora.linShare.view.tapestry.models.impl.FileSorterModel;
import org.linagora.linShare.view.tapestry.objects.FileStreamResponse;

@SupportsInformalParameters
@IncludeJavaScriptLibrary(value = { "ListDocument.js" })
public class ListDocument {

	/***************************************************************************
	 * Parameters
	 **************************************************************************/
	/**
	 * The user owner for the document list.
	 */
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	private UserVo user;

	/**
	 * The list of documents.
	 */
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private List<DocumentVo> documents;

	/***************************************************************************
	 * Properties
	 **************************************************************************/

	@SuppressWarnings("unused")
	@Property
	private String userlogin;

	@Property
	@Persist
	private List<DocumentVo> listSelected;

	@Property
	private DocumentVo document;

	@SuppressWarnings("unused")
	@Property
	private Boolean valueCheck;
	
	@Property
	private String action;
	

	/***************************************************************************
	 * Service injection
	 **************************************************************************/

	@Environmental
	private RenderSupport renderSupport;

	@Inject
	private PersistentLocale persistentLocale;

	@Inject
	private DocumentFacade documentFacade;
	@Inject
	private ParameterFacade parameterFacade;

	@Inject
	private UserFacade userFacade;

	@Inject
	private ComponentResources componentResources;

	@InjectComponent
	private UserDetailsDisplayer userDetailsDisplayer;
	
	
	@Property(write = false)
	@InjectComponent
	private FileUpdateUploader fileUpdateUploader;

	@Property
	@InjectComponent
	private FileEditForm fileEdit;
	
	@Property(write = false)
	@InjectComponent
	private WarningDisplayer warningHttp;

	@Property(write = false)
	@InjectComponent
	private WarningDisplayer warningSignature;

	@Property(write = false)
	@InjectComponent
	private WarningDisplayer warningShare;

	@Property(write = false)
	@InjectComponent
	private WarningDisplayer warningEncipherment;

//	@InjectComponent
//	private SignatureDetailsDisplayer signatureDetailsDisplayer;

	@InjectComponent
	private PasswordPopup passwordPopup;

	@InjectComponent
	private PasswordPopupSubmit passwordPopupSubmit;

	
	
	
	@Inject
	private LinkFactory linkFactory;

	@Inject
	private BeanModelSource beanModelSource;

	@SuppressWarnings("unchecked")
	@Property
	@Persist
	private BeanModel model;

	@Inject
	private Messages messages;

	@Persist
	private String pass;

	@ApplicationState
	@Property
	private CacheUserPinVo cachePin;

	/***************************************************************************
	 * Flags
	 **************************************************************************/
	@Property
	private boolean activeSignature;

	@Property
	private boolean activeEncipherment;

	@SuppressWarnings("unused")
	@Property
	private boolean userEnciphermentKeyGenerated;

	@SuppressWarnings("unused")
	private boolean filesSelected;

	@Persist("flash")
	private ActionFromBarDocument actionbutton;

	@Property
	private String deleteConfirmed; // this is nasty, but i didn't find a proper
									// workaround

	@Property
	@Persist
	private String currentUuid;

	
	/**
	 * Components Model.
	 */
	@SuppressWarnings("unused")
	@Property
	@Persist
	private SorterModel<DocumentVo> sorterModel;
	
	
	@Persist
	private boolean refreshFlag;
	
	@Persist
	private List<DocumentVo> docs;
	
	/***************************************************************************
	 * Phase render
	 **************************************************************************/

	/**
	 * Initialization of the filesSelected list and set the userLogin from the
	 * user ASO.
	 * 
	 * @throws BusinessException
	 */
	@SetupRender
	public void initUserlogin() throws BusinessException {

		listSelected = new ArrayList<DocumentVo>();
		userlogin = user.getLogin();
		actionbutton = ActionFromBarDocument.NO_ACTION;
		activeSignature = parameterFacade.loadConfig().getActiveSignature();
		activeEncipherment = parameterFacade.loadConfig()
				.getActiveEncipherment();
		userEnciphermentKeyGenerated = userFacade
				.isUserEnciphermentKeyGenerated(user);

		// if(model==null)
		initModel();

	}

	/**
	 * Initialize the JS value
	 */
	@AfterRender
	public void afterRender() {
		if ((documents != null) && (documents.size() > 0))
			renderSupport.addScript(String.format("countCheckbox('');"));

		// renderSupport.addScript("Event.observe('deleteSubmitLink', 'click',
		// function(event) { window_confirm_submit.showCenter(true); });");
	}

	/***************************************************************************
	 * ActionLink methods
	 **************************************************************************/

	/**
	 * The action triggered when the user click on the download link on the name
	 * of the file.
	 */

	public StreamResponse onActionFromDownload(String uuid)
			throws BusinessException {

		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents, uuid);
		if (null == currentDocumentVo) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,
					"invalid uuid for this user");
		} else {

			if (!currentDocumentVo.getEncrypted()) {
				InputStream stream = documentFacade.retrieveFileStream(
						currentDocumentVo, user);
				return new FileStreamResponse(currentDocumentVo, stream);
			} else {
				throw new BusinessException(
						BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,
						"invalid download for a protected document");
			}
		}

		// // function to decrypt inputstream ...

		// InputStream
		// stream=documentFacade.retrieveFileStream(currentDocumentVo, user);
		// String pass = cachePin.getPassword();
		// SymmetricEnciphermentPBEwithAES enc = null;
		// try {
		// enc = new
		// SymmetricEnciphermentPBEwithAES(pass,stream,null,Cipher.DECRYPT_MODE);
		// return new
		// FileStreamResponse(currentDocumentVo,enc.getCipherInputStream());
		// } catch (InvalidKeyException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// } catch (NoSuchAlgorithmException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// } catch (InvalidKeySpecException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// } catch (NoSuchPaddingException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// } catch (InvalidAlgorithmParameterException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// } catch (IOException e) {
		// throw new
		// BusinessException(BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,"can not
		// decrypt and download document");
		// }

	}

	/**
	 * The action triggered when the user click on the signature link include in
	 * the action menu for each document.
	 */
	public Object onActionFromSignature(String uuid) throws BusinessException {

		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents, uuid);

		if (null == currentDocumentVo) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,
					"invalid uuid for this user");
		} else {
			// context is a list of document (tab files)
			return linkFactory.createPageRenderLink("signature/SelectPolicy",
					true, new Object[] { DocToSignContext.DOCUMENT.toString(),
							currentDocumentVo.getIdentifier() });
		}
	}

	/**
	 * The action triggered when the user click on the Encipherment include in
	 * the action menu for each document.
	 * 
	 * @param uuid
	 *            the uuid of the document.
	 */

	public void onActionFromEncipherment(String uuid) throws BusinessException {
		currentUuid = uuid;
		actionbutton = ActionFromBarDocument.ENCYPHERMENT_ACTION;
		passwordPopup.getFormPassword().clearErrors(); // delete popup message
	}

	/**
	 * The action triggered when the user click on the Encipherment include in
	 * the list for each document.
	 * 
	 * @param uuid
	 *            the uuid of the document.
	 */

	public void onActionFromEnciphermentIcon(String uuid)
			throws BusinessException {
		onActionFromEncipherment(uuid);
	}
	
	
	public void onActionFromUpdateDoc(String uuid) throws BusinessException {
		currentUuid = uuid;
		fileUpdateUploader.setUuidDocToUpdate(uuid);
	}
	
    public Zone onActionFromFileEditProperties(String uuid) throws BusinessException {
        currentUuid = uuid;
        fileEdit.setUuidDocToedit(uuid);
        return fileEdit.getShowPopupWindow();
    }

	/**
	 * The action triggered when the user click on the download include in the
	 * action menu for each document.
	 * 
	 * @param uuid
	 *            the uuid of the document.
	 * @return stream the stream of the document.
	 */
	public StreamResponse onActionFromDownloadOther(String uuid)
			throws BusinessException {
		return onActionFromDownload(uuid);
	}

	public void onActionFromDownloadwithPopupBis(String uuid) {
		currentUuid = uuid;
	}

	public void onActionFromDownloadwithPopup(String uuid) {
		currentUuid = uuid;
	}

	/**
	 * The action triggered when the user click on the share link in the action
	 * menu for each document.
	 * 
	 * @param uuid
	 *            the uuid of the document.
	 */
	public void onActionFromUniqueShareLink(String uuid) {

		componentResources.getContainer().getComponentResources()
				.triggerEvent("eventShareUniqueFromListDocument",
						new Object[] { uuid }, null);
	}

	/**
	 * @see onActionFromUniqueShareLink
	 * @param uuid
	 *            the uuid of the document.
	 */
	public void onActionFromUniqueShareLinkBis(String uuid) {

		onActionFromUniqueShareLink(uuid);
	}

	/**
	 * The action triggered when the delete link is pushed in the action menu
	 * for each document.
	 * 
	 * @param uuid
	 *            the uuid of the document to delete.
	 * 
	 */
	public void onActionFromDelete(String uuid) {
		currentUuid = uuid;
	}

	public void onActionFromDeleteSubmitLink() {
		actionbutton = ActionFromBarDocument.DELETE_ACTION;
	}

	public Zone onActionFromShowUser(String mail) {
		return userDetailsDisplayer.getShowUser(mail);
	}

	public Zone onActionFromShowWarningHttp() {
		return warningHttp.getShowWarning();
	}

	public Zone onActionFromShowWarningHttpBis() {
		return warningHttp.getShowWarning();
	}

	public Zone onActionFromShowWarningSignature() {
		return warningSignature.getShowWarning();
	}

	public Zone onActionFromShowWarningShare() {
		return warningShare.getShowWarning();
	}

	public Zone onActionFromShowWarningShareBis() {
		return warningShare.getShowWarning();
	}

	public Zone onActionFromShowWarningEncipherment() {
		return warningEncipherment.getShowWarning();
	}

	public Zone onActionFromShowWarningEnciphermentIcon() {
		return onActionFromShowWarningEncipherment();
	}

//	public Zone onActionFromShowSignature(String docidentifier) {
//		return signatureDetailsDisplayer.getShowSignature(docidentifier);
//	}

	public void onActionFromEncyphermentSubmit() {
		actionbutton = ActionFromBarDocument.ENCYPHERMENT_ACTION;
	}

	public void onActionFromEncyphermentSubmitBis() {
		onActionFromEncyphermentSubmit();
	}

	public Object onActionFromEnciphermentNoPopup(String currentUuid) {

		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents,
				currentUuid);
		String pass = cachePin.getPassword();

		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pass);
		parameters.add(currentDocumentVo);
		componentResources.getContainer().getComponentResources().triggerEvent(
				"eventEncyphermentUniqueFromListDocument",
				parameters.toArray(), null);
		return null;
	}

	public void onActionFromEnciphermentNoPopupIcon(String currentUuid) {
		onActionFromEnciphermentNoPopup(currentUuid);
	}

    public void onActionFromRenameFile(String newName) {
        documentFacade.renameFile(currentUuid, newName);
    }

	/***************************************************************************
	 * Events
	 **************************************************************************/

	/**
	 * The event triggered by the confirm window when the user pushes on YES.
	 * 
	 * @throws BusinessException
	 *             exception throws when the uuid doesn't exist.
	 */
	@OnEvent(value = "listDocumentEvent")
	public void removeDocument() throws BusinessException {
		if (null != currentUuid) {
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventDeleteUniqueFromListDocument",
							new Object[] { currentUuid }, null);
		} else {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,
					"invalid uuid");
		}
	}

	@OnEvent(value = "sharedSubmit")
	public void sharedSubmit() {
		actionbutton = ActionFromBarDocument.SHARED_ACTION;
	}

	/*
	 * @OnEvent(value="deleteSubmit") public void deleteSubmit(){
	 * System.out.println("deleteSubmit"); actionbutton =
	 * ActionButtonForCheckBox.DELETE_ACTION; }
	 */
	@OnEvent(value = "signatureSubmit")
	public void signatureSubmit() {
		actionbutton = ActionFromBarDocument.SIGNATURE_ACTION;
	}

	@OnEvent(value = "encyphermentSubmitNopopup")
	public void encyphermentSubmitNopopup() {
		actionbutton = ActionFromBarDocument.ENCYPHERMENT_ACTION;
	}

	@SuppressWarnings("unchecked")
	@OnEvent(value="eventReorderList")
	public void reorderList(Object[] o1){
		
		this.docs=(List<DocumentVo>)o1[0];
		this.sorterModel=new FileSorterModel(this.docs);
		refreshFlag=true;
	}
	
	/**
	 * This method is called when the form is submitted. If the form is
	 * submitted by push on shared button. The method will trigger an event
	 * named "eventShare" with a list containing the filesSelected documents
	 * attached. If the form is submitted by push on delete button. The method
	 * will trigger an event named "eventDeleteFromListDocument" with a list
	 * containing the filesSelected documents attached.
	 * 
	 * @return null
	 */
	public Object onSuccessFromSearch() {
		
		actionbutton =  ActionFromBarDocument.fromString(action);
		
		switch (actionbutton) {
		case SHARED_ACTION:
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventShare", listSelected.toArray(), null);
			break;
		case DELETE_ACTION:
			if ("true".equals(deleteConfirmed)) {
				componentResources.getContainer().getComponentResources()
						.triggerEvent("eventDeleteFromListDocument",
								listSelected.toArray(), null);
			}
			break;
		case SIGNATURE_ACTION:
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventSignatureFromListDocument",
							listSelected.toArray(), null);
			break;
		case ENCYPHERMENT_ACTION:
			List<Object> parameters = new ArrayList<Object>();
			if (this.pass == null)
				this.pass = cachePin.getPassword();
			parameters.add(this.pass);
			parameters.add(listSelected);
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventEncyphermentFromListDocument",
							parameters.toArray(), null);
			pass = null;
			break;
		case NO_ACTION:
		default:
			break;
		}

		actionbutton = ActionFromBarDocument.NO_ACTION;

		return null;
	}
	

	/***************************************************************************
	 * Other methods
	 **************************************************************************/

	/**
	 * Property used for know if the list is empty.
	 * 
	 * @return true if the list is empty. else false.
	 * 
	 */
	public boolean isEmptyList() {
		if (null == documents || documents.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDocumentSignedByCurrentUser() {
		return documentFacade.isSignedDocumentByCurrentUser(user, document);
	}

	public boolean isDocumentSigned() {
		return documentFacade.isSignedDocument(document);
	}

	/**
	 * Format the creation date for good displaying using DateFormatUtils of
	 * apache commons lib.
	 * 
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(document.getCreationDate().getTime());
	}

	/**
	 * 
	 * @return false (the document is never filesSelected by default)
	 */
	public boolean isFilesSelected() {
		return false;
	}

	public String getFriendlySize() {
		return FileUtils.getFriendlySize(document.getSize(), messages);
	}
	
	/**
	 * remove all carriage retrun for chenille kit tool tip
	 * @return
	 */
	public String getFormatedComment() {
		String result = document.getFileComment().replaceAll("\r","");
		result = result.replaceAll("\n", " ");
		return result;
	}

	/**
	 * This method is called when the form is submitted.
	 * 
	 * @param filesSelected
	 *            filesSelected or not in the form.
	 */
	public void setFilesSelected(boolean selected) {
		if (selected) {
			listSelected.add(document);
		}
	}

	/**
	 * Help method for use in this component. It retrieves a documentVo by it's
	 * id.
	 * 
	 * @param documents
	 *            list of documents.
	 * @param uuid
	 *            the uuid of the document to retrieve.
	 * @return DocumentVo concerned by the search.
	 */
	private DocumentVo searchDocumentVoByUUid(List<DocumentVo> documents,
			String uuid) {
		for (DocumentVo documentVo : documents) {
			if (uuid.equals(documentVo.getIdentifier())) {
				return documentVo;
			}
		}
		return null;
	}

	/**
	 * model for the datagrid we need it to switch off the signature and the
	 * encrypted column dynamically administration can desactivate the signature
	 * and encryption function
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public BeanModel initModel() throws BusinessException {

		//Initialize the sorter model for sorter component.
		if(refreshFlag==true){
			documents=docs;
			refreshFlag=false;
		}
		sorterModel=new FileSorterModel(documents);

		model = beanModelSource.createDisplayModel(DocumentVo.class,
				componentResources.getMessages());

		// native tml in html was:
		// exclude="fileName,ownerLogin,encrypted,identifier, size, type,
		// shareActive, downloaded"
		// add="fileProperties,actions,selectedValue,signed"
		// reorder=fileProperties,shared,signed,actions

		
		model.add("fileProperties", null);
		model.add("actions", null);
		model.add("selectedValue", null);
		model.add("updateDoc", null);
        model.add("fileEdit", null);
		

		List<String> reorderlist = new ArrayList<String>();
		reorderlist.add("fileProperties");
		reorderlist.add("updateDoc");
        reorderlist.add("fileEdit");
		reorderlist.add("shared");

		if (activeSignature) {
			model.add("signed", null);
			reorderlist.add("signed");
		}

		if (activeEncipherment) {
			model.add("encryptedAdd", null);
			reorderlist.add("encryptedAdd");
		}

		reorderlist.add("actions");
		
		model.reorder(reorderlist.toArray(new String[reorderlist.size()]));

		return model;
	}

	/**
	 * this method is called when PasswordPopup for encipherment is called one
	 * only one item
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public Zone onValidateFormFromPasswordPopup() throws BusinessException {

		// if password is not good reject in popup else continue
		if (userFacade.checkEnciphermentKey(user, passwordPopup.getPassword())) {

			String pass = passwordPopup.getPassword();
			if (pass == null)
				return null;
			cachePin.setPassword(pass);

			DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents,
					currentUuid);

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(pass);
			parameters.add(currentDocumentVo);
			passwordPopup.getFormPassword().clearErrors();
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventEncyphermentUniqueFromListDocument",
							parameters.toArray(), null);

			return passwordPopup.formSuccess();
		} else {
			passwordPopup
					.getFormPassword()
					.recordError(
							messages
									.get("components.listDocument.passwordPopup.error.message"));
			return passwordPopup.formFail();
		}
	}

	/**
	 * this method is called when PasswordPopupSubmit for encipherment is called
	 * (list of files)
	 * 
	 * @return
	 */
	public Zone onValidateFormFromPasswordPopupSubmit() {
		if (userFacade.checkEnciphermentKey(user, passwordPopupSubmit
				.getPassword())) {
			this.pass = passwordPopupSubmit.getPassword();
			cachePin.setPassword(pass);
			return passwordPopupSubmit.formSuccess(); // submit form
		} else {
			passwordPopupSubmit
					.getFormPassword()
					.recordError(
							messages
									.get("components.listDocument.passwordPopup.error.message"));
			return passwordPopupSubmit.formFail();
		}
	}

}
