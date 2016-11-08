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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;

public class OperationHistory {

	private Long id;

	private String uuid;

	private Account account;

	private AbstractDomain domain;

	private Date creationDate;

	private Long operationValue;

	private OperationHistoryTypeEnum operationType;

	private ContainerQuotaType containerQuotaType;

	public OperationHistory() {

	}

	public OperationHistory(Account account, AbstractDomain domain,
			Long operationValue, OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType) {
		this.account = account;
		this.domain = domain;
		this.operationValue = operationValue;
		this.operationType = operationType;
		this.containerQuotaType = containerQuotaType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getOperationValue() {
		return operationValue;
	}

	public void setOperationValue(Long operationValue) {
		this.operationValue = operationValue;
	}

	public OperationHistoryTypeEnum getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationHistoryTypeEnum operationType) {
		this.operationType = operationType;
	}
	
	public String getInfo(){
		return " "+this.id+" "+this.operationType+" "+this.operationValue+" "+this.creationDate;
	}

	public ContainerQuotaType getContainerQuotaType() {
		return containerQuotaType;
	}

	public void setContainerQuotaType(ContainerQuotaType containerQuotaType) {
		this.containerQuotaType = containerQuotaType;
	}

	@Override
	public String toString() {
		return "OperationHistory [uuid=" + uuid + ", account=" + account + ", operationValue=" + operationValue + ", operationType=" + operationType
				+ ", containerQuotaType=" + containerQuotaType + "]";
	}

}
