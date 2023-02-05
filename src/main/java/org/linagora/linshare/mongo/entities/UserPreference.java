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

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = StringUserPreference.class, name = "string"),
		@Type(value = ListStringUserPreference.class, name = "list_string"),
		@Type(value = MapStringUserPreference.class, name = "map_string")
		})
@XmlRootElement(name = "UserPreference")
@XmlSeeAlso({ StringUserPreference.class,
	ListStringUserPreference.class,
	MapStringUserPreference.class
	})
@Document(collection="user_preferences")
@CompoundIndexes({ @CompoundIndex(name = "account_key", unique = true, def = "{'key': 1, 'accountUuid': 1}") })
public abstract class UserPreference {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected String uuid;

	@JsonIgnore
	protected String domainUuid;

	@JsonIgnore
	protected String accountUuid;

	protected String key;

	public UserPreference() {
		super();
	}

	public UserPreference(String key) {
		super();
		this.key = key;
//		this.uuid = UUID.randomUUID().toString();
	}

	@XmlTransient
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlTransient
	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	@XmlTransient
	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public void validate() {
		Validate.notEmpty(key, "Missing user preference key");
	}

	@Override
	public String toString() {
		return "UserPreference [uuid=" + uuid + ", domainUuid=" + domainUuid + ", accountUuid=" + accountUuid + ", key="
				+ key + "]";
	}
}
