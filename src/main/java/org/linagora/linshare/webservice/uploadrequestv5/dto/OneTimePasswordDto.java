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
package org.linagora.linshare.webservice.uploadrequestv5.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author fmartin
 *
 */
public class OneTimePasswordDto {

	@Schema(description = "requestUrlUuid", required = true)
	protected String requestUrlUuid;

	@Schema(description = "entryUuid", required = true)
	protected String entryUuid;

	@JsonIgnore
	protected String password;

	@Schema(description = "OTP Password")
	protected String otpPassword;

	@Schema(description = "creation date")
	protected Date creationDate;

	public OneTimePasswordDto() {
		super();
	}

	public OneTimePasswordDto(String requestUrlUuid, String entryUuid, String password, String otpPassword) {
		super();
		this.requestUrlUuid = requestUrlUuid;
		this.entryUuid = entryUuid;
		this.password = password;
		this.otpPassword = otpPassword;
		this.creationDate = new Date();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRequestUrlUuid() {
		return requestUrlUuid;
	}

	public void setRequestUrlUuid(String requestUrlUuid) {
		this.requestUrlUuid = requestUrlUuid;
	}

	public String getEntryUuid() {
		return entryUuid;
	}

	public void setEntryUuid(String entryUuid) {
		this.entryUuid = entryUuid;
	}

	public String getOtpPassword() {
		return otpPassword;
	}

	public void setOtpPassword(String otpPassword) {
		this.otpPassword = otpPassword;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "OneTimePasswordDto [requestUrlUuid=" + requestUrlUuid + ", entryUuid=" + entryUuid + ", password="
				+ password + ", otpPassword=" + otpPassword + ", creationDate=" + creationDate + "]";
	}
}
