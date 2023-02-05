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
package org.linagora.linshare.auth.exceptions;

public enum LinShareAuthenticationExceptionCode {

	DEFAULT_AUTHENTICATION_ERROR(1000),
	BAD_CREDENTIAL(1001),
	TOTP_BAD_FORMAT(1002),
	TOTP_BAD_VALUE(1003),
	ACCOUNT_LOCKED(1004),
	JWT_BAD_FORMAT(1005),
	DOMAIN_NOT_FOUND(1006),
	MISSING_CLAIM_VALUES(1007),
	ACCESS_NOT_GRANTED(1008),
	MULTIPLE_DOMAIN_NOT_FOUND(1009);

	private int value;

	private LinShareAuthenticationExceptionCode(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

}
