/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
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
