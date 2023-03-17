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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.mongo.entities.mto.DomainMto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Domain")
@Schema(name = "DomainV5Light", description = "A LinShare's domain")
public class DomainLightDto {

	@Schema(description = "Domain's uuid")
	private String uuid;

	@Schema(description = "Domain's name")
	private String name;

	public DomainLightDto() {
		super();
	}

	public DomainLightDto(AbstractDomain domain) {
		this.setUuid(domain.getUuid());
		this.setName(domain.getLabel());
	}

	public DomainLightDto(DomainMto domain) {
		this.setUuid(domain.getUuid());
		this.setName(domain.getLabel());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DomainLightDto [uuid=" + uuid + ", name=" + name + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DomainLightDto that = (DomainLightDto) o;
		return Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name);
	}
}
