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

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainLightDto;

import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ContainerQuota", description = "A container quota instance for accounts like users in a domain.")
public class ContainerQuotaDto {

	@Schema(description = "Resource's uuid")
	protected String uuid;

	@Schema(description = "The domain which this quota belongs to.")
	protected DomainLightDto domain;

	@Schema(description = "The parent domain which this quota belongs to.")
	protected DomainLightDto parentDomain;

	@Schema(description = "The value of the current container quota limit")
	protected Long quota;

	@Schema(description = "If true, allow to override quota field for the current object (domain, container or account)"
			+ "Otherwise the quota value can/will be updated by cascade by the defaultQuota field of your parent."
			+ "Useless for root domain quota")
	protected Boolean quotaOverride;

	@Schema(description = "The default quota value allowed for each child quota.")
	protected Long defaultQuota;

	@Schema(description = "By default, defaultQuota equal quota, every modification of the quota field will be applied to defaultQuota too."
			+ "If true, the defaultQuota value is unlinked from quota field.")
	protected Boolean defaultQuotaOverride;

	// @Schema(description = "")
	// protected Long quotaWarning;

	@Schema(description = "The used space (Read only)")
	protected Long usedSpace;

	@Schema(description = "The value of yesterday's used space (Read only)")
	protected Long yersterdayUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	protected Boolean maintenance;

	@Schema(description = "Quota creation date (Read only)")
	protected Date creationDate;

	@Schema(description = "Quota last modification date (Read only)")
	protected Date modificationDate;

	@Schema(description = "Quota last modification date by a batch (Read only)")
	protected Date batchModificationDate;

	@Schema(description = "Type (ContainerQuotaType)")
	protected ContainerQuotaType type;

	@Schema(description = "The default maximum file size allowed.")
	protected Long defaultMaxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean defaultMaxFileSizeOverride;

	@Schema(description = "The default quota value for defaultAccountQuota inside sub containers.")
	protected Long defaultAccountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean defaultAccountQuotaOverride;

	@Schema(description = "The maximum file size accepted, default value sub containers")
	protected Long maxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean maxFileSizeOverride;

	@Schema(description = "The default quota value for an account created inside the current container.")
	protected Long accountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean accountQuotaOverride;

	public ContainerQuotaDto() {
	}

	public ContainerQuotaDto(ContainerQuota cq) {
		this.uuid = cq.getUuid();
		this.domain = new DomainLightDto(cq.getDomain());
		this.domain.setType(cq.getDomain().getDomainType());
		if (cq.getParentDomain() != null) {
			this.parentDomain = new DomainLightDto(cq.getParentDomain());
		}
		this.quota = cq.getQuota();
		this.quotaOverride = cq.getQuotaOverride();
		this.defaultQuota = cq.getDefaultQuota();
		this.defaultQuotaOverride = cq.getDefaultQuotaOverride();
//		this.quotaWarning = quota.getQuotaWarning();
		this.usedSpace = cq.getCurrentValue();
		this.yersterdayUsedSpace = cq.getLastValue();
		this.maintenance = cq.getMaintenance();
		this.creationDate = cq.getCreationDate();
		this.modificationDate = cq.getModificationDate();
		this.batchModificationDate = cq.getBatchModificationDate();
		this.type = cq.getContainerQuotaType();
		this.defaultMaxFileSize = cq.getDefaultMaxFileSize();
		this.defaultMaxFileSizeOverride = cq.getDefaultMaxFileSizeOverride();
		this.defaultAccountQuota = cq.getDefaultAccountQuota();
		this.defaultAccountQuotaOverride = cq.getDefaultAccountQuotaOverride();
		this.maxFileSize = cq.getMaxFileSize();
		this.maxFileSizeOverride = cq.getMaxFileSizeOverride();
		this.accountQuota = cq.getAccountQuota();
		this.accountQuotaOverride = cq.getAccountQuotaOverride();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public DomainLightDto getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(DomainLightDto parentDomain) {
		this.parentDomain = parentDomain;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Boolean getQuotaOverride() {
		return quotaOverride;
	}

	public void setQuotaOverride(Boolean quotaOverride) {
		this.quotaOverride = quotaOverride;
	}

	public Long getDefaultQuota() {
		return defaultQuota;
	}

	public void setDefaultQuota(Long defaultQuota) {
		this.defaultQuota = defaultQuota;
	}

	public Boolean getDefaultQuotaOverride() {
		return defaultQuotaOverride;
	}

	public void setDefaultQuotaOverride(Boolean defaultQuotaOverride) {
		this.defaultQuotaOverride = defaultQuotaOverride;
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Long getYersterdayUsedSpace() {
		return yersterdayUsedSpace;
	}

	public void setYersterdayUsedSpace(Long yersterdayUsedSpace) {
		this.yersterdayUsedSpace = yersterdayUsedSpace;
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

	public Date getBatchModificationDate() {
		return batchModificationDate;
	}

	public void setBatchModificationDate(Date batchModificationDate) {
		this.batchModificationDate = batchModificationDate;
	}

	public ContainerQuotaType getType() {
		return type;
	}

	public void setType(ContainerQuotaType type) {
		this.type = type;
	}

	public Long getDefaultMaxFileSize() {
		return defaultMaxFileSize;
	}

	public void setDefaultMaxFileSize(Long defaultMaxFileSize) {
		this.defaultMaxFileSize = defaultMaxFileSize;
	}

	public Boolean getDefaultMaxFileSizeOverride() {
		return defaultMaxFileSizeOverride;
	}

	public void setDefaultMaxFileSizeOverride(Boolean defaultMaxFileSizeOverride) {
		this.defaultMaxFileSizeOverride = defaultMaxFileSizeOverride;
	}

	public Long getDefaultAccountQuota() {
		return defaultAccountQuota;
	}

	public void setDefaultAccountQuota(Long defaultAccountQuota) {
		this.defaultAccountQuota = defaultAccountQuota;
	}

	public Boolean getDefaultAccountQuotaOverride() {
		return defaultAccountQuotaOverride;
	}

	public void setDefaultAccountQuotaOverride(Boolean defaultAccountQuotaOverride) {
		this.defaultAccountQuotaOverride = defaultAccountQuotaOverride;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Boolean getMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public void setMaxFileSizeOverride(Boolean maxFileSizeOverride) {
		this.maxFileSizeOverride = maxFileSizeOverride;
	}

	public Long getAccountQuota() {
		return accountQuota;
	}

	public void setAccountQuota(Long accountQuota) {
		this.accountQuota = accountQuota;
	}

	public Boolean getAccountQuotaOverride() {
		return accountQuotaOverride;
	}

	public void setAccountQuotaOverride(Boolean accountQuotaOverride) {
		this.accountQuotaOverride = accountQuotaOverride;
	}

	public ContainerQuota toObject() {
		ContainerQuota quota = new ContainerQuota();
		quota.setUuid(getUuid());
		quota.setContainerQuotaType(getType());
		quota.setQuota(getQuota());
		quota.setQuotaOverride(getQuotaOverride());
		quota.setDefaultQuota(getDefaultQuota());
		quota.setDefaultQuotaOverride(getDefaultQuotaOverride());
		quota.setDefaultQuota(getDefaultQuota());
		quota.setDefaultQuotaOverride(getDefaultQuotaOverride());
		quota.setDefaultMaxFileSize(getDefaultMaxFileSize());
		quota.setDefaultMaxFileSizeOverride(getDefaultMaxFileSizeOverride());
		quota.setDefaultAccountQuota(getDefaultAccountQuota());
		quota.setDefaultAccountQuotaOverride(getDefaultAccountQuotaOverride());
		quota.setMaxFileSize(getMaxFileSize());
		quota.setMaxFileSizeOverride(getMaxFileSizeOverride());
		quota.setAccountQuota(getAccountQuota());
		quota.setAccountQuotaOverride(getAccountQuotaOverride());
		quota.setMaintenance(getMaintenance());
		return quota;
	}

	/*
	 * Transformers
	 */
	public static Function<ContainerQuota, ContainerQuotaDto> toDto() {
		return new Function<ContainerQuota, ContainerQuotaDto>() {
			@Override
			public ContainerQuotaDto apply(ContainerQuota arg0) {
				return new ContainerQuotaDto(arg0);
			}
		};
	}

	@Override
	public String toString() {
		return "ContainerQuotaDto [uuid=" + uuid + ", domain=" + domain + ", parentDomain=" + parentDomain + ", quota="
				+ quota + ", quotaOverride=" + quotaOverride + ", defaultQuota=" + defaultQuota
				+ ", defaultQuotaOverride=" + defaultQuotaOverride + ", usedSpace=" + usedSpace
				+ ", yersterdayUsedSpace=" + yersterdayUsedSpace + ", maintenance=" + maintenance + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + ", batchModificationDate="
				+ batchModificationDate + ", type=" + type + ", defaultMaxFileSize=" + defaultMaxFileSize
				+ ", defaultMaxFileSizeOverride=" + defaultMaxFileSizeOverride + ", defaultAccountQuota="
				+ defaultAccountQuota + ", defaultAccountQuotaOverride=" + defaultAccountQuotaOverride
				+ ", maxFileSize=" + maxFileSize + ", maxFileSizeOverride=" + maxFileSizeOverride + ", accountQuota="
				+ accountQuota + ", accountQuotaOverride=" + accountQuotaOverride + "]";
	}
}
