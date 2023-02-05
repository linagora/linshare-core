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
package org.linagora.linshare.core.service.impl.twake.client;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = TwakeUser.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeUser", description = "A Twake user")
public class TwakeUser {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String _id;
		private String email;
		private String name;
		private String surname;
		private Boolean isVerified;
		private Boolean isBlocked;
		private List<TwakeRole> roles;
		private String createdAt;
		private String updatedAt;

		public Builder _id(String _id) {
			this._id = _id;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder surname(String surname) {
			this.surname = surname;
			return this;
		}

		public Builder isVerified(Boolean isVerified) {
			this.isVerified = isVerified;
			return this;
		}

		public Builder isBlocked(Boolean isBlocked) {
			this.isBlocked = isBlocked;
			return this;
		}

		public Builder roles(List<TwakeRole> roles) {
			this.roles = roles;
			return this;
		}

		public Builder createdAt(String createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder updatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		public TwakeUser build() {
			Validate.notBlank(_id, "id must be set.");
			Validate.notBlank(email, "email must be set.");
			Validate.notBlank(name, "name must be set.");
			Validate.notBlank(surname, "surname must be set.");
			return new TwakeUser(_id, email, name, surname, isVerified, isBlocked, roles, createdAt, updatedAt);
		}
	}

	private final String _id;
	private final String email;
	private final String name;
	private final String surname;
	private final Boolean isVerified;
	private final Boolean isBlocked;
	private final List<TwakeRole> roles;
	private final String createdAt;
	private final String updatedAt;

	private TwakeUser(String _id, String email, String name, String surname, Boolean isVerified, Boolean isBlocked, List<TwakeRole> roles, String createdAt, String updatedAt) {
		this._id = _id;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.isVerified = isVerified;
		this.isBlocked = isBlocked;
		this.roles = roles;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return _id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public Boolean getVerified() {
		return isVerified;
	}

	public Boolean getBlocked() {
		return isBlocked;
	}

	public List<TwakeRole> getRoles() {
		return roles;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("_id", _id)
			.add("email", email)
			.add("name", name)
			.add("surname", surname)
			.add("isVerified", isVerified)
			.add("isBlocked", isBlocked)
			.add("roles", roles)
			.add("createdAt", createdAt)
			.add("updatedAt", updatedAt)
			.toString();
	}
}
