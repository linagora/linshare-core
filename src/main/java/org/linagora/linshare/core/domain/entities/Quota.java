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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;

public abstract class Quota {

	protected Long id;

	protected String uuid;

	protected Account account;

	protected AbstractDomain domain;

	protected AbstractDomain parentDomain;

	protected Long quota;

	protected Boolean quotaOverride;

	protected Long defaultQuota;

	protected Boolean defaultQuotaOverride;

	protected Long quotaWarning;

	protected Long currentValue;

	protected Long lastValue;

	protected Boolean maintenance;

	protected Date creationDate;

	protected Date modificationDate;

	protected Date batchModificationDate;

	protected Boolean domainShared;

	protected Boolean domainSharedOverride;

	public Quota() {
	}

	public Quota(AbstractDomain domain, AbstractDomain parentDomain, Long quota, Long quotaWarning) {
		// related domains.
		this.domain = domain;
		this.parentDomain = parentDomain;
		// quota configuration
		this.currentValue = 0L;
		this.lastValue = 0L;
		this.quota = quota;
		this.defaultQuota = quota;
		this.quotaOverride = false;
		this.defaultQuotaOverride = false;
		this.quotaWarning = quotaWarning;
		this.maintenance = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public AbstractDomain getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(AbstractDomain parentDomain) {
		this.parentDomain = parentDomain;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
	}

	public Long getLastValue() {
		return lastValue;
	}

	public void setLastValue(Long lastValue) {
		this.lastValue = lastValue;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Long getQuotaWarning() {
		return quotaWarning;
	}

	public void setQuotaWarning(Long quotaWarning) {
		this.quotaWarning = quotaWarning;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getBatchModificationDate() {
		return batchModificationDate;
	}

	public void setBatchModificationDate(Date batchModificationDate) {
		this.batchModificationDate = batchModificationDate;
	}

	public Boolean getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(Boolean maintenance) {
		this.maintenance = maintenance;
	}

	public Boolean getDefaultQuotaOverride() {
		return defaultQuotaOverride;
	}

	public void setDefaultQuotaOverride(Boolean defaultQuotaOverride) {
		this.defaultQuotaOverride = defaultQuotaOverride;
	}

	public Long getDefaultQuota() {
		return defaultQuota;
	}

	public void setDefaultQuota(Long defaultQuota) {
		this.defaultQuota = defaultQuota;
	}

	public Boolean getQuotaOverride() {
		return quotaOverride;
	}

	public void setQuotaOverride(Boolean quotaOverride) {
		this.quotaOverride = quotaOverride;
	}

	public Boolean getDomainShared() {
		return domainShared;
	}

	public void setDomainShared(Boolean domainShared) {
		this.domainShared = domainShared;
	}

	public Boolean getDomainSharedOverride() {
		return domainSharedOverride;
	}

	public void setDomainSharedOverride(Boolean domainSharedOverride) {
		this.domainSharedOverride = domainSharedOverride;
	}

	@Override
	public String toString() {
		return "Quota [uuid=" + uuid + ", domain=" + domain + ", quota=" + quota + ", quotaWarning=" + quotaWarning
				+ ", currentValue=" + currentValue + ", lastValue=" + lastValue + "]";
	}
}
