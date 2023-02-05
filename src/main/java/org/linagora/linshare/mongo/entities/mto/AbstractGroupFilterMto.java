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

import org.linagora.linshare.core.domain.constants.GroupFilterType;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;

public abstract class AbstractGroupFilterMto {

	protected String uuid;

	protected String label;

	protected String description;

	protected GroupFilterType type;

	protected AbstractGroupFilterMto() {
		super();
	}

	protected AbstractGroupFilterMto(GroupLdapPattern groupFilter) {
		this.uuid = groupFilter.getUuid();
		this.label = groupFilter.getLabel();
		this.description = groupFilter.getDescription();
		this.type = groupFilter.getType();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public GroupFilterType getType() {
		return type;
	}

	public void setType(GroupFilterType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "AbstractGroupFilterMto [uuid=" + uuid + ", label=" + label + ", type=" + type + "]";
	}
}
