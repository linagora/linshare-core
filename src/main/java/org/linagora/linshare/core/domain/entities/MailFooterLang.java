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

public class MailFooterLang implements Cloneable {

	private long id;

	private int language;

	private boolean readonly;

	private MailFooter mailFooter;

	private String uuid;

	private MailConfig mailConfig;

	public MailFooterLang() {
	}

	public MailFooterLang(MailFooterLang copied) {
		this.language = copied.language;
		this.mailFooter = copied.mailFooter;
		this.uuid = UUID.randomUUID().toString();
		this.readonly = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public MailFooter getMailFooter() {
		return mailFooter;
	}

	public void setMailFooter(MailFooter mailFooter) {
		this.mailFooter = mailFooter;
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

	@Override
	public MailFooterLang clone() {
		MailFooterLang p = null;
		try {
			p = (MailFooterLang) super.clone();
			p.id = 0;
			p.mailFooter = mailFooter.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return p;
	}
}
