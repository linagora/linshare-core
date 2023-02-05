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

public enum GroupMemberType {

	OWNER(0), MANAGER(1), MEMBER(2), WAITING_APPROVAL(3);

	private int value;

	private GroupMemberType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static GroupMemberType fromInt(int value) {
		for (GroupMemberType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
        throw new IllegalArgumentException("Doesn't match an existing GroupMemberType");
	}
}
