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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name="AccountQuota")
@ApiModel(value = "AccountQuota", description = "A quota instance for accounts like users or workgroups.")
public class AccountQuotaDto extends QuotaDto {

	@ApiModelProperty(value = "The maximum file size accepted.")
	protected Long maxFileSize;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	@ApiModelProperty(value = "The default maximum file size accepted.")
	protected Long defaultMaxFileSize;

	@ApiModelProperty(value = "If true, it is unlinked from its parent.")
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
