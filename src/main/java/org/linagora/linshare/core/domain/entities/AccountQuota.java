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

public class AccountQuota extends Quota {

	protected Long maxFileSize;

	protected Boolean maxFileSizeOverride;

	protected Boolean shared;

	protected ContainerQuota containerQuota;

	public AccountQuota() {
		super();
	}

	public AccountQuota(AbstractDomain domain, AbstractDomain parentDomain, Account account,
			ContainerQuota containerQuota) {
		super(domain, parentDomain, containerQuota.getAccountQuota(), containerQuota.getQuotaWarning());
		this.account = account;
		this.containerQuota = containerQuota;
		this.maxFileSize = containerQuota.getMaxFileSize();
		this.maxFileSizeOverride = false;
		this.quotaOverride = false;
		this.defaultQuota = null;
		this.defaultQuotaOverride = null;
		this.shared = false;
		if (this.containerQuota.getShared()) {
			this.shared = true;
		}
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public ContainerQuota getContainerQuota() {
		return containerQuota;
	}

	public void setContainerQuota(ContainerQuota containerQuota) {
		this.containerQuota = containerQuota;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public Boolean getMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public void setMaxFileSizeOverride(Boolean maxFileSizeOverride) {
		this.maxFileSizeOverride = maxFileSizeOverride;
	}

	@Override
	public String toString() {
		return "AccountQuota [id=" + id + ", uuid=" + uuid + ", account=" + account + ", domain=" + domain
				+ ", parentDomain=" + parentDomain + ", quota=" + quota + ", quotaWarning=" + quotaWarning
				+ ", currentValue=" + currentValue + ", lastValue=" + lastValue + ", fileSizeMax=" + maxFileSize + "]";
	}
}
