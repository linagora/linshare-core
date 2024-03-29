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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.collect.Lists;

/**
 * @author fred
 *
 */
public class AnonymousUrl {

	private Long id;

	private String urlPath;

	private String uuid;

	private String password;

	private Contact contact;

	private String temporaryPlainTextPassword;

	private Set<AnonymousShareEntry> anonymousShareEntries = new HashSet<AnonymousShareEntry>();

	// Temporary attribute to store log entries (for performance issue).
	protected List<AuditLogEntryUser> logs = Lists.newArrayList();

	public AnonymousUrl() {
	}

	public AnonymousUrl(String urlPath, Contact contact) {
		super();
		this.urlPath = urlPath;
		this.password = null;
		this.temporaryPlainTextPassword = null;
		this.contact = contact;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<AnonymousShareEntry> getAnonymousShareEntries() {
		return anonymousShareEntries;
	}

	public void setAnonymousShareEntries(Set<AnonymousShareEntry> anonymousShareEntries) {
		this.anonymousShareEntries = anonymousShareEntries;
	}

	public String getTemporaryPlainTextPassword() {
		return temporaryPlainTextPassword;
	}

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}

	public String getFullUrl(String baseUrl) {
		// compose the secured url to give in mail
		StringBuffer httpUrlBase = new StringBuffer();
		httpUrlBase.append(baseUrl);
		httpUrlBase.append(getUrlPath());
		if (!getUrlPath().endsWith("/")) {
			httpUrlBase.append('/');
		}
		httpUrlBase.append(getUuid());
		return httpUrlBase.toString();
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	/** Useful getters */
	public List<String> getDocumentNames() {
		List<String> docNames = new ArrayList<String>();
		for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
			docNames.add(anonymousShareEntry.getDocumentEntry().getName());
		}
		return docNames;
	}

	/**
	 * Get the owner of an anonymous url
	 * TODO add property Account for the owner
	 */
	public Account getOwner() {
		Account owner = null;
		for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
			owner = anonymousShareEntry.getEntryOwner();
			break;
		}
		return owner;
	}

	public boolean oneDocumentIsEncrypted() {
		boolean isOneDocEncrypted = false;
		for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
			if (anonymousShareEntry.getDocumentEntry().getCiphered()) {
				isOneDocEncrypted = true;
				break;
			}

		}
		return isOneDocEncrypted;
	}

	public boolean isPasswordProtected() {
		return getPassword() != null;
	}

	public String getReprentation() {
		return this.uuid;
	}

	public List<AuditLogEntryUser> getLogs() {
		return logs;
	}

	public void addLog(AuditLogEntryUser log) {
		this.logs.add(log);
	}
}
