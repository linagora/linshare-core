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

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;

public class AccountPermission {

	private long id;

	private TechnicalAccountPermissionType permission;

	public AccountPermission() {
		super();
	}

	public AccountPermission(TechnicalAccountPermissionType perm) {
		super();
		this.permission = perm;
	}

	public AccountPermission(String perm) {
		super();
		this.permission = TechnicalAccountPermissionType.valueOf(perm);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TechnicalAccountPermissionType getPermission() {
		return permission;
	}

	public void setPermission(TechnicalAccountPermissionType permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "AccountPermission [permission=" + permission + "]";
	}
}
