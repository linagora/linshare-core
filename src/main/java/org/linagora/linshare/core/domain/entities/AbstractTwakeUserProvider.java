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

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;

import com.google.common.base.MoreObjects;

public class AbstractTwakeUserProvider extends UserProvider {

	private TwakeConnection twakeConnection;

	private String twakeCompanyId;

	public AbstractTwakeUserProvider() {
		super();
	}

	public AbstractTwakeUserProvider(AbstractDomain domain, TwakeConnection twakeConnection, String twakeCompanyId) {
		super(domain);
		this.twakeConnection = twakeConnection;
		this.twakeCompanyId = twakeCompanyId;
	}

	public TwakeConnection getTwakeConnection() {
		return twakeConnection;
	}

	public void setTwakeConnection(TwakeConnection twakeConnection) {
		this.twakeConnection = twakeConnection;
	}

	public String getTwakeCompanyId() {
		return twakeCompanyId;
	}

	public void setTwakeCompanyId(String twakeCompanyId) {
		this.twakeCompanyId = twakeCompanyId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("domain", domain)
			.add("twakeConnection", twakeConnection)
			.add("twakeCompanyId", twakeCompanyId)
			.toString();
	}


	@Deprecated
	@Override
	public LDAPUserProviderDto toLDAPUserProviderDto() {
		// it is not used anymore, only kept for admin/v4 support.
		return null;
	}
}
