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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name = "SecondFactor",
		description = "This object will contain Second Factor Authenitcation  (2FA) current state."
		)
@XmlRootElement(name = "SecondFactor")
public class SecondFactorDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "Creation date of the shared key aka the secret secret")
	protected Date creationDate;

	@Schema(description = "Uuid")
	protected Boolean enabled;

	@Schema(description = "Weither or not the current user must enable 2FA by creating shared key")
	protected Boolean required;

	@Schema(description = "Weither or not the current user can remove its own shared key")
	protected Boolean canDeleteIt;

	@Schema(description = "Shared key aka shared secret")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String sharedKey;

	public SecondFactorDto() {
		super();
	}

	public SecondFactorDto(String uuid, Date creationDate, Boolean enabled) {
		super();
		this.uuid = uuid;
		this.creationDate = creationDate;
		this.enabled = enabled;
		this.required = false;
		this.canDeleteIt = false;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getCanDeleteIt() {
		return canDeleteIt;
	}

	public void setCanDeleteIt(Boolean canDeleteIt) {
		this.canDeleteIt = canDeleteIt;
	}

	public String getSharedKey() {
		return sharedKey;
	}

	public void setSharedKey(String sharedKey) {
		this.sharedKey = sharedKey;
	}
}
