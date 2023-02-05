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
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.UploadRequestMto;

@XmlRootElement
public class UploadRequestAuditLogEntry extends AuditLogEntryUser {

	protected UploadRequestMto resource;

	protected UploadRequestMto resourceUpdated;

	public UploadRequestAuditLogEntry() {
		super();
	}

	public UploadRequestAuditLogEntry(AccountMto authUser, AccountMto actor, LogAction action, AuditLogEntryType type,
			String resourceUuid, UploadRequest req) {
		super(authUser, actor, action, type, req.getUuid());
		this.resource = new UploadRequestMto(req);
	}

	public UploadRequestAuditLogEntry(AccountMto authUser, AccountMto owner, LogAction action, AuditLogEntryType type,
			String resourceUuid, UploadRequestMto req) {
		super(authUser, owner, action, type, req.getUuid());
		this.resource = req;
	}

	public UploadRequestMto getResource() {
		return resource;
	}

	public void setResource(UploadRequestMto resource) {
		this.resource = resource;
	}

	public UploadRequestMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(UploadRequestMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
