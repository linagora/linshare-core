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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonDeserialize(builder = AuthorDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Author")
@Schema(name = "Author", description = "Author light DTO")
public class AuthorDto {

	public static Builder builder() {
		return new Builder();
	}

	public static AuthorDto from(User author) {
		return builder()
			.uuid(author.getLsUuid())
			.firstName(author.getFirstName())
			.lastName(author.getLastName())
			.mail(author.getMail())
			.build();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {

		protected String uuid;
		protected String firstName;
		protected String lastName;
		protected String mail;

		public Builder uuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder mail(String mail) {
			this.mail = mail;
			return this;
		}

		public AuthorDto build() {
			Validate.notBlank(uuid, "'uuid' must be set.");
			Validate.notBlank(firstName, "'firstName' must be set.");
			Validate.notBlank(lastName, "'lastName' must be set.");
			Validate.notBlank(mail, "'mail' must be set.");
			return new AuthorDto(uuid, firstName, lastName, mail);
		}
	}

	@Schema(description = "Author's uuid", required = true)
	private final String uuid;

	@Schema(description = "Author's first name", required = true)
	private final String firstName;

	@Schema(description = "Author's last name", required = true)
	private final String lastName;

	@Schema(description = "Author's mail", required = true)
	private final String mail;


	private AuthorDto(String uuid, String firstName, String lastName, String mail) {
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}

	public String getUuid() {
		return uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMail() {
		return mail;
	}
}
