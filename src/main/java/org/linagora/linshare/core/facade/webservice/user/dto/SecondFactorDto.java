/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2020-2021 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
