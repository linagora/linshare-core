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
import java.util.Objects;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.ContainerQuota;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = ContainerQuotaDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ContainerQuota", description = "A container quota instance for accounts like users in a domain.")
public class ContainerQuotaDto {

	public static ContainerQuotaDto from(ContainerQuota cq) {
		return from(cq, Optional.empty());
	}

	public static ContainerQuotaDto from(ContainerQuota cq, Optional<Long> usedSpace) {
		return builder()
			.uuid(cq.getUuid())
			.domain(new DomainLightDto(cq.getDomain()))
			.parentDomain(cq)
			.quota(cq.getQuota())
			.quotaOverride(cq.getQuotaOverride())
			.defaultQuota(cq.getDefaultQuota())
			.defaultQuotaOverride(cq.getDefaultQuotaOverride())
			.usedSpace(usedSpace.orElse(cq.getCurrentValue()))
			.yesterdayUsedSpace(cq.getLastValue())
			.creationDate(cq.getCreationDate())
			.modificationDate(cq.getModificationDate())
			.maintenance(cq.getMaintenance())
			.batchModificationDate(cq.getBatchModificationDate())
			.type(cq.getContainerQuotaType())
			.defaultMaxFileSize(cq.getDefaultMaxFileSize())
			.defaultMaxFileSizeOverride(cq.getDefaultMaxFileSizeOverride())
			.defaultAccountQuota(cq.getDefaultAccountQuota())
			.defaultAccountQuotaOverride(cq.getDefaultAccountQuotaOverride())
			.maxFileSize(cq.getMaxFileSize())
			.maxFileSizeOverride(cq.getMaxFileSizeOverride())
			.accountQuota(cq.getAccountQuota())
			.accountQuotaOverride(cq.getAccountQuotaOverride())
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
		private Long yersterdayUsedSpace;
		private Boolean maintenance;
		private Date creationDate;
		private Date modificationDate;
		private Date batchModificationDate;
		private ContainerQuotaType type;
		private Long defaultMaxFileSize;
		private Boolean defaultMaxFileSizeOverride;
		private Long defaultAccountQuota;
		private Boolean defaultAccountQuotaOverride;
		private Long maxFileSize;
		private Boolean maxFileSizeOverride;
		private Long accountQuota;
		private Boolean accountQuotaOverride;

		public Builder uuid(String uuid) {
			this.uuid= uuid;
			return this;
		}

		public Builder domain(DomainLightDto domain) {
			this.domain = domain;
			return this;
		}

		public Builder parentDomain(ContainerQuota cq) {
			if (Objects.nonNull(cq.getParentDomain())) {
				this.parentDomain = new DomainLightDto(cq.getParentDomain());
			}
			return this;
		}

		public Builder type(ContainerQuotaType type) {
			this.type = type;
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

		public Builder yesterdayUsedSpace(Long yersterdayUsedSpace) {
			this.yersterdayUsedSpace = yersterdayUsedSpace;
			return this;
		}

		public Builder maintenance(Boolean maintenance) {
			this.maintenance = maintenance;
			return this;
		}

		public Builder creationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder modificationDate(Date modificationDate) {
			this.modificationDate = modificationDate;
			return this;
		}

		public Builder batchModificationDate(Date batchModificationDate) {
			this.batchModificationDate = batchModificationDate;
			return this;
		}

		public Builder defaultMaxFileSize(Long defaultMaxFileSize) {
			this.defaultMaxFileSize = defaultMaxFileSize;
			return this;
		}

		public Builder defaultMaxFileSizeOverride(Boolean defaultMaxFileSizeOverride) {
			this.defaultMaxFileSizeOverride = defaultMaxFileSizeOverride;
			return this;
		}

		public Builder defaultAccountQuota(Long defaultAccountQuota) {
			this.defaultAccountQuota = defaultAccountQuota;
			return this;
		}

		public Builder defaultAccountQuotaOverride(Boolean defaultAccountQuotaOverride) {
			this.defaultAccountQuotaOverride = defaultAccountQuotaOverride;
			return this;
		}

		public Builder maxFileSize(Long maxFileSize) {
			this.maxFileSize = maxFileSize;
			return this;
		}

		public Builder maxFileSizeOverride(Boolean maxFileSizeOverride) {
			this.maxFileSizeOverride = maxFileSizeOverride;
			return this;
		}

		public Builder accountQuota(Long accountQuota) {
			this.accountQuota = accountQuota;
			return this;
		}

		public Builder accountQuotaOverride(Boolean accountQuotaOverride) {
			this.accountQuotaOverride = accountQuotaOverride;
			return this;
		}

		public ContainerQuotaDto build() {
			return new ContainerQuotaDto(uuid, domain, parentDomain, quota, quotaOverride, defaultQuota,
					defaultQuotaOverride, usedSpace, yersterdayUsedSpace, maintenance, creationDate, modificationDate,
					batchModificationDate, type, defaultMaxFileSize, defaultMaxFileSizeOverride, defaultAccountQuota,
					defaultAccountQuotaOverride, maxFileSize, maxFileSizeOverride, accountQuota,
					accountQuotaOverride);
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

	@Schema(description = "The used space (Read only)")
	private final Long usedSpace;

	@Schema(description = "The value of yesterday's used space (Read only)")
	private final Long yesterdayUsedSpace;

	@Schema(description = "If set to true, uploads are disable due to server maintenance.")
	private final Boolean maintenance;

	@Schema(description = "Quota creation date (Read only)")
	private final Date creationDate;

	@Schema(description = "Quota last modification date (Read only)")
	private final Date modificationDate;

	@Schema(description = "Quota last modification date by a batch (Read only)")
	private final Date batchModificationDate;

	@Schema(description = "Type (ContainerQuotaType)")
	private final ContainerQuotaType type;

	@Schema(description = "The default maximum file size allowed.")
	private final Long defaultMaxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	private final Boolean defaultMaxFileSizeOverride;

	@Schema(description = "The default quota value for defaultAccountQuota inside sub containers.")
	private final Long defaultAccountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	private final Boolean defaultAccountQuotaOverride;

	@Schema(description = "The maximum file size accepted, default value sub containers")
	private final Long maxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	private final Boolean maxFileSizeOverride;

	@Schema(description = "The default quota value for an account created inside the current container.")
	private final Long accountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	private final Boolean accountQuotaOverride;

	private ContainerQuotaDto(String uuid, DomainLightDto domain, DomainLightDto parentDomain, Long quota,
			Boolean quotaOverride, Long defaultQuota, Boolean defaultQuotaOverride, Long usedSpace,
			Long yersterdayUsedSpace, Boolean maintenance, Date creationDate, Date modificationDate,
			Date batchModificationDate, ContainerQuotaType type, Long defaultMaxFileSize,
			Boolean defaultMaxFileSizeOverride, Long defaultAccountQuota, Boolean defaultAccountQuotaOverride,
			Long maxFileSize, Boolean maxFileSizeOverride, Long accountQuota, Boolean accountQuotaOverride) {
		super();
		this.uuid = uuid;
		this.domain = domain;
		this.parentDomain = parentDomain;
		this.quota = quota;
		this.quotaOverride = quotaOverride;
		this.defaultQuota = defaultQuota;
		this.defaultQuotaOverride = defaultQuotaOverride;
		this.usedSpace = usedSpace;
		this.yesterdayUsedSpace = yersterdayUsedSpace;
		this.maintenance = maintenance;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.batchModificationDate = batchModificationDate;
		this.type = type;
		this.defaultMaxFileSize = defaultMaxFileSize;
		this.defaultMaxFileSizeOverride = defaultMaxFileSizeOverride;
		this.defaultAccountQuota = defaultAccountQuota;
		this.defaultAccountQuotaOverride = defaultAccountQuotaOverride;
		this.maxFileSize = maxFileSize;
		this.maxFileSizeOverride = maxFileSizeOverride;
		this.accountQuota = accountQuota;
		this.accountQuotaOverride = accountQuotaOverride;
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

	public ContainerQuotaType getType() {
		return type;
	}

	public Long getDefaultMaxFileSize() {
		return defaultMaxFileSize;
	}

	public Boolean getDefaultMaxFileSizeOverride() {
		return defaultMaxFileSizeOverride;
	}

	public Long getDefaultAccountQuota() {
		return defaultAccountQuota;
	}

	public Boolean getDefaultAccountQuotaOverride() {
		return defaultAccountQuotaOverride;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public Boolean getMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public Long getAccountQuota() {
		return accountQuota;
	}

	public Boolean getAccountQuotaOverride() {
		return accountQuotaOverride;
	}

	public ContainerQuota toObject(Optional<String> uuid) {
		ContainerQuota quota = new ContainerQuota();
		quota.setUuid(uuid.orElse(getUuid()));
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
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.add("defaultMaxFileSize", defaultMaxFileSize)
				.add("defaultMaxFileSizeOverride", defaultMaxFileSizeOverride)
				.add("defaultAccountQuota", defaultAccountQuota)
				.add("defaultAccountQuotaOverride", defaultAccountQuotaOverride)
				.add("maxFileSize", maxFileSize)
				.add("maxFileSizeOverride", maxFileSizeOverride)
				.add("accountQuota", accountQuota)
				.add("accountQuotaOverride", accountQuotaOverride)
				.toString();
	}
}
