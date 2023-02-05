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
package org.linagora.linshare.mongo.entities.logs;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

@XmlRootElement
public class AuthenticationAuditLogEntryUser extends AuditLogEntryUser {

	protected String message;

	protected String login;

	protected String domainIdentifier;

	public AuthenticationAuditLogEntryUser() {
		super();
	}

	public AuthenticationAuditLogEntryUser(Account authUser, LogAction action, AuditLogEntryType type, String message) {
		super(new AccountMto(authUser), new AccountMto(authUser), action, type, authUser.getLsUuid());
		this.message = message;
	}

	public AuthenticationAuditLogEntryUser(String login, String domainIdentifier, LogAction action,
			AuditLogEntryType type, String message) {
		this.login = login;
		this.domainIdentifier = domainIdentifier;
		this.message = message;
		this.action = action;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}

	public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}

}
