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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.Statistic;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name="Statistic")
@Schema(name = "Statistic", description = "Statistic object for users, workgroups or domains.")
public class StatisticDto {

	@Schema(description = "Domain")
	private DomainLightDto domain;

	@Schema(description = "ParentDomain")
	private DomainLightDto parentDomain;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "OperationCount")
	private Long OperationCount;

	@Schema(description = "DeleteOperaionCount")
	private Long deleteOperationCount;

	@Schema(description = "AddOperationCount")
	private Long addOperationCount;

	@Schema(description = "AddOperationSum")
	private Long addOperationSum;

	@Schema(description = "DeleteOperaitionSum")
	private Long deleteOperaionSum;

	@Schema(description = "DiffOperationSum")
	private Long diffOperationSum;

	@Schema(description = "ActualOperaionSum")
	private Long actualOperationSum;

	@Schema(description = "Account")
	private AccountDto account;

	@Schema(description = "StatisticType")
	private StatisticType statisticType;

	@Schema(description = "StatisticDate")
	private Date statisticDate;

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
		if (statistic.getAccount() != null) {
			this.account = new AccountDto(statistic.getAccount(), true);
		}
		this.statisticType = statistic.getStatisticType();
		this.statisticDate = statistic.getStatisticDate();
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

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public Date getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(Date statisticDate) {
		this.statisticDate = statisticDate;
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
