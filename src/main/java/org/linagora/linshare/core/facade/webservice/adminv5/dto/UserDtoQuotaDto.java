/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="Quota")
@Schema(name = "Quota", description = "A quota instance for accounts.")
public class UserDtoQuotaDto {

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "The value of the quota limit")
	private Long quota;

	@Schema(description = "The value of yesterday's used space (Read only)")
	private Long yersterdayUsedSpace;

	@Schema(description = "The used space. Read only.")
	protected Long usedSpace;

	@Schema(description = "Return real time quota value.")
	private Long realTimeUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	private Boolean maintenance;

	@Schema(description = "Quota creation date. (Read only).")
	private Date creationDate;

	@Schema(description = "Quota last modification date (Read only).")
	private Date modificationDate;

	@Schema(description = "The maximum file size allowed.")
	private Long maxFileSize;

	@Schema(description = "The account to which the quota relies.")
	private AccountDto account;

	protected UserDtoQuotaDto() {
		super();
	}

	public UserDtoQuotaDto(AccountQuota quota) {
		this.uuid = quota.getUuid();
		this.quota = quota.getQuota();
		this.usedSpace = quota.getCurrentValue();
		this.yersterdayUsedSpace = quota.getLastValue();
		this.maintenance = quota.getMaintenance();
		this.maxFileSize = quota.getMaxFileSize();
		this.account = new AccountDto(quota.getAccount(), true);
		this.creationDate = quota.getCreationDate();
		this.modificationDate = quota.getModificationDate();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Long getYersterdayUsedSpace() {
		return yersterdayUsedSpace;
	}

	public void setYersterdayUsedSpace(Long yersterdayUsedSpace) {
		this.yersterdayUsedSpace = yersterdayUsedSpace;
	}

	public Long getRealTimeUsedSpace() {
		return realTimeUsedSpace;
	}

	public void setRealTimeUsedSpace(Long realTimeUsedSpace) {
		this.realTimeUsedSpace = realTimeUsedSpace;
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Boolean getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(Boolean maintenance) {
		this.maintenance = maintenance;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}
}
