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
package org.linagora.linshare.mongo.entities.light;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

/**
 * Object used to contains minimal required information about
 * {@link SharedSpaceRole}
 */
public class LightSharedSpaceRole extends GenericLightEntity {

	protected NodeType type;

	public LightSharedSpaceRole() {
		super();
	}

	public LightSharedSpaceRole(SharedSpaceRole role) {
		super(role);
		this.type = role.getType();
	}

	public LightSharedSpaceRole(String uuid, String name, NodeType type) {
		super(uuid, name);
		this.type = type;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LightSharedSpaceRole [type=" + type + ", uuid=" + uuid + ", name=" + name + "]";
	}

}
