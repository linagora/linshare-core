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

public enum FunctionalityType {

	DEFAULT(0),
	INTEGER(1),
	STRING(2),
	UNIT(3),
	UNIT_SIZE(4),
	UNIT_TIME(5),
	BOOLEAN(9),
	ENUM_LANG(10);

	private int value;

	private FunctionalityType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static FunctionalityType fromInt(int value) {
		for (FunctionalityType type : values()) {
			if (type.value == value) {
				return type;
			}
        }
		throw new IllegalArgumentException("Doesn't match an existing FunctionalityType");
	}
}
