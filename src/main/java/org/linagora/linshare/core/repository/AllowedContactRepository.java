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
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;

/**
 * Some guest can only see a restricted list of users : the guest allowed contacts.
 * 
 * @author sduprey
 *
 */
public interface AllowedContactRepository extends AbstractRepository<AllowedContact> {
	/**
	 * Find the allowed contact of some user
	 * @param owner the user
	 * @return
	 */
	List<AllowedContact> findByOwner(final User owner);
	/**
	 * Search the contacts of a guest by mail or name or firstName
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @param guest
	 * @return
	 */
	List<AllowedContact> searchContact(final String mail, final String firstName,
			final String lastName, final Guest guest);
	/**
	 * Delete all the AllowedContact pairs where user can be both a contact or
	 * an owner
	 * @param user
	 */
	void deleteAllByUserBothSides(final User user);
}
