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

import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPGroupProviderDto;

public abstract class GroupProvider extends Provider {

	protected AbstractDomain domain;

	protected GroupProviderType type;

	public GroupProvider() {
		super();
	}

	public GroupProvider(AbstractDomain domain) {
		this.domain = domain;
		this.creationDate = new Date();
		this.modificationDate = new Date();
	}

	public GroupProviderType getType() {
		return type;
	}

	public void setType(GroupProviderType type) {
		this.type = type;
	}

	public abstract LDAPGroupProviderDto toLDAPGroupProviderDto();

	public Boolean getSearchInOtherDomains() {
		return false;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "GroupProvider [Type=" + type + ", uuid=" + uuid + "]";
	}

}
