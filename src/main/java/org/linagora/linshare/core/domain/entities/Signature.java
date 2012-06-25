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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.utils.Base64Utils;

public class Signature implements Serializable {
	

	private static final long serialVersionUID = 2877902686906612071L;

	private long persistenceId;
	
	/**
	 * the identifier of the document.
	 */
	private String identifier;
	
	/**
	 * the name of the document.
	 */
	private String name;
	
	/**
	 * the creation date of the document.
	 */
	private Calendar creationDate;
	
	
	/**
	 * the document type.
	 */
	private String type;
	
	/**
	 * the document file size
	 */
	private Long size;
	
	
	private Document document;
	
	
	private User signer;

	private String certSubjectDn;

	private String certIssuerDn;

	private Date certNotAfter;

	private String cert;
	
	
	public static final String MIMETYPE = "text/xml";
	
	protected Signature(){
		this.identifier=null;
		this.name = null;
		this.type = MIMETYPE;
		this.creationDate = null;
		this.signer = null;
		this.size = null;
		this.certSubjectDn=null;
		this.certIssuerDn=null;
		this.certNotAfter=null;
	}
	
	public Signature(String identifier,String name, Calendar creationDate,User signer, Long size,X509Certificate signerCertificate) {
		super();
		this.identifier=identifier;
		this.name = name;
		this.type = MIMETYPE;
		this.creationDate = creationDate;
		this.signer = signer;
		this.size = size;
		this.certSubjectDn=signerCertificate.getSubjectX500Principal().toString();
		this.certIssuerDn=signerCertificate.getIssuerX500Principal().toString();
		this.certNotAfter=signerCertificate.getNotAfter();
		try {
			this.cert=Base64Utils.encodeBytes(signerCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
		}
	}
	
	@Override
	public boolean equals(Object o1){
		if(o1 instanceof Signature){
			return this.identifier.equals(((Signature)o1).identifier);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.identifier.hashCode();
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long id) {
		if(null == id) this.persistenceId = 0;
		else this.persistenceId = id;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}


	public long getSize() {
		return size;
	}

	public void setSize(long fileSize) {
		this.size = fileSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public User getSigner() {
		return signer;
	}
	public void setSigner(User signer) {
		this.signer = signer;
	}

	public String getCertSubjectDn() {
		return certSubjectDn;
	}

	public void setCertSubjectDn(String certSubjectDn) {
		this.certSubjectDn = certSubjectDn;
	}

	public String getCertIssuerDn() {
		return certIssuerDn;
	}

	public void setCertIssuerDn(String certIssuerDn) {
		this.certIssuerDn = certIssuerDn;
	}

	public Date getCertNotAfter() {
		return certNotAfter;
	}

	public void setCertNotAfter(Date certNotAfter) {
		this.certNotAfter = certNotAfter;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}
	
}
