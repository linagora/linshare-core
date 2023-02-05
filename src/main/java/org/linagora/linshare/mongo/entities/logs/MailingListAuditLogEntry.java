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
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.MailingListMto;

@XmlRootElement(name = "MailingListAuditLogEntry")
public class MailingListAuditLogEntry extends AuditLogEntryUser {

	private MailingListMto resource;

	private MailingListMto resourceUpdated;

	public MailingListAuditLogEntry() {
	}

	public MailingListAuditLogEntry(AccountMto authUser, AccountMto owner, LogAction action, AuditLogEntryType type,
			ContactList l) {
		super(authUser, owner, action, type, l.getUuid());
		this.resource = new MailingListMto(l);
	}

	public MailingListMto getResource() {
		return resource;
	}

	public void setResource(MailingListMto list) {
		this.resource = list;
	}

	public MailingListMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(MailingListMto listUpdated) {
		this.resourceUpdated = listUpdated;
	}
}
