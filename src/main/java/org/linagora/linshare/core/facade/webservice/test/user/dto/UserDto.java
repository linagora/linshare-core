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
package org.linagora.linshare.core.facade.webservice.test.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserDto {

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "FirstName")
	private String firstName;

	@Schema(description = "LastName")
	private String lastName;

	@Schema(description = "Mail", required = true)
	private String mail;

	@Schema(description = "DomainUuid")
	private String domainUuid;

	@Schema(description = "DomainName")
	private String domainName;

	@Schema(description = "canCreateGuest")
	private Boolean canCreateGuest;

	@Schema(description = "personalSpaceEnabled")
	private Boolean personalSpaceEnabled;

	public UserDto(String uuid, String firstName, String lastName, String mail, String domainUuid, String domainName,
			Boolean canCreateGuest, Boolean personalSpaceEnabled) {
		super();
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.domainUuid = domainUuid;
		this.domainName = domainName;
		this.canCreateGuest = canCreateGuest;
		this.personalSpaceEnabled = personalSpaceEnabled;
	}

	public UserDto() {
		super();
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

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Boolean isCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	public Boolean isPersonalSpaceEnabled() {
		return personalSpaceEnabled;
	}

	public void setPersonalSpaceEnabled(Boolean personalSpaceEnabled) {
		this.personalSpaceEnabled = personalSpaceEnabled;
	}

	@Override
	public String toString() {
		return "UserDto [uuid=" + uuid + ", firstName=" + firstName + ", lastName=" + lastName + ", mail=" + mail
				+ ", domainUuid=" + domainUuid + ", domainName=" + domainName + ", canCreateGuest=" + canCreateGuest
				+ ", personalSpaceEnabled=" + personalSpaceEnabled + "]";
	}

}
