/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.domain.constants;

public enum FunctionalityType {

	DEFAULT(0),
	INTEGER(1),
	STRING(2),
	UNIT(3),
	UNIT_SIZE(4),
	UNIT_TIME(5),
	UNIT_BOOLEAN(6),
	UNIT_BOOLEAN_TIME(7),
	RANGE_UNIT(8);

	private int value;

	private FunctionalityType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static FunctionalityType fromInt(int value) {
        switch (value) {
            case 0: return FunctionalityType.DEFAULT;
            case 1: return FunctionalityType.INTEGER;
            case 2: return FunctionalityType.STRING;
            case 3: return FunctionalityType.UNIT;
            case 4: return FunctionalityType.UNIT_SIZE;
            case 5: return FunctionalityType.UNIT_TIME;
            case 6: return FunctionalityType.UNIT_BOOLEAN;
            case 7: return FunctionalityType.UNIT_BOOLEAN_TIME;
            case 8: return FunctionalityType.RANGE_UNIT;
            default : throw new IllegalArgumentException("Doesn't match an existing FunctionalityType");
        }
	}
}
