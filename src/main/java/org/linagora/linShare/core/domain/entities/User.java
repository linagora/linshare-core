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
package org.linagora.linShare.core.domain.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;

import edu.emory.mathcs.backport.java.util.Arrays;


public abstract class User extends Account {
	
	
	public User() {
	}
	
	public User(String firstName, String lastName, String mail) { 
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.lsUid = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.role = Role.SIMPLE;
		this.enable = true;
		this.destroyed = false;
		this.comment = "";
		this.canUpload = true;
		this.restricted = false;
		this.enciphermentKeyPass = null;
		
		// TODO: To be deleted:
//		Set<Share>
		
		this.documents = new HashSet<Document>();
		this.shares = new HashSet<Share>();
		this.securedUrls = new HashSet<SecuredUrl>();
		this.receivedShares = new HashSet<Share>();
	}

	protected String firstName;
	
	protected String lastName;
	
	protected String mail;
	
	protected byte[] enciphermentKeyPass;
	
	protected Date notAfter;
	
	protected Date notBefore;
	
	protected Date expirationDate;
	
	protected String ldapUid;
	
	protected boolean canUpload;
	
	protected String comment;
	
	protected boolean restricted;
	
	protected TechnicalAccountPermission technicalAccountPermission;
	
	public void setFirstName(String value) {
		this.firstName = value;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setLastName(String value) {
		this.lastName = value;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setMail(String value) {
		this.mail = value;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getLogin() {
		return mail;
	}
	
	public byte[] getEnciphermentKeyPass() {
		return enciphermentKeyPass;
	}
	public void setEnciphermentKeyPass(byte[] enciphermentKeyPass) {
		this.enciphermentKeyPass = Arrays.copyOf(enciphermentKeyPass, enciphermentKeyPass.length);
	}
	
	public void setNotAfter(Date value) {
		this.notAfter = value;
	}
	
	public Date getNotAfter() {
		return notAfter;
	}
	
	public void setNotBefore(Date value) {
		this.notBefore = value;
	}
	
	public Date getNotBefore() {
		return notBefore;
	}
	
	public void setExpirationDate(Date value) {
		this.expirationDate = value;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public String getLdapUid() {
		return ldapUid;
	}

	public void setLdapUid(String ldapUid) {
		this.ldapUid = ldapUid;
	}

	public void setCanUpload(boolean value) {
		this.canUpload = value;
	}
	
	public boolean getCanUpload() {
		return canUpload;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setRestricted(boolean value) {
		this.restricted = value;
	}
	
	public boolean isRestricted() {
		return restricted;
	}
	
	public void setTechnicalAccountPermission(TechnicalAccountPermission value) {
		this.technicalAccountPermission = value;
	}
	
	public TechnicalAccountPermission getTechnicalAccountPermission() {
		return technicalAccountPermission;
	}
	
	
	
	
	
	// ----------------------------------------------------------
	
	  /** If the user is allowed to create guest */
    private Boolean canCreateGuest;

	public Boolean getCanCreateGuest() {
		return canCreateGuest;
	}
	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}
	
	
	/** User's document */
    private Set<Document> documents;
    
    /** Shares that user has shared to other */
    private Set<Share> shares;
    
    /** Secured URL that user has shared to other */
    private Set<SecuredUrl> securedUrls;
    
    /**
     * Shares that user has received from other user
     */
    private Set<Share> receivedShares;
    
     /**
     * signatures made by this user on documents
     */

    private Set<Signature> ownSignatures;
    
    
    
    
	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documentList) {
		this.documents = documentList;
	}
    
	
	public void addShare(Share share) throws BusinessException {
		if (this.canUpload)
			this.shares.add(share);
		else 
			throw new BusinessException(BusinessErrorCode.USER_CANNOT_UPLOAD , "User is not allow to upload file");
	}
	
	public void deleteShare(Share share) throws BusinessException {
		if (this.canUpload)
			this.shares.remove(share);
		else 
			throw new BusinessException(BusinessErrorCode.USER_CANNOT_UPLOAD , "User is not allow to upload file");
	}
	
	
	public void deleteReceivedShare(Share share) {
			this.receivedShares.remove(share);
	}
	
	public void addReceivedShare(Share share){
			this.receivedShares.add(share);
	}
	
	public void addDocument(Document aDoc) throws BusinessException {
		if (this.canUpload)
			this.documents.add(aDoc);
		else 
			throw new BusinessException(BusinessErrorCode.USER_CANNOT_UPLOAD , "User is not allow to upload file");
	}

	public void deleteDocument(Document aDoc) {
		this.documents.remove(aDoc);
	}
	public Set<Share> getShares() {
		return shares;
	}
	public void setShares(Set<Share> shares) {
		this.shares = shares;
	}
	public Set<SecuredUrl> getSecuredUrls() {
		return securedUrls;
	}
	public void setSecuredUrls(Set<SecuredUrl> securedUrls) {
		this.securedUrls = securedUrls;
	}
	public Set<Share> getReceivedShares() {
		return receivedShares;
	}
	public void setReceivedShares(Set<Share> receivedShares) {
		this.receivedShares = receivedShares;
	}
		
	public Set<Signature> getOwnSignatures() {
		return ownSignatures;
	}
	public void setOwnSignatures(Set<Signature> ownSignatures) {
		this.ownSignatures = ownSignatures;
	}
}
