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

	public ContainerQuota() {
		super();
	}

	/**
	 * Initialization of a new container.
	 * @param domain
	 * @param parentDomain
	 * @param domainQuota
	 * @param parentContainerQuota
	 */
	public ContainerQuota(AbstractDomain domain, AbstractDomain parentDomain, DomainQuota domainQuota,
			ContainerQuota parentContainerQuota) {
		super(domain, parentDomain,
				parentContainerQuota.getDefaultQuota(),
				parentContainerQuota.getQuotaWarning());

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

	public void setBusinessDefaultMaxFileSize(Long maxFileSize) {
		if (maxFileSize != null) {
			this.defaultMaxFileSize = maxFileSize;
		}
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
