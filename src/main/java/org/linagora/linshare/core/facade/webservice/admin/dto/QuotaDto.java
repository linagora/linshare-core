/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainLightDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="Quota")
@Schema
public class QuotaDto {

	@Schema(description = "uuid")
	protected String uuid;

	@Schema(description = "The domain which this quota belongs to.")
	protected DomainLightDto domain;

	@Schema(description = "The parent domain which this quota belongs to.")
	protected DomainLightDto parentDomain;

	@Schema(description = "The limit (quota)")
	protected Long quota;

	@Schema(description = "If true, allow to override quota field for the current object (domain, container or account)"
			+ "Othervise the quota value can/will be updated by cascade by the defaultQuota field of your parent."
			+ "Useless for root domain quota")
	protected Boolean quotaOverride;

	@Schema(description = "The limit (quota) for each child quota")
	protected Long defaultQuota;

	@Schema(description = "By default, defaultQuota equal quota, every modification of the quota field will be apply to defaultQuota too."
			+ "If true, the defaultQuota value is unlinked from quota field.")
	protected Boolean defaultQuotaOverride;

	// @Schema(description = "")
	// protected Long quotaWarning;

	@Schema(description = "The used space. Read only.")
	protected Long usedSpace;

	@Schema(description = "Yesterday used space. Read only.")
	protected Long yersterdayUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	protected Boolean maintenance;

	@Schema(description = "Quota creation date. Read only.")
	protected Date creationDate;

	@Schema(description = "Quota last modification date. Read only.")
	protected Date modificationDate;

	@Schema(description = "Quota last modification date by a batch. Read only.")
	protected Date batchModificationDate;

	public QuotaDto() {
	}

	public QuotaDto(Quota quota) {
		this.uuid = quota.getUuid();
		this.domain = new DomainLightDto(quota.getDomain());
		this.domain.setType(quota.getDomain().getDomainType());
		if (quota.getParentDomain() != null) {
			this.parentDomain = new DomainLightDto(quota.getParentDomain());
		}
		this.quota = quota.getQuota();
		this.quotaOverride = quota.getQuotaOverride();
		this.defaultQuota = quota.getDefaultQuota();
		this.defaultQuotaOverride = quota.getDefaultQuotaOverride();
//		this.quotaWarning = quota.getQuotaWarning();
		this.usedSpace = quota.getCurrentValue();
		this.yersterdayUsedSpace = quota.getLastValue();
		this.maintenance = quota.getMaintenance();
		this.creationDate = quota.getCreationDate();
		this.modificationDate = quota.getModificationDate();
		this.batchModificationDate = quota.getBatchModificationDate();
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
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

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getYersterdayUsedSpace() {
		return yersterdayUsedSpace;
	}

	public void setYersterdayUsedSpace(Long yersterdayUsedSpace) {
		this.yersterdayUsedSpace = yersterdayUsedSpace;
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

	public DomainLightDto getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(DomainLightDto parentDomain) {
		this.parentDomain = parentDomain;
	}

}
