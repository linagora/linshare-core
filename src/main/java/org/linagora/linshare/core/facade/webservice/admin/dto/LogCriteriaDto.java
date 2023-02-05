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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class LogCriteriaDto extends org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto{

	@Schema(description = "List of actor's mail")
	private List<String> actorMails; // The selected user

	@Schema(description = "First name of the actor")
	private String actorFirstName;

	@Schema(description = "Last name of the actor")
	private String actorLastName;

	@Schema(description = "Domain of the actor")
	private String actorDomain;

	public LogCriteriaDto() {
	}

	public LogCriteriaDto(List<String> actorMails, String actorFirstName,
			String actorLastName, String actorDomain) {
		this.actorMails = actorMails;
		this.actorFirstName = actorFirstName;
		this.actorLastName = actorLastName;
		this.actorDomain = actorDomain;
	}

	public List<String> getActorMails() {
		return actorMails;
	}

	public void setActorMails(List<String> mails) {
		this.actorMails = mails;
	}

	public String getActorFirstName() {
		return actorFirstName;
	}

	public void setActorFirstName(String firstname) {
		this.actorFirstName = firstname;
	}

	public String getActorLastName() {
		return actorLastName;
	}

	public void setActorLastName(String lastname) {
		this.actorLastName = lastname;
	}

	public String getActorDomain() {
		return actorDomain;
	}

	public void setActorDomain(String actorDomain) {
		this.actorDomain = actorDomain;
	}
}
