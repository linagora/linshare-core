/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainLightDto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name="Quota")
@ApiModel
public class QuotaDto {

	@ApiModelProperty(value = "uuid")
	protected String uuid;

	@ApiModelProperty(value = "The domain which this quota belongs to.")
	protected DomainLightDto domain;

	@ApiModelProperty(value = "The parent domain which this quota belongs to.")
	protected DomainLightDto parentDomain;

	@ApiModelProperty(value = "The limit (quota)")
	protected Long quota;

	@ApiModelProperty(value = "If true, allow to override quota field for the current object (domain, container or account)"
			+ "Othervise the quota value can/will be updated by cascade by the defaultQuota field of your parent."
			+ "Useless for root domain quota")
	protected Boolean quotaOverride;

	@ApiModelProperty(value = "The limit (quota) for each child quota")
	protected Long defaultQuota;

	@ApiModelProperty(value = "By default, defaultQuota equal quota, every modification of the quota field will be apply to defaultQuota too."
			+ "If true, the defaultQuota value is unlinked from quota field.")
	protected Boolean defaultQuotaOverride;

	// @ApiModelProperty(value = "")
	// protected Long quotaWarning;

	@ApiModelProperty(value = "The used space. Read only.")
	protected Long usedSpace;

	@ApiModelProperty(value = "Yesterday used space. Read only.")
	protected Long yersterdayUsedSpace;

	@ApiModelProperty(value = "If set to true, uploads are disable due to server maintenance.")
	protected Boolean maintenance;

	@ApiModelProperty(value = "Quota creation date. Read only.")
	protected Date creationDate;

	@ApiModelProperty(value = "Quota last modification date. Read only.")
	protected Date modificationDate;

	@ApiModelProperty(value = "Quota last modification date by a batch. Read only.")
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
