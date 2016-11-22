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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.Statistic;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name="Statistic")
@ApiModel(value = "Statistic", description = "Statistic object for users, workgroups or domains.")
public class StatisticDto {

	@ApiModelProperty(value = "Domain")
	private DomainLightDto domain;

	@ApiModelProperty(value = "ParentDomain")
	private DomainLightDto parentDomain;

	@ApiModelProperty(value = "CreationDate")
	private Date creationDate;

	@ApiModelProperty(value = "OperationCount")
	private Long OperationCount;

	@ApiModelProperty(value = "DeleteOperaionCount")
	private Long deleteOperationCount;

	@ApiModelProperty(value = "AddOperationCount")
	private Long addOperationCount;

	@ApiModelProperty(value = "AddOperationSum")
	private Long addOperationSum;

	@ApiModelProperty(value = "DeleteOperaitionSum")
	private Long deleteOperaionSum;

	@ApiModelProperty(value = "DiffOperationSum")
	private Long diffOperationSum;

	@ApiModelProperty(value = "ActualOperaionSum")
	private Long actualOperationSum;

	@ApiModelProperty(value = "Account")
	private AccountDto account;

	@ApiModelProperty(value = "StatisticType")
	private StatisticType statisticType;

	public StatisticDto() {
	}

	public StatisticDto(Statistic statistic){
		this.domain = new DomainLightDto(statistic.getDomain());
		this.parentDomain = new DomainLightDto(statistic.getParentDomain());
		this.creationDate = statistic.getCreationDate();
		this.actualOperationSum = statistic.getActualOperationSum();
		this.addOperationCount = statistic.getCreateOperationCount();
		this.addOperationSum = statistic.getCreateOperationSum();
		this.deleteOperationCount = statistic.getDeleteOperationCount();
		this.deleteOperaionSum = statistic.getDeleteOperationSum();
		this.diffOperationSum = statistic.getDiffOperationSum();
		this.OperationCount = statistic.getOperationCount();
		this.account = new AccountDto(statistic.getAccount(), true);
		this.statisticType = statistic.getStatisticType();
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getOperationCount() {
		return OperationCount;
	}

	public void setOperationCount(Long operationCount) {
		OperationCount = operationCount;
	}

	public Long getDeleteOperationCount() {
		return deleteOperationCount;
	}

	public void setDeleteOperationCount(Long deleteOperationCount) {
		this.deleteOperationCount = deleteOperationCount;
	}

	public Long getAddOperationCount() {
		return addOperationCount;
	}

	public void setAddOperationCount(Long addOperationCount) {
		this.addOperationCount = addOperationCount;
	}

	public Long getAddOperationSum() {
		return addOperationSum;
	}

	public void setAddOperationSum(Long addOperationSum) {
		this.addOperationSum = addOperationSum;
	}

	public Long getDeleteOperaionSum() {
		return deleteOperaionSum;
	}

	public void setDeleteOperaionSum(Long deleteOperaionSum) {
		this.deleteOperaionSum = deleteOperaionSum;
	}

	public Long getDiffOperationSum() {
		return diffOperationSum;
	}

	public void setDiffOperationSum(Long diffOperationSum) {
		this.diffOperationSum = diffOperationSum;
	}

	public Long getActualOperationSum() {
		return actualOperationSum;
	}

	public void setActualOperationSum(Long actualOperationSum) {
		this.actualOperationSum = actualOperationSum;
	}

	public AccountDto getAccount() {
		return account;
	}

	public void setAccount(AccountDto account) {
		this.account = account;
	}

	public static Function<Statistic, StatisticDto> toDto(){
		return new Function<Statistic, StatisticDto>() {

			@Override
			public StatisticDto apply(Statistic statistic) {
				return new StatisticDto(statistic);
			}
		};
	}
}
