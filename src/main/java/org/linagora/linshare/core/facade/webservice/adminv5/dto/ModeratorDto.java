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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = ModeratorDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Moderator", description = "A guest moderator.")
public class ModeratorDto {

	public static ModeratorDto from(Moderator moderator) {
		return builder()
			.uuid(moderator.getUuid())
			.role(moderator.getRole())
			.creationDate(moderator.getCreationDate())
			.modificationDate(moderator.getModificationDate())
			.account(new AccountLightDto(moderator.getAccount()))
			.guest(new AccountLightDto(moderator.getGuest()))
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String uuid;
		private ModeratorRole role;
		private Date creationDate; 
		private Date modificationDate; 
		private AccountLightDto account; 
		private AccountLightDto guest;

		public Builder uuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder role(ModeratorRole role) {
			this.role = role;
			return this;
		}

		public Builder creationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder modificationDate(Date modificationDate) {
			this.modificationDate = modificationDate;
			return this;
		}

		public Builder account(AccountLightDto account) {
			this.account = account;
			return this;
		}

		public Builder guest(AccountLightDto guest) {
			this.guest = guest;
			return this;
		}

		public ModeratorDto build() {
			return new ModeratorDto(uuid, role, creationDate, modificationDate, account, guest);
		}
	}

	@Schema(description = "Unique identifier of the resource")
	private String uuid;

	@Schema(description = "The role of guest's moderator.", required = true)
	private ModeratorRole role;

	@Schema(description = "creation date of this resource")
	private Date creationDate;

	@Schema(description = "modification date of this resource")
	private Date modificationDate;

	@Schema(description = "The guest moderator's account.", required = true)
	private AccountLightDto account;

	@Schema(description = "The guest to whose the moderator is.", required = true)
	private AccountLightDto guest;

	public ModeratorDto() {
	}

	public ModeratorDto(String uuid, ModeratorRole role, Date creationDate, Date modificationDate,
			AccountLightDto account, AccountLightDto guest) {
		this.uuid = uuid;
		this.role = role;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.account = account;
		this.guest = guest;
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

	public AccountLightDto getAccount() {
		return account;
	}

	public void setAccount(AccountLightDto account) {
		this.account = account;
	}

	public AccountLightDto getGuest() {
		return guest;
	}

	public void setGuest(AccountLightDto guest) {
		this.guest = guest;
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

	public Moderator toModeratorObject (ModeratorDto dto, Account account, Guest guest) {
		Moderator moderator = new Moderator();
		moderator.setRole(dto.getRole());
		moderator.setAccount(account);
		moderator.setGuest(guest);
		return moderator;
	}
}
