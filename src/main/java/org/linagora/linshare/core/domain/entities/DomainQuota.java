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

public class DomainQuota extends Quota {

	protected Long currentValueForSubdomains;

	protected Boolean defaultDomainShared;

	protected Boolean defaultDomainSharedOverride;

	public DomainQuota() {
		super();
	}

	public DomainQuota(DomainQuota parentQuota, AbstractDomain domain) {
		super(domain, parentQuota.getDomain(),
				parentQuota.getDefaultQuota(),
				parentQuota.getQuotaWarning());
		this.currentValueForSubdomains = 0L;
		this.domainShared = parentQuota.getDefaultDomainShared();
		this.domainSharedOverride = false;
		this.defaultDomainShared = parentQuota.getDefaultDomainShared();
		this.defaultDomainSharedOverride = false;
	}

	public DomainQuota(DomainQuota quota) {
		this.domain = quota.getDomain();
		this.parentDomain = quota.getParentDomain();
		this.currentValue = quota.getCurrentValue();
		this.lastValue = quota.getLastValue();
		this.quota = quota.getQuota();
		this.defaultQuota = quota.getDefaultQuota();
		this.quotaOverride = quota.getQuotaOverride();
		this.defaultQuotaOverride = quota.getDefaultQuotaOverride();
		this.quotaWarning = quota.getQuotaWarning();
		this.maintenance = quota.getMaintenance();
		this.domainShared = quota.getDomainShared();
		this.domainSharedOverride = quota.getDomainSharedOverride();
		this.defaultDomainShared = quota.getDefaultDomainShared();
		this.defaultDomainSharedOverride = quota.getDefaultDomainSharedOverride();
	}

	public Long getCurrentValueForSubdomains() {
		return currentValueForSubdomains;
	}

	public void setCurrentValueForSubdomains(Long currentValueForSubdomains) {
		this.currentValueForSubdomains = currentValueForSubdomains;
	}

	public Boolean getDefaultDomainShared() {
		return defaultDomainShared;
	}

	public void setDefaultDomainShared(Boolean defaultDomainShared) {
		this.defaultDomainShared = defaultDomainShared;
	}

	public Boolean getDefaultDomainSharedOverride() {
		return defaultDomainSharedOverride;
	}

	public void setDefaultDomainSharedOverride(Boolean defaultDomainSharedOverride) {
		this.defaultDomainSharedOverride = defaultDomainSharedOverride;
	}

	@Override
	public String toString() {
		return "DomainQuota [id=" + id + ", uuid=" + uuid + ", account=" + account + ", domain=" + domain
				+ ", parentDomain=" + parentDomain + ", quota=" + quota + ", quotaWarning=" + quotaWarning
				+ ", currentValue=" + currentValue + ", lastValue=" + lastValue + "]";
	}
}
