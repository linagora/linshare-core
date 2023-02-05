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

import org.linagora.linshare.core.domain.constants.AdvancedStatisticType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author fmartin
 *
 */
@XmlRootElement(name = "MimeTypeStatistic")
@Document(collection = "mime_type_statistic")
public class MimeTypeStatistic {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String uuid;

	protected Long value;

	protected String domainUuid;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String parentDomainUuid;

	protected Date creationDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String statisticDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AdvancedStatisticType type;

	protected String mimeType;

	protected Long totalSize;

	protected String humanMimeType;

	public MimeTypeStatistic() {
		super();
	}

	public MimeTypeStatistic(
			Long value,
			Long size,
			String parentDomainUuid,
			String domainUuid,
			String mimeType,
			String humanMimeType) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.value = value;
		this.totalSize = size;
		this.domainUuid = domainUuid;
		this.parentDomainUuid = parentDomainUuid;
		this.creationDate = new Date();
		this.statisticDate = LocalDate.ofInstant(
			creationDate.toInstant(),
			ZoneId.systemDefault()
		).toString();
		this.mimeType = mimeType;
		this.humanMimeType = humanMimeType;
		this.type = AdvancedStatisticType.DAILY;
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

	public String getStatisticDate() {
		return statisticDate;
	}

	public void setStatisticDate(String statisticDate) {
		this.statisticDate = statisticDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public AdvancedStatisticType getType() {
		return type;
	}

	public void setType(AdvancedStatisticType type) {
		this.type = type;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}

	@Override
	public String toString() {
		return "MimeTypeStatistic [id=" + id + ", value=" + value + ", totalSize=" + totalSize + ", mimeType="
				+ mimeType + ", statisticDate=" + statisticDate + ", type=" + type + ", humanMimeType=" + humanMimeType
				+ ", domainUuid=" + domainUuid + "]";
	}

	public void addValue(Long value) {
		this.value += value;
	}

	public void addTotalSize(Long size) {
		this.totalSize += size;
	}

}
