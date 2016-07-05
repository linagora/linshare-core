/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ThreadMemberAutoCompleteResult", description = "This DTO will contains members or autocomplete user")
@XmlRootElement(name = "ThreadMemberAutoCompleteResult")
public class ThreadMemberAutoCompleteResultDto extends AutoCompleteResultDto {

	@ApiModelProperty(value = "user uuid")
	protected String userUuid;

	@ApiModelProperty(value = "thread uuid")
	protected String threadUuid;

	@ApiModelProperty(value = "firstName")
	protected String firstName;

	@ApiModelProperty(value = "lastName")
	protected String lastName;

	@ApiModelProperty(value = "domain")
	protected String domain;

	@ApiModelProperty(value = "mail")
	protected String mail;

	@ApiModelProperty(value = "true it is a thread member")
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

	public ThreadMemberAutoCompleteResultDto(ThreadMember member) {
		super(member.getUser().getLsUuid(), member.getUser().getMail());
		this.firstName = member.getUser().getFirstName();
		this.lastName = member.getUser().getLastName();
		this.domain = member.getUser().getDomainId();
		this.mail = member.getUser().getMail();
		this.isMember = true;
		this.userUuid = member.getUser().getLsUuid();
		this.threadUuid = member.getThread().getLsUuid();
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
