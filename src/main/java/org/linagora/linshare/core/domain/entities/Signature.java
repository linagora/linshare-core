/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Signature implements Serializable {
	
	private static final long serialVersionUID = -2226193317956985434L;

	public static final String MIMETYPE = "text/xml";

	private Long id;
	
	private Account signer;
	
	private Document document;
	
	private String uuid;
	
	private String name;
	
	private Calendar creationDate;
	
	private Calendar modificationDate;
	
	private String type;
	
	private Long size;
	
	private String certSubjectDn;
	
	private String certIssuerDn;
	
	private Date certNotAfter;
	
	private String cert;
	
	private Integer sortOrder;
	
	
	protected Signature() {
		this.uuid = null;
		this.name = null;
		this.type = MIMETYPE;
		this.creationDate = null;
		this.signer = null;
		this.size = null;
		this.certSubjectDn = null;
		this.certIssuerDn = null;
		this.certNotAfter = null;
	}
	
	public Signature(String uuid, String name, Calendar creationDate, Calendar modificationDate, Account signer, Document doc, Long size, X509Certificate signerCertificate) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.type = MIMETYPE;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.signer = signer;
		this.document = doc;
		this.size = size;
		this.certSubjectDn = signerCertificate.getSubjectDN().toString();
		this.certIssuerDn = signerCertificate.getIssuerDN().toString();
		this.certNotAfter = signerCertificate.getNotAfter();
		try {
			this.cert = Base64.getEncoder().encodeToString(signerCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object o1) {
		if (o1 instanceof Signature) {
			return this.uuid.equals(((Signature)o1).uuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getSigner() {
		return signer;
	}

	public void setSigner(Account signer) {
		this.signer = signer;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
