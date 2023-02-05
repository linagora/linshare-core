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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.springframework.data.annotation.Transient;

public class AnonymousUrlMto {

	protected String urlPath;

	protected String uuid;

	// TODO : AKO: What is the purpose of these fields ?
	@Transient
	protected String password;

	protected String contactMail;

	@Transient
	protected String temporaryPlainTextPassword;

	public AnonymousUrlMto() {
		super();
	}

	public AnonymousUrlMto(AnonymousUrl url) {
		this.uuid = url.getUuid();
		this.urlPath = url.getUrlPath();
		this.password = url.getPassword();
		this.contactMail = url.getContact().getMail();
		this.temporaryPlainTextPassword = url.getTemporaryPlainTextPassword();
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

	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	public String getTemporaryPlainTextPassword() {
		return temporaryPlainTextPassword;
	}

	public void setTemporaryPlainTextPassword(String temporaryPlainTextPassword) {
		this.temporaryPlainTextPassword = temporaryPlainTextPassword;
	}
}
