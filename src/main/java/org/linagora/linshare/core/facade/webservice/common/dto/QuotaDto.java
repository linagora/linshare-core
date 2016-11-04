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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement
@ApiModel
public class QuotaDto {

	@ApiModelProperty(value = "Account")
	protected AccountDto account;

	@ApiModelProperty(value = "Domain")
	protected DomainLightDto domain;

	@ApiModelProperty(value = "ParentDomain")
	protected DomainLightDto parentDomain;

	@ApiModelProperty(value = "Quota")
	protected Long quota;

	@ApiModelProperty(value = "QuotaWarning")
	protected Long quotaWarning;

	@ApiModelProperty(value = "CurrentValue")
	protected Long currentValue;

	@ApiModelProperty(value = "LastValue")
	protected Long lastValue;

	@ApiModelProperty(value = "FileSizeMax")
	protected Long fileSizeMax;

	@ApiModelProperty(value = "override")
	protected Boolean override;

	public QuotaDto() {
	}

	public QuotaDto(Quota quota) {
		Account account = quota.getAccount();
		if (account != null) {
			this.account = new AccountDto(account, true);
		}
		this.domain = new DomainLightDto(quota.getDomain());
		if (quota.getParentDomain() != null) {
			this.parentDomain = new DomainLightDto(quota.getParentDomain());
		}
		this.quota = quota.getQuota();
		this.quotaWarning = quota.getQuotaWarning();
		this.currentValue = quota.getCurrentValue();
		this.lastValue = quota.getLastValue();
		this.fileSizeMax = quota.getFileSizeMax();
		this.override = quota.getOverride();
	}

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public DomainLightDto getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(DomainLightDto parentDomain) {
		this.parentDomain = parentDomain;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Long getQuotaWarning() {
		return quotaWarning;
	}

	public void setQuotaWarning(Long quotaWarning) {
		this.quotaWarning = quotaWarning;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
	}

	public Long getLastValue() {
		return lastValue;
	}

	public void setLastValue(Long lastValue) {
		this.lastValue = lastValue;
	}

	public Long getFileSizeMax() {
		return fileSizeMax;
	}

	public void setFileSizeMax(Long fileSizeMax) {
		this.fileSizeMax = fileSizeMax;
	}

	public Boolean getOverride() {
		return override;
	}

	public void setOverride(Boolean override) {
		this.override = override;
	}
}
