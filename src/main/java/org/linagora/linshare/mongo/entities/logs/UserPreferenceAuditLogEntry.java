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
import org.linagora.linshare.mongo.entities.UserPreference;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
@XmlRootElement
public class UserPreferenceAuditLogEntry extends AuditLogEntryUser {

	protected UserPreference resource;

	private UserPreference resourceUpdated;

	public UserPreferenceAuditLogEntry() {
		super();
	}

	public UserPreferenceAuditLogEntry(Account authUser, Account actor, LogAction action, AuditLogEntryType type, UserPreference resource) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, resource.getUuid());
		this.resource = resource;
	}

	public UserPreference getResource() {
		return resource;
	}

	public void setResource(UserPreference resource) {
		this.resource = resource;
	}

	public UserPreference getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(UserPreference resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
