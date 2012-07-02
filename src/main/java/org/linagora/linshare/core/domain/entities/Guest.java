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

/** Guest is a user that is not registered in LDAP server.
 */
public class Guest extends User {


	/** Default constructor for hibernate. */
    @SuppressWarnings("unused")
	private Guest() {
        super();
    }

	public Guest(String firstName, String lastName, String mail, String password, Boolean canUpload, String comment) {
        super(firstName, lastName, mail);
        this.canUpload = canUpload;
        this.password = password;
        this.comment = comment;
        this.restricted = false;
    }
	
	public Guest(String firstName, String lastName, String mail) {
        super(firstName, lastName, mail);
        this.restricted = false;
    }

	@Override
	public AccountType getAccountType() {
		return AccountType.GUEST;
	}
    
}
