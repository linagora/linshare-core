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
package org.linagora.linshare.core.repository;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;


public interface GuestRepository extends UserRepository<Guest> {

	/**
	 * Find outdated guest account identifiers.
	 * 
	 * @return a list of outdated guest identifiers
	 */
	List<String> findOutdatedGuestIdentifiers();

	List<String> findAllGuests();

	List<String> findGuestsAboutToExpire(int nbDaysBeforeExpiration);

	 /** Search some guests.
	  * anyWhere matching
	 * @param mail user mail.
	 * @param firstName user first name.
	 * @param lastName user last name.
	 * @return List<Guest> a list of matching users.
	 */
	List<Guest> searchGuestAnyWhere(String mail, String firstName, String lastName);
	List<Guest> searchGuestAnyWhere(String firstName, String lastName);
	List<Guest> searchGuestAnyWhere(String pattern);

	void evict(Guest entity);

	Guest findByDomainAndMail(AbstractDomain domain, String mail);
	Guest findByMail(final String mail);

	List<String> findAllWithDeprecatedPasswordEncoding();

	List<String> findAllGuestsUuids();

	List<Guest> findAll(Account moderator, Optional<ModeratorRole> moderatorRole,
			Optional<String> pattern);
	List<Guest> findAll(List<AbstractDomain> domains,
			Optional<String> pattern);
	List<Guest> findAll(List<AbstractDomain> domains, Account moderator, Optional<ModeratorRole> moderatorRole,
			Optional<String> pattern);
}
