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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

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
