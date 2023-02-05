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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class WorkGroupLightNode {

	protected String uuid;

	protected String name;

	public WorkGroupLightNode() {
		super();
	}

	public WorkGroupLightNode(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
	}

	public WorkGroupLightNode(WorkGroupNode node) {
		super();
		this.uuid = node.getUuid();
		this.name = node.getName();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "WorkGroupLightNode [uuid=" + uuid + ", name=" + name + "]";
	}

}
