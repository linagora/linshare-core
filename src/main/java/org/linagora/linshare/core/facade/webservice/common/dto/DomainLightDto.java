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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Domain")
@Schema(name = "DomainLightDto", description = "")
public class DomainLightDto {

	private String label;

	private String identifier;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private DomainType type;

	public DomainLightDto(){}

	public DomainLightDto(AbstractDomain domain) {
		this.identifier = domain.getUuid();
		this.label = domain.getLabel();
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

	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DomainLightDto [label=" + label + ", identifier=" + identifier
				+ "]";
	}
}
