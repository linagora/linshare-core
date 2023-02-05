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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "BasicStatistic")
@Document(collection = "basic_statistic")
public class BasicStatistic {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String uuid;

	protected Long value;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String domainUuid;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String parentDomainUuid;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected LogAction action;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Date creationDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String statisticDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AuditLogEntryType resourceType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected BasicStatisticType type;

	public BasicStatistic() {
		super();
	}

	public BasicStatistic(Long value, String parentDomainUuid,
			AuditLogEntry entity, BasicStatisticType type) {
		super();
		this.value = value;
		this.domainUuid = entity.getAuthUser().getDomain().getUuid();
		this.parentDomainUuid = parentDomainUuid;
		this.action = entity.getAction();
		this.creationDate = entity.getCreationDate();
		this.statisticDate = LocalDate.ofInstant(
			creationDate.toInstant(),
			ZoneId.systemDefault()
		).toString();
		this.resourceType = entity.getType();
		this.type = type;
		this.uuid = UUID.randomUUID().toString();
	}

	public BasicStatistic(Long value, String domainUuid, String parentDomainUuid, LogAction action, Date creationDate,
			AuditLogEntryType resourceType, BasicStatisticType type) {
		super();
		this.value = value;
		this.domainUuid = domainUuid;
		this.parentDomainUuid = parentDomainUuid;
		this.action = action;
		this.creationDate = creationDate;
		this.statisticDate = LocalDate.ofInstant(
				creationDate.toInstant(),
				ZoneId.systemDefault()
			).toString();
		this.resourceType = resourceType;
		this.type = type;
		this.uuid = UUID.randomUUID().toString();
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

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(String statisticDate) {
		this.statisticDate = statisticDate;
	}

	public AuditLogEntryType getResourceType() {
		return resourceType;
	}

	public void setResourceType(AuditLogEntryType resourceType) {
		this.resourceType = resourceType;
	}

	public BasicStatisticType getType() {
		return type;
	}

	public void setType(BasicStatisticType type) {
		this.type = type;
	}
}
