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
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A Share provides for link between Owner of a document, a document, and 
 * a recipient of a document.
 * @author ncharles
 *
 */
public class Share implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 242454729006516690L;

	private long persistenceId;
	
	/** the document's owner */
	private User sender;
	
	/** the recipient of the document */
	private User receiver;
	
	/** the document */
	private Document document;
	
	/** the expiration date of the share*/
	private Calendar expirationDate;
	
	/** the date of the share*/
	private Calendar sharingDate;
	
	/** is the sharing is still active or not */
	private Boolean shareActive;

	/** is the document has been downloaded */
	private Boolean downloaded;
	
	/**
	 * The comment when the share is done.
	 */
	private String comment;
	
	/**
	 * modifying from protected to public for using BeanUtils without construct 
	 * a document with null in parameters
	 */
	protected Share() {
		
	}


	public Share(User sender, User receiver,
			Document document,String comment, Calendar expirationDate, Boolean shareActive, Boolean downloaded) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.document = document;
		this.comment=comment;
		this.expirationDate = expirationDate;
		this.shareActive = shareActive;
		this.downloaded = downloaded;
		this.sharingDate = new GregorianCalendar();
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long id) {
		if(null == id) this.persistenceId = 0;
		else this.persistenceId = id;
	}
	
	public User getSender() {
		return sender;
	}


	public User getReceiver() {
		return receiver;
	}


	public Document getDocument() {
		return document;
	}


	public Calendar getExpirationDate() {
		return expirationDate;
	}


	public void setSender(User sender) {
		this.sender = sender;
	}


	public void setRecipient(User receiver) {
		this.receiver = receiver;
	}


	public void setDocument(Document document) {
		this.document = document;
	}


	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
	public Boolean getShareActive() {
		return shareActive;
	}


	public void setShareActive(Boolean shareActive) {
		this.shareActive = shareActive;
	}

	

	public Boolean getDownloaded() {
		return downloaded;
	}


	public void setDownloaded(Boolean downloaded) {
		this.downloaded = downloaded;
	}

	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public boolean equals(Object o1){
		if(o1 instanceof Share){
			return ( (this.sender.equals(((Share)o1).sender)) && 
					(this.document.equals(((Share)o1).document)) &&
					(this.receiver.equals(((Share)o1).receiver)) );
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return new StringBuilder().append(sender.hashCode()).append(document.hashCode())
		.append(receiver.hashCode()).toString().hashCode();
	}


	public void setSharingDate(Calendar sharingDate) {
		this.sharingDate = sharingDate;
	}


	public Calendar getSharingDate() {
		return sharingDate;
	}



	
	
}
