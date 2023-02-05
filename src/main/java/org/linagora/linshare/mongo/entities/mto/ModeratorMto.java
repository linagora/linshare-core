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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Moderator;

import com.google.common.base.MoreObjects;

public class ModeratorMto {

	private String uuid;

	private ModeratorRole moderatorRole;

	private AccountMto account;

	private AccountMto guest;

	private Date creationDate;

	private Date modificationDate;

	public ModeratorMto() {
	}

	public ModeratorMto(Moderator moderator) {
		this.uuid = moderator.getUuid();
		this.moderatorRole = moderator.getRole();
		this.account = new AccountMto(moderator.getAccount());
		this.guest = new AccountMto(moderator.getGuest());
		this.creationDate = moderator.getCreationDate();
		this.modificationDate = moderator.getModificationDate();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ModeratorRole getModeratorRole() {
		return moderatorRole;
	}

	public void setModeratorRole(ModeratorRole moderatorRole) {
		this.moderatorRole = moderatorRole;
	}

	public AccountMto getAccount() {
		return account;
	}

	public void setAccount(AccountMto account) {
		this.account = account;
	}

	public AccountMto getGuest() {
		return guest;
	}

	public void setGuest(AccountMto guest) {
		this.guest = guest;
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("uuid", uuid)
				.add("moderatorRole", moderatorRole)
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.add("account", account)
				.add("guest", guest)
				.toString();
	}
}
