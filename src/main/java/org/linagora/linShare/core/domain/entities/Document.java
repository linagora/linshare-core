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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Document implements Serializable {
	

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
	 * the expiration date of the document.
	 */
	private Calendar expirationDate;
	
	/**
	 * the deletion date of the document.
	 */
	private Calendar deletionDate;
	
	/**
	 * the document owner.
	 */
	private User owner;
	
	/**
	 * the document type.
	 */
	private String type;
	
	/**
	 * if the document is encrypted.
	 */
	private Boolean encrypted;
	
	/**
	 * if the document is shared.
	 */
	private Boolean shared;
	
	/**
	 * the document file size
	 */
	private Long size;
	
	
	private List<Signature> signatures;
	
	
	private String fileComment;
	
	/**
	 * UUID of the thumbnail file
	 */
	private String thmbUUID;
	
	
	/**
	 * timsStampresponse encoded (der)
	 */
	private byte[] timeStamp;
	
	
	public Document(String identifier,String name, String type, Calendar creationDate,
			Calendar expirationDate, User owner, Boolean encrypted,
			Boolean shared,Long size) {
		super();
		this.identifier=identifier;
		this.name = name;
		this.type = type;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.owner = owner;
		this.encrypted = encrypted;
		this.shared = shared;
		this.size = size;
		this.signatures = null;
		this.thmbUUID = null;
		this.timeStamp = null;
		this.deletionDate = null;
	}
	/**
	 * modifying from protected to public for using BeanUtils without construct 
	 * a document with null in parameters
	 */
	public Document(){
		super();
	}
	
	@Override
	public boolean equals(Object o1){
		if(o1 instanceof Document){
			return this.identifier.equals(((Document)o1).identifier);
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

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	
	public Boolean getEncrypted() {
		return encrypted;
	}

	public Boolean getShared() {
		return shared;
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

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	
	public void setEncrypted(Boolean encrypted) {
		this.encrypted = encrypted;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
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
	
	public String getFileComment() {
		return fileComment;
	}
	public void setFileComment(String fileComment) {
		this.fileComment = fileComment;
	}
	public List<Signature> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<Signature> signatures) {
		this.signatures = signatures;
	}
	public void setThmbUUID(String thmbUUID) {
		this.thmbUUID = thmbUUID;
	}
	public String getThmbUUID() {
		return thmbUUID;
	}
	public byte[] getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(byte[] timeStamp) {
		this.timeStamp = Arrays.copyOf(timeStamp,timeStamp.length);
	}
	public void setDeletionDate(Calendar deletionDate) {
		this.deletionDate = deletionDate;
	}
	public Calendar getDeletionDate() {
		return deletionDate;
	}
	
}
