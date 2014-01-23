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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.collections.map.HashedMap;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.SecuredUrlFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.enums.ActionFromBarDocument;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.models.SorterModel;
import org.linagora.linshare.view.tapestry.models.impl.FileSorterModel;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SupportsInformalParameters
@Import(library= { "ListDocument.js"})
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

	
	private Map<String, String> tooltipValues;
	private Map<String, String> tooltipGroupValues;
	
	private String tooltipValue;
	@Property
	private String tooltipTitle;
	
	private String tooltipGroupValue;
	@Property
	private String tooltipGroupTitle;
	
	/***************************************************************************
	 * Service injection
	 **************************************************************************/

	@Environmental
	private JavaScriptSupport renderSupport;

	@Inject
	private DocumentFacade documentFacade;
	@Inject
	private AbstractDomainFacade domainFacade;
	
	@Inject 
	private ShareFacade shareFacade;
	
	@Inject
	private SecuredUrlFacade securedUrlFacade;

	@Inject
	private FunctionalityFacade functionalityFacade;
	
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
	
	@InjectComponent
    private Grid documentGrid;

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

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

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
    
    @Property
    private boolean showUpd;

	@Inject
	@Symbol("linshare.tapestry.paging")
	@Property
	private int paging;

   
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
		
		Collections.sort(documents);

		listSelected = new ArrayList<DocumentVo>();
		userlogin = user.getLogin();
		actionbutton = ActionFromBarDocument.NO_ACTION;
		activeSignature = documentFacade.isSignatureActive(user);
		activeEncipherment = documentFacade.isEnciphermentActive(user);
		showUpd = functionalityFacade.isEnableUpdateFiles(user.getDomainIdentifier());
		// if(model==null)
		initModel();
		buildTooltipValues();
	}

	/**
	 * Build the contents (title and messages) of the tooltip which shows
	 * the active sharings of files
	 */
	private void buildTooltipValues() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.date"));

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
				
				if (docVo.getShared()) {
					StringBuffer tempBuf = new StringBuffer();
					StringBuffer tempBufGroup = new StringBuffer();
					Map<String,String> templateParams=new HashMap<String, String>();
					
					filleHeaderParams(templateParams);
					
					List<ShareDocumentVo> shares = shareFacade.getSharingsByUserAndFile(user, docVo);
					Map<String, Calendar> securedUrls = shareFacade.getAnonymousSharingsByUserAndFile(user, docVo);

					for (ShareDocumentVo share : shares) {
						UserVo receiver = share.getReceiver();
						fillRowParams(templateRowParams, receiver.getFirstName(), receiver.getLastName(), receiver.getMail(), formatter.format(share.getShareExpirationDate().getTime()));
						tempBuf.append(templating.getMessage(templateRow, templateRowParams));
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
				tooltipValues.put(docVo.getIdentifier(), value.replaceAll("[\r\n]+", ""));
				tooltipGroupValues.put(docVo.getIdentifier(), valueGroup.toString().replaceAll("[\r\n]+", ""));
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
		
		String tmp_mail = mail; 
		
		if(mail.length() > 30){
			tmp_mail = mail.substring(0,27);
			tmp_mail += "...";
		}
		templateRowParams.put("${mail}", tmp_mail);
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
		return tooltipValues.get(document.getIdentifier());
	}
	public String getTooltipGroupValue() {
		return tooltipGroupValues.get(document.getIdentifier());
	}
	
	public String getTypeCSSClass() {
		String ret = document.getType();
		ret = ret.replace("/", "_");
		ret = ret.replace("+", "__");
		ret = ret.replace(".", "_-_");
		return ret;
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
		// when user has been logged out
		if (documents == null) {
			return null;
		}

		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents, uuid);
		if (null == currentDocumentVo) {
			businessMessagesManagementService.notify(new BusinessException(
					BusinessErrorCode.INVALID_UUID,
					"invalid uuid for this user"));
			return null;
		} else {
				InputStream stream = documentFacade.retrieveFileStream(currentDocumentVo, user);
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

	public Zone onActionFromShowUser(String mail) throws BusinessException {
		return userDetailsDisplayer.getShowUser(mail);
	}


	public Zone onActionFromShowWarningSignature() {
		return warningSignature.getShowWarning();
	}

	public Zone onActionFromShowSignature(String docidentifier) throws BusinessException {
		return signatureDetailsDisplayer.getShowSignature(docidentifier);
	}
	

	public void onActionFromEncyphermentSubmit() {
		actionbutton = ActionFromBarDocument.CRYPT_ACTION;
	}


    public void onActionFromRenameFile(String newName) {
        documentFacade.renameFile(userlogin, currentUuid, newName);
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
		
		if (listSelected.size()<1) {
            businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.NOFILE_SELECTED,
                    MessageSeverity.WARNING));
    		return null;
		}
		
		actionbutton =  ActionFromBarDocument.fromString(action);
		
		switch (actionbutton) {
		case SHARED_ACTION:
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventShare", listSelected.toArray(), null);
			break;
		case GROUP_SHARE_ACTION:
			componentResources.getContainer().getComponentResources()
					.triggerEvent("eventGroupShare", listSelected.toArray(), null);
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
		return (null == documents || documents.isEmpty());
	}

	public boolean isDocumentSignedByCurrentUser() throws BusinessException {
		return documentFacade.isSignedDocumentByCurrentUser(user, document);
	}

	public boolean isDocumentSigned() {
		return documentFacade.isSignedDocument(userlogin, document);
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
	 * remove all carriage return for chenille kit tool tip
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
		if (refreshFlag) {
			documents = docs;
			refreshFlag = false;
		}
		sorterModel = new FileSorterModel(documents);

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
		return documentFacade.documentHasThumbnail(userlogin, document.getIdentifier());
	}
	
	public void onThumbnail(String docID) {
		InputStream stream = null;
		DocumentVo currentDocumentVo = searchDocumentVoByUUid(documents, docID);
        try {
			stream = documentFacade.getDocumentThumbnail(user.getLsUuid(), currentDocumentVo.getIdentifier());
	    } catch (Exception e) {
			logger.error("Trying to get a thumbnail linked to a document which doesn't exist anymore");
		}
		if (stream == null)
			return;
		OutputStream os = null;
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		try {
			os = response.getOutputStream("image/png");
			BufferedImage bufferedImage=ImageIO.read(stream);
			if (bufferedImage != null)
				ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
}
