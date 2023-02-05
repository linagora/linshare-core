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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.stream.Stream;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.service.impl.twake.client.TwakeUser;
import org.linagora.linshare.core.service.impl.twake.client.TwakeUsersResponse;

public class TwakeUserProviderServiceImpl extends AbstractTwakeUserProviderServiceImpl {

	@Override
	protected boolean isValid(AbstractDomain domain) {
		return !domain.isGuestDomain();
	}

	@Override
	protected Stream<TwakeUser> filterValidUser(TwakeUsersResponse twakeUsersResponse) throws IOException {
		return twakeUsersResponse
			.getList()
			.stream()
			.filter(user -> !user.getBlocked())
			.filter(user -> !isGuest(user));
	}

	private boolean isGuest(TwakeUser user) {
		return user.getRoles()
			.stream()
			.filter(role -> role.getRoleCode().equals(GUEST_ROLE))
			.findFirst()
			.isPresent();
	}
}
