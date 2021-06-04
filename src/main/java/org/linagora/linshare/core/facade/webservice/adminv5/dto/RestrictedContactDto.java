/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
