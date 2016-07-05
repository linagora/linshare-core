/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.ThreadRoles;
import org.linagora.linshare.core.domain.entities.ThreadMember;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ThreadMember")
@ApiModel(value = "ThreadMember", description = "A thread member")
public class WorkGroupMemberDto {

    @ApiModelProperty(value = "Admin")
	private boolean admin;

    @ApiModelProperty(value = "Readonly")
	private boolean readonly;

    @ApiModelProperty(value = "Role")
	private String role;

    @ApiModelProperty(value = "FirstName")
	private String firstName;

    @ApiModelProperty(value = "LastName")
	private String lastName;

    @ApiModelProperty(value = "UserUuid")
	private String userUuid;

    @ApiModelProperty(value = "UserMail")
	private String userMail;

    @ApiModelProperty(value = "UserDomainId")
	private String userDomainId;

    @ApiModelProperty(value = "ThreadUuid")
	private String threadUuid;

	public WorkGroupMemberDto(ThreadMember member) {
		this.firstName = member.getUser().getFirstName();
		this.lastName = member.getUser().getLastName();
		this.admin = member.getAdmin();
		this.readonly = !member.getCanUpload();
		this.role = (admin ? ThreadRoles.ADMIN
				: readonly ? ThreadRoles.RESTRICTED : ThreadRoles.NORMAL)
				.name().toLowerCase();
		this.userUuid = member.getUser().getLsUuid();
		this.threadUuid = member.getThread().getLsUuid();
		this.userMail = member.getUser().getMail();
		this.userDomainId = member.getUser().getDomainId();
	}

	public WorkGroupMemberDto() {
		super();
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getThreadUuid() {
		return threadUuid;
	}

	public void setThreadUuid(String threadUuid) {
		this.threadUuid = threadUuid;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserDomainId() {
		return userDomainId;
	}

	public void setUserDomainId(String userDomainId) {
		this.userDomainId = userDomainId;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
