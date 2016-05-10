/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({"route"})
@XmlRootElement(name = "InconsistentSearchDto")
@XmlType(name="InconsistentSearchDto")
@ApiModel(value = "InconsistentSearchDto", description = "InconsistentSearchDto")
public class InconsistentSearchDto {

	protected String uuid;

	@ApiModelProperty(value = "Email")
	protected String userMail;

	@ApiModelProperty(value = "Account is in ldap")
	protected Boolean ldap = false;

	@ApiModelProperty(value = "Account is in database")
	protected Boolean database  = false;

	@ApiModelProperty(value = "Account is guest")
	protected Boolean guest = false;

	@ApiModelProperty(value = "Domain identifier")
	private String identifier;

	@ApiModelProperty(value = "Domain label")
	protected String label;

	@ApiModelProperty(value = "Domain type")
	protected String domainType;

	public InconsistentSearchDto() {
		super();
	}

	public InconsistentSearchDto(AbstractDomain domain, String userMail) {
		this.userMail = userMail;
		this.label = domain.getLabel();
		this.identifier = domain.getUuid();
		this.domainType = domain.getDomainType().toString();
		this.uuid = UUID.randomUUID().toString();
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public Boolean isLdap() {
		return ldap;
	}

	public void setLdap(Boolean ldap) {
		this.ldap = ldap;
	}

	public Boolean isDatabase() {
		return database;
	}

	public void setDatabase(Boolean database) {
		this.database = database;
	}

	public Boolean isGuest() {
		return guest;
	}

	public void setGuest(Boolean guest) {
		this.guest = guest;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InconsistentSearchDto other = (InconsistentSearchDto) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
