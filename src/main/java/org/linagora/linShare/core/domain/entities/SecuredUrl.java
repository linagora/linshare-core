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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SecuredUrl {

	private Long persistenceId;

	final private String urlPath;
	final private String alea;

	/** the document's owner */
	private User sender;

	/** recipients email list for the secured url**/
	private List<Contact> recipients;
	
	private Calendar expirationTime;

	private List<Document> documents;

	private String password;
	
	// very nasty fix 
	private String temporaryPlainTextpassword;

	// for hibernate
	protected SecuredUrl() {
		this.urlPath = null;
		this.alea = null;
		recipients = null;
	}

	public SecuredUrl(String urlPath, String alea, Calendar expiration, User sender, List<Contact> recipients) {
		if (StringUtils.isEmpty(urlPath) || null == expiration) {
			throw new IllegalArgumentException(
					"The UrlPath is mandatory for the SecuredUrl.");
		}
		this.urlPath = urlPath.toLowerCase();
		this.alea = alea;
		this.expirationTime = expiration;
		this.documents = new ArrayList<Document>();
		this.sender = sender;
		
		this.recipients = new ArrayList<Contact>(recipients);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SecuredUrl)) {
			return false;
		}
		SecuredUrl other = (SecuredUrl) obj;
		return (this.urlPath.equals(other.getUrlPath()) && this.alea.equals(other.getAlea()));
	}

	@Override
	public int hashCode() {
		return (this.urlPath+this.alea).hashCode();
		//return new StringBuilder(this.urlPath.hashCode()).append(this.alea.hashCode()).hashCode();
	}

	/**
	 * Return the expiration Date of the current SecuredUrl
	 * 
	 * @return expirationTime
	 */
	public Calendar getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Set the expirationTime of the current SecuredUrl
	 * 
	 * @param expirationTime
	 */
	public void setExpirationTime(Calendar expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * @return the persistenceId
	 */
	public Long getPersistenceId() {
		return persistenceId;
	}

	/**
	 * @param persistenceId
	 *            the persistenceId to set
	 */
	public void setPersistenceId(Long id) {
		this.persistenceId = id;
	}

	/**
	 * return the url which will send to the user when it will be registered
	 * 
	 * @return urlPath
	 */
	public String getUrlPath() {
		return this.urlPath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean addDocument(Document document) {
		return this.documents.add(document);
	}

	public void addDocuments(List<Document> documents) {
		for (Document document : documents) {
			this.documents.add(document);
		}

	}

	public boolean removeDocument(Document document) {
		return this.documents.remove(document);
	}

	public List<Document> getDocuments() {
		return Collections.unmodifiableList(documents);
	}

	public String getAlea() {
		return alea;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public List<Contact> getRecipients() {
		return Collections.unmodifiableList(recipients);
	}

	public String getTemporaryPlainTextpassword() {
		return temporaryPlainTextpassword;
	}

	public void setTemporaryPlainTextpassword(String temporaryPlainTextpassword) {
		this.temporaryPlainTextpassword = temporaryPlainTextpassword;
	}

}
