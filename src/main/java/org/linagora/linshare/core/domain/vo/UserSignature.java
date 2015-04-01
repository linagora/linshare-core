/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.domain.vo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import org.linagora.linsign.client.keystore.KeystoreType;
import org.linagora.linsign.client.ui.UiService;
import org.linagora.linsign.client.ui.impl.UiServiceImpl;
import org.linagora.linsign.exceptions.CheckSignerKeyException;
import org.linagora.linsign.exceptions.ComputeSignatureException;
import org.linagora.linsign.exceptions.CorruptedFileException;
import org.linagora.linsign.exceptions.CreateSignedDocumentContainerException;
import org.linagora.linsign.exceptions.FinalizeDocumentException;
import org.linagora.linsign.exceptions.ObjectNotFoundException;
import org.linagora.linsign.exceptions.PolicyNotFoundException;
import org.linagora.linsign.server.portal.ServerInterface;
import org.linagora.linsign.server.portal.impl.ServerImpl;


public class UserSignature {
	
	private UiService ui;
	
	private String sigUUID;
	private KeystoreType signerKeystoreType;
	private String alias;
    
	private List<File> allFilesToSign; //remember the files to work with
	
	//keep the the file to sign (linshare)
	private List<DocumentVo>  documentVos;
	//keep application context : list of document, list a shared document ? (linshare)
	private DocToSignContext docToSignContext;
	
    private String oidSignaturePolicy; //select a policy to sign your document
	private X509Certificate signercert; //remember the choosen signer certificate
	
	
	private String errorMessage;
	
	
	public UserSignature() {
		createUserSignature();
	}
	
	/**
	 * this class stay on the server side and make call to signature service
	 */
	private void createUserSignature() {
		if(ui==null){
			ServerInterface si = new ServerImpl();
			//la signature est assurée par l'applet, pas besoin de ce service
			//SignatureService signserv = new SignatureServiceImpl(); 
			
			ui = new UiServiceImpl();
			((UiServiceImpl) ui).setServerInterface(si);
			//((UiServiceImpl) ui).setSignatureService(signserv);
		}
	}
	
	public String init(String oidSignaturePolicy) throws PolicyNotFoundException{
		sigUUID = ui.initSignatureProcess(oidSignaturePolicy);
		//System.out.println("new:"+sigUUID);
		return sigUUID;
	}
	
	public void sendCertificate(byte[] x509cert) throws ObjectNotFoundException, CertificateException {
			//first send certificate
			ui.sendCertificate(sigUUID, x509cert);
			
			//keep information of the certificate
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			signercert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(x509cert));
	}
	
	public void sendDocuments(List<File> pathToLocalfiles) throws ObjectNotFoundException, CreateSignedDocumentContainerException, IOException {
			ui.sendDocuments(sigUUID, pathToLocalfiles,false);
	}
	public Map<String, String> getAllBase64HashTBS() throws ObjectNotFoundException, ComputeSignatureException, CorruptedFileException {
		Map<String, String> m = ui.getAllBase64HashTBS(sigUUID);
		return m;
	}
	public void finalizeDocument(Map<String, String> signatures) throws ObjectNotFoundException, FinalizeDocumentException, CheckSignerKeyException, CorruptedFileException {
			ui.finalizeDocument(sigUUID, signatures);
		
	}
	
	public List<File> getFinalizedDocument() throws ObjectNotFoundException {
		List<File> files  = ui.getFinalizedDocument(sigUUID);
		return files;
	}
	
	
	
	public KeystoreType getSignerKeystoreType() {
		return signerKeystoreType;
	}
	
	public String getSignerKeystoreTypeName() {
		return signerKeystoreType.name();
	}

	public void setSignerKeystoreType(KeystoreType signerKeystoreType) {
		this.signerKeystoreType = signerKeystoreType;
	}
	

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void close(){
		if(sigUUID!=null) ui.cleanSignatureProcess(sigUUID,true); //erase instance and files on server
		sigUUID = null;
	}

	public String getSigUUID() {
		return sigUUID;
	}

	public String getOidSignaturePolicy() {
		return oidSignaturePolicy;
	}

	public void setOidSignaturePolicy(String oidSignaturePolicy) {
		this.oidSignaturePolicy = oidSignaturePolicy;
	}

	public X509Certificate getSignercert() {
		return signercert;
	}

	public List<File> getAllFilesToSign() {
		return allFilesToSign;
	}

	public void setAllFilesToSign(List<File> allFilesToSign) {
		this.allFilesToSign = allFilesToSign;
	}

	public List<DocumentVo>  getDocumentVos() {
		return documentVos;
	}

	public void setDocumentVos(List<DocumentVo>  documentVos) {
		this.documentVos = documentVos;
	}

	public DocToSignContext getDocContext() {
		return docToSignContext;
	}

	public void setDocContext(DocToSignContext docToSignContext) {
		this.docToSignContext = docToSignContext;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
