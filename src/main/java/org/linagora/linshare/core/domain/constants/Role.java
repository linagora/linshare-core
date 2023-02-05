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
package org.linagora.linshare.core.domain.constants;

/**
 */
public enum Role {
	SIMPLE(0), ADMIN(1), SYSTEM(2), SUPERADMIN(3), DELEGATION(4), UPLOAD_REQUEST(6), SAFE(7), ANONYMOUS(8);

	private int value;

	private Role(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static Role fromInt(int value) {
		for (Role role : values()) {
			if (role.value == value) {
				return role;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing Role");
	}

	public static Role toDefaultRole(Role defaultRole, String wantedRole) {
		if (wantedRole == null) {
			return defaultRole;
		}
		Role found = null;
		for (Role role : Role.values()) {
			if (wantedRole.toUpperCase().equals(role.toString())) {
				found = role;
			}
		}
		if (found != null) {
			return found;
		}
		return defaultRole;
	}
}
