/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@XmlRootElement(name = "BasicStatistic")
@Document(collection = "basic_statistic")
public class BasicStatistic {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected String uuid;
	
	protected Long value;

	protected String domainUuid;

	protected String parentDomainUuid;

	protected LogAction action;

	protected Date creationDate;

	protected AuditLogEntryType resourceType;

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
