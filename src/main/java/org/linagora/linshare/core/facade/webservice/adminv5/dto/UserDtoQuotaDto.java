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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AccountQuota;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="Quota")
@Schema(name = "UserV5Quota", description = "A quota instance for accounts.")
public class UserDtoQuotaDto {

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "The value of the current account quota limit")
	private Long quota;

	@Schema(description = "The default quota value allowed for each account on his personal space. (Read only)")
	private Long defaultQuota;
	
	@Schema(description = "If true, allow to override quota field for the current account"
			+ "Otherwise the quota value can/will be updated by cascade by the defaultQuota value."
			+ "Useless for root domain quota")
	private Boolean quotaOverride;

	@Schema(description = "The value of yesterday's used space (Read only)")
	private Long yesterdayUsedSpace;

	@Schema(description = "The used space. (Read only)")
	protected Long usedSpace;

	@Schema(description = "Real time used space of account quota. (Read only)")
	private Long realTimeUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	private Boolean maintenance;

	@Schema(description = "Quota creation date. (Read only).")
	private Date creationDate;

	@Schema(description = "Quota last modification date (Read only).")
	private Date modificationDate;

	@Schema(description = "The maximum file size allowed.")
	private Long maxFileSize;

	@Schema(description = "The default maximum file size allowed. (Read only)")
	private Long defaultMaxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	private Boolean maxFileSizeOverride;

	@Schema(description = "The account to which the quota belongs. (Read only)")
	private AccountLightDto account;

	protected UserDtoQuotaDto() {
		super();
	}

	public UserDtoQuotaDto(AccountQuota quota, Long realTimeUsedSpace) {
		super();
		this.uuid = quota.getUuid();
		this.quota = quota.getQuota();
		this.defaultQuota = quota.getContainerQuota().getDefaultAccountQuota();
		this.quotaOverride = quota.getQuotaOverride();
		this.yesterdayUsedSpace = quota.getLastValue();
		this.usedSpace = quota.getCurrentValue();
		this.realTimeUsedSpace = realTimeUsedSpace;
		this.maintenance = quota.getMaintenance();
		this.creationDate = quota.getCreationDate();
		this.modificationDate = quota.getModificationDate();
		this.maxFileSize = quota.getMaxFileSize();
		this.defaultMaxFileSize = quota.getContainerQuota().getDefaultMaxFileSize();
		this.maxFileSizeOverride = quota.getMaxFileSizeOverride();
		this.account = new AccountLightDto(quota.getAccount());
	}

	public AccountQuota toObject() {
		AccountQuota quota = new AccountQuota();
		quota.setUuid(getUuid());
		quota.setQuota(getQuota());
		quota.setQuotaOverride(isQuotaOverride());
		quota.setMaxFileSize(getMaxFileSize());
		quota.setMaxFileSizeOverride(isMaxFileSizeOverride());
		return quota;
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

	public Long getYesterdayUsedSpace() {
		return yesterdayUsedSpace;
	}

	public void setYesterdayUsedSpace(Long yesterdayUsedSpace) {
		this.yesterdayUsedSpace = yesterdayUsedSpace;
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

	public Long getDefaultMaxFileSize() {
		return defaultMaxFileSize;
	}

	public void setDefaultMaxFileSize(Long defaultMaxFileSize) {
		this.defaultMaxFileSize = defaultMaxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Long getDefaultQuota() {
		return defaultQuota;
	}

	public void setDefaultQuota(Long defaultQuota) {
		this.defaultQuota = defaultQuota;
	}

	public Boolean isQuotaOverride() {
		return quotaOverride;
	}

	public void setQuotaOverride(Boolean quotaOverride) {
		this.quotaOverride = quotaOverride;
	}

	public Boolean isMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public void setMaxFileSizeOverride(Boolean maxFileSizeOverride) {
		this.maxFileSizeOverride = maxFileSizeOverride;
	}

	public AccountLightDto getAccount() {
		return account;
	}

	public void setAccount(AccountLightDto account) {
		this.account = account;
	}
}
