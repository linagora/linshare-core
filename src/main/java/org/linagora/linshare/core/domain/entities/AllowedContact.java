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
package org.linagora.linshare.core.domain.entities;

/**
 * An allowed contact for a restricted guest. This object represents
 * the pair (guest, allowed contact)
 * 
 * @author sduprey
 *
 */
public class AllowedContact {

	private User contact;
	private User owner;
	private Long persistenceId;

	/** constructor for hibernate **/
	protected AllowedContact() {
	};

	public AllowedContact(User owner, User contact) {
		this.contact = contact;
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof AllowedContact) {
			return (o == this || (((AllowedContact) o).getOwner().equals(this.getOwner()) && ((AllowedContact) o)
							.getContact().equals(this.getContact())));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getOwner().hashCode() + this.getContact().hashCode();
	}

	public void setContact(User contact) {
		this.contact = contact;
	}

	public User getContact() {
		return contact;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

}
