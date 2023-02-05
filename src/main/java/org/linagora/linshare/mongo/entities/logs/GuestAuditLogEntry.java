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
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.UserMto;

@XmlRootElement(name = "GuestAuditLogEntry")
public class GuestAuditLogEntry extends AuditLogEntryUser {

	private UserMto resource;

	private UserMto resourceUpdated;

	public GuestAuditLogEntry() {
	}

	public GuestAuditLogEntry(Account authUser, Account actor, LogAction action, AuditLogEntryType type, Guest guest) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, guest.getLsUuid());
		this.resource = new UserMto(guest);
		this.addRelatedDomains(guest.getDomain().getUuid());
	}

	public AccountMto getResource() {
		return resource;
	}

	public void setResource(UserMto resource) {
		this.resource = resource;
	}

	public UserMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(UserMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
