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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SharedSpaceWorkgroupMemberAutoCompleteResultDto", description = "This DTO will contains members")
@XmlRootElement(name = "SharedSpaceWorkgroupMemberAutoCompleteResultDto")
public class WorkgroupMemberAutoCompleteResultDto extends AutoCompleteResultDto {

	@Schema(description = "account uuid")
	protected String accountUuid;

	@Schema(description = "firstName")
	protected String firstName;

	@Schema(description = "lastName")
	protected String lastName;

	@Schema(description = "mail")
	protected String mail;

	@Schema(description = "shared space uuid")
	protected String sharedSpaceUuid;

	@Schema(description = "shared space member uuid (can be null)")
	protected String sharedSpaceMemberUuid;

	public WorkgroupMemberAutoCompleteResultDto() {
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
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

	public String getSharedSpaceUuid() {
		return sharedSpaceUuid;
	}

	public void setSharedSpaceUuid(String sharedSpaceUuid) {
		this.sharedSpaceUuid = sharedSpaceUuid;
	}

	public String getSharedSpaceMemberUuid() {
		return sharedSpaceMemberUuid;
	}

	public void setSharedSpaceMemberUuid(String sharedSpaceMemberUuid) {
		this.sharedSpaceMemberUuid = sharedSpaceMemberUuid;
	}

	@Override
	public String toString() {
		return "WorkgroupMemberAutoCompleteResultDto [firstName=" + firstName + ", lastName=" + lastName + ", mail="
				+ mail + ", accountUuid=" + accountUuid + ", sharedSpaceUuid=" + sharedSpaceUuid
				+ ", sharedSpaceMemberUuid=" + sharedSpaceMemberUuid + ", getIdentifier()=" + getIdentifier()
				+ ", getDisplay()=" + getDisplay() + "]";
	}

}
