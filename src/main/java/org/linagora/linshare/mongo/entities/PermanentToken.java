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

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "PermanentToken")
@Document(collection = "permanent_tokens")
public class PermanentToken {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "uuid")
	protected String uuid;

	@Schema(description = "Light entity that contains only the name and uuid of the domain.", required = true)
	protected GenericLightEntity domain;

	@Schema(description = "Light entity that contains only the name and uuid of the actor.", required = true)
	protected GenericLightEntity actor;

	@Schema(description = "issuer", required = true)
	protected String issuer;

	@Schema(description = "creation Date")
	protected Date creationDate;

	@Schema(description = "token name", required = true)
	protected String label;

	@Schema(description = "description of the token")
	protected String description;

	@Schema(description = "owner email", required = true)
	protected String subject;

	@Schema(description = "jwt token, not persisted")
	@Transient
	protected String token;

	public PermanentToken() {
		super();
	}

	// For Tests
	public PermanentToken(String label, String description) {
		this.label = label;
		this.description = description;
	}

	public PermanentToken(String tokenUuid,
			Date creationDate,
			String issuer,
			String label,
			String description,
			GenericLightEntity actor,
			String subject,
			GenericLightEntity domain) {
		this.uuid = tokenUuid;
		this.creationDate = creationDate;
		this.issuer = issuer;
		this.subject = subject;
		this.label = label;
		this.description = description;
		this.actor = actor;
		this.domain = domain;
	}

	public PermanentToken(PermanentToken jwtLongTime) {
		this.id = jwtLongTime.getId();
		this.uuid = jwtLongTime.getUuid();
		this.creationDate = jwtLongTime.getCreationDate();
		this.issuer = jwtLongTime.getIssuer();
		this.subject = jwtLongTime.getSubject();
		this.label = jwtLongTime.getLabel();
		this.description = jwtLongTime.getDescription();
		this.actor = jwtLongTime.getActor();
		this.domain = jwtLongTime.getDomain();
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

	public GenericLightEntity getDomain() {
		return domain;
	}

	public void setDomain(GenericLightEntity domain) {
		this.domain = domain;
	}

	public GenericLightEntity getActor() {
		return actor;
	}

	public void setActor(GenericLightEntity actor) {
		this.actor = actor;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "PermanentToken [id=" + id + ", uuid=" + uuid + ", domain=" + domain + ", actor=" + actor + ", issuer="
				+ issuer + ", creationDate=" + creationDate + ", label=" + label + ", description=" + description
				+ ", subject=" + subject + ", token=" + token + "]";
	}

}