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

import java.util.Date;

import org.linagora.linshare.core.domain.constants.ModeratorRole;

public class Moderator {

	private long id;

	private String uuid;

	private ModeratorRole role;

	private Date creationDate;

	private Date modificationDate;

	private Account account;

	private Guest guest;

	public Moderator() {
		super();
	}

	public Moderator(ModeratorRole role, Account account, Guest guest) {
		this.role = role;
		this.account = account;
		this.guest = guest;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ModeratorRole getRole() {
		return role;
	}

	public void setRole(ModeratorRole role) {
		this.role = role;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	@Override
	public String toString() {
		return "Moderator [uuid=" + uuid + ", role=" + role + ", creationDate=" + creationDate + ", modificationDate="
				+ modificationDate + ", account=" + account + ", guest=" + guest + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((guest == null) ? 0 : guest.hashCode());
		result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Moderator other = (Moderator) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		return true;
	}

	public boolean isAdminModerator() {
		return ModeratorRole.ADMIN.equals(this.getRole());
	}

	public boolean isSimpleModerator() {
		return ModeratorRole.SIMPLE.equals(this.getRole());
	}
}
