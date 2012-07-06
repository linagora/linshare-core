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


public class Contact {
	
	private long persistenceId;
	private String mail;

	// for hibernate
	protected Contact() {
		this.mail = null;
	}

	public Contact(String mail) {
		this.mail = mail;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Contact)) {
			return false;
		}
		Contact other = (Contact) obj;
		return (this.mail.equals(other.mail));
	}

	@Override
	public int hashCode() {
		return mail.hashCode();
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "Contact is : " + this.mail;
	}

	public long getPersistenceId() {
		return persistenceId;
	}
	
}
