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

import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="AccountQuota")
@Schema(name = "AccountQuota", description = "A quota instance for accounts like users or workgroups.")
public class AccountQuotaDto extends QuotaDto {

	@Schema(description = "The maximum file size accepted.")
	protected Long maxFileSize;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "The default maximum file size accepted.")
	protected Long defaultMaxFileSize;

	@Schema(description = "If true, it is unlinked from its parent.")
	protected Boolean maxFileSizeOverride;

	protected AccountDto account;

	public AccountQuotaDto() {
	}

	public AccountQuotaDto(AccountQuota quota) {
		super(quota);
		this.maxFileSize = quota.getMaxFileSize();
		this.maxFileSizeOverride = quota.getMaxFileSizeOverride();
		this.account = new AccountDto(quota.getAccount(), true);
		this.defaultMaxFileSize = quota.getContainerQuota().getMaxFileSize();
		this.defaultQuota = quota.getContainerQuota().getAccountQuota();
	}

	public AccountQuota toObject() {
		AccountQuota quota = new AccountQuota();
		quota.setUuid(getUuid());
		quota.setMaxFileSize(getMaxFileSize());
		quota.setMaxFileSizeOverride(getMaxFileSizeOverride());
		quota.setQuota(getQuota());
		quota.setQuotaOverride(getQuotaOverride());
		return quota;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}

	public Boolean getMaxFileSizeOverride() {
		return maxFileSizeOverride;
	}

	public void setMaxFileSizeOverride(Boolean maxFileSizeOverride) {
		this.maxFileSizeOverride = maxFileSizeOverride;
	}

	public Long getDefaultMaxFileSize() {
		return defaultMaxFileSize;
	}

	public void setDefaultMaxFileSize(Long defaultMaxFileSize) {
		this.defaultMaxFileSize = defaultMaxFileSize;
	}

	/*
	 * Transformers
	 */
	public static Function<AccountQuota, AccountQuotaDto> toDto() {
		return new Function<AccountQuota, AccountQuotaDto>() {
			@Override
			public AccountQuotaDto apply(AccountQuota arg0) {
				return new AccountQuotaDto(arg0);
			}
		};
	}
}
