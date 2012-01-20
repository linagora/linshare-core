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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;

import edu.emory.mathcs.backport.java.util.Arrays;

public abstract class User {

    /** Surrogate key. */
    private long id;

	/** the name of the user. */
	private final String login;
	
	/** the lastName of the user. */
	private String lastName;
	
	/** the first name of the user. */
	private String firstName;
	
	/** the mail of the user. */
	private final String mail;
	
    /** User first login date. */
	private Date creationDate;

    /** User role. */
    private Role role = Role.SIMPLE;
    
    /** User default language */
    private String locale;
    
    /** User encipherment challenge (pbe) */
    private byte[] enciphermentKeyPass;

    /** User password (if applicable). */
    private String password;

    
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
    
    
    /** If the user is allowed to upload file */
    private Boolean canUpload;
    
    /** If the user is allowed to create guest */
    private Boolean canCreateGuest;
    
    private AbstractDomain domain;

    protected User() {
    	this.login = null;
		this.firstName = null;
		this.lastName= null;
		this.mail = null;
    	this.documents = new HashSet<Document>();
    	this.shares=new HashSet<Share>();
    	this.receivedShares=new HashSet<Share>();
    	this.securedUrls=new HashSet<SecuredUrl>();
    	this.domain = null;
    }
    /** Default constructor.
     * @param login login.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail email.
     * @param userType user type.
     */
	protected User(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest) {
		this.login = login;
		this.firstName = firstName;
		this.lastName= lastName;
		this.mail = mail;
        this.role = Role.SIMPLE;
        this.documents = new HashSet<Document>();
    	this.shares=new HashSet<Share>();
    	this.receivedShares=new HashSet<Share>();
    	this.securedUrls=new HashSet<SecuredUrl>();
        this.canUpload = canUpload;
        this.canCreateGuest = canCreateGuest;
        this.enciphermentKeyPass=null;
    	this.domain = null;
	}
	
	@Override
	public boolean equals(Object o1){
		if(o1 instanceof User && o1 != null) {
			return this.login.equals(((User)o1).login);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.login.hashCode();
	}

	public String getLogin() {
		return login;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMail() {
		return mail;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public abstract UserType getUserType();

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}


	public Boolean getCanUpload() {
		return canUpload;
	}
	public void setCanUpload(Boolean canUpload) {
		this.canUpload = canUpload;
	}
	
	public Boolean getCanCreateGuest() {
		return canCreateGuest;
	}
	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}
	
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public byte[] getEnciphermentKeyPass() {
		return enciphermentKeyPass;
	}
	public void setEnciphermentKeyPass(byte[] enciphermentKeyPass) {
		this.enciphermentKeyPass = Arrays.copyOf(enciphermentKeyPass, enciphermentKeyPass.length);
	}
	
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
	public Set<Signature> getOwnSignatures() {
		return ownSignatures;
	}
	public void setOwnSignatures(Set<Signature> ownSignatures) {
		this.ownSignatures = ownSignatures;
	}
	
	public AbstractDomain getDomain() {
		return domain;
	}
	
	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
	
	public String getDomainId() {
		return ( (this.domain == null) ? null : this.domain.getIdentifier() );
	}
}
