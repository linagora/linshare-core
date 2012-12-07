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
package org.linagora.linshare.view.tapestry.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.utils.ArchiveZipStream;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;


/**
 * display signature info in window popup
 *
 */
public class SignatureDetailsDisplayer {
	
	
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=900", "height=400"})
    private WindowWithEffects signatureDetailsWindow;
    	
    @InjectComponent
    private Zone signatureDetailsTemplateZone;
    
    @Inject
    private DocumentFacade documentFacade;
    
    @SessionState
    @Property
    private UserVo userVo;
    
	@SuppressWarnings("unused")
	@Property
	@Persist
    private List<SignatureVo> signatures;
	
	@SuppressWarnings("unused")
	@Property
	@Persist
	private SignatureVo signature;
	
	@SuppressWarnings("unused")
	@Property
	private boolean isSignedByCurrentUser;
	
	@SuppressWarnings("unused")
	@Property
	private SignatureVo userOwnsignature;
	
	@SuppressWarnings("unused")
	@Property
	private String currentfileName;
	
	@Persist
	private DocumentVo currentdoc; //keep the doc associated to all signatures we are going to display
	
    @Inject
    private Messages messages;
	
	@Inject
	private PersistentLocale persistentLocale;
	
    
	public Zone getShowSignature(String docidentifier) {
		
		currentdoc = documentFacade.getDocument(userVo.getLogin(), docidentifier);
		
		currentfileName = currentdoc.getFileName();
		
		isSignedByCurrentUser = documentFacade.isSignedDocumentByCurrentUser(userVo, currentdoc);
		userOwnsignature = documentFacade.getSignature(userVo, currentdoc);
		
		signatures = documentFacade.getAllSignatures(userVo, currentdoc);
		
	    return signatureDetailsTemplateZone;
	}
	
	public String getUserOwnsignatureDate(){
		if (userOwnsignature!=null) {
			String pattern = messages.get("global.pattern.timestamp");
			return DateFormatUtils.format(userOwnsignature.getCreationDate().getTime(), pattern, persistentLocale.get());
		}
		else return null;
	}
	
	
	/**
	 * Format the creation date for good displaying using DateFormatUtils of apache commons lib.
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate(){
		if (signature!=null)
			{
				String pattern = messages.get("global.pattern.timestamp");
				return DateFormatUtils.format(signature.getCreationDate().getTime(), pattern, persistentLocale.get());
			}
		else return null;
	}
	
	public String getCertValidity(){
		if (signature!=null) 
		{
			String pattern = messages.get("global.pattern.timestamp");
			return DateFormatUtils.format(signature.getCertNotAfter().getTime(), pattern, persistentLocale.get());
		}
		else return null;
	}
	
	public boolean isSignedByCurrentUser(){
		return isSignedByCurrentUser;
	}
	
	public String getJSONId(){
		return signatureDetailsWindow.getJSONId();
	}
	
	
	/**
	 * The action declenched when the user click on the download for the signature
	 */
	public StreamResponse onActionFromDownload(String uuid) throws BusinessException{

		SignatureVo currentSignature= searchDocumentVoByUUid(signatures,uuid);
		
		if(currentSignature==null){
			throw new BusinessException(BusinessErrorCode.INVALID_UUID,"invalid signature uuid");
		}else{
			InputStream stream=documentFacade.retrieveSignatureFileStream(currentSignature);
			return new FileStreamResponse(currentSignature,stream);
		}

	}
	
	
	public StreamResponse onActionFromDownloadSignedArchive() throws IOException, BusinessException{
			
			Map<String,InputStream> map = new HashMap<String, InputStream>();

			map.put(currentdoc.getFileName(), documentFacade.retrieveFileStream(currentdoc, userVo.getLogin()));
			
			for (SignatureVo oneSignature : signatures) {
				String fileName = oneSignature.getName()+"_"+oneSignature.getPersistenceId()+".xml";
				map.put(fileName, documentFacade.retrieveSignatureFileStream(oneSignature));
			}	
			
			//prepare an archive zip
			ArchiveZipStream ai = new ArchiveZipStream(map);
			
			String archiveName = "signed_" + currentdoc.getFileName() + ".zip";
			
			return (new FileStreamResponse(ai,archiveName));
	}
	
	
	
	private SignatureVo searchDocumentVoByUUid(List<SignatureVo> allSignatures,String uuid){
		for(SignatureVo onesignature:allSignatures){
			if(uuid.equals(onesignature.getIdentifier())){
				return onesignature;
			}
		}
		return null;
	}
	
	
}
