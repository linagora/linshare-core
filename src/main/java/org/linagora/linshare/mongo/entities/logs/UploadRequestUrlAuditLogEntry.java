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
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.UploadRequestUrlMto;

@XmlRootElement
public class UploadRequestUrlAuditLogEntry extends AuditLogEntryUser {

	protected UploadRequestUrlMto resource;

	protected UploadRequestUrlMto resourceUpdated;

	public UploadRequestUrlAuditLogEntry() {
		super();
	}

	public UploadRequestUrlAuditLogEntry(AccountMto authUser, AccountMto actor, LogAction action, AuditLogEntryType type,
			String resourceUuid, UploadRequestUrl urUrl) {
		super(authUser, actor, action, type, urUrl.getUuid());
		this.resource = new UploadRequestUrlMto(urUrl);
		this.addRelatedResources(urUrl.getUploadRequest().getUuid());
	}

	public UploadRequestUrlMto getResource() {
		return resource;
	}

	public void setResource(UploadRequestUrlMto resource) {
		this.resource = resource;
	}

	public UploadRequestUrlMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(UploadRequestUrlMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
