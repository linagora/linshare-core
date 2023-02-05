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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DomainPolicy;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "DomainPolicy")
@Schema(name = "DomainPolicy", description = "Policy of a domain, defining the access policy of the domain")
public class DomainPolicyDto {

	@Schema(description = "Identifier")
	private String identifier;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "Access policy of the domain")
	private DomainAccessPolicyDto accessPolicy;

	public DomainPolicyDto(final DomainPolicy p) {
		this.identifier = p.getUuid();
		this.label = p.getLabel();
		this.description = p.getDescription();
		this.accessPolicy = new DomainAccessPolicyDto(p.getDomainAccessPolicy());
	}

	public DomainPolicyDto() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainAccessPolicyDto getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(DomainAccessPolicyDto domainAccessPolicy) {
		this.accessPolicy = domainAccessPolicy;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
