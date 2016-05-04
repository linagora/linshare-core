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
package org.linagora.linshare.view.tapestry.pages.signature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignaturePolicyVo;
import org.linagora.linshare.core.domain.vo.UserSignature;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.view.tapestry.encoders.SignaturePolicyEncoder;
import org.linagora.linsign.exceptions.CreateSignedDocumentContainerException;
import org.linagora.linsign.exceptions.ObjectNotFoundException;
import org.linagora.linsign.exceptions.PolicyNotFoundException;
import org.linagora.linsign.utils.sign.config.SignaturePolicies;
import org.linagora.linsign.utils.sign.config.SignaturePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SelectPolicy {

	@Inject
	private PageRenderLinkSource linkFactory;

	@Inject
	private Messages messages;

	@Inject
	private DocumentFacade documentFacade;
	@Inject
	private AbstractDomainFacade domainFacade;
	@Inject
	private ShareFacade shareFacade;


	@Persist
	@Property
	private SignaturePolicyEncoder signaturePoliciesEncoder;
	@Persist
	@Property
	private List<SignaturePolicyVo> availableSignaturePolicies;
	@Persist
	@Property
	private SignaturePolicyVo selectedPolicy;


	@SessionState
	@Property
	private UserSignature userSignature;

	@SessionState
	private UserVo userVo;

	private Logger log = LoggerFactory.getLogger(SelectPolicy.class);

	/**
	 * set to "/tmp/linSignDocuments";
	 */
	@Inject @Symbol("linshare.signature.tmp.dir")
    private String TEMP_SIGNATURE_DIR;

	private boolean stopWizard;


	public Object onActivate(Object[] docIdentifiers) {

		stopWizard = false;


		try {
			//get the context can be sharedlist or the list of document (position 0)
			//next get the files to sign from the list of document

			if (docIdentifiers.length>=2){
				//list of documents or list of shared documents ??
				DocToSignContext context = DocToSignContext.valueOf((String) docIdentifiers[0]);

				//signature activation is NOT activated so quit !
				if (!documentFacade.isSignatureActive(userVo)){
					if(context.equals(DocToSignContext.DOCUMENT)){
						return  linkFactory.createPageRenderLink("files/Index");
					} else if (context.equals(DocToSignContext.SHARED)) {
						return  linkFactory.createPageRenderLink("Index");
					} else return null;
				}

				List<DocumentVo> docTosign = new ArrayList<DocumentVo>(docIdentifiers.length-1);

				//retrieve info on doc to sign

					if(context.equals(DocToSignContext.DOCUMENT)){
						for (int i = 1; i < docIdentifiers.length; i++) {

							DocumentVo doc =documentFacade.getDocument(userVo, (String) docIdentifiers[i]);

							//in the this list of document it can be documentVO or shareddocumentVO
							if(!doc.getOwnerLogin().equalsIgnoreCase(userVo.getLogin())){
								//not the owner
								// TO be fix ? but how ?
//								doc = new ShareDocumentVo(doc,null,userVo,null,null,null,null,0); //sduprey: jamais accedé ?
							}

							//want to sign only one time (moreover we do not want to sign encrypted doc)
							if(!documentFacade.isSignedDocumentByCurrentUser(userVo, doc) && !doc.getEncrypted()){
								docTosign.add(doc);
							}
						}
					} else {
						for (int i = 1; i < docIdentifiers.length; i++) {
							DocumentVo doc =documentFacade.getDocument(userVo, (String) docIdentifiers[i]);
//							ShareDocumentVo shareddoc = new ShareDocumentVo(doc,userVo,userVo,null,null,null);
							ShareDocumentVo shareddoc = null;
							List<ShareDocumentVo> docVos = shareFacade.getAllSharingReceivedByUser(userVo);
							for (ShareDocumentVo shareDocumentVo : docVos) {
								if (shareDocumentVo.getIdentifier().equals(doc.getIdentifier())) {
									shareddoc = shareDocumentVo;
									break;
								}
							}
							if (shareddoc != null) {
								//want to sign only one time (moreover we do not want to sign encrypted doc)
								if(!documentFacade.isSignedDocumentByCurrentUser(userVo, doc) && !doc.getEncrypted()){
									docTosign.add(shareddoc);
								}
							}
						}
					}

					//if no document to sign return
					if(docTosign.size()==0){
						if(context.equals(DocToSignContext.DOCUMENT)){
							return  linkFactory.createPageRenderLink("files/Index");
						} else if (context.equals(DocToSignContext.SHARED)) {
							return  linkFactory.createPageRenderLink("Index");
						} else return null;
					}


				userSignature = new UserSignature();
				userSignature.setDocContext(context);
				userSignature.setDocumentVos(docTosign);
			}

			//prepare select box
			availableSignaturePolicies = findAvailableSignaturePolicies();
			signaturePoliciesEncoder = new SignaturePolicyEncoder(availableSignaturePolicies);

			//we go directly to select keystore if just one choice for policies (this is the case at this time)
			if(availableSignaturePolicies.size()==1){
				selectedPolicy = availableSignaturePolicies.get(0);
				userSignature.setOidSignaturePolicy(selectedPolicy.getOid());
				initSelectFiles(); // file to sign
				return SelectKeystore.class;
			}
		} catch (BusinessException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.loadParameter"));
		} catch (PolicyNotFoundException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.policyNotFoundException"));
		} catch (ObjectNotFoundException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.objectNotFoundException"));
		} catch (CreateSignedDocumentContainerException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.createSignedDocumentContainerException"));
		} catch (IOException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.iOException"));
		}

		if(stopWizard) return ExitSignatureWithError.class;

		return null;
	}

	public void onValidateForm(){
		userSignature.setOidSignaturePolicy(selectedPolicy.getOid());
	}

	public Object onSuccess() {

		stopWizard=false;

		try {
			initSelectFiles();
		} catch (BusinessException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.loadParameter"));
		} catch (PolicyNotFoundException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.policyNotFoundException"));
		} catch (ObjectNotFoundException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.objectNotFoundException"));
		} catch (CreateSignedDocumentContainerException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.createSignedDocumentContainerException"));
		} catch (IOException e) {
			stopWizard=true;
			log.error(e.toString(),e);
			userSignature.setErrorMessage(messages.get("pages.signature.error.iOException"));
		}

		if(stopWizard) return ExitSignatureWithError.class;
		else return SelectKeystore.class;
	}



	private List<SignaturePolicyVo> findAvailableSignaturePolicies() throws PolicyNotFoundException  {

		Set<String> oids = SignaturePolicies.getInstance().getAvailableSignaturePolicyOID();

		List<SignaturePolicyVo> policiesSelect = new ArrayList<SignaturePolicyVo>();

		for (String oneOid : oids) {
			try {
				SignaturePolicy sp = SignaturePolicies.getInstance().getSignaturePolicy(oneOid);
				policiesSelect.add(new SignaturePolicyVo(sp.getLabel(),sp.getOID()));
			} catch (PolicyNotFoundException e) {
				throw e;
			}
		}

		return policiesSelect;
	}


    private void initSelectFiles() throws PolicyNotFoundException, ObjectNotFoundException, CreateSignedDocumentContainerException, IOException, BusinessException {
    	//ckeck integrity on signed jar (linsign config and core)
    	SignaturePolicies.getInstance();
    	String oidPolicy = null;
    	InputStream is = null;

    	try {
			//get user policy selection
			oidPolicy = userSignature.getOidSignaturePolicy();
			String signatureUuid = userSignature.init(oidPolicy); //create signature instance to put file in it


			String tempUserSignatureDir = TEMP_SIGNATURE_DIR + "/" + signatureUuid + "/";
			File wdir = new File(tempUserSignatureDir);
			if(!wdir.exists()) wdir.mkdirs();

			List<File> filetoSign = new ArrayList<File>(); 

			for (DocumentVo doc : userSignature.getDocumentVos()) {
				//copy File to sign (use service to extract data with jack rabbit)

				File currentFile = new File(tempUserSignatureDir+doc.getFileName());
				filetoSign.add(currentFile) ;
				is = documentFacade.retrieveFileStream(doc, userVo);
				copy(is, currentFile);
			}

			userSignature.sendDocuments(filetoSign);
		} catch (PolicyNotFoundException e) {
			throw e;
		} catch (ObjectNotFoundException e) {
			throw e;
		} catch (CreateSignedDocumentContainerException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (BusinessException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.toString());
				}
			}
		}
    }


    /**
     * Copies src file to dst file.
     * If the dst file does not exist, it is created
     * @param src
     * @param dst
     * @throws IOException
     */
    private static void copy(InputStream in, File dst) throws IOException {
    	BufferedOutputStream bouf = null;
    	BufferedInputStream bif = null;

    	try {
    		bouf = new BufferedOutputStream(new FileOutputStream(dst));
    		bif = new BufferedInputStream(in);

    		// Transfer bytes from in to out
    		byte[] buf = new byte[2048];
    		int len;

    		while ((len = bif.read(buf)) > 0) {
    			bouf.write(buf, 0, len);
    		}
    		bouf.flush();
    	} catch (IOException e) {
    		throw e;
    	} finally {
    		if (bif != null){
    			try {
    				bif.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}	
    		if (bouf != null){
    			try {
    				bouf.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}	
    	}
    }
} 

