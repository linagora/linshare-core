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

public class MailLayout implements Cloneable {

	private long id;

	private String description;

	private AbstractDomain domain;

	private boolean visible;

	private String layout;

	private Date creationDate;

	private Date modificationDate;

	private String uuid;

	private boolean readonly;

	private String messagesFrench;

	private String messagesEnglish;

	private String messagesRussian;

	private String messagesVietnamese;

	public MailLayout() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public MailLayout clone() {
		MailLayout p = null;
		try {
			p = (MailLayout) super.clone();
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
