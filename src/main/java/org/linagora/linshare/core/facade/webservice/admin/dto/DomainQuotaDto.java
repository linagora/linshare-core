/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
