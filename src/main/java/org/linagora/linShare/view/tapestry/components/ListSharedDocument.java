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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.services.LinkFactory;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.domain.vo.DocToSignContext;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linShare.view.tapestry.models.SorterModel;
import org.linagora.linShare.view.tapestry.models.impl.FileSorterModel;
import org.linagora.linShare.view.tapestry.models.impl.SharedFileSorterModel;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListSharedDocument {

	private static final Logger logger = LoggerFactory.getLogger(ListSharedDocument.class);
	/***********************************
	 * Parameters
	 ***********************************/
	/**r
	 * The user owner for the document list.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	private UserVo user;

	/**
	 * The list of documents.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<ShareDocumentVo> shareDocuments;
	
	
	/***********************************
	 * Properties
	 ***********************************/
	

	@SuppressWarnings("unused")
	@Property
	private ShareDocumentVo shareDocument;
	

	/***********************************
	 * Service injection
	 ***********************************/
	
	@Inject
	private PersistentLocale persistentLocale;
	
	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private ShareFacade shareFacade;
	
	@Inject
	private ParameterFacade parameterFacade;
	
	@Inject
	private ComponentResources componentResources;

	@Inject
	private BeanModelSource beanModelSource;
	
	@InjectComponent
	private UserDetailsDisplayer userDetailsDisplayer;
	
//	@InjectComponent
//	private SignatureDetailsDisplayer signatureDetailsDisplayer;
	
	@Inject
	private LinkFactory linkFactory;
	
	@SuppressWarnings("unchecked")
	@Property
	@Persist
	private BeanModel model;
	
	@Inject
	private Messages messages;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
    
    @Inject
	private Templating templating;
	
	@Inject
	@Path("context:templates/download-message.html")
	private Asset downloadTemplate;
	
	@Inject
	@Path("context:templates/download-message.txt")
	private Asset downloadTemplateTxt;
	
	/***********************************
	 * Flags
	 ***********************************/
	@Persist
	private String currentUuid;
	
	@Persist
	private List<ShareDocumentVo> componentdocuments;
	
	@Property
	private boolean activeSignature;
	
	@Property
	private boolean enabledToUpload;
	
	@Persist
	private boolean refreshFlag;
	
	@Persist
	private List<ShareDocumentVo> docs;
	
	
	/**
	 * Components Model.
	 */
	@SuppressWarnings("unused")
	@Property
	@Persist
	private SorterModel<ShareDocumentVo> sorterModel;
	
	
	/*********************************
	 * Phase render
	 *********************************/

	/**
	 * Initialization of the selected list and set the userLogin from the user ASO.
	 * @throws BusinessException 
	 */
	@SetupRender
	public void init() throws BusinessException {
		this.componentdocuments = shareDocuments;
		this.activeSignature = parameterFacade.loadConfig().getActiveSignature();
		//if(model==null) // need to redo the model each type, for the config may change 
		model=initModel();
		
	}

	/********************************
	 * ActionLink methods
	 *********************************/
	
	/**
	 * The action declenched when the user click on the download link on the name of the file. 
	 */
	public StreamResponse onActionFromDownload(String uuid) throws BusinessException{

		ShareDocumentVo currentSharedDocumentVo=searchDocumentVoByUUid(componentdocuments,uuid);
		
		if(null==currentSharedDocumentVo){
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid for this user");
		}else{
			boolean alreadyDownloaded = currentSharedDocumentVo.getDownloaded();
			
			InputStream stream=documentFacade.retrieveFileStream(currentSharedDocumentVo, user);
			
			//send an email to the owner if it is the first time the document is downloaded
			if (!alreadyDownloaded) {
				notifyOwnerByEmail(currentSharedDocumentVo);
				componentdocuments=shareFacade.getAllSharingReceivedByUser(user); //maj valeur downloaded dans le VO
			}
			
			return new FileStreamResponse(currentSharedDocumentVo,stream);
		}

	}
	
	private void notifyOwnerByEmail(ShareDocumentVo currentSharedDocumentVo) {
		try {
			
			String subject=messages.get("mail.user.all.download.subject");
			
			String downloadTemplateContent = templating.readFullyTemplateContent(downloadTemplate.getResource().openStream());
			
			String downloadTemplateContentTxt = templating.readFullyTemplateContent(downloadTemplateTxt.getResource().openStream());
			shareFacade.sendDownloadNotification(currentSharedDocumentVo, user, subject, downloadTemplateContent, downloadTemplateContentTxt);
		} catch (IOException e) {
			logger.error("Bad mail template", e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad template",e);
		}		
	}

	public void onActionFromDelete(String uuid){
		currentUuid = uuid;
	}
	
//	public Object onActionFromSignature(String uuid) throws BusinessException{
//		currentUuid = uuid;
//		ShareDocumentVo shareddoc = searchDocumentVoByUUid(componentdocuments,uuid);
//		
//		if(null==shareddoc){
//			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid for this user");
//		}else{
//			// context is shared document
//			return linkFactory.createPageRenderLink("signature/SelectPolicy", true, new Object[]{DocToSignContext.SHARED.toString(),shareddoc.getIdentifier()});
//		}
//	}
	
	public Zone onActionFromShowUser(String mail) {
		return userDetailsDisplayer.getShowUser(mail);	
	}
	
//	public Zone onActionFromShowSignature(String docidentifier) {
//		return signatureDetailsDisplayer.getShowSignature(docidentifier);
//	}
	
    public void onActionFromCopy(String docIdentifier) {
        ShareDocumentVo shareDocumentVo = searchDocumentVoByUUid(componentdocuments, docIdentifier);
        boolean copyDone = false;
        boolean alreadyDownloaded = shareDocumentVo.getDownloaded();
        
        //create the copy of the document and remove it from the received documents
        try {
            shareFacade.createLocalCopy(shareDocumentVo, user);
            copyDone = true;
        } catch (BusinessException e) {
            // process business exception. Can be thrown if no space left or wrong mime type.
            businessMessagesManagementService.notify(e);
        }
        
        //send an email to the owner if it is the first time the document is downloaded
		if (!alreadyDownloaded) 
			notifyOwnerByEmail(shareDocumentVo);
		
        if (copyDone) {
            businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.LOCAL_COPY_OK,
                MessageSeverity.INFO));
        }
    }
	
	/***************************
	 * Events 
	 ***************************/
	
	/**
	 * The event triggered by the confirm window when the user pushes on YES.
	 * @throws BusinessException exception throws when the uuid doesn't exist.
	 */
	@OnEvent(value="listDocumentEvent")
	public void removeDocument() throws BusinessException{
		if(null!=currentUuid){
			componentResources.getContainer().getComponentResources().triggerEvent("eventDeleteUniqueFromListDocument", new Object[]{currentUuid}, null);
		}else{
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid");
		}
	}

	
	@OnEvent(value="signatureDocumentEvent")
	public void signatureDocument() throws BusinessException{
		if(null!=currentUuid){
			componentResources.getContainer().getComponentResources().triggerEvent("eventSignatureUniqueFromListDocument", new Object[]{currentUuid}, null);
		}else{
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid");
		}
	}
	
	
	
	
	/***************************
	 * Other methods
	 ****************************/
	
	/**
	 * Property used for know if the list is empty.
	 * @return true if the list is empty. else false.
	 * 
	 */
	public boolean isEmptyList(){
		if(null==shareDocuments || shareDocuments.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Format the creation date for good displaying using DateFormatUtils of apache commons lib.
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate(){
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(shareDocument.getCreationDate().getTime());
	}

	/**
	 * Format the creation date for good displaying using DateFormatUtils of apache commons lib.
	 * @return creation date the date in localized format.
	 */
	public String getExpirationDate(){
	   SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
	   return formatter.format(shareDocument.getShareExpirationDate().getTime());
	}
	
	public String getFriendlySize(){
		return FileUtils.getFriendlySize(shareDocument.getSize(),messages);
	}
	public String getSharedBy(){
		return shareDocument.getSender().getFirstName()+" "+shareDocument.getSender().getLastName();
	}

	public boolean isDocumentSignedByCurrentUser(){
		return documentFacade.isSignedDocumentByCurrentUser(user, shareDocument);
	}
	
	public boolean isDocumentSigned(){
		return documentFacade.isSignedDocument(shareDocument);
	}
	
	/**
	 * Help method for use in this component. It retrieves a documentVo by it's id.
	 * @param documents list of documents.  
	 * @param uuid the uuid of the document to retrieve.
	 * @return DocumentVo concerned by the search.
	 */
	private ShareDocumentVo searchDocumentVoByUUid(List<ShareDocumentVo> documents,String uuid){
		for(ShareDocumentVo share:documents){
			if(uuid.equals(share.getIdentifier())){
				return share;
			}
		}
		return null;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@OnEvent(value="eventReorderList")
	public void reorderList(Object[] o1){
		
		this.docs=(List<ShareDocumentVo>)o1[0];
		this.sorterModel=new SharedFileSorterModel(this.docs);
		refreshFlag=true;
	}
	

	/**
	 * model for the datagrid
	 * we need it to switch off the signature column dynamically
	 * administration can deactivate the signature function
	 * @return
	 * @throws BusinessException
	 */
	public BeanModel initModel() throws BusinessException {
		
		
		//Initialize the sorter model for sorter component.
		if(refreshFlag==true){
			shareDocuments=docs;
			refreshFlag=false;
		}
        
		sorterModel=new SharedFileSorterModel(shareDocuments);
    	
    	model = beanModelSource.createDisplayModel(ShareDocumentVo.class, componentResources.getMessages());
        
		
    	
    	
    	// Native TML in HTML was:
		// exclude="fileName, identifier, size, encrypted, ownerLogin, shared, type, shareActive, downloaded, comment"
		// add="fileProperties,expirationDate,signed,actions"

    	// Another native TML in HTML was: 
		// exclude="identifier, size, encrypted, ownerLogin, shared, type, shareActive, downloaded, comment"
		// add="friendlySize,createDate,expirationDate,signed,sharedBy,actions"
    	
    	if(activeSignature){
        	model.add("fileProperties",null);
        	model.add("expirationDate",null);
        	model.add("signed",null);
        	model.add("actions",null);
        	model.reorder("fileProperties","expirationDate","signed","actions");
    	} else {
        	model.add("fileProperties",null);
        	model.add("expirationDate",null);
        	model.add("actions",null);
        	model.reorder("fileProperties","expirationDate","actions");
    	}
        return model;
    }

	public boolean isActiveSignature() {
		return activeSignature;
	}
	
	public boolean isEnabledToUpload() {
		return user.isUpload();
	}

}
