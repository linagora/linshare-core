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

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@XmlRootElement(name = "EventNotification")
@Document(collection = "event_notifications")
@JsonIgnoreProperties({ "relatedAccounts" })
public class EventNotification {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected AuditLogEntry event;

	protected List<String> relatedAccounts;

	public EventNotification(AuditLogEntry log, List<String> uuids) {
		super();
		this.event = log;
		this.relatedAccounts = uuids;
	}

	public EventNotification(AuditLogEntry log, String... uuids) {
		super();
		this.event = log;
		this.relatedAccounts = Lists.newArrayList(uuids);
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AuditLogEntry getEvent() {
		return event;
	}

	public void setEvent(AuditLogEntry event) {
		this.event = event;
	}

	public List<String> getRelatedAccounts() {
		return relatedAccounts;
	}

	public void setRelatedAccounts(List<String> relatedAccounts) {
		this.relatedAccounts = relatedAccounts;
	}

}
