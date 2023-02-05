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

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;

public class TechnicalAccount extends User {

	public TechnicalAccount() {
		super();
	}

	public TechnicalAccount(TechnicalAccountDto dto) {
		super();
		this.lastName = dto.getName();
		this.lsUuid = dto.getUuid();
		this.setMail(dto.getMail());
		this.destroyed = 0;
		this.canUpload = false;
		this.canCreateGuest = false;
		this.enable = dto.isEnable();
		this.mailLocale = Language.ENGLISH;
		this.externalMailLocale = Language.ENGLISH;
		this.role = dto.getRole();
		this.password = dto.getPassword();
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.TECHNICAL_ACCOUNT;
	}

	@Override
	public String getAccountRepresentation() {
		return this.lastName + "(" + lsUuid + ")";
	}

	@Override
	public String toString() {
		return getAccountRepresentation();
	}
}
