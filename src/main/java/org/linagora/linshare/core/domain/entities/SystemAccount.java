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
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;

public class SystemAccount extends Account {

	public SystemAccount() {
		super();
		role = Role.SYSTEM;
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.SYSTEM;
	}

	@Override
	public String getAccountRepresentation() {
		return this.lsUuid;
	}

	@Override
	public String getFullName() {
		return this.lsUuid;
	}

	@Override
	public ContainerQuotaType getContainerQuotaType() {
		return null;
	}

	@Override
	public String toString() {
		return "SystemAccount [id=" + id + ", lsUuid=" + lsUuid + ", role=" + role + ", account type=" + getAccountType() + "]";
	}

}
