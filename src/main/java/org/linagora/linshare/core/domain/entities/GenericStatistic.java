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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.StatisticType;

public abstract class GenericStatistic {

	protected Long id;

	protected Account account;

	protected AbstractDomain domain;

	protected AbstractDomain parentDomain;

	protected Long actualOperationSum;

	protected Date creationDate;

	protected Date statisticDate;

	protected Long operationCount;

	protected Long deleteOperationCount;

	protected Long createOperationCount;

	protected Long createOperationSum;

	protected Long deleteOperationSum;

	protected Long diffOperationSum;

	protected StatisticType statisticType;

	public GenericStatistic() {
	}

	public GenericStatistic(Account account, AbstractDomain domain, AbstractDomain parentDomain,
			Long actualOperationSum, Long operationCount, Long deleteOperationCount, Long createOperationCount,
			Long createOperationSum, Long deleteOperationSum, Long diffOperationSum, StatisticType statisticType) {
		this.domain = domain;
		this.parentDomain = parentDomain;
		this.account = account;
		this.actualOperationSum = actualOperationSum;
		this.operationCount = operationCount;
		this.deleteOperationCount = deleteOperationCount;
		this.createOperationCount = createOperationCount;
		this.createOperationSum = createOperationSum;
		this.deleteOperationSum = deleteOperationSum;
		this.diffOperationSum = diffOperationSum;
		this.statisticType = statisticType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public AbstractDomain getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(AbstractDomain parentDomain) {
		this.parentDomain = parentDomain;
	}

	public Long getActualOperationSum() {
		return actualOperationSum;
	}

	public void setActualOperationSum(Long actualOperationSum) {
		this.actualOperationSum = actualOperationSum;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getOperationCount() {
		return operationCount;
	}

	public void setOperationCount(Long operationCount) {
		this.operationCount = operationCount;
	}

	public Long getDeleteOperationCount() {
		return deleteOperationCount;
	}

	public void setDeleteOperationCount(Long deleteOperationCount) {
		this.deleteOperationCount = deleteOperationCount;
	}

	public Long getCreateOperationCount() {
		return createOperationCount;
	}

	public void setCreateOperationCount(Long createOperationCount) {
		this.createOperationCount = createOperationCount;
	}

	public Long getCreateOperationSum() {
		return createOperationSum;
	}

	public void setCreateOperationSum(Long createOperationSum) {
		this.createOperationSum = createOperationSum;
	}

	public Long getDeleteOperationSum() {
		return deleteOperationSum;
	}

	public void setDeleteOperationSum(Long deleteOperationSum) {
		this.deleteOperationSum = deleteOperationSum;
	}

	public Long getDiffOperationSum() {
		return diffOperationSum;
	}

	public void setDiffOperationSum(Long diffOperationSum) {
		this.diffOperationSum = diffOperationSum;
	}

	public abstract StatisticType getStatisticType();

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(Date statisticDate) {
		this.statisticDate = statisticDate;
	}

	@Override
	public String toString() {
		return "GenericStatistic [id=" + id + ", account=" + account + ", domain=" + domain + ", actualOperationSum="
				+ actualOperationSum + ", creationDate=" + creationDate + ", operationCount=" + operationCount
				+ ", deleteOperationCount=" + deleteOperationCount + ", createOperationCount=" + createOperationCount
				+ ", createOperationSum=" + createOperationSum + ", deleteOperationSum=" + deleteOperationSum
				+ ", diffOperationSum=" + diffOperationSum + ", statisticType=" + statisticType + "]";
	}
}
