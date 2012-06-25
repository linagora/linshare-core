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
package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.GuestRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class GuestRepositoryImpl extends GenericUserRepositoryImpl<Guest>  implements GuestRepository {

	
	public GuestRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Guest user) {
		DetachedCriteria det = DetachedCriteria.forClass(Guest.class).add(Restrictions.eq("lsUid", user.getLsUid()));
		return det;
	}

	/** Search some guests.
	 * If given agument is null, it's not considered.
	 * @param mail user mail.
	 * @param firstName user first name.
	 * @param lastName user last name.
	 * @param ownerLogin login of the user who creates the searched guest(s).
	 * @return a list of matching users.
	 */
	public List<Guest> searchGuest(String mail, String firstName, String lastName, User owner) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
		if (mail != null) {
			criteria.add(Restrictions.like("mail", mail, MatchMode.START).ignoreCase());
		}
		if (firstName != null) {
			criteria.add(Restrictions.like("firstName", firstName, MatchMode.START).ignoreCase());
		}
		if (lastName != null) {
			criteria.add(Restrictions.like("lastName", lastName, MatchMode.START).ignoreCase());
		}
		if (owner != null) {
			criteria.add(Restrictions.eq("owner", owner));
		}
		return findByCriteria(criteria);
	}


    /** Find outdated guest accounts.
     * @return a list of outdated guests (null if no one found).
     */
    public List<Guest> findOutdatedGuests() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
        criteria.add(Restrictions.lt("expirationDate", new Date()));
        return findByCriteria(criteria);
    }

	/**
	 * @see GuestRepository#searchGuestAnyWhere(String, String, String, String)
	 */
	public List<Guest> searchGuestAnyWhere(String mail, String firstName, String lastName) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Guest.class);
		if (mail != null) {
			criteria.add(Restrictions.like("mail", mail, MatchMode.ANYWHERE).ignoreCase());
		}
		if (firstName != null) {
			criteria.add(Restrictions.like("firstName", firstName, MatchMode.ANYWHERE).ignoreCase());
		}
		if (lastName != null) {
			criteria.add(Restrictions.like("lastName", lastName, MatchMode.ANYWHERE).ignoreCase());
		}
		return findByCriteria(criteria);
	}

}
