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

public enum UserType {

	// 0 Account
	// 1 USER
	// 
	INTERNAL(2), GUEST(3), TECHNICALACCOUNT(4), THREAD(5), ROOT(6);

	private int value;

	private UserType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static UserType fromInt(int value) {
        switch (value) {
            case 2: return UserType.INTERNAL;
            case 3: return UserType.GUEST;
            case 4: return UserType.TECHNICALACCOUNT;
            case 5: return UserType.THREAD;
            case 6: return UserType.ROOT;
            default : throw new IllegalArgumentException("Doesn't match an existing UserType");
        }
	}
}
