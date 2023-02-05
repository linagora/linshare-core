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

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;

public abstract class UserProvider extends Provider {

	protected AbstractDomain domain;

	protected UserProviderType userProviderType;

	public UserProviderType getUserProviderType() {
		return userProviderType;
	}

	public UserProvider() {
		super();
	}
	public UserProvider(AbstractDomain domain) {
		super();
		this.domain = domain;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.uuid = UUID.randomUUID().toString();
	}


	@Override
	public String toString() {
		return "UserProvider [Type=" + userProviderType + ", uuid=" + uuid + "]";
	}

	/**
	 * alias
	 * @return UserProviderType
	 */
	public UserProviderType getType() {
		return userProviderType;
	}

	protected void setUserProviderType(UserProviderType userProviderType) {
		this.userProviderType = userProviderType;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	/**
	 * Do not use it any more. deprecated.
	 * @return
	 */
	@Deprecated
	public abstract LDAPUserProviderDto toLDAPUserProviderDto();

}
