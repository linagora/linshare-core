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
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.mongo.entities.mto.DomainPatternMto;
@XmlRootElement
public class DomainPatternAuditLogEntry extends AuditLogEntryAdmin {

	private DomainPatternMto resource;

	private DomainPatternMto resourceUpdated;

	public DomainPatternAuditLogEntry() {
	}

	public DomainPatternAuditLogEntry(Account authUser, String domainUuid, LogAction action, AuditLogEntryType type,
			UserLdapPattern pattern) {
		super(authUser, domainUuid, action, type, pattern.getUuid());
		this.resource = new DomainPatternMto(pattern, false);
	}

	public DomainPatternMto getPatternMto() {
		return resource;
	}

	public void setPatternMto(DomainPatternMto patternMto) {
		this.resource = patternMto;
	}

	public DomainPatternMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(DomainPatternMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
