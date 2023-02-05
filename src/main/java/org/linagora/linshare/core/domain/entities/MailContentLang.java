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

import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;

public class MailContentLang implements Cloneable {

	private long id;

	private int language;

	private boolean readonly;

	private MailContent mailContent;

	private int mailContentType;

	private String uuid;

	private MailConfig mailConfig;

	public MailContentLang() {
	}

	public MailContentLang(MailContentLang copied) {
		this.language = copied.language;
		this.mailContent = copied.mailContent;
		this.mailContentType = copied.mailContentType;
		this.uuid = UUID.randomUUID().toString();
		this.readonly = false;
	}

	/*
	 * for filtering purpose
	 */
	public MailContentLang(Language language, MailContentType mailContentType) {
		this.language = language.toInt();
		this.mailContentType = mailContentType.toInt();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLanguage(int value) {
		this.language = value;
	}

	public int getLanguage() {
		return language;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public void setMailContentType(int value) {
		this.mailContentType = value;
	}

	public int getMailContentType() {
		return mailContentType;
	}

	public void setMailContent(MailContent value) {
		this.mailContent = value;
	}

	public MailContent getMailContent() {
		return mailContent;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
		this.mailConfig = mailConfig;
	}

	public boolean businessEquals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailContentLang other = (MailContentLang) obj;
		if (language != other.language)
			return false;
		if (mailContentType != other.mailContentType)
			return false;
		return true;
	}

	@Override
	public MailContentLang clone() {
		MailContentLang p = null;
		try {
			p = (MailContentLang) super.clone();
			p.id = 0;
			p.mailContent = mailContent.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return p;
	}
}
