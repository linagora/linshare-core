/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
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
