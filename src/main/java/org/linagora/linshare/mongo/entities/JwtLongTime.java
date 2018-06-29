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

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "JwtLongTime")
@Document(collection = "jwt_longtime")
public class JwtLongTime {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@ApiModelProperty(value = "uuid")
	protected String uuid;

	@ApiModelProperty(value = "domain uuid")
	protected String domainUuid;

	@ApiModelProperty(value = "actor uuid")
	protected String actorUuid;

	@ApiModelProperty(value = "issuer")
	protected String issuer;

	@ApiModelProperty(value = "creation Date")
	protected Date creationDate;

	@ApiModelProperty(value = "token name")
	protected String label;

	@ApiModelProperty(value = "description")
	protected String description;

	@ApiModelProperty(value = "owner email")
	protected String subject;

	public JwtLongTime() {
		super();
	}

	public JwtLongTime(String tokenUuid,
			Date creationDate,
			String issuer,
			String label,
			String description,
			String actorUuid,
			String subject,
			String domainUuid) {
		this.uuid = tokenUuid;
		this.creationDate = creationDate;
		this.issuer = issuer;
		this.subject = subject;
		this.label = label;
		this.description = description;
		this.actorUuid = actorUuid;
		this.domainUuid = domainUuid;
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

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getActorUuid() {
		return actorUuid;
	}

	public void setActorUuid(String actorUuid) {
		this.actorUuid = actorUuid;
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

	@Override
	public String toString() {
		return "JwtLongTime [id=" + id + ", uuid=" + uuid + ", domainUuid=" + domainUuid + ", actorUuid=" + actorUuid
				+ ", issuer=" + issuer + ", creationDate=" + creationDate + ", label=" + label + ", description="
				+ description + ", subject=" + subject + "]";
	}

}