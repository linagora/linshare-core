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

import org.linagora.linshare.core.domain.entities.AccountQuota;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DomainQuota", description = "A domain quota instance for a domain.")
public class AccountQuotaDto {

	@Schema(description = "Resource's uuid")
	private final String uuid;

	@Schema(description = "Related account")
	protected AccountLightDto account;

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

	@Schema(description = "If true, this mode exempt from settings each container's quota of this domain(Personal/sharedSpaces quota), the global comsuption of all domain's users will be seen.")
	private final Boolean domainShared;

	@Schema(description = "Shows if domainShared is overrided on the subdomains..")
	private final Boolean domainSharedOverride;

	@Schema(description = "The maximum file size accepted.")
	protected final Long maxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected final Boolean maxFileSizeOverride;

	public AccountQuotaDto(AccountQuota quota) {
		super();
		this.uuid = quota.getUuid();
		this.account = new AccountLightDto(quota.getAccount());
		this.domain = new DomainLightDto(quota.getDomain());
		if (Objects.nonNull(quota.getParentDomain())) {
			parentDomain = new DomainLightDto(quota.getParentDomain());
		} else {
			this.parentDomain = null;
		}
		this.quota = quota.getQuota();
		this.quotaOverride = quota.getQuotaOverride();
		this.defaultQuota = quota.getDefaultQuota();
		this.defaultQuotaOverride = quota.getDefaultQuotaOverride();
		this.usedSpace = quota.getCurrentValue();
		this.yesterdayUsedSpace = quota.getLastValue();
		this.maintenance = quota.getMaintenance();
		this.creationDate = quota.getCreationDate();
		this.modificationDate = quota.getModificationDate();
		this.batchModificationDate = quota.getBatchModificationDate();
		this.domainShared = quota.getDomainShared();
		this.domainSharedOverride = quota.getDomainSharedOverride();
		this.maxFileSize = quota.getMaxFileSize();
		this.maxFileSizeOverride = quota.getMaxFileSizeOverride();
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

	public Boolean getDomainShared() {
		return domainShared;
	}

	public Boolean getDomainSharedOverride() {
		return domainSharedOverride;
	}

	public AccountLightDto getAccount() {
		return account;
	}

	public void setAccount(AccountLightDto account) {
		this.account = account;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public Boolean getMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public static Function<AccountQuota, AccountQuotaDto> toDto(){
		return new Function<AccountQuota, AccountQuotaDto>() {
			@Override
			public AccountQuotaDto apply(AccountQuota quota) {
				return new AccountQuotaDto(quota);
			}
		};
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
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.toString();
	}
}
