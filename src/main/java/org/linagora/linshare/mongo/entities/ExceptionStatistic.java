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
package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name = "ExceptionStatistic")
@Document(collection = "exception_statistic")
public class ExceptionStatistic implements Cloneable {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	protected String uuid;

	protected Long value;

	protected String domainUuid;

	protected String parentDomainUuid;

	protected Date creationDate;

	protected ExceptionStatisticType type;

	protected ExceptionType exceptionType;

	protected String stackTrace;

	protected BusinessErrorCode errorCode;

	public ExceptionStatistic() {
		super();
	}

	public ExceptionStatistic(Long value, String domainUuid, String parentDomainUuid, BusinessErrorCode errorCode,
			String stackTrace, ExceptionType exceptionType, ExceptionStatisticType type) {
		super();
		this.value = value;
		this.domainUuid = domainUuid;
		this.parentDomainUuid = parentDomainUuid;
		this.stackTrace = stackTrace;
		this.creationDate = new Date();
		this.type = type;
		this.exceptionType = exceptionType;
		this.uuid = UUID.randomUUID().toString();
		this.errorCode = errorCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getParentDomainUuid() {
		return parentDomainUuid;
	}

	public void setParentDomainUuid(String parentDomainUuid) {
		this.parentDomainUuid = parentDomainUuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public ExceptionStatisticType getType() {
		return type;
	}

	public void setType(ExceptionStatisticType type) {
		this.type = type;
	}

	public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public BusinessErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(BusinessErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
