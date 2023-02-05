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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

@XmlRootElement(name = "AuditLogEntryAdmin")
@XmlTransient
public abstract class AuditLogEntryAdmin extends AuditLogEntry {

	protected String targetDomainUuid;

	public AuditLogEntryAdmin() {
		super();
	}

	public AuditLogEntryAdmin(Account authUser, String targetDomainUuid, LogAction action, AuditLogEntryType type, String resourceUuid) {
		super();
		this.authUser = new AccountMto(authUser);
		this.targetDomainUuid = targetDomainUuid;
		this.type = type;
		this.action = action;
		this.creationDate = new Date();
		this.resourceUuid = resourceUuid;
		this.addRelatedDomains(targetDomainUuid, authUser.getDomainId());
	}

	public String getTargetDomainUuid() {
		return targetDomainUuid;
	}

	public void setTargetDomainUuid(String targetDomainUuid) {
		this.targetDomainUuid = targetDomainUuid;
	}
}
