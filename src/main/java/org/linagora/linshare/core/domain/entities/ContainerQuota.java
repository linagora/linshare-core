/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;

public class ContainerQuota extends Quota {

	protected Long defaultMaxFileSize;

	protected Boolean defaultMaxFileSizeOverride;

	protected Long defaultAccountQuota;

	protected Boolean defaultAccountQuotaOverride;

	protected ContainerQuotaType containerQuotaType;

	protected Boolean shared;

	protected DomainQuota domainQuota;

	protected Long maxFileSize;

	protected Boolean maxFileSizeOverride;

	protected Long accountQuota;

	protected Boolean accountQuotaOverride;

	public ContainerQuota() {
		super();
	}

	/**
	 * Initialization of a new container.
	 * 
	 * @param domain
	 * @param parentDomain
	 * @param domainQuota
	 * @param parentContainerQuota
	 */
	public ContainerQuota(AbstractDomain domain, AbstractDomain parentDomain, DomainQuota domainQuota,
			ContainerQuota parentContainerQuota) {
		super(domain, parentDomain, parentContainerQuota.getDefaultQuota(), parentContainerQuota.getQuotaWarning());

		// Link to the parent
		this.domainQuota = domainQuota;

		// max file size for account quota children.
		this.defaultMaxFileSize = parentContainerQuota.getDefaultMaxFileSize();
		this.defaultMaxFileSizeOverride = false;

		// quota for account quota children.
		this.defaultAccountQuota = parentContainerQuota.getDefaultAccountQuota();
		this.defaultAccountQuotaOverride = false;

		// Kind of container.
		this.containerQuotaType = parentContainerQuota.getContainerQuotaType();
		this.shared = false;
		if (this.containerQuotaType.equals(ContainerQuotaType.WORK_GROUP)) {
			this.shared = true;
		}

		this.maxFileSize = parentContainerQuota.getDefaultMaxFileSize();
		this.maxFileSizeOverride = false;
		this.accountQuota = parentContainerQuota.getDefaultAccountQuota();
		this.accountQuotaOverride = false;
	}

	/**
	 * For tests only.
	 */
	public ContainerQuota(AbstractDomain domain, AbstractDomain parentDomain, DomainQuota domainQuota, long quota,
			long quotaWarning, long fileSizeMax, long currentValue, long lastValue, ContainerQuotaType containerType) {
		super(domain, parentDomain, quota, quotaWarning);

		// Link to the parent
		this.domainQuota = domainQuota;

		// max file size for account quota children.
		this.defaultMaxFileSize = fileSizeMax;
		this.defaultMaxFileSizeOverride = false;

		// quota for account quota children.
		this.defaultAccountQuota = quota;
		this.defaultAccountQuotaOverride = false;

		// Kind of container.
		this.containerQuotaType = containerType;

		this.currentValue = currentValue;
		this.lastValue = lastValue;
		this.shared = false;
		if (this.containerQuotaType.equals(ContainerQuotaType.WORK_GROUP)) {
			this.shared = true;
		}
		this.maxFileSize = fileSizeMax;
		this.maxFileSizeOverride = false;
		this.accountQuota = quota;
		this.accountQuotaOverride = false;
	}

	public ContainerQuota(ContainerQuota quota) {
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

		this.defaultMaxFileSize = quota.getDefaultMaxFileSize();
		this.defaultMaxFileSizeOverride = quota.getDefaultMaxFileSizeOverride();

		// quota for account quota children.
		this.defaultAccountQuota = quota.getDefaultAccountQuota();
		this.defaultAccountQuotaOverride = quota.getDefaultAccountQuotaOverride();

		// Kind of container.
		this.containerQuotaType = quota.getContainerQuotaType();
		this.shared = quota.getShared();

		this.maxFileSize = quota.getDefaultMaxFileSize();
		this.maxFileSizeOverride = quota.getDefaultMaxFileSizeOverride();
		this.accountQuota = quota.getDefaultAccountQuota();
		this.accountQuotaOverride = quota.getDefaultAccountQuotaOverride();
	}

	public ContainerQuotaType getContainerQuotaType() {
		return containerQuotaType;
	}

	public void setContainerQuotaType(ContainerQuotaType containerQuotaType) {
		this.containerQuotaType = containerQuotaType;
	}

	public DomainQuota getDomainQuota() {
		return domainQuota;
	}

	public void setDomainQuota(DomainQuota domainQuota) {
		this.domainQuota = domainQuota;
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

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
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

	public boolean isWorkgroup() {
		return (getContainerQuotaType().equals(ContainerQuotaType.WORK_GROUP));
	}

	@Override
	public String toString() {
		return "ContainerQuota [containerType=" + containerQuotaType + ", uuid=" + uuid + ", account=" + account
				+ ", quota=" + quota + ", quotaWarning=" + quotaWarning + ", currentValue=" + currentValue
				+ ", lastValue=" + lastValue + ", fileSizeMax=" + defaultMaxFileSize + "]";
	}

}
