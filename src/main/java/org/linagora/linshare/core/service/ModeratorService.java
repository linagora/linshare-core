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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;

import javax.annotation.Nonnull;

public interface ModeratorService {

	Moderator create(Account authUser, Account actor, Moderator moderator, boolean onGuestCreation);

	Moderator find(Account authUser, Account actor, String uuid);

	/**
	 * Returns the Moderator instance for the given actor and guest.
	 *
	 * @param authUser The authenticated user.
	 * @param actor The actor account.
	 * @param guest The guest account, must not be null.
	 * @return An Optional containing the Moderator if found, or an empty Optional otherwise.
	 */
	Optional<Moderator> findByActorAndGuest(final Account authUser, final Account actor, final @Nonnull Guest guest);

	Moderator update(Account authUser, Account actor, Moderator moderator, ModeratorDto dto);

	Moderator delete(Account authUser, Account actor, Moderator moderator);

	List<Moderator> deleteAllModerators(Account authUser, Account actor, List<Moderator> moderators, Guest guest);

	List<Moderator> findAllByGuest(Account authUser, Account actor, String guestUuid, ModeratorRole role, String pattern);
}
