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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.collections.map.HashedMap;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Response;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.SecuredUrlFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.DocToSignContext;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.enums.ActionFromBarDocument;
import org.linagora.linShare.view.tapestry.models.SorterModel;
import org.linagora.linShare.view.tapestry.models.impl.FileSorterModel;
import org.linagora.linShare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SupportsInformalParameters
@IncludeJavaScriptLibrary(value = { "ListDocument.js"})
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
    
    @Parameter(required = false, defaultPrefix = BindingConstants.PROP)
    @Property
    private boolean inSearch;

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

	
	private Map<Integer, String> tooltipValues;
	private Map<Integer, String> tooltipGroupValues;
	
	private String tooltipValue;
	@Property
	private String tooltipTitle;
	
	private String tooltipGroupValue;
	@Property
	private String tooltipGroupTitle;
	
	@Property
	private int rowIndex;
	
	
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
	private ShareFacade shareFacade;
	
	@Inject
	private SecuredUrlFacade securedUrlFacade;

	@Inject
	private ComponentResources componentResources;  
	
	@Inject
	private Response response;


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
	private WarningDisplayer warningSignature;
	
	@Property(write = false)
	@InjectComponent
	private WarningDisplayer warningShare;
	
	

	@InjectComponent
	private SignatureDetailsDisplayer signatureDetailsDisplayer;

	@InjectComponent
	private PasswordCryptPopup passwordCryptPopup;
	@InjectComponent
	private PasswordDecryptPopup passwordDecryptPopup;

	@InjectComponent
	private PasswordCryptPopupSubmit passwordCryptPopupSubmit;
	@InjectComponent
	private PasswordDecryptPopupSubmit passwordDecryptPopupSubmit;
	
	

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Inject
	private BeanModelSource beanModelSource;
	
    @Inject
	private Templating templating;
	
	@Inject
	@Path("context:templates/tooltip.tml")
	private Asset tooltipTemplate;
	
	@Inject
	@Path("context:templates/tooltip_row.tml")
	private Asset tooltipTemplateRow;
	
	@Inject
	@Path("context:templates/tooltipGroup.tml")
	private Asset tooltipTemplateGroup;
	
	@Inject
	@Path("context:templates/tooltipGroup_row.tml")
	private Asset tooltipTemplateGroupRow;

	@SuppressWarnings("unchecked")
	@Property
	@Persist
	private BeanModel model;

	@Inject
	private Messages messages;

	@Persist
	private String pass;


	/***************************************************************************
	 * Flags
	 **************************************************************************/
	@Property
	private boolean activeSignature;

	@Property
	private boolean activeEncipherment;


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

    private Logger logger = LoggerFactory.getLogger(ListDocument.class);
	
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

		// if(model==null)
		initModel();
		buildTooltipValues();
	}

	/**
	 * Build the contents (title and messages) of the tooltip which shows
	 * the active sharings of files
	 */
	private void buildTooltipValues() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));

		tooltipTitle = messages.get("components.listDocument.tooltip.title");
		tooltipGroupTitle = messages.get("components.listDocument.tooltipGroup.title");
		tooltipValues = new HashedMap();
		tooltipGroupValues = new HashedMap();
		
		try {
			String templateContainer = templating.readFullyTemplateContent(tooltipTemplate.getResource().openStream());
			String templateRow = templating.readFullyTemplateContent(tooltipTemplateRow.getResource().openStream());
			String templateGroupContainer = templating.readFullyTemplateContent(tooltipTemplateGroup.getResource().openStream());
			String templateGroupRow = templating.readFullyTemplateContent(tooltipTemplateGroupRow.getResource().openStream());
			
			Map<String,String> templateRowParams=new HashMap<String, String>();
			String value = "";
			String valueGroup = "";
			
			int i=0;
			for (DocumentVo docVo : documents) {
				
				if (docVo.getShared()||docVo.getSharedWithGroup()) {
					StringBuffer tempBuf = new StringBuffer();
					StringBuffer tempBufGroup = new StringBuffer();
					Map<String,String> templateParams=new HashMap<String, String>();
					
					filleHeaderParams(templateParams);
					
					List<Share> shares = shareFacade.getSharingsByUserAndFile(user, docVo);
					Map<String, Calendar> securedUrls = securedUrlFacade.getSharingsByMailAndFile(user, docVo);

					for (Share share : shares) {
						User receiver = share.getReceiver();
						if (receiver.getUserType().equals(UserType.GROUP)) {
							filleGroupRowParams(templateRowParams, receiver.getLastName(), formatter.format(share.getExpirationDate().getTime()));
							tempBufGroup.append(templating.getMessage(templateGroupRow, templateRowParams));
							
						}
						else {
							fillRowParams(templateRowParams, receiver.getFirstName(), receiver.getLastName(), receiver.getMail(), formatter.format(share.getExpirationDate().getTime()));
							tempBuf.append(templating.getMessage(templateRow, templateRowParams));
						}
					}
					Set<Entry<String, Calendar>> set = securedUrls.entrySet();
					for (Entry<String, Calendar> entry : set) {
						fillRowParams(templateRowParams, " ", " ", entry.getKey(), formatter.format(entry.getValue().getTime()));
						tempBuf.append(templating.getMessage(templateRow, templateRowParams));
					}
					
					templateParams.put("${rows}", tempBuf.toString());
					templateParams.put("${group_rows}", tempBufGroup.toString());
					
					value = templating.getMessage(templateContainer, templateParams);
					valueGroup = templating.getMessage(templateGroupContainer, templateParams);
				}
				else {
					value = messages.get("components.listDocument.tooltip.noEntry");
					valueGroup = messages.get("components.listDocument.tooltip.noEntry");
				}
				tooltipValues.put(i, value.replaceAll("[\r\n]+", ""));
				tooltipGroupValues.put(i, valueGroup.toString().replaceAll("[\r\n]+", ""));
				i++;
			}
			
		} catch (IOException e) {
			logger.error("Bad mail template", e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad template",e);
		}
	}
	
	private void filleHeaderParams(Map<String,String> templateParams) {
		templateParams.put("${header_user}", messages.get("components.listDocument.tooltip.table.user"));
		templateParams.put("${header_group}", messages.get("components.listDocument.tooltip.table.group"));
		templateParams.put("${header_mail}", messages.get("components.listDocument.tooltip.table.mail"));
		templateParams.put("${header_expiration}", messages.get("components.listDocument.tooltip.table.dateExpiration"));
		templateParams.put("${description}", messages.get("components.listDocument.tooltip.description"));
	}
	
	private void fillRowParams(Map<String,String> templateRowParams, String firstName, 
			String lastName, String mail, String expiration) {
		templateRowParams.put("${firstname}", firstName);
		templateRowParams.put("${lastname}", lastName);
		templateRowParams.put("${mail}", mail);
		templateRowParams.put("${expiration}", expiration);
	}
	
	private void filleGroupRowParams(Map<String, String> templateRowParams, String name, String expiration) {
		templateRowParams.put("${name}", name);
		templateRowParams.put("${expiration}", expiration);
	}
	
	/**
	 * Returns the good tooltip content for the document pointed by
     * the user in the list of documents.
	 */
	public String getTooltipValue() {
		return tooltipValues.get(rowIndex);
	}
	public String getTooltipGroupValue() {
		return tooltipGroupValues.get(rowIndex);
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

//			if (!currentDocumentVo.getEncrypted()) {
//				InputStream stream = documentFacade.retrieveFileStream(
//						currentDocumentVo, user);
//				return new FileStreamResponse(currentDocumentVo, stream);
//			} else {
//				throw new BusinessException(
//						BusinessErrorCode.CANNOT_DECRYPT_DOCUMENT,
//						"invalid download for a protected document");
//			}
			
				InputStream stream = documentFacade.retrieveFileStream(
						currentDocumentVo, user);
				return new FileStreamResponse(currentDocumentVo, stream);
		}

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
			return pageRenderLinkSource.createPageRenderLinkWithContext("signature/SelectPolicy",
					new Object[] { DocToSignContext.DOCUMENT.toString(),
							currentDocumentVo.getIdentifier() });
		}
	}


	/**
	 * The action triggered when the user click on the icon include in the list for each document.
	 * @param uuid the uuid of the document.
	 */

	public void onActionFromEncryptIcon(String uuid)
			throws BusinessException {
		currentUuid = uuid;
		actionbutton = ActionFromBarDocument.CRYPT_ACTION;
		passwordCryptPopup.getFormPassword().clearErrors(); // delete popup message
	}
	
	/**
	 * The action triggered when the user click on the icon include in the list for each document.
	 * @param uuid the uuid of the document.
	 */

	public void onActionFromDecryptIcon(String uuid)
			throws BusinessException {
		currentUuid = uuid;
		actionbutton = ActionFromBarDocument.DECRYPT_ACTION;
		passwordCryptPopup.getFormPassword().clearErrors(); // delete popup message
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
	public void onActionFromUniqueShareWithGroupLinkBis(String uuid) {

		componentResources.getContainer().getComponentResources()
			.triggerEvent("eventShareWithGroupUniqueFromListDocument",
				new Object[] { uuid }, null);
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


	public Zone onActionFromShowWarningSignature() {
		return warningSignature.getShowWarning();
	}

	public Zone onActionFromShowSignature(String docidentifier) {
		return signatureDetailsDisplayer.getShowSignature(docidentifier);
	}
	
	public Zone onActionFromShowWarningShareTer() {
		return warningShare.getShowWarning();
	}
	

	public void onActionFromEncyphermentSubmit() {
		actionbutton = ActionFromBarDocument.CRYPT_ACTION;
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


	@SuppressWarnings("unchecked")
	@OnEvent(value="eventReorderList")
	public void reorderList(Object[] o1){
		if(o1!=null && o1.length>0){
			this.docs=(List<DocumentVo>)Arrays.copyOf(o1,1)[0];
			this.sorterModel=new FileSorterModel(this.docs);
			refreshFlag=true;
		}
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
		case CRYPT_ACTION:
			List<Object> cryptParameters = new ArrayList<Object>();
			cryptParameters.add(this.pass);
			cryptParameters.add(listSelected);
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventCryptListDocFromListDocument",
							cryptParameters.toArray(), null);
			break;
		case DECRYPT_ACTION:
			List<Object> decryptParameters = new ArrayList<Object>();
			decryptParameters.add(this.pass);
			decryptParameters.add(listSelected);
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventDecryptListDocFromListDocument",
							decryptParameters.toArray(), null);
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
		reorderlist.add("sharedWithGroup");

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
	public Zone onValidateFormFromPasswordCryptPopup() throws BusinessException {
		
		if (passwordCryptPopup.getPassword().equals(passwordCryptPopup.getConfirm())) {
			String pass = passwordCryptPopup.getPassword();

			DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents,currentUuid);

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(pass);
			parameters.add(currentDocumentVo);
			passwordCryptPopup.getFormPassword().clearErrors();
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventCryptOneDocFromListDocument",
							parameters.toArray(), null);

			return passwordCryptPopup.formSuccess();
		} else {
			// if password is not like confirm reject in popup
			return passwordCryptPopup.formFail();
		}
	}
	
	/**
	 * this method is called when PasswordPopup for encipherment is called one
	 * only one item
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public Zone onValidateFormFromPasswordDecryptPopup() throws BusinessException {
		
			String pass = passwordDecryptPopup.getPassword();

			DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents,currentUuid);

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(pass);
			parameters.add(currentDocumentVo);
			passwordCryptPopup.getFormPassword().clearErrors();
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventDecryptOneDocFromListDocument",
							parameters.toArray(), null);

			return passwordCryptPopup.formSuccess();
	}
	
	

	/**
	 * this method is called when PasswordPopupSubmit for encipherment is called
	 * (list of files)
	 * 
	 * @return
	 */
	public Zone onValidateFormFromPasswordCryptPopupSubmit() {
		if(passwordCryptPopupSubmit.getPassword().equals(passwordCryptPopupSubmit.getConfirm())){
			this.pass=passwordCryptPopupSubmit.getPassword();
			return passwordCryptPopupSubmit.formSuccess(); // submit form
		} else {
			return passwordCryptPopupSubmit.formFail();
		}
	}
	
	/**
	 * this method is called when PasswordPopupSubmit for encipherment is called
	 * (list of files)
	 * 
	 * @return
	 */
	public Zone onValidateFormFromPasswordDecryptPopupSubmit() {
		if(passwordDecryptPopupSubmit.getPassword()!=null){
			this.pass=passwordDecryptPopupSubmit.getPassword();
			return passwordDecryptPopupSubmit.formSuccess(); // submit form
		} else {
			return passwordDecryptPopupSubmit.formFail();
		}
	}

	public Link getThumbnailPath() {
        return componentResources.createEventLink("thumbnail", document.getIdentifier());
	}
	
	public boolean getThumbnailExists() {
		return documentFacade.documentHasThumbnail(document.getIdentifier());
	}
	
	public void onThumbnail(String docID) {
		InputStream stream=null;
		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents,
				docID);
			stream = documentFacade.getDocumentThumbnail(currentDocumentVo.getIdentifier());
			if (stream==null) return;
		OutputStream os = null;
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.setHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			try {
			os = response.getOutputStream("image/png");
				BufferedImage bufferedImage=ImageIO.read(stream);
				if (bufferedImage!=null)
					ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		 finally {

			try {
				if (os!=null) {
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
