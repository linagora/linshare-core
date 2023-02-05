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
import org.linagora.linshare.mongo.entities.mto.AbstractWorkSpaceFilterMto;

@XmlRootElement(name = "WorkSpaceFilterAuditLogEntry")
public class WorkSpaceFilterAuditLogEntry extends AuditLogEntryAdmin {

	private AbstractWorkSpaceFilterMto resource;

	private AbstractWorkSpaceFilterMto resourceUpdated;

	protected WorkSpaceFilterAuditLogEntry() {
		super();
	}

	public WorkSpaceFilterAuditLogEntry(Account authUser, String domainUuid, LogAction action, AuditLogEntryType type,
			AbstractWorkSpaceFilterMto workSpaceFilter) {
		super(authUser, domainUuid, action, type, workSpaceFilter.getUuid());
		this.resource = workSpaceFilter;
	}

	public AbstractWorkSpaceFilterMto getResource() {
		return resource;
	}

	public void setResource(AbstractWorkSpaceFilterMto resource) {
		this.resource = resource;
	}

	public AbstractWorkSpaceFilterMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(AbstractWorkSpaceFilterMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	@Override
	public String toString() {
		return "WorkSpaceFilterAuditLogEntry [resource=" + resource + ", resourceUpdated=" + resourceUpdated + "]";
	}
}
