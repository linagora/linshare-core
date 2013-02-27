/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SignatureVo implements Serializable {
	

	private static final long serialVersionUID = 2877902686906612071L;

	
	private Calendar creationDate;
	private UserVo signer;
	private Long persistenceId;
	private String identifier;
	private String certSubjectDn;
	private String certIssuerDn;
	private Date certNotAfter;
	private String cert;
	private Long size;
	private String name;
	
	
	/**
	 * modifying from protected to public for using BeanUtils without construct 
	 * a document with null in parameters
	 */
	public SignatureVo(){
		this.creationDate=null;
		this.signer=null;
		this.identifier=null;
		this.certSubjectDn=null;
		this.certIssuerDn=null;
		this.certNotAfter=null;
		this.cert=null;
		this.size = null;
		this.name=null;
		this.persistenceId = null;
	}
	
	@Override
	public boolean equals(Object o1){
		if(o1 instanceof SignatureVo){
			return this.identifier.equals(((SignatureVo)o1).identifier);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.identifier.hashCode();
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	
	//setter getter
	
	public UserVo getSigner() {
		return signer;
	}

	public void setSigner(UserVo signer) {
		this.signer = signer;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getCompleteName() {
		return signer.getCompleteName();
	}
	public String getMail() {
		return signer.getMail();
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

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}
	public void setPersistenceId(Long persistenceId) {
		this.persistenceId=persistenceId ;
	}
	
}
