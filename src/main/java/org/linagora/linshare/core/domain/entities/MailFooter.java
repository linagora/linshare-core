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

import java.util.Date;

import org.linagora.linshare.core.domain.constants.Language;

public class MailFooter implements Cloneable {

	private long id;

	private String description;

	private AbstractDomain domain;

	private boolean visible;

	private String footer;

	private Date creationDate;

	private Date modificationDate;

	private String uuid;

	private boolean readonly;

	private String messagesFrench;

	private String messagesEnglish;

	private String messagesRussian;

	private String messagesVietnamese;

	public MailFooter() {
	}

	public void setId(long value) {
		this.id = value;
	}

	public long getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setFooter(String value) {
		this.footer = value;
	}

	public String getFooter() {
		return footer;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getMessagesFrench() {
		return messagesFrench;
	}

	public void setMessagesFrench(String messagesFrench) {
		this.messagesFrench = messagesFrench;
	}

	public String getMessagesEnglish() {
		return messagesEnglish;
	}

	public void setMessagesEnglish(String messagesEnglish) {
		this.messagesEnglish = messagesEnglish;
	}

	public String getMessagesRussian() {
		return messagesRussian;
	}

	public void setMessagesRussian(String messagesRussian) {
		this.messagesRussian = messagesRussian;
	}

	public String getMessagesVietnamese() {
		return messagesVietnamese;
	}

	public void setMessagesVietnamese(String messagesVietnamese) {
		this.messagesVietnamese = messagesVietnamese;
	}

	@Override
	public String toString() {
		return "MailFooter [id=" + id + ", name=" + description + ", domain=" + domain + ", visible=" + visible
				+ ", footer=" + footer + ", creationDate=" + creationDate + ", modificationDate="
				+ modificationDate + ", uuid=" + uuid + "]";
	}

	@Override
	public MailFooter clone() {
		MailFooter p = null;
		try {
			p = (MailFooter) super.clone();
			p.id = 0;
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return p;
	}

	/**
	 * Helpers
	 */
	public String getMessages(Language lang) {
		if (lang.equals(Language.FRENCH)) {
			return getMessagesFrench();
		} else if (lang.equals(Language.RUSSIAN)) {
			return getMessagesRussian();
		} else if (lang.equals(Language.VIETNAMESE)) {
			return getMessagesVietnamese();
		} else {
			return getMessagesEnglish();
		}
	}
}
