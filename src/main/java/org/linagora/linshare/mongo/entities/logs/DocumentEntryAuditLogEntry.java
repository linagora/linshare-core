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
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.DocumentMto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement
public class DocumentEntryAuditLogEntry extends AuditLogEntryUser {

	protected DocumentMto resource;

	protected DocumentMto resourceUpdated;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected CopyMto copiedTo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected CopyMto copiedFrom;

	public DocumentEntryAuditLogEntry() {
		super();
	}

	public DocumentEntryAuditLogEntry(Account authUser, Account actor, DocumentEntry entry, LogAction action) {
		super(new AccountMto(authUser), new AccountMto(actor), action, AuditLogEntryType.DOCUMENT_ENTRY, entry.getUuid());
		this.resource = new DocumentMto(entry);
	}

	public DocumentMto getResource() {
		return resource;
	}

	public void setResource(DocumentMto resource) {
		this.resource = resource;
	}

	public DocumentMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(DocumentMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	public CopyMto getCopiedTo() {
		return copiedTo;
	}

	public void setCopiedTo(CopyMto copiedTo) {
		this.copiedTo = copiedTo;
	}

	public CopyMto getCopiedFrom() {
		return copiedFrom;
	}

	public void setCopiedFrom(CopyMto copiedFrom) {
		this.copiedFrom = copiedFrom;
	}

}
