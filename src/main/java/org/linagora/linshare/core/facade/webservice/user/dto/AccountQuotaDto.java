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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="Quota")
@Schema
public class AccountQuotaDto {

	@Schema(description = "The limit (quota)")
	protected Long quota;

	@Schema(description = "The used space")
	protected Long usedSpace;

	@Schema(description = "The maximum file size accepted.")
	protected Long maxFileSize;

	@Schema(description = "If true, uploads are disable due to server maintenance.")
	protected Boolean maintenance;

	@Schema(description = "The domain used space")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Long domainUsedSpace;

	public AccountQuotaDto() {
	}

	public AccountQuotaDto(Long quota, Long usedSpace, Long maxFileSize, Boolean maintenance) {
		super();
		this.quota = quota;
		this.usedSpace = usedSpace;
		this.maxFileSize = maxFileSize;
		this.maintenance = maintenance;
		this.domainUsedSpace = null;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Boolean getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(Boolean maintenance) {
		this.maintenance = maintenance;
	}

	public Long getDomainUsedSpace() {
		return domainUsedSpace;
	}

	public void setDomainUsedSpace(Long domainUsedSpace) {
		this.domainUsedSpace = domainUsedSpace;
	}

}
