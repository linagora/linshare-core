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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.ContainerQuota;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="ContainerQuota")
@Schema(name = "ContainerQuota", description = "A container quota instance for accounts like users in a domain.")
public class ContainerQuotaDto extends QuotaDto {

	@Schema(description = "type (ContainerQuotaType)")
	protected ContainerQuotaType type;

	@Schema(description = "The maximum file size accepted, default value sub containers")
	protected Long defaultMaxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean defaultMaxFileSizeOverride;

	@Schema(description = "The default quota value for defaultAccountQuota inside sub containers.")
	protected Long defaultAccountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean defaultAccountQuotaOverride;

	@Schema(description = "The maximum file size accepted, default value sub containers")
	protected Long maxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean maxFileSizeOverride;

	@Schema(description = "The default quota value for an account created inside the current container.")
	protected Long accountQuota;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean accountQuotaOverride;

	public ContainerQuotaDto() {
	}

	public ContainerQuotaDto(ContainerQuota cq) {
		super(cq);
		this.type = cq.getContainerQuotaType();
		this.defaultMaxFileSize = cq.getDefaultMaxFileSize();
		this.defaultMaxFileSizeOverride = cq.getDefaultMaxFileSizeOverride();
		this.defaultAccountQuota = cq.getDefaultAccountQuota();
		this.defaultAccountQuotaOverride = cq.getDefaultAccountQuotaOverride();
		this.maxFileSize = cq.getMaxFileSize();
		this.maxFileSizeOverride = cq.getMaxFileSizeOverride();
		this.accountQuota = cq.getAccountQuota();
		this.accountQuotaOverride = cq.getAccountQuotaOverride();
	}

	public ContainerQuotaType getType() {
		return type;
	}

	public void setType(ContainerQuotaType type) {
		this.type = type;
	}

	public Long getDefaultMaxFileSize() {
		return defaultMaxFileSize;
	}

	public void setDefaulMaxFileSize(Long maxFileSize) {
		this.defaultMaxFileSize = maxFileSize;
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

	public void setDefaultMaxFileSize(Long defaultMaxFileSize) {
		this.defaultMaxFileSize = defaultMaxFileSize;
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

	public ContainerQuota toObject() {
		ContainerQuota quota = new ContainerQuota();
		quota.setUuid(getUuid());
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

	/*
	 * Transformers
	 */
	public static Function<ContainerQuota, ContainerQuotaDto> toDto() {
		return new Function<ContainerQuota, ContainerQuotaDto>() {
			@Override
			public ContainerQuotaDto apply(ContainerQuota arg0) {
				return new ContainerQuotaDto(arg0);
			}
		};
	}

}
