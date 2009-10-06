/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.repository;

import java.util.List;

import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.User;


public interface GuestRepository extends UserRepository<Guest> {
	 /** Search some guests.
	  * (start matching)
     * @param mail user mail.
     * @param firstName user first name.
     * @param lastName user last name.
     * @param ownerLogin login of the user who creates the searched guest(s).
     * @return a list of matching users.
     */
    List<Guest> searchGuest(String mail, String firstName, String lastName, User owner);
    
    /** Find outdated guest accounts.
     * @return a list of outdated guests (null if no one found).
     */
    List<Guest> findOutdatedGuests();

    
	 /** Search some guests.
	  * anyWhere matching
     * @param mail user mail.
     * @param firstName user first name.
     * @param lastName user last name.
     * @param ownerLogin login of the user who creates the searched guest(s).
     * @return a list of matching users.
     */
    List<Guest> searchGuestAnyWhere(String mail, String firstName, String lastName, String ownerLogin);
    
    
}
