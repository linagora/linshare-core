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

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

public class GuestDomain extends AbstractDomain {

	public GuestDomain() {
	}

	public GuestDomain(String label) {
		super(label);
	}

	public GuestDomain(String name, AbstractDomain parent) {
		super(name, parent);
	}

	@Deprecated
	public GuestDomain(DomainDto domainDto, AbstractDomain parent) {
		super(domainDto, parent);
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.GUESTDOMAIN;
	}

	@Override
	public boolean isGuestDomain() {
		return true;
	}

	@Override
	public boolean isSubDomain() {
		return true;
	}

	@Override
	public void updateDomainWith(AbstractDomain d) {
		super.updateDomainWith(d);
		this.defaultRole = Role.SIMPLE;
	}
}
