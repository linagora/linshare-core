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

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;

import com.google.common.collect.Maps;

public class WelcomeMessages implements Cloneable {

	private long id;

	private String uuid;

	private String name;

	private String description;

	private Date creationDate;

	private Date modificationDate;

	private Map<SupportedLanguage, WelcomeMessagesEntry> welcomeMessagesEntries;

	/**
	 * owner domain
	 */
	private AbstractDomain domain;

	public WelcomeMessages() {
	}

	public WelcomeMessages(String name, String description,
			AbstractDomain domain,
			Map<SupportedLanguage, WelcomeMessagesEntry> customEntries) {
		this.name = name;
		this.description = description;
		this.domain = domain;
		this.welcomeMessagesEntries = customEntries;
	}

	public WelcomeMessages(WelcomeMessages c) {
		this.name = c.getName();
		this.description = c.getDescription();
		Collection<WelcomeMessagesEntry> values = c.getWelcomeMessagesEntries()
				.values();
		this.welcomeMessagesEntries = Maps.newHashMap();
		for (WelcomeMessagesEntry entry : values) {
			this.welcomeMessagesEntries
					.put(entry.getLang(),
							new WelcomeMessagesEntry(entry.getLang(), entry
									.getValue()));
		}
	}

	public Object clone() {
		WelcomeMessages welcome = null;
		try {
			welcome = (WelcomeMessages) super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		welcome.id = 0;
		return welcome;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public void setBussinessName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setBussinessDescription(String description) {
		if (description != null) {
			this.description = description;
		}
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Map<SupportedLanguage, WelcomeMessagesEntry> getWelcomeMessagesEntries() {
		return welcomeMessagesEntries;
	}

	public void addWelcomeMessagesEntry(SupportedLanguage key, String entry) {
		if (welcomeMessagesEntries == null) {
			welcomeMessagesEntries = Maps.newHashMap();
		}
		this.welcomeMessagesEntries.put(key, new WelcomeMessagesEntry(key,
				entry));
	}

	public void setWelcomeMessagesEntries(
			Map<SupportedLanguage, WelcomeMessagesEntry> customisationEntries) {
		this.welcomeMessagesEntries = customisationEntries;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
}
