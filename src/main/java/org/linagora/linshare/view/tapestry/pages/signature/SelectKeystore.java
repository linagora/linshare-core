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
package org.linagora.linshare.view.tapestry.pages.signature;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.vo.UserSignature;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.Base64Utils;
import org.linagora.linshare.view.tapestry.models.impl.SimpleSelectModel;
import org.linagora.linsign.client.keystore.KeystoreType;
import org.linagora.linsign.client.keystore.UserAgent;
import org.linagora.linsign.exceptions.ObjectNotFoundException;
import org.linagora.linsign.exceptions.PolicyNotFoundException;
import org.linagora.linsign.utils.sign.config.SignaturePolicies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectKeystore {
	
	@Inject
	private PageRenderLinkSource linkFactory;
	
    
	@Inject
	private RequestGlobals requestGlobals;
    
	@Property
	@Persist
	private KeystoreType selectedKeystore;
	
	@Property
	private List<KeystoreType> availableKeystore;
	
	
	@Persist
	@Property
	private SelectModel keystoreTypeModel;
	
	
	
	@SessionState
	@Property
	private UserSignature userSignature;
	
	@Environmental
    private JavaScriptSupport renderSupport;
	
	@Inject
	private PersistentLocale persistentLocale;
	
    @Inject
    private Messages messages;
	
	
	@Property
	private Locale locale;
	
	@Persist
	@Property
	private String cert;
	@Persist
	@Property
	private String alias;
	
	private Logger log = LoggerFactory.getLogger(SelectKeystore.class);
	
	private boolean buttonCancel;
	
	
	@Inject
	private Response response;
	
	
	@SetupRender
	public void initPage(){
		
		boolean stopWizard= false;
		
		try {
			availableKeystore = findAvailableKeystore();
			locale = persistentLocale.get();
			buttonCancel = false;
			
			//field select of tml file, KeystoreType key in message bundle
			keystoreTypeModel = new SimpleSelectModel<KeystoreType>(availableKeystore,messages,"KeystoreType");
			if(selectedKeystore==null && availableKeystore.size()>0){ //select browser by default if exists
				int index = availableKeystore.indexOf(KeystoreType.BROWSER);
				if(index!=-1) selectedKeystore = availableKeystore.get(index);
			}
		} catch (PolicyNotFoundException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.policyNotFoundException"));
		} catch (BusinessException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.noKeystore"));
		}
		
		
		if(stopWizard){
			try {
				response.sendRedirect(linkFactory.createPageRenderLink("signature/ExitSignatureWithError"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	void onSelectedFromCancel() {
		buttonCancel = true; }
	
	
	public Object onSuccess(){
		
		boolean stopWizard = false;
		
		
		if(buttonCancel) {
			userSignature.close();
			return  linkFactory.createPageRenderLink("files/Index");
		}
		
			
		if(selectedKeystore!=null)
		userSignature.setSignerKeystoreType(selectedKeystore);
			
			if(cert!=null&&!cert.equals("")){ // check if close applet without selection
				
				try {
					byte[] certBytes = Base64Utils.decode(cert);
					userSignature.sendCertificate(certBytes);
					userSignature.setAlias(alias);
					
				} catch (ObjectNotFoundException e) {
					stopWizard=true;
					log.error(e.toString(),e);
					userSignature.setErrorMessage(messages.get("pages.signature.error.objectNotFoundException"));
				} catch (CertificateException e) {
					//bad certificate
					stopWizard=true;
					log.error(e.toString(),e);
					userSignature.setErrorMessage(messages.get("pages.signature.error.certificateException"));
				}
				
				if(stopWizard) return ExitSignatureWithError.class;
				else return SignDocument.class;
				
			} else {
				return null; //reload page with applet
			}
			
	}
	
    
	private List<KeystoreType> findAvailableKeystore() throws PolicyNotFoundException, BusinessException
	{

		String oidpolicy = userSignature.getOidSignaturePolicy();
		
		HttpServletRequest req = requestGlobals.getHTTPServletRequest();
		
		String useragent = req.getHeader("user-agent");
		boolean supportBrowser = UserAgent.isBrowserSupported(useragent);
		
		List<KeystoreType> availableKeystore = new ArrayList<KeystoreType>();
		
		
		Set<KeystoreType> s;
		
		s = SignaturePolicies.getInstance().getSignaturePolicy(oidpolicy).getAvailableKeystore();
		
		for (KeystoreType keystoreType : s) {
			
			if(keystoreType != KeystoreType.BROWSER) {
				availableKeystore.add(keystoreType);
			} else {
				//we can access the keystore of the browser, so check the navigator!
				if(supportBrowser) availableKeystore.add(keystoreType);
			} 
		}
		
		if(availableKeystore.size()==0){
			throw new BusinessException(BusinessErrorCode.CANNOT_SIGN_DOCUMENT,"no available keystore to sign");
		}
		
		return availableKeystore;
	}
} 

