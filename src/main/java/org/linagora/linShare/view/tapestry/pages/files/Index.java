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
package org.linagora.linShare.view.tapestry.pages.files;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.utils.FileUtils;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.components.FileUploader;
import org.linagora.linShare.view.tapestry.components.WindowWithEffects;
import org.linagora.linShare.view.tapestry.services.MyMultipartDecoder;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;



/**
 * Page to upload files, search for documents, and handle the sharing
 * Rely heavily on uploader, sharePanelComponent and searchComponent
 * This page handles all the mechanics behind these component, all the communication
 * is done through event :
 * - clearListObject : clear the current document share list
 * - eventDocument : launched by search component, contains in object[0] the resulting List<DocumentVo>
 * - eventDeleteFromListDocument : delete all the documents contained DocumentVo[] from the repository
 * - eventShare : add all the the documents contained DocumentVo[] in the sharing
 * - eventDeleteUniqueFromListDocument : delete the unique document whom UUID is object[0] from the repository
 * - eventShareUniqueFromListDocument : add the unique document whom UUID is object[0] in the current sharing
 * - eventReorderList : reorder the list of Document.
 * - deleteFromSharePanel : delete from the sharing the DocumentVo contained in object[0]
 * 
 * - sharePopupEvent : create the sharing, using the session objects
 * 
 * This page handles as well the Upload exception for the multipart decoder
 * 
 * @author ncharles
 *
 */
public class Index {

	public final static Logger Logger=LoggerFactory.getLogger(Index.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    private boolean shareSessionObjectsExists;

	@SessionState
	@Property
	private UserVo userVo;

	
    @InjectComponent
    private FileUploader fileUploader;
    
    @Inject
    private ComponentResources resources;
    
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	
	@SuppressWarnings("unused")
	@Component(parameters = {"style=bluelighting", "show=false","width=600", "height=250"})
	private WindowWithEffects windowUpload;
    
	@Inject
	private SearchDocumentFacade searchDocumentFacade;

	@Inject
	private DocumentFacade documentFacade; 
	
	@Inject
	private Templating templating;
	
	@SuppressWarnings("unused")
	@Inject
	private Messages messages;

    @Inject
    private Response response;

    @Inject
    @Path("context:templates/shared-message.html")
    private Asset guestSharedTemplate;
    
    @Inject
    private PropertiesSymbolProvider propertiesSymbolProvider;
    
    @Environmental
    private RenderSupport renderSupport;
    
	@Inject
    private ParameterRepository parameterRepository;
	
	@Inject
	private MyMultipartDecoder myMultipartDecoder;
    
	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	
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
	 *                       Phase processing
	 ************************************************************ */


	@SuppressWarnings("unused")
	@SetupRender
	private void initList(){
		if (!shareSessionObjectsExists) {
            shareSessionObjects = new ShareSessionObjects();
        }
    
		if(!flag){
			listDocumentsVo=searchDocumentFacade.retrieveDocument(userVo);
			
		}
		
		if (fileMessage==null) {
			fileMessage = "";
		}
		
	}
	
	@SuppressWarnings("unused")
	@CleanupRender
	private void initFlag(){
		shareSessionObjects.setMessages(new ArrayList<String>());
		//flag=false;
	}


    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	/**
	 * This is when the upload fails.
	 * It must be in the page, and not in the component
	 */
	public Object onUploadException(Throwable cause) {
		if (cause instanceof
				FileUploadBase.FileSizeLimitExceededException) {
			shareSessionObjects.addError(String.format(messages.get("pages.upload.FileSizeLimitExceededException"),
					FileUtils.getFriendlySize(parameterRepository.loadConfig().getFileSizeMax(), messages)));
		}
		myMultipartDecoder.cleanException();
		return this;
	}
	
	
	/**
	 * Sharing process between this page and user page.
	 */
    @OnEvent(value="sharePanel")
    public void onShare(Object[] elements) {
    	
    	
    	//redirect on user tab (this function is not here any more ...)
//    	if (shareSessionObjects.getUsers() == null || shareSessionObjects.getUsers().size() == 0) {
//        	 
//        	 Link linkUser = linkFactory.createPageRenderLink("user/index", true);
//            try {
//            	shareSessionObjects.addMessage(messages.get("pages.index.message.toUser"));
//                response.sendRedirect(linkUser);
//            } catch (IOException ex) {
//                throw new TechnicalException("Bad URL" + ex);
//            }
//        } else {
//        	flagFinishShare=true;
//        }
    	flagFinishShare=true;
    	
    }
	
	
	/**
	 * Clear the shared document list
	 */
	@OnEvent(value="clearListObject")
	public void clearList(){
		reinitASO();
	}
    
    @OnEvent(value="resetListFiles")
    public void resetListFiles(Object[] o1) {
		inSearch=false;
		listDocumentsVo=searchDocumentFacade.retrieveDocument(userVo);
    }
    
    @OnEvent(value="inFileSearch")
    public void inSearch(Object[] o1) {
    	inSearch = true;
    }
	

	
	/**
	 * The search component returns a document list, and we store it
	 * @param object : object[0] contains a List<DocumentVo>
	 */
	@SuppressWarnings("unchecked")
	@OnEvent(value="eventDocument")
	public void initListDoc(Object[] object){
		flag=true;
		this.listDocumentsVo = (List<DocumentVo>)Arrays.copyOf(object,1)[0];
	}


	/**
	 * Delete the document from the repository/facade 
	 * Invoked when a user clicks on "delete" button in the searched document list
	 * It also removes the documents from the shared list 
	 * @param object a DocumentVo[]
	 */
	@OnEvent(value="eventDeleteFromListDocument")
	public void deleteFromListDocument(Object[] object){
		 
		boolean flagError=false;
		for(Object currentObject:object){
			try {
				documentFacade.removeDocument(userVo,((DocumentVo)currentObject));
				
			} catch (BusinessException e) {
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failRemovingFile"),
						((DocumentVo)currentObject).getFileName()) );
			}
			shareSessionObjects.removeDocument((DocumentVo)currentObject);
		}

		if(null!=object && object.length>0 && !flagError){
			shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"),object.length));
					
			resetListFiles(null);
		}
	}
	
	
	/**
	 * encrypt/decrypt the document from the repository/facade 
	 * Invoked when a user clicks on "encrypt/decrypt" button in the searched document list
	 * @param object a DocumentVo[]
	 */
	@SuppressWarnings("unchecked")
	@OnEvent(value="eventEncyphermentFromListDocument")
	public void encyphermentFromListDocument(Object[] object){
		 
		String pass = (String) object[0];
		List<DocumentVo> docObject = (List<DocumentVo>) object[1];
		
		boolean ko = false; //if one problem exist in encrypt/decrypt many files
		int numberIgnore = 0;  //if it exists an entry which is already shared ignore it
		
		for(DocumentVo currentObject:docObject){
			try {
				if(!currentObject.getEncrypted()){
					
					if(!currentObject.getShared()){
						documentFacade.encryptDocument(currentObject, userVo,pass);
					} else {
						numberIgnore++;
					}
					
					
				} else {
					documentFacade.decryptDocument(currentObject, userVo,pass);
				}
				
			} catch (BusinessException e) {
				
				ko = true;
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failfileEncipherment"),
						(currentObject).getFileName()) );
			}
			
		}
		
		if(!ko && numberIgnore<docObject.size()) shareSessionObjects.addMessage(messages.get("pages.index.message.fileEncipherment"));
		if(numberIgnore>0) shareSessionObjects.addWarning(messages.get("pages.index.message.fileEncipherment.ignoreSharedFile"));
		
	}
	
	
	
	
	@OnEvent(value="eventEncyphermentUniqueFromListDocument")
	public void refreshFromListDocument(Object[] object){
			
		String pass = (String) object[0];
		DocumentVo currentDocumentVo = (DocumentVo) object[1];


		if(currentDocumentVo.getShared()){ //ignore shared file in encrypt/decrypt process
			shareSessionObjects.addWarning(messages.get("pages.index.message.fileEncipherment.ignoreSharedFile"));
		}
		else {

			try {
				if(!currentDocumentVo.getEncrypted()&&!currentDocumentVo.getShared()){
					documentFacade.encryptDocument(currentDocumentVo, userVo,pass);
				} else {
					documentFacade.decryptDocument(currentDocumentVo, userVo,pass);
				}
				shareSessionObjects.addMessage(messages.get("pages.index.message.fileEncipherment"));

			} catch (BusinessException e) {
				shareSessionObjects.addError(String.format(messages.get("pages.index.message.failfileEncipherment"),
						(currentDocumentVo).getFileName()) );
			}

		}	
	}
	
	
//	/**
//	 * sign the document 
//	 * Invoked when a user clicks on "sign" button in the searched document list
//	 * @param object a DocumentVo[]
//	 */
//	@SuppressWarnings("unchecked")
//	@OnEvent(value="eventSignatureFromListDocument")
//	public void signatureFromListDocument(Object[] object){
//
//		List<String> identifiers = new ArrayList<String>();
//		
//		//context is a list of document (tab files)
//		identifiers.add(DocToSignContext.DOCUMENT.toString());
//		
//		for(Object currentObject:object){
//			DocumentVo doc =  (DocumentVo) currentObject;
//			identifiers.add(doc.getIdentifier());
//		}
//		
//        Link mylink = linkFactory.createPageRenderLink("signature/SelectPolicy", true,identifiers.toArray());
//		
//        try {
//            response.sendRedirect(mylink);
//        } catch (IOException ex) {
//            throw new TechnicalException("Bad URL" + ex);
//        }
//		
//	}


	/**
	 * Add the document list in the shared list
	 * Invoked when the user click on the multi share button 
	 * @param object : a DocumentVo[]
	 */
	@OnEvent(value="eventShare")
	public void initShareList(Object[] object){
		
		DocumentVo doc;
		boolean giveWarning = false;
		
		for(Object currentObject:object){
			doc = (DocumentVo)currentObject;
			
			if(!doc.getEncrypted())	{ //do not put encrypted document in sharing mode !!!
				shareSessionObjects.addDocument(doc);
			}	else {
				giveWarning = true; //we want to share an encrypted document in the list
			}
		}

		shareSessionObjects.setMultipleSharing(true); //enable to multiple file sharing
		
		if(giveWarning){
     		shareSessionObjects.addWarning(messages.get("pages.index.message.shareWithExclusionEncryptedFiles"));
		}
	}

	/**
	 * Delete a unique document from the repository
	 * It also removes the document from the shared list
	 * Invoked when a user click on the action delete button 
	 * @param object : object[0] is the UUID of the document to be deleted
	 * @throws BusinessException
	 */	
	@OnEvent(value="eventDeleteUniqueFromListDocument")
	public void deleteUniqueFromListDocument(Object[] object) throws BusinessException {
		try {
	
			DocumentVo documentVo=getDocumentByUUIDInList((String)object[0]);
			if(null!=documentVo){
				documentFacade.removeDocument(userVo, documentVo);
				shareSessionObjects.removeDocument(documentVo);
				shareSessionObjects.addMessage(String.format(messages.get("pages.index.message.fileRemoved"),
						documentVo.getFileName()) );
			} else {
				throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid");
			}
		} catch (BusinessException e) {
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid uuid",e);
		}
	}


	/**
	 * Add a unique document in the share list
	 * Invoked when the user click on the unique share button
	 * @param object : object[0] contains the document UUID
	 * @throws BusinessException
	 */
	@OnEvent(value="eventShareUniqueFromListDocument")
	public void shareUniqueFromListDocument(Object[] object) throws BusinessException {
		DocumentVo documentVoTemp=null;

		for(DocumentVo currentDocumentVo:this.listDocumentsVo){
			if(currentDocumentVo.getIdentifier().equals((String)object[0])){
				documentVoTemp=currentDocumentVo;
				break;
			}
		}
		if(null!=documentVoTemp){
			
			//check is the document is encrypted and give a warning
			if(documentVoTemp.getEncrypted()){
			shareSessionObjects.addWarning(String.format(messages.get("pages.index.message.shareOneEncryptedFile"),
					documentVoTemp.getFileName()) );
			} else {
				
				//enable direct sharing on this document
				flagFinishShare=true;
				shareSessionObjects.getDocuments().clear(); //delete all other doc
				shareSessionObjects.addDocument(documentVoTemp);
				shareSessionObjects.setMultipleSharing(false);
			}
		}

	}
	
	@OnEvent(value="eventShareWithGroupUniqueFromListDocument")
	public void shareUniqueWithGroupFromListDocument(Object[] object) throws BusinessException {
		DocumentVo documentVoTemp=null;

		for(DocumentVo currentDocumentVo:this.listDocumentsVo){
			if(currentDocumentVo.getIdentifier().equals((String)object[0])){
				documentVoTemp=currentDocumentVo;
				break;
			}
		}
		if(null!=documentVoTemp){
			
			//check is the document is encrypted and give a warning
			if(documentVoTemp.getEncrypted()){
			shareSessionObjects.addWarning(String.format(messages.get("pages.index.message.shareOneEncryptedFile"),
					documentVoTemp.getFileName()) );
			} else {
				
				//enable direct sharing on this document
				flagGroupShare=true;
				shareSessionObjects.getDocuments().clear(); //delete all other doc
				shareSessionObjects.addDocument(documentVoTemp);
				shareSessionObjects.setMultipleSharing(false);
			}
		}

	}
	
	/**
	 * Remove the document from the share list
	 * @param object : object[0] contains a the DocumentVo
	 */
	@OnEvent(value="deleteFromSharePanel")
	public void deleteSharePanel(Object[] object){
		shareSessionObjects.removeDocument((DocumentVo)object[0]);
		
	}
	
	
	@OnEvent(value="eventToggleAdvancedSearchSorterComponent")
	public void toggleAdvancedSearch(Object[] object){
		advanced = (Boolean) object[0];
		flag=!flag;
	}
	
	
	private void reinitASO(){
        shareSessionObjects = new ShareSessionObjects();      
	}
	
	/**
	 * show the popup if it is to be shown
	 */
    @AfterRender
    public void afterRender() {
    	
    	if (flagFinishShare) {
    		
    		//show the share window popup
             renderSupport.addScript(String.format("confirmWindow.showCenter(true)"));
            flagFinishShare=false;
    	}
    	
    	if (flagGroupShare) {
            renderSupport.addScript(String.format("groupShareWindow.showCenter(true)"));
            flagGroupShare=false;
    	}

    }
    
    
    private DocumentVo getDocumentByUUIDInList(String UUId) {
    	for (DocumentVo doc : listDocumentsVo) {
			if ((doc.getIdentifier()).equals(UUId)) {
				return doc;
			}
		}
    	throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not find the document" );
    }
    
    public String getJSonId() {
        return windowUpload.getJSONId();
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	Logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

}
