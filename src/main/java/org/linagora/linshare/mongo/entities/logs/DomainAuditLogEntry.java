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
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.mto.DomainMto;
@XmlRootElement
public class DomainAuditLogEntry extends AuditLogEntryAdmin {

	private DomainMto resource;

	private DomainMto resourceUpdated;

	public DomainAuditLogEntry() {
	}

	public DomainAuditLogEntry(Account authUser, LogAction action, AuditLogEntryType type, AbstractDomain domain) {
		super(authUser, domain.getUuid(), action, type, domain.getUuid());
		this.setDomain(new DomainMto(domain));
	}

	public DomainMto getDomain() {
		return resource;
	}

	public void setDomain(DomainMto domain) {
		this.resource = domain;
	}

	public DomainMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(DomainMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
