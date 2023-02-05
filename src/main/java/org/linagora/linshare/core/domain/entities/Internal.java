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
package org.linagora.linshare.core.domain.entities;

import java.util.UUID;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

/**
 * Internal user.
 */
/**
 * @author fred
 *
 */
public class Internal extends User {

	/** Default constructor for hibernate. */
	public Internal() {
		super();
		lsUuid = UUID.randomUUID().toString();
		this.inconsistent = false;
	}

	/**
	 * Constructor.
	 * 
	 * @param firstName
	 *            first name.
	 * @param lastName
	 *            last name.
	 * @param mail
	 *            email.
	 * @param ldapUid
	 */
	public Internal(String firstName, String lastName, String mail,
			String ldapUid) {
		super(firstName, lastName, mail);
		this.ldapUid = ldapUid;
		this.lsUuid = UUID.randomUUID().toString();
		this.inconsistent = false;
	}

	public Internal(UserDto userDto) {
		super(userDto);
	}
	public Internal(GenericUserDto userDto) {
		this.lsUuid = userDto.getUuid();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.setMail(userDto.getMail());
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.INTERNAL;
	}

	@Override
	public String getAccountRepresentation() {
		return this.firstName + " " + this.lastName + "(" + lsUuid + ", "
				+ this.ldapUid + ")";
	}
}
