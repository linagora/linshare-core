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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.AccountType;

/** Internal user.
 */
public class Internal extends User {

    /** Default constructor for hibernate. */
    @SuppressWarnings("unused")
	private Internal() {
        super();
    }

    /** Constructor.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail email.
     * @param ldapUid TODO
     */
    public Internal(String firstName, String lastName, String mail, String ldapUid) {
        super(firstName, lastName, mail);
        this.ldapUid = ldapUid;
    }

	@Override
	public AccountType getAccountType() {
		return AccountType.INTERNAL;
	}

}
