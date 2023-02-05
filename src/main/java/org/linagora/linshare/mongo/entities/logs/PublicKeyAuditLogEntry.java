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

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.PublicKeyLs;
import org.linagora.linshare.mongo.entities.mto.PublicKeyLsMto;

public class PublicKeyAuditLogEntry extends AuditLogEntryAdmin {

	protected PublicKeyLsMto resource;

	public PublicKeyAuditLogEntry() {
		super();
	}

	public PublicKeyAuditLogEntry(Account authUser, LogAction action, AuditLogEntryType type, String resourceUuid,
			PublicKeyLs publicKey) {
		super(authUser, publicKey.getDomainUuid(), action, type, resourceUuid);
		this.resource = new PublicKeyLsMto(publicKey);
	}

	public PublicKeyLsMto getResource() {
		return resource;
	}

	public void setResource(PublicKeyLsMto resource) {
		this.resource = resource;
	}
}
