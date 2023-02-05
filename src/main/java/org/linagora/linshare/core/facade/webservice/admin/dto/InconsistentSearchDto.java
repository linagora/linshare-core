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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.linagora.linshare.core.domain.entities.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"route"})
@XmlRootElement(name = "InconsistentSearchDto")
@XmlType(name="InconsistentSearchDto")
@Schema(name = "InconsistentSearchDto", description = "InconsistentSearchDto")
public class InconsistentSearchDto {

	protected String uuid;

	@Schema(description = "Email")
	protected String userMail;

	@Schema(description = "Account is in ldap")
	protected Boolean ldap = false;

	@Schema(description = "Account is in database")
	protected Boolean database  = false;

	@Schema(description = "Account is guest")
	protected Boolean guest = false;

	@Schema(description = "Domain identifier")
	private String identifier;

	@Schema(description = "Domain label")
	protected String label;

	@Schema(description = "Domain type")
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
