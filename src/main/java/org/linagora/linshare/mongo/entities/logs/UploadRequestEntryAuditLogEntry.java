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
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.UploadRequestEntryMto;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement
public class UploadRequestEntryAuditLogEntry extends AuditLogEntryUser {

	protected UploadRequestEntryMto resource;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected CopyMto copiedTo;

	public UploadRequestEntryAuditLogEntry() {
		super();
	}

	public UploadRequestEntryAuditLogEntry(AccountMto authUser, AccountMto actor, LogAction action, AuditLogEntryType type,
			String resourceUuid, UploadRequestEntry urEntry) {
		super(authUser, actor, action, type, urEntry.getUuid());
		this.resource = new UploadRequestEntryMto(urEntry);
		this.addRelatedResources(urEntry.getUploadRequestUrl().getUploadRequest().getUuid());
	}

	public UploadRequestEntryMto getResource() {
		return resource;
	}

	public void setResource(UploadRequestEntryMto resource) {
		this.resource = resource;
	}

	public CopyMto getCopiedTo() {
		return copiedTo;
	}

	public void setCopiedTo(CopyMto copiedTo) {
		this.copiedTo = copiedTo;
	}

}