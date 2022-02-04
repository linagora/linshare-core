/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.linagora.linshare.core.domain.entities.DomainQuota;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = DomainQuotaDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DomainQuota", description = "A domain quota instance for a domain.")
public class DomainQuotaDto {

	public static DomainQuotaDto from(DomainQuota dq) {
		return from(dq, Optional.empty(), Optional.empty());
	}

	public static DomainQuotaDto from(DomainQuota dq, Optional<Long> usedSpace, Optional<Long> currentValueForSubdomains) {
		DomainLightDto parentDomainLight = null;
		if (Objects.nonNull(dq.getParentDomain())) {
			parentDomainLight = new DomainLightDto(dq.getParentDomain());
		}
		return builder()
			.uuid(dq.getUuid())
			.domain(new DomainLightDto(dq.getDomain()))
			.parentDomain(parentDomainLight)
			.quota(dq.getQuota())
			.quotaOverride(dq.getQuotaOverride())
			.defaultQuota(dq.getDefaultQuota())
			.defaultQuotaOverride(dq.getDefaultQuotaOverride())
			.usedSpace(usedSpace.orElse(dq.getCurrentValue()))
			.yesterdayUsedSpace(dq.getLastValue())
			.maintenance(dq.getMaintenance())
			.batchModificationDate(dq.getBatchModificationDate())
			.currentValueForSubdomains(currentValueForSubdomains.orElse(dq.getCurrentValueForSubdomains()))
			.domainShared(dq.getDomainShared())
			.domainSharedOverride(dq.getDomainSharedOverride())
			.defaultDomainShared(dq.getDefaultDomainShared())
			.defaultDomainSharedOverride(dq.getDefaultDomainSharedOverride())
			.creationDate(dq.getCreationDate())
			.modificationDate(dq.getModificationDate())
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String uuid;
		private DomainLightDto domain;
		private DomainLightDto parentDomain;
		private Long quota;
		private Boolean quotaOverride;
		private Long defaultQuota;
		private Boolean defaultQuotaOverride;
		private Long usedSpace;
		private Long yesterdayUsedSpace;
		private Boolean maintenance;
		private Date batchModificationDate;
		private Date creationDate;
		private Date modificationDate;
		private Long currentValueForSubdomains;
		private Boolean domainShared;
		private Boolean domainSharedOverride;
		private Boolean defaultDomainShared;
		private Boolean defaultDomainSharedOverride;

		public Builder uuid(String uuid) {
			this.uuid= uuid;
			return this;
		}

		public Builder domain(DomainLightDto domain) {
			this.domain = domain;
			return this;
		}

		public Builder parentDomain(DomainLightDto domain) {
			this.parentDomain = domain;
			return this;
		}

		public Builder quota(Long quota) {
			this.quota = quota;
			return this;
		}

		public Builder quotaOverride(Boolean quotaOverride) {
			this.quotaOverride = quotaOverride;
			return this;
		}

		public Builder defaultQuota(Long defaultQuota) {
			this.defaultQuota = defaultQuota;
			return this;
		}

		public Builder defaultQuotaOverride(Boolean defaultQuotaOverride) {
			this.defaultQuotaOverride = defaultQuotaOverride;
			return this;
		}

		public Builder usedSpace(Long usedSpace) {
			this.usedSpace = usedSpace;
			return this;
		}

		public Builder yesterdayUsedSpace(Long yesterdayUsedSpace) {
			this.yesterdayUsedSpace = yesterdayUsedSpace;
			return this;
		}

		public Builder maintenance(Boolean maintenance) {
			this.maintenance = maintenance;
			return this;
		}

		public Builder batchModificationDate(Date batchModificationDate) {
			this.batchModificationDate = batchModificationDate;
			return this;
		}

		public Builder modificationDate(Date modificationDate) {
			this.modificationDate = modificationDate;
			return this;
		}

		public Builder creationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder currentValueForSubdomains(Long currentValueForSubdomains) {
			this.currentValueForSubdomains = currentValueForSubdomains;
			return this;
		}

		public Builder domainShared(Boolean domainShared) {
			this.domainShared = domainShared;
			return this;
		}

		public Builder domainSharedOverride(Boolean domainSharedOverride) {
			this.domainSharedOverride = domainSharedOverride;
			return this;
		}

		public Builder defaultDomainSharedOverride(Boolean defaultDomainSharedOverride) {
			this.defaultDomainSharedOverride = defaultDomainSharedOverride;
			return this;
		}
	
		public Builder defaultDomainShared(Boolean defaultDomainShared) {
			this.defaultDomainShared = defaultDomainShared;
			return this;
		}

		public DomainQuotaDto build() {
			return new DomainQuotaDto(uuid, domain, parentDomain, quota, quotaOverride, defaultQuota,
					defaultQuotaOverride, usedSpace, yesterdayUsedSpace, maintenance, creationDate, modificationDate,
					batchModificationDate, currentValueForSubdomains, domainShared, domainSharedOverride,
					defaultDomainShared, defaultDomainSharedOverride);
		}
	}

	@Schema(description = "Resource's uuid")
	private final String uuid;

	@Schema(description = "The domain which this quota belongs to.")
	private final DomainLightDto domain;

	@Schema(description = "The parent domain which this quota belongs to.")
	private final DomainLightDto parentDomain;

	@Schema(description = "The value of the current container quota limit")
	private final Long quota;

	@Schema(description = "If true, allow to override quota field for the current object (domain, container or account)"
			+ "Otherwise the quota value can/will be updated by cascade by the defaultQuota field of your parent."
			+ "Useless for root domain quota")
	private final Boolean quotaOverride;

	@Schema(description = "The default quota value allowed for each child quota.")
	private final Long defaultQuota;

	@Schema(description = "By default, defaultQuota equal quota, every modification of the quota field will be applied to defaultQuota too."
			+ "If true, the defaultQuota value is unlinked from quota field.")
	private final Boolean defaultQuotaOverride;

	@Schema(description = "The used space", accessMode = Schema.AccessMode.READ_ONLY)
	private final Long usedSpace;

	@Schema(description = "The value of yesterday's used space", accessMode = Schema.AccessMode.READ_ONLY)
	private final Long yesterdayUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	private final Boolean maintenance;

	@Schema(description = "Quota creation date", accessMode = Schema.AccessMode.READ_ONLY)
	private final Date creationDate;

	@Schema(description = "Quota last modification date", accessMode = Schema.AccessMode.READ_ONLY)
	private final Date modificationDate;

	@Schema(description = "Quota last modification date by a batch", accessMode = Schema.AccessMode.READ_ONLY)
	private final Date batchModificationDate;

	@Schema(description = "The current quota value of subdomains")
	private final Long currentValueForSubdomains;

	@Schema(description = "If true, this mode exempt from settings each container's quota of this domain(Personal/sharedSpaces quota), the global comsuption of all domain's users will be seen.")
	private final Boolean domainShared;

	@Schema(description = "Shows if domainShared is overrided on the subdomains..")
	private final Boolean domainSharedOverride;

	@Schema(description = "Default value of domainShared on the subdomains.")
	private final Boolean defaultDomainShared;

	@Schema(description = "Default value of overriding the domainShared on the subdomains.")
	private final Boolean defaultDomainSharedOverride;

	private DomainQuotaDto(String uuid, DomainLightDto domain, DomainLightDto parentDomain, Long quota,
			Boolean quotaOverride, Long defaultQuota, Boolean defaultQuotaOverride, Long usedSpace,
			Long yesterdayUsedSpace, Boolean maintenance, Date creationDate, Date modificationDate,
			Date batchModificationDate, Long currentValueForSubdomains,
			Boolean domainShared, Boolean domainSharedOverride, Boolean defaultDomainShared,
			Boolean defaultDomainSharedOverride) {
		super();
		this.uuid = uuid;
		this.domain = domain;
		this.parentDomain = parentDomain;
		this.quota = quota;
		this.quotaOverride = quotaOverride;
		this.defaultQuota = defaultQuota;
		this.defaultQuotaOverride = defaultQuotaOverride;
		this.usedSpace = usedSpace;
		this.yesterdayUsedSpace = yesterdayUsedSpace;
		this.maintenance = maintenance;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.batchModificationDate = batchModificationDate;
		this.currentValueForSubdomains = currentValueForSubdomains;
		this.domainShared = domainShared;
		this.domainSharedOverride = domainSharedOverride;
		this.defaultDomainShared = defaultDomainShared;
		this.defaultDomainSharedOverride = defaultDomainSharedOverride;
	}

	public String getUuid() {
		return uuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public DomainLightDto getParentDomain() {
		return parentDomain;
	}

	public Long getQuota() {
		return quota;
	}

	public Boolean getQuotaOverride() {
		return quotaOverride;
	}

	public Long getDefaultQuota() {
		return defaultQuota;
	}

	public Boolean getDefaultQuotaOverride() {
		return defaultQuotaOverride;
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public Long getYesterdayUsedSpace() {
		return yesterdayUsedSpace;
	}

	public Boolean getMaintenance() {
		return maintenance;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Date getBatchModificationDate() {
		return batchModificationDate;
	}

	public Long getCurrentValueForSubdomains() {
		return currentValueForSubdomains;
	}

	public Boolean getDomainShared() {
		return domainShared;
	}

	public Boolean getDomainSharedOverride() {
		return domainSharedOverride;
	}

	public Boolean getDefaultDomainShared() {
		return defaultDomainShared;
	}

	public Boolean getDefaultDomainSharedOverride() {
		return defaultDomainSharedOverride;
	}

	public DomainQuota toObject(Optional<String> uuid) {
		DomainQuota quota = new DomainQuota();
		quota.setUuid(uuid.orElse(getUuid()));
		quota.setQuota(getQuota());
		quota.setQuotaOverride(getQuotaOverride());
		quota.setDefaultQuota(getDefaultQuota());
		quota.setDefaultQuotaOverride(getDefaultQuotaOverride());
		quota.setMaintenance(getMaintenance());
		quota.setDomainShared(getDomainShared());
		quota.setDomainSharedOverride(getDomainSharedOverride());
		quota.setDefaultDomainShared(getDefaultDomainShared());
		quota.setDefaultDomainSharedOverride(getDefaultDomainSharedOverride());
		return quota;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("uuid", uuid)
				.add("domain", domain)
				.add("quota", quota)
				.add("quotaOverride", quotaOverride)
				.add("defaultQuota", defaultQuota)
				.add("defaultQuotaOverride",defaultQuotaOverride)
				.add("usedSpace", usedSpace)
				.add("maintenance", maintenance)
				.add("domainShared", domainShared)
				.add("domainSharedOverride", domainSharedOverride)
				.add("defaultDomainShared", defaultDomainShared)
				.add("defaultDomainSharedOverride", defaultDomainSharedOverride)
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.toString();
	}
}
