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
package org.linagora.linshare.core.facade.webservice.test.user.guest.dto;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonDeserialize(builder = GuestPasswordDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "GuestPassword", description = "Guest password")
public class GuestPasswordDto {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {

		private String uuid;
		private String password;

		public Builder uuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public GuestPasswordDto build() {
			Validate.notBlank(uuid, "'uuid' must be set.");
			Validate.notBlank(password, "'password' must be set.");
			return new GuestPasswordDto(uuid, password);
		}
	}

	@Schema(description = "Guest's uuid")
	private final String uuid;

	@Schema(description = "Guest's password", required = true)
	private final String password;

	private GuestPasswordDto(String uuid, String password) {
		this.uuid = uuid;
		this.password = password;
	}

	public String getUuid() {
		return uuid;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("uuid", uuid)
			.add("password", password)
			.toString();
	}
}
