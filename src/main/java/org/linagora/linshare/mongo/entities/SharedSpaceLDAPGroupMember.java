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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public class SharedSpaceLDAPGroupMember extends SharedSpaceMember {

	protected String externalId;

	protected Date syncDate;

	public SharedSpaceLDAPGroupMember(SharedSpaceNodeNested node, LightSharedSpaceRole role, SharedSpaceAccount account,
			String externalId, Date syncDate) {
		super(node, role, account);
		this.externalId = externalId;
		this.syncDate = syncDate;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	@Override
	public String toString() {
		return "SharedSpaceLDAPGroupMember [externalId=" + externalId + ", syncDate=" + syncDate + ", uuid=" + uuid
				+ ", node=" + node + ", role=" + role + ", account=" + account + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + ", user=" + user + "]";
	}
}
