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
package org.linagora.linshare.view.tapestry.pages.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserSignature;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linsign.client.applet.ParameterConverterJavaJavascript;
import org.linagora.linsign.exceptions.CheckSignerKeyException;
import org.linagora.linsign.exceptions.ComputeSignatureException;
import org.linagora.linsign.exceptions.CorruptedFileException;
import org.linagora.linsign.exceptions.FinalizeDocumentException;
import org.linagora.linsign.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SignDocument {
	
	
	@SessionState
	@Property
	private UserSignature userSignature;
	
	@Property
	private List<DocumentVo> documents;
	@Property
	private DocumentVo document;
	
	@Persist
	private Map<String,String> hashes;
	
	
	@Property
	private String signContent;
	
	
	@Inject
	private PageRenderLinkSource linkFactory;
	
	@Inject
	private DocumentFacade documentFacade;
	
	@Property
	@SessionState
	private UserVo userVo;
	
	@Inject
	private PersistentLocale persistentLocale;

	@Inject
	private Messages messages;
	
	@Inject
	private Response response;
	
	@Property
	private Locale locale;
	
	private Logger log = LoggerFactory.getLogger(SignDocument.class);
	
	private boolean buttonCancel;
	
	
    @SetupRender
	public void startSignContent()
	{
    	boolean stopWizard = false;
    	
    	try {
			hashes = userSignature.getAllBase64HashTBS();
			signContent= ParameterConverterJavaJavascript.convertMapToString(hashes);
			locale = persistentLocale.get();
			buttonCancel = false;
			documents = userSignature.getDocumentVos();
			
		} catch (ObjectNotFoundException e) {
			//instance de signature introuvable: quitter l'application...
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.objectNotFoundException"));
		} catch (ComputeSignatureException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.computeSignatureException"));
		} catch (CorruptedFileException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.corruptedFileException"));
		}
		
		
		if(stopWizard){
			try {
				response.sendRedirect(linkFactory.createPageRenderLink("signature/ExitSignatureWithError"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
    
    
	void onSelectedFromCancel() { buttonCancel = true; }
    
    public Object onSuccess() {
    	
    	boolean stopWizard = false;
		
    	//finalize documents
    	
		//user cancel signature ?
		if (buttonCancel||signContent==null||signContent.equals("")){
			userSignature.close();
			return linkFactory.createPageRenderLink("files/Index");
		}
		
		Map<String,String> signatures = ParameterConverterJavaJavascript.convertStringToMap(signContent);
		
		
		FileInputStream fi = null;
		
		try {
			userSignature.finalizeDocument(signatures);
			
			List<File> outSignedFiles = userSignature.getFinalizedDocument();
			List<DocumentVo> docVos = userSignature.getDocumentVos();
			
			
			for (int i=0; i<outSignedFiles.size(); i++) {
				
				File oneFile = outSignedFiles.get(i);
				DocumentVo currentDoc = docVos.get(i);
				
				fi = new FileInputStream(oneFile);
				documentFacade.insertSignatureFile(fi, oneFile.length(), oneFile.getName(), userVo, currentDoc,userSignature.getSignercert());
				
				fi.close();
				
			}
			
		} catch (ObjectNotFoundException e) {
			//instance de signature introuvable: quitter l'application...
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.objectNotFoundException"));
		} catch (FinalizeDocumentException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.finalizeDocumentException"));
		} catch (CheckSignerKeyException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.checkSignerKeyException"));
		} catch (CorruptedFileException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.corruptedFileException"));
		} catch (IOException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.iOException"));
		} catch (BusinessException e) {
			// insertSignatureFile
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.database"));
		} finally {
			if(fi!=null) try {fi.close();} catch (IOException e) {}
	    	
			hashes=null;
	    	userSignature.close(); //delete working directory and resources
		}
		
		if(stopWizard) return ExitSignatureWithError.class;
		else{ //ok
			if(userSignature.getDocContext().equals(DocToSignContext.DOCUMENT)){
				return  linkFactory.createPageRenderLink("files/Index");
			} else if (userSignature.getDocContext().equals(DocToSignContext.SHARED)) {
				return  linkFactory.createPageRenderLink("Index");
			}  else return null;
		}
    }
    
    
    /**
     * format file size in the view for the document
     * @return
     */
	public String getFriendlySize(){
		return FileUtils.getFriendlySize(document.getSize(), messages);
	}
	
	/**
	 * Format the creation date in the view for good displaying using DateFormatUtils of apache commons lib.
	 * @return creation date the date in localized format.
	 */
	public String getCreateDate(){
		return DateFormatUtils.format(document.getCreationDate().getTime(), "dd/MM/yyyy", persistentLocale.get());
	}
	
	/**
	 * Format the signer cert date (not after)in the view for good displaying using DateFormatUtils of apache commons lib.
	 * @return creation date the date in localized format.
	 */
	public String getSignerCertNotAfter(){
		return DateFormatUtils.format(userSignature.getSignercert().getNotAfter().getTime(), "dd/MM/yyyy HH:mm", persistentLocale.get());
	}
	
	
    
}
