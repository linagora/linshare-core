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
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

@XmlRootElement
public class SharedSpaceMemberAuditLogEntry extends AuditLogEntryUser {

	protected SharedSpaceMember resource;

	private SharedSpaceMember resourceUpdated;

	// WorkAround
	// we have to duplicate workGroup information
	// to avoid side effect on back and front-end
	protected WorkGroupLightDto workGroup;

	public SharedSpaceMemberAuditLogEntry() {
		super();
	}

	public SharedSpaceMemberAuditLogEntry(Account authUser, Account actor, LogAction action, AuditLogEntryType type,
			SharedSpaceMember member) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, member.getUuid());
		member.setUser(member.getAccount());
		this.workGroup = new WorkGroupLightDto(member.getNode());
		addRelatedResources(member.getNode().getUuid());
		this.resource = member;
		// Related accounts or related domains are provided by an external method named businessService.addMembersToRelatedAccountsAndRelatedDomains()
		// Unfortunately it should been done by the constructor, but it was not done like that :'(
	}

	public SharedSpaceMember getResource() {
		return resource;
	}

	public void setResource(SharedSpaceMember resource) {
		this.resource = resource;
	}

	public SharedSpaceMember getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(SharedSpaceMember resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	public WorkGroupLightDto getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroupLightDto workGroup) {
		this.workGroup = workGroup;
	}

}
