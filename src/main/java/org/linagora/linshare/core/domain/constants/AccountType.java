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

public enum AccountType {

	// 0 Account
	// 1 USER
	// 
	INTERNAL(2), GUEST(3), TECHNICALACCOUNT(4), THREAD(5), ROOT(6), SYSTEM(7);

	private int value;

	private AccountType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static AccountType fromInt(int value) {
        switch (value) {
            case 2: return AccountType.INTERNAL;
            case 3: return AccountType.GUEST;
            case 4: return AccountType.TECHNICALACCOUNT;
            case 5: return AccountType.THREAD;
            case 6: return AccountType.ROOT;
            case 7: return AccountType.SYSTEM;
            default : throw new IllegalArgumentException("Doesn't match an existing AccountType");
        }
	}
}
