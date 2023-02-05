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

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

public class GenericLightEntity {

	protected String uuid;
	protected String name;

	public GenericLightEntity() {
		super();
	}

	public GenericLightEntity(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
	}

	public GenericLightEntity(SharedSpaceRole role) {
		super();
		this.uuid = role.getUuid();
		this.name = role.getName();
	}

	public GenericLightEntity(GenericLightEntity entity) {
		super();
		this.uuid = entity.getUuid();
		this.name = entity.getName();
	}

	public GenericLightEntity(AbstractDomain domain) {
		super();
		this.uuid = domain.getUuid();
		this.name = domain.getLabel();
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
		return "GenericLightEntity [uuid=" + uuid + ", name=" + name + "]";
	}

}
