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
package org.linagora.linshare.core.business.service;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;

public interface ModeratorBusinessService {

	Moderator create(Moderator moderator);

	Moderator find(String uuid);

	Moderator update(Moderator moderator);

	Moderator delete(Moderator moderator);

	List<Moderator> findAllByGuest(Guest guest, ModeratorRole role, String pattern);

	Optional<Moderator> findByGuestAndAccount(Account actor, Guest guest);

	void deleteAllModerators(Guest guest);

	List<String> findAllModeratorUuidsByGuest(Account guest);
}
