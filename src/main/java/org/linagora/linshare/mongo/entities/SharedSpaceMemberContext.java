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

public class SharedSpaceMemberContext {

	protected SharedSpaceRole role;

	protected SharedSpaceRole nestedRole;

	public SharedSpaceMemberContext() {
		super();
	}

	public SharedSpaceMemberContext(SharedSpaceRole role) {
		super();
		this.role = role;
	}

	public SharedSpaceMemberContext(SharedSpaceRole role, SharedSpaceRole nestedRole) {
		super();
		this.role = role;
		this.nestedRole = nestedRole;
	}

	public SharedSpaceRole getRole() {
		return role;
	}

	public void setRole(SharedSpaceRole role) {
		this.role = role;
	}

	public SharedSpaceRole getNestedRole() {
		return nestedRole;
	}

	public void setNestedRole(SharedSpaceRole nestedRole) {
		this.nestedRole = nestedRole;
	}

}
