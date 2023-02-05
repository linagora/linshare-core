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
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.ThreadMemberMto;

@Deprecated
@XmlRootElement
public class ThreadMemberAuditLogEntry extends AuditLogEntryUser {

	protected WorkGroupLightDto workGroup;

	protected ThreadMemberMto resource;

	private ThreadMemberMto resourceUpdated;

	public ThreadMemberAuditLogEntry() {
		super();
	}

	public ThreadMemberAuditLogEntry(Account authUser, Account owner, LogAction action, AuditLogEntryType type,
			WorkgroupMember member) {
		super(new AccountMto(authUser), new AccountMto(owner), action, type, member.getUser().getLsUuid());
		this.resource = new ThreadMemberMto(member);
		this.workGroup = new WorkGroupLightDto(member.getThread());
	}

	public ThreadMemberMto getResource() {
		return resource;
	}

	public void setResource(ThreadMemberMto resource) {
		this.resource = resource;
	}

	public ThreadMemberMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(ThreadMemberMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	public WorkGroupLightDto getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroupLightDto workGroup) {
		this.workGroup = workGroup;
	}
}