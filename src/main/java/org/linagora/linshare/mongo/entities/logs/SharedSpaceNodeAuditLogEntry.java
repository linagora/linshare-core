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
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

@XmlRootElement
public class SharedSpaceNodeAuditLogEntry extends AuditLogEntryUser {

	protected SharedSpaceNode resource;

	private SharedSpaceNode resourceUpdated;

	public SharedSpaceNodeAuditLogEntry() {
		super();
	}

	public SharedSpaceNodeAuditLogEntry(Account authUser, Account actor, LogAction action, AuditLogEntryType type,
			SharedSpaceNode node) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, node.getUuid());
		this.resource = node;
		// bind nested workgroups traces with drive trace
		if (node.getParentUuid() != null && node.getNodeType().equals(NodeType.WORK_GROUP)) {
			addRelatedResources(node.getParentUuid());
		}
	}

	public SharedSpaceNode getResource() {
		return resource;
	}

	public void setResource(SharedSpaceNode resource) {
		this.resource = resource;
	}

	public SharedSpaceNode getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(SharedSpaceNode resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}
}
