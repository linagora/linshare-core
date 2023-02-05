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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AllowedContact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RestrictedContact")
@Schema(name = "RestrictedContactV5", description = "A RestrictedContact must be a user.")
public class RestrictedContactDto {

	@Schema(description = "AllowedContact's uuid")
	private String uuid;

	@Schema(description = "AllowedContact's firstName is optional")
	private String firstName;

	@Schema(description = "AllowedContact's lastName is optional")
	private String lastName;

	@Schema(description = "AllowedContact's mail")
	private String mail;

	@Schema(description = "AllowedContact's domain")
	private DomainLightDto domain;

	protected RestrictedContactDto() {
		super();
	}

	protected RestrictedContactDto(String uuid, String firstName, String lastName, String mail, DomainLightDto domain) {
		super();
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.domain = domain;
	}

	public RestrictedContactDto(AllowedContact allowedContact) {
		this.uuid = allowedContact.getContact().getLsUuid();
		this.firstName = allowedContact.getContact().getFirstName();
		this.lastName = allowedContact.getContact().getLastName();
		this.mail = allowedContact.getContact().getMail();
		this.domain = new DomainLightDto(allowedContact.getContact().getDomain());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	/*
	 * Transformers
	 */
	public static Function<AllowedContact, RestrictedContactDto> toDto() {
		return new Function<AllowedContact, RestrictedContactDto>() {
			@Override
			public RestrictedContactDto apply(AllowedContact arg0) {
				return new RestrictedContactDto(arg0);
			}
		};
	}
}
