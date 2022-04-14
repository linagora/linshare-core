/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Moderator;

import com.google.common.base.MoreObjects;

public class ModeratorMto {

	private String uuid;

	private ModeratorRole role;

	private AccountMto account;

	private AccountMto guest;

	private Date creationDate;

	private Date modificationDate;

	public ModeratorMto() {
	}

	public ModeratorMto(Moderator moderator) {
		this.uuid = moderator.getUuid();
		this.role = moderator.getRole();
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

	public ModeratorRole getRole() {
		return role;
	}

	public void setRole(ModeratorRole role) {
		this.role = role;
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
				.add("role", role)
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.add("account", account)
				.add("guest", guest)
				.toString();
	}
}
