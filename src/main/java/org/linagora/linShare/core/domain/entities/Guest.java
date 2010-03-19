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
package org.linagora.linShare.core.domain.entities;

import java.util.Date;

/** Guest is a user that is not registered in LDAP server.
 */
public class Guest extends User {

    /** The account expiry date. */
    private Date expiryDate;

	/** The password of the user to connect to the application. */
	private String password;

	/** The comment about the user */
	private String comment;
	
    /** The user who has created this guest. */
    private User owner;
    
    private boolean restricted;

    /** Default constructor. */
    protected Guest() {
        super();
        this.password = null;
        this.restricted = false;
    }

    /** Constructor.
     * @param login login.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail email.
     * @param password password.
     * @param canUpload : true if the user can upload file
     */
	public Guest(String login, String firstName, String lastName, String mail, String password, Boolean canUpload, Boolean canCreateGuest, String comment) {
        super(login, firstName, lastName, mail, canUpload, canCreateGuest);
        this.password = password;
        this.comment = comment;
        this.restricted = false;
    }

    @Override
    public UserType getUserType() {
        return UserType.GUEST;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isRestricted() {
		return restricted;
	}
    
    
}
