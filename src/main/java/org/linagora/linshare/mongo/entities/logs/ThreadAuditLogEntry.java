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
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.ThreadMto;

@Deprecated
public class ThreadAuditLogEntry extends AuditLogEntryUser {

	protected ThreadMto resource;

	private ThreadMto resourceUpdated;

	public ThreadAuditLogEntry() {
	}

	public ThreadAuditLogEntry(Account authUser, Account owner, LogAction action, AuditLogEntryType type, ThreadMto thread) {
		super(new AccountMto(authUser), new AccountMto(owner), action, type, thread.getUuid());
		this.resource = thread;
	}

	public ThreadMto getResource() {
		return resource;
	}

	public void setResource(ThreadMto thread) {
		this.resource = thread;
	}

	public ThreadMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(ThreadMto threadUpdated) {
		this.resourceUpdated = threadUpdated;
	}
}
