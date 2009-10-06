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
package org.linagora.linShare.core.domain.vo;

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
