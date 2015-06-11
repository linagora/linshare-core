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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.utils.Base64Utils;

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
			this.cert = Base64Utils.encodeBytes(signerCertificate.getEncoded());
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
