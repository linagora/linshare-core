/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.AllowedContact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonDeserialize(builder = RestrictedContactDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RestrictedContact")
@Schema(name = "RestrictedContact", description = "Restricted Contact")
public class RestrictedContactDto {

	public static Builder builder() {
		return new Builder();
	}

	public static RestrictedContactDto from(AllowedContact allowedContact) {
		return builder()
			.uuid(allowedContact.getContact().getLsUuid())
			.firstName(allowedContact.getContact().getFirstName())
			.lastName(allowedContact.getContact().getLastName())
			.mail(allowedContact.getContact().getMail())
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

		public RestrictedContactDto build() {
			Validate.notBlank(uuid, "'uuid' must be set.");
			Validate.notBlank(firstName, "'firstName' must be set.");
			Validate.notBlank(lastName, "'lastName' must be set.");
			Validate.notBlank(mail, "'mail' must be set.");
			return new RestrictedContactDto(uuid, firstName, lastName, mail);
		}
	}

	@Schema(description = "RestrictedContact's uuid", required = true)
	private final String uuid;

	@Schema(description = "RestrictedContact's first name", required = true)
	private final String firstName;

	@Schema(description = "RestrictedContact's last name", required = true)
	private final String lastName;

	@Schema(description = "RestrictedContact's mail", required = true)
	private final String mail;

	private RestrictedContactDto(String uuid, String firstName, String lastName, String mail) {
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("uuid", uuid)
			.add("firstame", firstName)
			.add("lastName", lastName)
			.add("mail", mail)
			.toString();
	}
}
