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

import org.linagora.linshare.core.domain.entities.Account;

public enum AccountType {

	// 0 Account
	// 1 USER
	// 
	INTERNAL(2), GUEST(3), TECHNICAL_ACCOUNT(4), THREAD(5), ROOT(6), SYSTEM(7);

	private int value;

	private AccountType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static AccountType fromInt(int value) {
		for (AccountType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing AccountType");
	}

	/*
	 * check whether an account is ROOT or SYSTEM
	 */
	public boolean hasAllRights(Account a) {
		return a.getAccountType().value >= ROOT.value;
	}

	public boolean isUser() {
		return this.equals(AccountType.GUEST)
				|| this.equals(AccountType.INTERNAL)
				|| this.equals(AccountType.ROOT);
	}
}
