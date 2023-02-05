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

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"externalId", "prefix", "syncDate", "domain"})
public class SharedSpaceLDAPGroup extends SharedSpaceNode {

	protected String externalId;

	protected String prefix;

	protected Date syncDate;

	protected GenericLightEntity domain;

	public SharedSpaceLDAPGroup() {
		super();
	}

	public SharedSpaceLDAPGroup(String externalId, String prefix, Date syncDate, GenericLightEntity domain) {
		super();
		this.externalId = externalId;
		this.prefix = prefix;
		this.syncDate = syncDate;
		this.domain = domain;
	}

	public SharedSpaceLDAPGroup(String name, String parentUuid, NodeType nodeType, String externalId, String prefix,
			Date syncDate, GenericLightEntity domain) {
		super(name, parentUuid, nodeType);
		this.externalId = externalId;
		this.prefix = prefix;
		this.syncDate = syncDate;
		this.domain = domain;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	@Override
	public String toString() {
		return "SharedSpaceLDAPGroup [externalId=" + externalId + ", prefix=" + prefix + ", syncDate=" + syncDate
				+ ", domain=" + domain + ", uuid=" + uuid + ", name=" + name + ", parentUuid=" + parentUuid
				+ ", nodeType=" + nodeType + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate
				+ "]";
	}

}
