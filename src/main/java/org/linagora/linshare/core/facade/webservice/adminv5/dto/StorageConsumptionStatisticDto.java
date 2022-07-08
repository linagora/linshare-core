/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.Statistic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Statistic", description = "Statistic object for users, workgroups or domains.")
public class StorageConsumptionStatisticDto {


	@Schema(description = "Creation date of this record")
	private Date creationDate;

	@Schema(description = "This record was generated for this date")
	private Date statisticDate;

	@Schema(description = "Domain")
	private DomainLightDto domain;

	@Schema(description = "StatisticType")
	private StatisticType statisticType;

	@Schema(description = "Nb (counter) of added files")
	private Long countOfAddedFiles;

	@Schema(description = "Nb (counter) of deleted files")
	private Long countOfDeletedFiles;

	@Schema(description = "Sum of added and deleted counter files")
	private Long countOfFiles;

	@Schema(description = "Sum of all added files")
	private Long sumOfAddedFiles;

	@Schema(description = "Sum of all deleted files")
	private Long sumOfDeletedFiles;

	@Schema(description = "Sum of all added minus sum of deleted files")
	private Long diffOperationSum;

	@Schema(description = "ActualOperaionSum")
	private Long actualOperationSum;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Account")
	private AccountLightDto account;

	public StorageConsumptionStatisticDto() {
	}

	public StorageConsumptionStatisticDto(Statistic statistic){
		this.creationDate = statistic.getCreationDate();
		this.statisticDate = statistic.getStatisticDate();
		this.domain = new DomainLightDto(statistic.getDomain());
		this.statisticType = statistic.getStatisticType();
		this.countOfAddedFiles = statistic.getCreateOperationCount();
		this.countOfDeletedFiles = statistic.getDeleteOperationCount();
		this.countOfFiles = statistic.getOperationCount();
		this.sumOfAddedFiles = statistic.getCreateOperationSum();
		this.sumOfDeletedFiles = statistic.getDeleteOperationSum();
		this.diffOperationSum = statistic.getDiffOperationSum();
		this.actualOperationSum = statistic.getActualOperationSum();
		if (statistic.getAccount() != null) {
			this.account = new AccountLightDto(statistic.getAccount());
		}
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(Date statisticDate) {
		this.statisticDate = statisticDate;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	public Long getCountOfAddedFiles() {
		return countOfAddedFiles;
	}

	public void setCountOfAddedFiles(Long countOfAddedFiles) {
		this.countOfAddedFiles = countOfAddedFiles;
	}

	public Long getCountOfDeletedFiles() {
		return countOfDeletedFiles;
	}

	public void setCountOfDeletedFiles(Long countOfDeletedFiles) {
		this.countOfDeletedFiles = countOfDeletedFiles;
	}

	public Long getCountOfFiles() {
		return countOfFiles;
	}

	public void setCountOfFiles(Long countOfFiles) {
		this.countOfFiles = countOfFiles;
	}

	public Long getSumOfAddedFiles() {
		return sumOfAddedFiles;
	}

	public void setSumOfAddedFiles(Long sumOfAddedFiles) {
		this.sumOfAddedFiles = sumOfAddedFiles;
	}

	public Long getSumOfDeletedFiles() {
		return sumOfDeletedFiles;
	}

	public void setSumOfDeletedFiles(Long sumOfDeletedFiles) {
		this.sumOfDeletedFiles = sumOfDeletedFiles;
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

	public AccountLightDto getAccount() {
		return account;
	}

	public void setAccount(AccountLightDto account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "StorageConsumptionStatisticDto [creationDate=" + creationDate + ", statisticDate=" + statisticDate
				+ ", domain=" + domain + ", statisticType=" + statisticType + ", countOfAddedFiles=" + countOfAddedFiles
				+ ", countOfDeletedFiles=" + countOfDeletedFiles + ", countOfFiles=" + countOfFiles
				+ ", sumOfAddedFiles=" + sumOfAddedFiles + ", sumOfDeletedFiles=" + sumOfDeletedFiles
				+ ", diffOperationSum=" + diffOperationSum + ", actualOperationSum=" + actualOperationSum + ", account="
				+ account + "]";
	}

	public static Function<Statistic, StorageConsumptionStatisticDto> toDto(){
		return new Function<Statistic, StorageConsumptionStatisticDto>() {

			@Override
			public StorageConsumptionStatisticDto apply(Statistic statistic) {
				return new StorageConsumptionStatisticDto(statistic);
			}
		};
	}
}
