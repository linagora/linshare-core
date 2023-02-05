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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DomainQuota;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "DomainQuota")
@Schema(name = "DomainQuota", description = "A domain quota instance for a domain.")
public class DomainQuotaDto extends QuotaDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "List of all quota containers.")
	List<String> containerUuids;

	protected Long currentValueForSubdomains;

	protected Boolean domainShared;

	protected Boolean domainSharedOverride;

	protected Boolean defaultDomainShared;

	protected Boolean defaultDomainSharedOverride;

	public DomainQuotaDto() {
	}

	public DomainQuotaDto(DomainQuota quota) {
		super(quota);
		this.currentValueForSubdomains = quota.getCurrentValueForSubdomains();
		this.domainShared = quota.getDomainShared();
		this.domainSharedOverride = quota.getDomainSharedOverride();
		this.defaultDomainShared = quota.getDefaultDomainShared();
		this.defaultDomainSharedOverride = quota.getDefaultDomainSharedOverride();
	}

	public List<String> getContainerUuids() {
		return containerUuids;
	}

	public void setContainerUuids(List<String> containerUuids) {
		this.containerUuids = containerUuids;
	}

	public Long getCurrentValueForSubdomains() {
		return currentValueForSubdomains;
	}

	public void setCurrentValueForSubdomains(Long currentValueForSubdomains) {
		this.currentValueForSubdomains = currentValueForSubdomains;
	}

	public void addContainerUuids(String containerUuid) {
		if (this.containerUuids == null) {
			this.containerUuids = Lists.newArrayList();
		}
		this.containerUuids.add(containerUuid);
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

	public DomainQuota toObject() {
		DomainQuota quota = new DomainQuota();
		quota.setUuid(getUuid());
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

	/*
	 * Transformers
	 */
	public static Function<DomainQuota, DomainQuotaDto> toDto() {
		return new Function<DomainQuota, DomainQuotaDto>() {
			@Override
			public DomainQuotaDto apply(DomainQuota arg0) {
				return new DomainQuotaDto(arg0);
			}
		};
	}

}
