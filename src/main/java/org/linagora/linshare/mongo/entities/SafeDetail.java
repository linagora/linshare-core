/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Mehdi Attia
 *
 */
@XmlRootElement(name = "SafeDetail")
@Document(collection = "safe_detail")
public class SafeDetail {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "AccountUuid")
	protected String accountUuid;

	// FIXME: workaround
//	@JsonIgnore
	@Schema(description = "ContainerUuid")
	protected String containerUuid;

	@Schema(description = "Country Code, on 2 characters")
	protected String countryCode;

	@Schema(description = "ControlKey, on 2 characters")
	protected String controlKey;

	@Schema(description = "Role, on 4 characters fixed on \"SAFE\"")
	protected String role;

	@Schema(description = "IUFSC describes the provider of the safe detail, on 8 characters")
	protected String iufsc;

	@Schema(description = "Reserve for futur extension, on 8 characters")
	protected String reserve;

	@Schema(description = "Number identifier, on 30 characters")
	protected String uuid;

	@Schema(description = "CreationDate of this safeDetail")
	protected Date creationDate;

	@Schema(description = "Description")
	protected String description;

	public SafeDetail(String accountUuid, String containerUuid, String description) {
		super();
		this.accountUuid = accountUuid;
		this.containerUuid = containerUuid;
		this.description = description;
		this.creationDate = new Date();
		this.uuid = RandomStringUtils.randomAlphanumeric(30);
		this.reserve = "00000000";
		this.role = "SAFE";
	}

	public SafeDetail(SafeDetail safeDetail) {
		super();
		this.accountUuid = safeDetail.getAccountUuid();
		this.containerUuid = safeDetail.getContainerUuid();
		this.description = safeDetail.getDescription();
		this.creationDate = new Date();
		this.uuid = RandomStringUtils.randomAlphanumeric(30);
		this.reserve = "00000000";
		this.role = "SAFE";
		this.countryCode = safeDetail.getCountryCode();
		this.controlKey = safeDetail.getControlKey();
		this.iufsc = safeDetail.getIufsc();
	}

	public SafeDetail() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public String getContainerUuid() {
		return containerUuid;
	}

	public void setContainerUuid(String containerUuid) {
		this.containerUuid = containerUuid;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getControlKey() {
		return controlKey;
	}

	public void setControlKey(String controlKey) {
		this.controlKey = controlKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getIufsc() {
		return iufsc;
	}

	public void setIufsc(String iufsc) {
		this.iufsc = iufsc;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSafeDetailRic() {
		return new StringBuilder().append(countryCode).append(controlKey).append(role).append(iufsc).append(reserve)
				.append(uuid).toString();
	}

	@Override
	public String toString() {
		return "SafeDetail [id=" + id + ", accountUuid=" + accountUuid + ", containerUuid=" + containerUuid
				+ ", countryCode=" + countryCode + ", controlKey=" + controlKey + ", role=" + role + ", iufsc=" + iufsc
				+ ", reserve=" + reserve + ", uuid=" + uuid + ", creationDate=" + creationDate + ", description="
				+ description + "]";
	}
}
