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
package org.linagora.linshare.core.domain.constants;

public enum DomainAccessRuleType {

	ALLOW_ALL(0), DENY_ALL(1), ALLOW(2), DENY(3);

	private int value;

	private DomainAccessRuleType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static DomainAccessRuleType fromInt(int value) {
        switch (value) {
            case 0: return DomainAccessRuleType.ALLOW_ALL;
            case 1: return DomainAccessRuleType.DENY_ALL;
            case 2: return DomainAccessRuleType.ALLOW;
            case 3: return DomainAccessRuleType.DENY;
            default : throw new IllegalArgumentException("Doesn't match an existing DomainAccessRuleType");
        }
	}
}
