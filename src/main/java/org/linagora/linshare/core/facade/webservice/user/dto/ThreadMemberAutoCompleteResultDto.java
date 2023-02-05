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

import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.core.domain.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ThreadMemberAutoCompleteResult", description = "This DTO will contains members or autocomplete user")
@XmlRootElement(name = "ThreadMemberAutoCompleteResult")
public class ThreadMemberAutoCompleteResultDto extends AutoCompleteResultDto {

	@Schema(description = "user uuid")
	protected String userUuid;

	@Schema(description = "thread uuid")
	protected String threadUuid;

	@Schema(description = "firstName")
	protected String firstName;

	@Schema(description = "lastName")
	protected String lastName;

	@Schema(description = "domain")
	protected String domain;

	@Schema(description = "mail")
	protected String mail;

	@Schema(description = "true it is a thread member")
	protected boolean isMember;

	public ThreadMemberAutoCompleteResultDto() {
	}

	public ThreadMemberAutoCompleteResultDto(User user) {
		super(user.getLsUuid(), user.getMail());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomainId();
		this.mail = user.getMail();
		this.isMember = false;
		this.userUuid = user.getLsUuid();
	}

	public ThreadMemberAutoCompleteResultDto(WorkgroupMember member) {
		super(member.getUser().getLsUuid(), member.getUser().getMail());
		this.firstName = member.getUser().getFirstName();
		this.lastName = member.getUser().getLastName();
		this.domain = member.getUser().getDomainId();
		this.mail = member.getUser().getMail();
		this.isMember = true;
		this.userUuid = member.getUser().getLsUuid();
		this.threadUuid = member.getThread().getLsUuid();
	}

	public ThreadMemberAutoCompleteResultDto(SharedSpaceMember member, User user) {
		super(user.getLsUuid(), user.getMail());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.domain = user.getDomainId();
		this.mail = user.getMail();
		this.isMember = true;
		this.userUuid = user.getLsUuid();
		this.threadUuid = member.getNode().getUuid();
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
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
}
