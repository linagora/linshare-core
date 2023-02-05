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

import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPWorkSpaceProviderDto;

import com.google.common.base.MoreObjects;

public abstract class WorkSpaceProvider extends Provider {

	protected AbstractDomain domain;

	protected WorkSpaceProviderType type;

	public WorkSpaceProvider() {
		super();
	}

	public WorkSpaceProvider(AbstractDomain domain) {
		this.domain = domain;
	}

	public WorkSpaceProviderType getType() {
		return type;
	}

	public void setType(WorkSpaceProviderType type) {
		this.type = type;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("Type", type)
				.add("uuid", uuid)
				.toString();
	}

	public abstract LDAPWorkSpaceProviderDto toLDAPWorkSpaceProviderDto();

	public Boolean getSearchInOtherDomains() {
		return false;
	}
}
