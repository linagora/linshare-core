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
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

public class RootDomain extends AbstractDomain {

	public RootDomain() {
	}

	public RootDomain(String label) {
		super(label);
		this.defaultRole=Role.SUPERADMIN;
		this.defaultTapestryLocale=SupportedLanguage.ENGLISH;
	}

	@Deprecated
	public RootDomain(DomainDto domainDto) {
		super(domainDto, null);
		this.defaultRole=Role.SUPERADMIN;
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.ROOTDOMAIN;
	}

	@Override
	public boolean isRootDomain() {
		return true;
	}

	@Override
	public void updateDomainWith(AbstractDomain d) {
		this.label = d.getLabel();
		this.description = d.getDescription();
		this.defaultTapestryLocale = d.getDefaultTapestryLocale();
		this.externalMailLocale = d.getExternalMailLocale();
		this.authShowOrder = d.getAuthShowOrder();
		this.defaultRole = Role.SUPERADMIN;
	}

}
