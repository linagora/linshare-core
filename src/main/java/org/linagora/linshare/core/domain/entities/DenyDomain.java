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

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;

public class DenyDomain extends DomainAccessRule {

	private AbstractDomain domain;

	/*
	 * A default constructor is needed for hibernate for loading entities, but
	 * you can not persist this entity without setting up a domain. That is why
	 * this contructor is private.
	 */
	protected DenyDomain() {
		super();
	}

	public DenyDomain(AbstractDomain domain) {
		super();
		this.domain = domain;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	@Override
	public DomainAccessRuleType getDomainAccessRuleType() {
		return DomainAccessRuleType.DENY;
	}

	@Override
	public String toString() {
		return "DenyDomain [domain=" + domain + "]";
	}

}
