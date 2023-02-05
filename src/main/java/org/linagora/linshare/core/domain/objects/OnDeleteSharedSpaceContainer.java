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
package org.linagora.linshare.core.domain.objects;

import java.util.List;

import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;

import com.google.common.collect.Lists;

public class OnDeleteSharedSpaceContainer {

	private List<AuditLogEntryUser> logs = Lists.newArrayList();

	private List<MailContainerWithRecipient> mailContainers = Lists
			.newArrayList();

	public OnDeleteSharedSpaceContainer() {
		super();
	}

	public List<AuditLogEntryUser> getLogs() {
		return logs;
	}

	public void setLogs(List<AuditLogEntryUser> logs) {
		this.logs = logs;
	}

	public List<MailContainerWithRecipient> getMailContainers() {
		return mailContainers;
	}

	public void setMailContainers(List<MailContainerWithRecipient> mailContainers) {
		this.mailContainers = mailContainers;
	}

	public void addLog(SharedSpaceMemberAuditLogEntry log) {
		this.logs.add(log);
	}

	public void addMailContainersAddEmail(MailContainerWithRecipient mailContainer) {
		this.mailContainers.add(mailContainer);
	}
}
