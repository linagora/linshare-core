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

import java.util.Date;
import java.util.Set;


public abstract class User extends Account {
	
	protected Set<ThreadMember> myThreads = new java.util.HashSet<ThreadMember>();
	
	protected String firstName;
	
	protected String lastName;
	
	protected String mail;
	
	protected Date notAfter;
	
	protected Date notBefore;
	
	protected Date expirationDate;
	
	protected String ldapUid;
	
	protected boolean canUpload;
	
	protected String comment;
	
	protected boolean restricted;
	
	protected TechnicalAccountPermission technicalAccountPermission;
	
	/** If the user is allowed to create guest */
	protected Boolean canCreateGuest;

	
	public User() {
	}
	
	public User(String firstName, String lastName, String mail) { 
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.lsUuid = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.role = Role.SIMPLE;
		this.enable = true;
		this.destroyed = false;
		this.comment = "";
		this.canUpload = true;
		this.canCreateGuest = true;
		this.restricted = false;
	}

	
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
	
	public Set<ThreadMember> getMyThreads() {
		return myThreads;
	}

	public void setMyThreads(Set<ThreadMember> myThreads) {
		this.myThreads = myThreads;
	}
	
	public Boolean getCanCreateGuest() {
		return canCreateGuest;
	}
	
	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}
}
