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
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.MailingListContactMto;
import org.linagora.linshare.mongo.entities.mto.MailingListMto;

@XmlRootElement(name = "MailingListContactAuditLogEntry")
public class MailingListContactAuditLogEntry extends AuditLogEntryUser {

	private MailingListContactMto resource;

	private MailingListContactMto resourceUpdated;

	protected MailingListMto list;

	public MailingListContactAuditLogEntry() {
	}

	public MailingListContactAuditLogEntry(AccountMto authUser, AccountMto owner, LogAction action, AuditLogEntryType type,
			ContactList list, ContactListContact contact) {
		super(authUser, owner, action, type, contact.getUuid());
		this.resource = new MailingListContactMto(contact);
		this.list = new MailingListMto(list);
	}

	public MailingListMto getList() {
		return list;
	}

	public void setList(MailingListMto list) {
		this.list = list;
	}

	public MailingListContactMto getResource() {
		return resource;
	}

	public void setResource(MailingListContactMto resource) {
		this.resource = resource;
	}

	public MailingListContactMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(MailingListContactMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

}
